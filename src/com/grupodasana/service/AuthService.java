package com.grupodasana.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.log4j.Logger;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.gson.Gson;
import com.grupodasana.control.PreventasControl;
import com.grupodasana.control.SubscriptionsControl;
import com.grupodasana.control.UsuariosControl;
import com.grupodasana.controller.ConnectionHibernate;
import com.grupodasana.controller.Enviroment;
import com.grupodasana.controller.ErrorResponse;
import com.grupodasana.controller.IResponse;
import com.grupodasana.controller.PasswordUtils;
import com.grupodasana.entities.PreventasEntity;
import com.grupodasana.entities.SubscriptionsEntity;
import com.grupodasana.entities.users.UsuariosEntity;
import com.grupodasana.service.old.SuscripcionPHP;
import com.grupodasana.service.old.UsuarioPHP;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "authService")
@Path("authService")
public class AuthService extends GenericService {

	private static final Logger log = Logger.getLogger(AuthService.class);
	UsuariosControl control = new UsuariosControl(ConnectionHibernate.factory);
	SubscriptionsControl subControl = new SubscriptionsControl(ConnectionHibernate.factory);
	PreventasControl pControl = new PreventasControl(ConnectionHibernate.factory);

	@POST
	@Path("authUser")
	@Produces(MediaType.APPLICATION_JSON)
	public Response authUser(UsuariosEntity usuarioAuth) {
		String message = "[SERVICE] No se pudo autenticar de " + this.getClass().getName();
		UsuariosEntity usuarioResponse = null;
		try {
			usuarioResponse = control.obtenerPorEmail(usuarioAuth.getEmailUsuario());

			if (usuarioResponse != null) {
				String mySecurePassword = PasswordUtils.generateSecurePassword(usuarioAuth.getPasswordUsuario(),
						Enviroment.SALT);
				if (usuarioResponse.getPasswordUsuario().equals(mySecurePassword)) {
					// se generará el token
					Algorithm algorithm = Algorithm.HMAC256(Enviroment.PS_SECRET);
					String token = JWT.create().withIssuer("auth0").withSubject(usuarioResponse.getEmailUsuario())
							.sign(algorithm);

					usuarioResponse.setJwtTokenUser(token);
					usuarioResponse.setPasswordUsuario("");

					// verificar suscripciones pasadas o preventa
					SubscriptionsEntity sus = this.verificarSuscripcionPreventa(usuarioResponse.getIdUsuarios(),
							usuarioAuth.getEmailUsuario());
					
					usuarioResponse.setSubscriptionEntity(sus);

					return IResponse.OK200(usuarioResponse);
				} else {
					return IResponse.errorResponse401(ErrorResponse.USUARIO_INVALIDO, message);
				}
			} else {
				// usuario no existe
				// buscar en la base de datos anterior con la api de php
				String url = "https://pruebasapi.dasana.mx/api/usuario/" + usuarioAuth.getEmailUsuario() + "/"
						+ usuarioAuth.getPasswordUsuario();

				OkHttpClient client = new OkHttpClient();
				Request request = new Request.Builder().url(url).get().build();
				com.squareup.okhttp.Response response = client.newCall(request).execute();

				if (response.code() == 200 || response.code() == 201) {
					String body = response.body().string();
					if (!body.equals("null")) {
						Gson gson = new Gson();
						UsuarioPHP user = gson.fromJson(body, UsuarioPHP.class);

						UsuariosEntity entity = new UsuariosEntity();
						entity.setNombreUsuario(user.getNombre());
						entity.setApellidosUsuario(user.getApellidos());
						entity.setEmailUsuario(user.getEmail());
						entity.setFechaNacimientoUsuario(user.getNacimiento());
						entity.getIdPais().setIdCountry(138); // mexico
						entity.setCreadoPor("automigrado");

						// Guardar club como cliente en stripe
						String customerId = this.guardarClienteStripe(entity);
						entity.setIdCustomerStripe(customerId);

						String mySecurePassword = PasswordUtils.generateSecurePassword(usuarioAuth.getPasswordUsuario(),
								Enviroment.SALT);

						entity.setPasswordUsuario(mySecurePassword);
						Integer id = control.guardar(entity);
						entity.setIdUsuarios(id);

						return authUser(usuarioAuth);
					}

					return IResponse.errorResponse401(ErrorResponse.USUARIO_INVALIDO, message);
				}
			}

		} catch (Exception e) {
			log.error(message, e);
			message += ": " + e.getMessage();
		}

		return IResponse.error500(message);
	}

