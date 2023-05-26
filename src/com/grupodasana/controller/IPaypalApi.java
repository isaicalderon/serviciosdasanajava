package com.grupodasana.controller;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.grupodasana.entities.SubcriptionPlanEntity;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

/**
 * Java Api Paypal v1.0.1
 * En desarrollo por ialeman
 * 
 * @createdDate 10/10/21
 * @updateDate 07/12/21
 * 
 * @author ialeman
 * 
 */
public class IPaypalApi {

//	private static final String CLIENT_ID = "AZSPiAuuOHGY_UmUDLJfOlRjlM08YzC3lJ5kCYClMBVm6BDR_QFzM4PQail7GqmVWLj8pz9W23eSTDhp"; // isai sandbox
//	private static final String CLIENT_ID = "AYCaliD5HZZ1_VF-CWBsx0k3jCxtEQgJRu13UIW7qgsY8ib16eZ-mxfdcoyTQvW6PlQk6Inw6YwnNbP-";
	
//	private static final String SECRET_KEY = "EKBJM4zk5X1GFLH_JxzY5Vu4-XUHdWFEGGnDIVW42lgr1CFdgE1EjyPq_8JcAwrfth0DxzVkRdsDqdLE"; // isai sandbox
//	private static final String SECRET_KEY = "EKRZPTn3usHhjhNNSnOHwH5_JPJ1opxm_h97RIIHrUapeSIvyWYihssGPn36YN-kZG8DQSufrPCfS0uK";
	
	private String CLIENT_ID = "";
	private String SECRET_KEY = "";
	private boolean liveMode = false;

	/**
	 * Constructor de paypal 
	 * 
	 * @param liveMode
	 * @param CLIENT_ID
	 * @param SECRET_KEY
	 */
	public IPaypalApi(boolean liveMode, String CLIENT_ID, String SECRET_KEY) {
		this.liveMode = liveMode;
		this.CLIENT_ID = CLIENT_ID;
		this.SECRET_KEY = SECRET_KEY;
	}
	

	public String retriveAccessToken() throws Exception {
		String url = "https://api.sandbox.paypal.com/v1/oauth2/token";
		if (liveMode) {
			url = "https://api.paypal.com/v1/oauth2/token";
		}
		
		String basic64 = CLIENT_ID + ":" + SECRET_KEY;
		basic64 = Base64.getEncoder().encodeToString(basic64.getBytes());
		
		MediaType mediaType = MediaType.parse("text/plain");
		OkHttpClient client = new OkHttpClient();
		RequestBody body = RequestBody.create(mediaType, "grant_type=client_credentials");
		Request request = new Request.Builder()
				.url(url)
				.post(body)
				.addHeader("Authorization", "Basic " + basic64)
				.addHeader("Content-Type", "text/plain")
				.build();
		Response response = client.newCall(request).execute();
		
		if (response.code() == 200) {
			String bodyText = response.body().string();
			JsonElement bodyObject = new JsonParser().parse(bodyText);
			return bodyObject.getAsJsonObject().get("access_token").getAsString();
		}

		return "";
	}

