package com.grupodasana.service;

import java.io.FileInputStream;
import java.io.InputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import javax.imageio.ImageIO;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.log4j.Logger;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.grupodasana.control.PermisosUsuarioControl;
import com.grupodasana.control.RecuperacionesControl;
import com.grupodasana.control.UsuariosControl;
import com.grupodasana.controller.ConnectionHibernate;
import com.grupodasana.controller.Enviroment;
import com.grupodasana.controller.ErrorResponse;
import com.grupodasana.controller.IResponse;
import com.grupodasana.controller.PasswordUtils;
import com.grupodasana.entities.users.PermisosUsuariosEntity;
import com.grupodasana.entities.users.RecuperacionesEntity;
import com.grupodasana.entities.users.UsuariosEntity;
//import com.mysql.fabric.Response;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "usuariosService")
@Path("usuariosService")
public class UsuariosService extends GenericService {
	private static final Logger log = Logger.getLogger(UsuariosService.class);
	private UsuariosControl control = new UsuariosControl(ConnectionHibernate.factory);
	private PermisosUsuarioControl pControl = new PermisosUsuarioControl(ConnectionHibernate.factory);
	private RecuperacionesControl rControl = new RecuperacionesControl(ConnectionHibernate.factory);
	private final String rutaImage = env.RUTA_API() + "images/usuarios/";

	@GET
	@Path("migraciones/make")
	@Produces(MediaType.APPLICATION_JSON)
	public Response migraciones() {
		String message = "[SERVICE] No se pudo guardar de " + this.getClass().getName();
		try {

		} catch (Exception e) {
			log.error(message, e);
			message += ": " + e.getMessage();
		}
		return IResponse.error500(message);
	}

	@GET
	@Path("getAll")
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenerTodo() {
		String message = "[SERVICE] No se pudo guardar de " + this.getClass().getName();
		try {
			List<UsuariosEntity> list = control.obtenerTodo();
			return IResponse.OK200(list);
		} catch (Exception e) {
			log.error(message, e);
			message += ": " + e.getMessage();
		}

		return IResponse.error500(message);
	}

	@POST
	@Path("getByDates")
	@Produces(MediaType.APPLICATION_JSON)
	@SuppressWarnings("deprecation")
	public Response obtenerPorFechas(@QueryParam("fechaInicial") Date fechaInicial,
			@QueryParam("fechaFinal") Date fechaFinal) {

		String message = "[SERVICE] No se pudo guardar de " + this.getClass().getName();
		try {
			int daysCount[] = { 0, 0, 0, 0, 0, 0, 0 };

//			for (UsuariosEntity temp : list) {
//				Calendar cal = Calendar.getInstance();
//				cal.setTime(temp.getFechaCreado());
//				daysCount[cal.get(Calendar.DAY_OF_WEEK) - 1]++;
//			}

			int delta = (int) ((fechaInicial.getTime() - fechaFinal.getTime()) / 1000) / 86400;

			fechaFinal.setHours(0);
			fechaFinal.setMinutes(0);
			fechaFinal.setSeconds(0);
			fechaFinal.setDate(fechaFinal.getDate() - 1); // inicial
//			delta += 1; 

			Date secondDate;

			for (int i = 0; i < delta; i++) {
				fechaFinal.setDate(fechaFinal.getDate() + 1);
				secondDate = new Date(fechaFinal.getTime());
				secondDate.setHours(11);
				secondDate.setMinutes(59);
				secondDate.setSeconds(59);
//				secondDate.setDate(secondDate.getDate() + 1);
//				System.out.println(fechaFinal+" -> "+secondDate);

				List<UsuariosEntity> list = control.obtenerPorFechas(fechaFinal, secondDate);
				daysCount[i] = list.size();
			}

			return IResponse.OK200(daysCount);
		} catch (Exception e) {
			log.error(message, e);
			message += ": " + e.getMessage();
		}

		return IResponse.error500(message);
	}