	/**
	 * Guardar� el club como cliente en Stripe y regresar� su ID
	 * 
	 * @param clubEntity
	 * @return
	 * @throws StripeException
	 */
	private String guardarClienteStripe(UsuariosEntity usuarioEntity) throws StripeException {
		Stripe.apiKey = env.STRIPE_SK();

		Map<String, Object> params = new HashMap<>();
		params.put("description", usuarioEntity.getNombreUsuario() + " " + usuarioEntity.getApellidosUsuario());
		params.put("name", usuarioEntity.getNombreUsuario());
		params.put("email", usuarioEntity.getEmailUsuario().trim().toLowerCase());

		Customer customer = Customer.create(params);
		return customer.getId();
	}

	private SubscriptionsEntity verificarSuscripcionPreventa(Integer idUser, String email) throws Exception {
		Calendar cal = Calendar.getInstance();

		// verificar un automigrado
		SubscriptionsEntity subTemp = subControl.obtenerMigrado(email, "SERVICE.PREVENTA.REEDEM");
		if (subTemp != null) {
			if (subTemp.getIdSubscription() > 0) {
				return subTemp;
			}
		}

		subTemp = subControl.obtenerMigrado(email, "SERVICE.PHP.MIGRADO");
		if (subTemp != null) {
			if (subTemp.getIdSubscription() > 0) {
				return subTemp;
			}
		}

		// consultar primero preventa
		List<PreventasEntity> preventaList = pControl.obtenerPorEmail(email);

		if (!preventaList.isEmpty()) {
			SubscriptionsEntity suscripcion = new SubscriptionsEntity();
			suscripcion.getUsuarioEntity().setIdUsuarios(idUser);
			suscripcion.setIdCustomSubscription(null);
			suscripcion.getSubcriptionPlansEntity().setIdSubcriptionPlans(15); // hardcode
			suscripcion.getSubscriptionCardEntity().setIdSubscriptionCard(null);
			suscripcion.setSubscriptionType("bonificada");
			suscripcion.setSubscriptionStatus("active");

			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.set(Calendar.MONTH, Calendar.JANUARY);
			cal.set(Calendar.YEAR, 2022);
			suscripcion.setSubscriptionStart((cal.getTimeInMillis() / 1000));

			cal.set(Calendar.DAY_OF_MONTH, 31);
			cal.set(Calendar.MONTH, Calendar.DECEMBER);
			cal.set(Calendar.YEAR, 2022);
			suscripcion.setSubscriptionEnd((cal.getTimeInMillis() / 1000));

			suscripcion.setTrialPeriodDays(0);
			suscripcion.setCustomPrice((long) 0);
			suscripcion.setCustomPriceDecimal(BigDecimal.valueOf(0.0));
			suscripcion.setExecutedFrom("SERVICE.PREVENTA.REEDEM");
			suscripcion.setCreatedBy("self");
			subControl.guardar(suscripcion);
			return suscripcion;
		}

		String url = "https://pruebasapi.dasana.mx/api/suscripciones/" + email;
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder().url(url).get().build();
		com.squareup.okhttp.Response response = client.newCall(request).execute();

		if (response.code() == 200 || response.code() == 201) {
			String body = response.body().string();
			if (!body.equals("null")) {
				Gson gson = new Gson();

				SuscripcionPHP[] list = gson.fromJson(body, SuscripcionPHP[].class);
				List<SuscripcionPHP> susList = Arrays.asList(list);

				if (susList.size() > 0) {
					SuscripcionPHP sub = susList.get(0);
					SubscriptionsEntity suscripcion = new SubscriptionsEntity();
					suscripcion.getUsuarioEntity().setIdUsuarios(idUser);
					suscripcion.setIdCustomSubscription(sub.getIdcompra());
					suscripcion.getSubcriptionPlansEntity().setIdSubcriptionPlans(15); // hardcode
					suscripcion.getSubscriptionCardEntity().setIdSubscriptionCard(null);
					suscripcion.setSubscriptionType(sub.getSubscription_type());
					suscripcion.setSubscriptionStatus("active");

					suscripcion.setSubscriptionStart((sub.getFechainicio().getTime() / 1000));
					suscripcion.setSubscriptionEnd((sub.getFechafinal().getTime() / 1000));

					suscripcion.setTrialPeriodDays(0);
					suscripcion.setCustomPrice(sub.getCantidad().longValue());
					suscripcion.setCustomPriceDecimal(BigDecimal.valueOf(sub.getCantidad()));
					suscripcion.setExecutedFrom("SERVICE.PHP.MIGRADO");
					suscripcion.setCreatedBy("self");

					subControl.guardar(suscripcion);
					return suscripcion;
				}

			}
		}

		return null;
	}

}
