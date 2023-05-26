package com.grupodasana.controller;

/**
 * Clase enviroment para manejar las keys de pruebas y producción cambiando una
 * sola variable
 * 
 * @author ialeman
 *
 */
public class Enviroment {

	public static final String SALT = "AB";
	public static final String PS_SECRET = "As3cr3t";

	private boolean liveMode = false;

	private String stripeSk = "sk_test_";
	private String rutaApi 		  = "C:\\Users\\kirk\\dasanaapi\\storage\\";
	private String rutaApiCorreos = "C:\\Users\\kirk\\dasanaapi\\correos\\";

	private String paypalClientId = "keys";
	private String paypalSecretKey = "secret";
	
	private String openpayId = "id";
	private String openpaySecret = "pk";

	/**
	 * Constructor principal, si el liveMode es true, cambiará las llaves a las oficiales
	 * 
	 * @param liveMode
	 */
	public Enviroment(boolean liveMode) {
		if (liveMode == true) {
			stripeSk = "sk_live_";
			rutaApi = "/home/dasanaapi/storage/";
//			rutaApi = "/home/dasanaco/public_html/dasanaapi/storage/";
			rutaApiCorreos = "/home/dasanaapi/correos/";
			
			this.liveMode = liveMode;
			
			paypalClientId = "id";
			paypalSecretKey = "secret";
			
			openpayId = "id";
			openpaySecret = "secret";
		}
	}

	/**
	 * Función para reemplazar las llaves sin mover el código fuente
	 * Para propositos de pruebas de cada programador
	 * 
	 * @param stripeSk
	 * @param rutaApi
	 * @param paypalClientId
	 * @param paypalSecretKey
	 */
	public void replaceSandboxKeys(String stripeSk, String rutaApi, String paypalClientId, String paypalSecretKey) {
		this.stripeSk = stripeSk;
		this.rutaApi = rutaApi;
		this.paypalClientId = paypalClientId;
		this.paypalSecretKey = paypalSecretKey;
		this.liveMode = false; // sandbox
	}

	/* getters */
	public String STRIPE_SK() {
		return stripeSk;
	}

	public String RUTA_API() {
		return rutaApi;
	}
	
	public String RUTA_API_CORREOS() {
		return rutaApiCorreos;
	}

	public boolean isLiveMode() {
		return liveMode;
	}

	public String PAYPAL_CLIENT_ID() {
		return paypalClientId;
	}

	public String PAYPAL_SECRET_KEY() {
		return paypalSecretKey;
	}
	
	public String OPENPAY_ID() {
		return openpayId;
	}
	
	public String OPENPAY_SECRET() {
		return openpaySecret;
	}

}