	@POST
	@Path("getByCountry")
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenerPorIdPais(@QueryParam("idPais") Integer idPais) {
		String message = "[SERVICE] No se pudo guardar de " + this.getClass().getName();
		try {
			List<UsuariosEntity> list = control.obtenerPorIdPais(idPais);
			return IResponse.OK200(list.size());
		} catch (Exception e) {
			log.error(message, e);
			message += ": " + e.getMessage();
		}

		return IResponse.error500(message);
	}

	@POST
	@Path("add")
	@Produces(MediaType.APPLICATION_JSON)
	public Response guardar(UsuariosEntity entity) {
		String message = "[SERVICE] No se pudo guardar de " + this.getClass().getName();
		UsuariosEntity repetido;
		try {
			// TODO Mover este proceso al query de registro?
			String email = entity.getEmailUsuario();
			repetido = control.obtenerPorEmail(email);

			if (repetido == null) {
				// Guardar club como cliente en stripe
				String customerId = this.guardarClienteStripe(entity);
				entity.setIdCustomerStripe(customerId);

				String mySecurePassword = PasswordUtils.generateSecurePassword(entity.getPasswordUsuario(),
						Enviroment.SALT);
				entity.setPasswordUsuario(mySecurePassword);
				Integer id = control.guardar(entity);
				entity.setIdUsuarios(id);

				// proceso para enviar el correo
				String recuperacion = env.RUTA_API_CORREOS() + "nuevo_registro.html";
				InputStream ins = new FileInputStream(recuperacion);
				Scanner obj = new Scanner(ins);
				String html = "";

				while (obj.hasNextLine()) {
					html += obj.nextLine();
				}

				obj.close();
				String titulo = "¡Hola! Bienvenida a la familia Dasana.";
				boolean sended = enviarMensajeHTML(html, titulo, email);
				if (sended) {
					System.out.println("Mensaje enviado");
				}

				return IResponse.OK200(entity);
			}

			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(new ErrorResponse(ErrorResponse.USUARIO_DUPLICADO, message)).build();
		} catch (Exception e) {
			log.error(message, e);
		}

		return IResponse.error500(message);
	}

