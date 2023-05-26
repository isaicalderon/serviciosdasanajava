package com.grupodasana.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.grupodasana.controller.Enviroment;
import com.grupodasana.entities.SubgrupoAlimentosEntity;

public class GenericService {
	private static final Logger log = Logger.getLogger(GenericService.class);
//	protected final String STRIPE_APIKEY_TEST = "sk_test_51JO3oNBMyasUmecHC9IHlwWpzUT9feXfp4YOA3t7GBJsz0h7uYCx98WI0ZOh2hR7agcsIvqkS6OnWLxNTKpMkws900P7Pjo0Sd";
	protected Enviroment env = new Enviroment(true);
	protected String emailRemitente = "noreply@dasana.mx";
	protected String passwordRemitente = "noreply@D4s4n4";
	

	public GenericService() {
		// remplazar keys si es necesario para cada programador, seguirá siendo sandbox
//		env.replaceSandboxKeys(
//				"sk_test_51Joy2iKPOpx9qN4eH84sPt0avEiN0Keq1dBDHUsDUNu53KOMH7wBg3hEP56GtlShNhFrs0pWB7CpXsXJBjeGZ9Mz00Jzdolek8",
//				"C:\\Users\\kirk\\dasanaapi\\storage\\",
//				"AZSPiAuuOHGY_UmUDLJfOlRjlM08YzC3lJ5kCYClMBVm6BDR_QFzM4PQail7GqmVWLj8pz9W23eSTDhp",
//				"EKBJM4zk5X1GFLH_JxzY5Vu4-XUHdWFEGGnDIVW42lgr1CFdgE1EjyPq_8JcAwrfth0DxzVkRdsDqdLE");
	}

	/**
	 * Verificará el token y obtendrá el email del usuarioa
	 * 
	 * @param token_access
	 * @return String emailUser
	 * @throws Exception
	 */
	public String getEmailVerifyToken(String token_access) throws Exception {
		try {
			Algorithm algorithm = Algorithm.HMAC256(Enviroment.PS_SECRET);
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
	 * Obtendrá el index de una lista, según un id
	 * 
	 * @param subGrupoList
	 * @param idSubGrupo
	 * @return
	 */
	public Integer findIndex(List<SubgrupoAlimentosEntity> subGrupoList, Integer idSubGrupo) {
		Integer index = -1;
		for (int i = 0; i < subGrupoList.size(); i++) {
			if (idSubGrupo == subGrupoList.get(i).getIdSubgrupoAlimentos()) {
				index = i;
				break;
			}
		}
		return index;
	}

	/**
	 * Verificará el token
	 * 
	 * @param token_access
	 * @return String emailUser
	 * @throws Exception
	 */
	public boolean verifyToken(String token_access) throws Exception {
		try {

			Algorithm algorithm = Algorithm.HMAC256(Enviroment.PS_SECRET);
			JWTVerifier verifier = JWT.require(algorithm).withIssuer("auth0").build(); // Reusable verifier instance
			DecodedJWT jwt = verifier.verify(token_access);

			if (jwt != null) {
				return true;
			}

			return false;
		} catch (Exception e) {
			String mensajeError = "No se ha podido verificar el token de la sesión";
			log.error(mensajeError, e);
			throw new Exception(mensajeError);
		}
	}

	/**
	 * 
	 * @param mimeType
	 * @return
	 */
	public String typeOfMimeType(String mimeType) {
		String extension = "";

		// check image's extension
		switch (mimeType) {
		case "data:image/jpeg;base64":
		case "data:image/jpg;base64":
			extension = "jpeg";
			break;
		case "data:image/png;base64":
			extension = "png";
			break;
		case "data:application/pdf;base64":
			extension = "pdf";
			break;
		case "data:video/mp4;base64":
			extension = "mp4";
			break;
		default:
			// should write cases for more images types
			extension = "jpg";
			break;
		}

		return extension;
	}

	/**
	 * Reemplaza los caracteres especiales por normales
	 * 
	 * @param value
	 * @return
	 */
	public String replacesUtf8(String value) {
		value = value.toLowerCase();
		value = value.replace("á", "a");
		value = value.replace("é", "e");
		value = value.replace("í", "i");
		value = value.replace("ó", "ó");
		value = value.replace("ú", "u");
		value = value.replace("ñ", "nn");
		value = value.replace("&", "i");
		value = value.replace("+", "mas");
		value = value.replace("?", "");
		value = value.replace("¿", "");
		value = value.replace("!", "");
		value = value.replace("¡", "");
		
		return value;
	}

	public boolean copiarArchivo(String destination, String fileName, InputStream in) throws Exception {
		String error = "";
		boolean copiaCorrecta = true;
		OutputStream out = null;
		File theDir = new File(destination);

		// Crea el directorio
		if (!theDir.exists()) {
			try {
				theDir.mkdirs();
			} catch (SecurityException se) {
				error = "No se ha podido guardar el archivo " + fileName + " en la ruta " + destination;
				log.error(error, se);
			}
		}

		try {
			out = new FileOutputStream(destination + fileName);
			int read = 0;
			byte[] bytes = new byte[1024];
			while ((read = in.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
		} catch (Exception e) {
			copiaCorrecta = false;
		} finally {
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.flush();
				out.close();
				try {
					out.close();
				} catch (IOException ex) {
					copiaCorrecta = false;
				}
			}
		}
		return copiaCorrecta;
	}

	/**
	 * Crea un string random de una longitud dada.
	 * 
	 * @param n Longitud del string generado
	 * @return Un String random
	 */
	public String randomString(int n) {
		byte[] array = new byte[256];
		new Random().nextBytes(array);

		String randomString = new String(array, Charset.forName("UTF-8"));
		StringBuffer r = new StringBuffer();
		String AlphaNumericString = randomString.replaceAll("[^A-Za-z0-9]", "");

		for (int k = 0; k < AlphaNumericString.length(); k++) {
			if (Character.isLetter(AlphaNumericString.charAt(k)) && (n > 0)
					|| Character.isDigit(AlphaNumericString.charAt(k)) && (n > 0)) {
				r.append(AlphaNumericString.charAt(k));
				n--;
			}
		}

		return r.toString();
	}
	
	public Session preparedSendEmail() throws Exception {
		Properties propiedades = new Properties();
		propiedades.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		propiedades.put("mail.smtp.host", "dasana.mx");
		propiedades.put("mail.smtp.auth", "true");
		propiedades.put("mail.smtp.port", "465");

		Session sesion = Session.getDefaultInstance(propiedades, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(emailRemitente, passwordRemitente);
			}
		});

//		Message mensaje = prepararMensaje(sesion, emailRemitente, destinatario, token, nombre);
//		Transport.send(mensaje);
//		System.out.println("Mensaje enviado.");
		return sesion;
	}
	
	/**
	 * Crea y prepara una instancia de Message para enviar un correo de
	 * recuperación.
	 * 
	 * @param html         El código html que se enviará
	 * @param titulo       Titulo que tendrá el email
	 * @param remitente    E-mail que enviará el correo
	 * @param destinatario E-mail que recibirá el correo
	 * @return true o false si se envio el correo correctamente.
	 */
	public boolean enviarMensajeHTML(String html, String titulo, String destinatario) {
		try {
			Session sesion = preparedSendEmail();
			MimeMessage mensaje = new MimeMessage(sesion);
			mensaje.setFrom(new InternetAddress(emailRemitente));
			mensaje.setRecipient(Message.RecipientType.TO, new InternetAddress(destinatario));
			mensaje.setSubject(titulo);
			mensaje.setContent(html, "text/html");
			Transport.send(mensaje);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}
		return false;
	}


}