	public String createProduct(String name, String description, String type, String category) throws Exception {
		String url = "https://api-m.sandbox.paypal.com/v1/catalogs/products";
		if (liveMode) {
			url = "https://api-m.paypal.com/v1/catalogs/products";
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", name);
		map.put("description", description);
		map.put("type", type);
		map.put("category", category);
		
		Gson gson = new Gson();
		JsonObject json = gson.toJsonTree(map).getAsJsonObject();
		
		MediaType mediaType = MediaType.parse("application/json");
		OkHttpClient client = new OkHttpClient();
		RequestBody body = RequestBody.create(mediaType, json.toString());
		String accessToken = this.retriveAccessToken();
		// accessToken = Base64.getEncoder().encodeToString(accessToken.getBytes());
		Request request = new Request.Builder()
				.url(url)
				.post(body)
				.addHeader("Content-Type", "application/json")
				.addHeader("Authorization", "Bearer " + accessToken)
				.build();
		Response response = client.newCall(request).execute();
		
		if (response.code() == 200 || response.code() == 201) {
			String bodyText = response.body().string();
			JsonElement bodyObject = new JsonParser().parse(bodyText);
			return bodyObject.getAsJsonObject().get("id").getAsString();
		}
		
		return null;
	}
	
	public String createPlan(String productId, SubcriptionPlanEntity planEntity) throws Exception  {
		String url = "https://api-m.sandbox.paypal.com/v1/billing/plans";
		if(liveMode) {
			url = "https://api-m.paypal.com/v1/billing/plans";
		}
		
//		Map<String, Object> rootMap = new HashMap<String, Object>();
//		rootMap.put("product_id", productId);
//		rootMap.put("name", planEntity.getSubcriptionPlansName());
//		rootMap.put("description", planEntity.getDescriptionPlan());
//		Map<String, Object> billingMap = new HashMap<String, Object>();
//		billingMap.put(url, billingMap);
		
		String bodySend = "{\r\n"
				+ "      \"product_id\": \""+productId+"\",\r\n"
				+ "      \"name\": \""+planEntity.getSubcriptionPlansName()+"\",\r\n"
				+ "      \"description\": \""+planEntity.getDescriptionPlan()+"\",\r\n"
				+ "      \"billing_cycles\": [\r\n"
				+ "          {\r\n"
				+ "          \"frequency\": {\r\n"
				+ "            \"interval_unit\": \"DAY\",\r\n"
				+ "            \"interval_count\": "+planEntity.getTrialDays()+"\r\n"
				+ "          },\r\n"
				+ "          \"tenure_type\": \"TRIAL\",\r\n"
				+ "          \"sequence\": 1,\r\n"
				+ "          \"total_cycles\": 1\r\n"
				+ "        },\r\n"
				+ "        {\r\n"
				+ "          \"frequency\": {\r\n"
				+ "            \"interval_unit\": \""+planEntity.getTipoRecurrencia().getCodeRecurrencia().toUpperCase()+"\",\r\n"
				+ "            \"interval_count\": 1\r\n"
				+ "          },\r\n"
				+ "          \"tenure_type\": \"REGULAR\",\r\n"
				+ "          \"sequence\": 2,\r\n"
				+ "          \"total_cycles\": 0,\r\n"
				+ "          \"pricing_scheme\": {\r\n"
				+ "            \"fixed_price\": {\r\n"
				+ "              \"value\": \""+planEntity.getSubcriptionPlanPrice()+"\",\r\n"
				+ "              \"currency_code\": \""+planEntity.getCurrencyTypeEntity().getCurrencyTypeIsoCode()+"\"\r\n"
				+ "            }\r\n"
				+ "          }\r\n"
				+ "        }\r\n"
				+ "      ],\r\n"
				+ "      \"payment_preferences\": {\r\n"
				+ "        \"auto_bill_outstanding\": true,\r\n"
				+ "        \"setup_fee\": {\r\n"
				+ "          \"value\": \"0\",\r\n"
				+ "          \"currency_code\": \""+planEntity.getCurrencyTypeEntity().getCurrencyTypeIsoCode()+"\"\r\n"
				+ "        },\r\n"
				+ "        \"setup_fee_failure_action\": \"CANCEL\",\r\n"
				+ "        \"payment_failure_threshold\": 3\r\n"
				+ "      },\r\n"
				+ "      \"taxes\": {\r\n"
				+ "        \"percentage\": \"0\",\r\n"
				+ "        \"inclusive\": false\r\n"
				+ "      }\r\n"
				+ "    }";
		
		MediaType mediaType = MediaType.parse("application/json");
		OkHttpClient client = new OkHttpClient();
		RequestBody body = RequestBody.create(mediaType, bodySend);
		String accessToken = this.retriveAccessToken();
		// accessToken = Base64.getEncoder().encodeToString(accessToken.getBytes());
		Request request = new Request.Builder()
				.url(url)
				.post(body)
				.addHeader("Accept", "application/json")
				.addHeader("Authorization", "Bearer " + accessToken)
				.addHeader("Content-Type", "application/json")
				.build();
		Response response = client.newCall(request).execute();
		
		if(response.code() == 200 || response.code() == 201) {
			String bodyText = response.body().string();
			JsonElement bodyObject = new JsonParser().parse(bodyText);
			return bodyObject.getAsJsonObject().get("id").getAsString();
		}
		return null;
	}

	public String retriveSubscriptionList(String planId) throws Exception {
		String url = "https://api.sandbox.paypal.com/v1/billing/plans/"+planId;
		if(liveMode) {
			url = "https://api.paypal.com/v1/billing/plans/"+planId;
		}
		
		OkHttpClient client = new OkHttpClient();
		String accessToken = this.retriveAccessToken();
		Request request = new Request.Builder()
				.url(url)
				.get()
				.addHeader("Content-Type", "application/json")
				.addHeader("Authorization", "Bearer " + accessToken)
				.build();
		Response response = client.newCall(request).execute();
		
		if(response.code() == 200 || response.code() == 201) {
			String bodyText = response.body().string();
			return bodyText;
		}
		
		return "";
	}
	
	public String createWebhook(String urlWebhook) throws Exception {
		String url = "https://api-m.sandbox.paypal.com/v1/notifications/webhooks";
		if(liveMode) {
			url = "https://api-m.paypal.com/v1/notifications/webhooks";
		}
		
		MediaType mediaType = MediaType.parse("application/json");
		String bodySend = "{\r\n"
				+ "  \"url\": \""+urlWebhook+"\",\r\n"
				+ "  \"event_types\": [\r\n"
				+ "    {\r\n"
				+ "      \"name\": \"*\"\r\n"
				+ "    }\r\n"
				+ "  ]\r\n"
				+ "}";
		
		OkHttpClient client = new OkHttpClient();
		String accessToken = this.retriveAccessToken();
		RequestBody body = RequestBody.create(mediaType, bodySend);
		Request request = new Request.Builder()
				.url(url)
				.post(body)
				.addHeader("Content-Type", "application/json")
				.addHeader("Authorization", "Bearer " + accessToken)
				.build();
		Response response = client.newCall(request).execute();
		if(response.code() == 200 || response.code() == 201) {
			String bodyText = response.body().string();
			return bodyText;
		}
		
		return null;
	}
	
	public String retriveOrder(String orderId) throws Exception {
		String url = "https://api-m.sandbox.paypal.com/v2/checkout/orders/"+orderId;
		if(liveMode) {
			url = "https://api.paypal.com/v2/checkout/orders/"+orderId;
		}
		
		OkHttpClient client = new OkHttpClient();
		String accessToken = this.retriveAccessToken();
		Request request = new Request.Builder()
				.url(url)
				.get()
				.addHeader("Content-Type", "application/json")
				.addHeader("Authorization", "Bearer " + accessToken)
				.build();
		Response response = client.newCall(request).execute();
		if(response.code() == 200 || response.code() == 201) {
			String bodyText = response.body().string();
			return bodyText;
		}
		
		return null;
	}

	public String retriveWebhook(String webhookId) throws Exception {
		String url = "https://api-m.sandbox.paypal.com/v1/notifications/webhooks-events/"+webhookId;
		if(liveMode) {
			url = "https://api-m.paypal.com/v1/notifications/webhooks-events/"+webhookId;
		}
		
		OkHttpClient client = new OkHttpClient();
		String accessToken = this.retriveAccessToken();
		Request request = new Request.Builder()
				.url(url)
				.get()
				.addHeader("Content-Type", "application/json")
				.addHeader("Authorization", "Bearer " + accessToken)
				.build();
		Response response = client.newCall(request).execute();
		if(response.code() == 200 || response.code() == 201) {
			String bodyText = response.body().string();
			return bodyText;
		}
		
		return null;
	}
	
	
	
}