	@POST
	@Path("edit")
	@Produces(MediaType.APPLICATION_JSON)
	public Response editarUsuario(UsuariosEntity entity) {
		String message = "[SERVICE] No se pudo guardar de " + this.getClass().getName();
		try {
			control.editar(entity);
			return IResponse.OK200(entity);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return IResponse.error500(message);
	}

	@POST
	@Path("editImage")
	@Produces(MediaType.APPLICATION_JSON)
	public Response editarImageProfile(UsuariosEntity entity) {
		String message = "[SERVICE] No se pudo guardar de " + this.getClass().getName();
		try {
			Date fechaActual = new Date();
			String imageName = replacesUtf8(entity.getNombreUsuario()).replace(" ", "-");
			String imageType = entity.getImageType();
			imageName = imageName + "-" + "profile" + "-" + fechaActual.getTime() + imageType;
			byte[] imageTmpFront = Base64.getDecoder().decode(entity.getBase64Image());
			InputStream imagen = new ByteArrayInputStream(imageTmpFront);
			boolean saveImage = copiarArchivo(rutaImage, imageName, imagen);
			entity.setUrlImageUsuarios(imageName);
			if (saveImage) {
				control.editarImageUsuario(entity);
				return Response.status(Status.OK).entity(imageName).build();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return IResponse.error500(message);
	}

	@GET
	@Path("getImage")
	@Produces({ "image/png", "image/jpg" })
	public Response obtenerImagen(@QueryParam("imageName") String imageName) {
		String message = "[SERVICE] No se encontró la imagen de " + this.getClass().getName();
		BufferedImage image;
		try {
			if (imageName != null) {
				String imageSplit[] = imageName.split("\\.");
				String imageType = imageSplit[1];
				String urlImage = rutaImage + "" + imageName;
				image = ImageIO.read(new File(urlImage));
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ImageIO.write(image, imageType, baos);
				byte[] imageData = baos.toByteArray();

				return Response.ok(imageData).build();
			}
		} catch (IOException e) {
			System.out.println("No se encontró la imagen del usuario");
		}

		return IResponse.error500(message);
	}

	@POST
	@Path("edit")
	@Produces(MediaType.APPLICATION_JSON)
	public Response editar(UsuariosEntity entity) {
		String message = "[SERVICE] No se pudo guardar de " + this.getClass().getName();
		try {
			control.editar(entity);
			return IResponse.OK200(entity);
		} catch (Exception e) {
			log.error(message, e);
		}

		return IResponse.error500(message);
	}

	@GET
	@Path("getByEmail")
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtener(@QueryParam("email") String email) {
		String message = "[SERVICE] No se pudo obtener de " + this.getClass().getName();
		UsuariosEntity usuario = null;

		try {
			usuario = control.obtenerPorEmail(email);

			if (usuario != null) {
				return IResponse.OK200(usuario);
			}

			return Response.status(Status.NOT_FOUND).build();

		} catch (Exception e) {
			log.error(message, e);
		}

		return IResponse.error500(message);
	}

	@POST
	@Path("getValidateToken")
	@Produces(MediaType.APPLICATION_JSON)
	public Response validateToken(@QueryParam("token") String token) {
		String message = "[SERVICE] No se pudo validar " + this.getClass().getName();
		try {
			String emailUser = getEmailVerifyToken(token);
			return IResponse.OK200(emailUser);
		} catch (Exception e) {
			log.error(message, e);
			return Response.status(Status.UNAUTHORIZED).entity(new ErrorResponse(ErrorResponse.TOKEN_INVALIDO, message))
					.build();
		}
	}

	@POST
	@Path("getAccessUser")
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenerUsuario(@HeaderParam("authorization") String bearerToken) {
		String message = "[SERVICE] No se pudo obtener de " + this.getClass().getName();
		UsuariosEntity usuarioResponse = null;
		String emailUser;

		try {
			String splitToken[] = bearerToken.split(" ");
			emailUser = getEmailVerifyToken(splitToken[1]);
		} catch (Exception e) {
			log.error(message, e);
			return Response.status(Status.UNAUTHORIZED).entity(new ErrorResponse(ErrorResponse.TOKEN_INVALIDO, message))
					.build();
		}

		try {
			usuarioResponse = control.obtenerPorEmail(emailUser);

			if (usuarioResponse != null) {
				// clear password
				usuarioResponse.setPasswordUsuario("");
				// obtenemos los permisos
				List<PermisosUsuariosEntity> permisosList = pControl
						.obtenerPermisosUsuario(usuarioResponse.getIdUsuarios());
				usuarioResponse.setPermisosList(permisosList);

				return IResponse.OK200(usuarioResponse);
			}

			return Response.status(Status.UNAUTHORIZED)
					.entity(new ErrorResponse(ErrorResponse.USUARIO_INVALIDO, message)).build();

		} catch (Exception e) {
			log.error(message, e);
		}

		return IResponse.error500(message);
	}

	@GET
	@Path("getRecoveryByToken")
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenerRecuperacionByToken(@QueryParam("token") String token) {
		String message = "[SERVICE] No se pudo obtener de " + this.getClass().getName();
		RecuperacionesEntity recuperacion = null;

		try {
			recuperacion = rControl.obtenerPorToken(token);

			if (recuperacion != null) {
				return IResponse.OK200(recuperacion);
			}

			return Response.status(Status.NOT_FOUND).build();

		} catch (Exception e) {
			log.error(message, e);
		}

		return Response.status(Status.INTERNAL_SERVER_ERROR)
				.entity(new ErrorResponse(ErrorResponse.SERVER_ERROR, message)).build();
	}

	@GET
	@Path("encriptar")
	@Produces(MediaType.APPLICATION_JSON)
	public String encriptarPassword(@QueryParam("password") String password) {
		String mySecurePassword = PasswordUtils.generateSecurePassword(password, Enviroment.SALT);
		return mySecurePassword;
	}

	@POST
	@Path("sendEmailRecovery/{email}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response enviarCorreoRecuperacion(@PathParam("email") String email) {
		String message = "[SERVICE] No se pudo obtener de " + this.getClass().getName();
		UsuariosEntity usuario = null;

		try {
			usuario = control.obtenerPorEmail(email);

			if (usuario != null) {
				String nombre = usuario.getNombreUsuario();
				String token = "";
				RecuperacionesEntity recuperacionExistente = null;

				do {
					token = randomString(8);
					recuperacionExistente = rControl.obtenerPorToken(token);
				} while (recuperacionExistente != null);

				String recuperacion = env.RUTA_API_CORREOS() + "recuperacion.html";
				InputStream ins = new FileInputStream(recuperacion);
				Scanner obj = new Scanner(ins);
				String html = "";

				while (obj.hasNextLine()) {
					html += obj.nextLine();
				}

				obj.close();

				html = html.replace("_NOMBRE_", nombre);
				html = html.replace("_TOKEN_", token);

				String titulo = "Dasana Recuperación Contraseña";
				boolean sended = enviarMensajeHTML(html, titulo, email);

				if (sended) {
					System.out.println("Mensaje enviado.");
				}

				RecuperacionesEntity recuperacionEntity = prepararRecuperacion(usuario, token);
				rControl.guardar(recuperacionEntity);
				return Response.status(Status.OK).build();
			}

			return Response.status(Status.NOT_FOUND).build();

		} catch (Exception e) {
			log.error(message, e);
		}

		return Response.status(Status.INTERNAL_SERVER_ERROR)
				.entity(new ErrorResponse(ErrorResponse.SERVER_ERROR, message)).build();
	}

	@GET
	@Path("validateRecoverycode")
	@Produces(MediaType.APPLICATION_JSON)
	public Response validarcodigorecuperacion(@QueryParam("code") String code, @QueryParam("email") String email) {
		String message = "[SERVICE] No se pudo obtener de " + this.getClass().getName();
		UsuariosEntity usuario = null;

		try {
			usuario = control.obtenerPorEmail(email);
			if (usuario != null) {
//				String nombre = usuario.getNombreUsuario();

				RecuperacionesEntity recuperacionExistente = null;

				recuperacionExistente = rControl.obtenerPorToken(code);
				if (recuperacionExistente != null) {
					if (recuperacionExistente.getCreadoPor().equals(email)) {// el correo corresponde
						return Response.status(Status.OK).build();
					} else {// no corresponde el codigo al correo
						return Response.status(Status.NOT_FOUND).build();
					}
				} else {
					return Response.status(Status.NOT_FOUND).build();
				}
			}

			return Response.status(Status.NOT_FOUND).build();

		} catch (Exception e) {
			log.error(message, e);
		}

		return Response.status(Status.INTERNAL_SERVER_ERROR)
				.entity(new ErrorResponse(ErrorResponse.SERVER_ERROR, message)).build();
	}

	@GET
	@Path("getUserData")
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenerDatosUsuario(@QueryParam("iduser") Integer idUser) {
		String message = "[SERVICE] No se pudo obtener de " + this.getClass().getName();
		UsuariosEntity usuario = null;

		try {
			usuario = control.obtenerPorId(idUser);

			if (usuario != null) {
				return IResponse.OK200(usuario);
			}

			return Response.status(Status.NOT_FOUND).build();

		} catch (Exception e) {
			log.error(message, e);
		}

		return IResponse.error500(message);
	}

	@GET
	@Path("redeemRecoveryToken")
	@Produces(MediaType.APPLICATION_JSON)
	public Response redimirTokenRecuperacion(@QueryParam("token") String token,
			@QueryParam("password") String contrasenia) {
		String message = "[SERVICE] No se pudo obtener de " + this.getClass().getName();

		try {
			RecuperacionesEntity recuperacion = rControl.obtenerPorToken(token);
			if (recuperacion != null && !recuperacion.getRedimidoRecuperacion()) {
				Integer idRecuperacion = recuperacion.getIdRecuperacion();
				Integer idUsuario = recuperacion.getIdUsuario();
				UsuariosEntity usuario = control.obtenerPorId(idUsuario);
				if (usuario != null) {
					String email = usuario.getEmailUsuario();
					String contraseniaSegura = PasswordUtils.generateSecurePassword(contrasenia, Enviroment.SALT);
					control.editarContraseniaPorId(idUsuario, contraseniaSegura, email);
					rControl.redimirTokenRecuperacion(idRecuperacion, email);
					return Response.status(Status.OK).build();
				}
			}
			return Response.status(Status.NOT_FOUND).build();

		} catch (Exception e) {
			log.error(message, e);
		}

		return Response.status(Status.INTERNAL_SERVER_ERROR)
				.entity(new ErrorResponse(ErrorResponse.SERVER_ERROR, message)).build();
	}

	/**
	 * Prepara un objeto de TokensRecuperacionEntity para guardarlo
	 * 
	 * @param usuario UsuariosEntity que quiere recuperar su cuenta
	 * @param token   token de recuperación
	 * @return Una instancia de TokensRecuperacionEntity preparada para guardarse.
	 */
	private RecuperacionesEntity prepararRecuperacion(UsuariosEntity usuario, String token) {
		RecuperacionesEntity tokenRecuperacionEntity = new RecuperacionesEntity();
		tokenRecuperacionEntity.setIdUsuario(usuario.getIdUsuarios());
		tokenRecuperacionEntity.setTokenRecuperacion(token);
		tokenRecuperacionEntity.setRedimidoRecuperacion(false);
		tokenRecuperacionEntity.setCreadoPor(usuario.getEmailUsuario());
		return tokenRecuperacionEntity;
	}

	/**
	 * Envía un mail de recuperación.
	 * 
	 * @param n Longitud del string generado
	 * @return Un String random
	 */
//	private boolean enviarEmailRecuperacion(String destinatario, String token, String nombre) throws Exception {
//		Properties propiedades = new Properties();
//		propiedades.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//		propiedades.put("mail.smtp.host", "dasana.mx");
//		propiedades.put("mail.smtp.auth", "true");
//		propiedades.put("mail.smtp.port", "465");
//
//		String emailRemitente = "noreply@dasana.mx";
//		String passwordRemitente = "noreply@D4s4n4";
//
//		Session sesion = Session.getDefaultInstance(propiedades, new Authenticator() {
//			@Override
//			protected PasswordAuthentication getPasswordAuthentication() {
//				return new PasswordAuthentication(emailRemitente, passwordRemitente);
//			}
//		});
//
//		Message mensaje = prepararMensaje(sesion, emailRemitente, destinatario, token, nombre);
//		Transport.send(mensaje);
//		System.out.println("Mensaje enviado.");
//		return true;
//	}

	/**
	 * Verificará el token y obtendrá el email del usuarioa
	 * 
	 * @param token_access
	 * @return String emailUser
	 * @throws Exception
	 */

	public String getEmailVerifyToken(String token_access) throws Exception {
		try {
			Algorithm algorithm = Algorithm.HMAC256("d4s4n@s3cr3t");
			JWTVerifier verifier = JWT.require(algorithm).withIssuer("auth0").build(); // Reusable verifier instance
			DecodedJWT jwt = verifier.verify(token_access);

			if (jwt != null) {
				return jwt.getSubject();
			}

			return null;
		} catch (Exception e) {
			String mensajeError = "No se ha podido verificar el token de la sesión";
			log.error(mensajeError, e);
			throw new Exception(mensajeError);
		}
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
}
