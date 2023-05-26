package com.grupodasana.service;

//import javax.json.JsonObject;
import javax.ws.rs.Path;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;





@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "notificacionService")
@Path("notificacionService")
public class NotificacionService extends GenericService {

//	private static final Logger log = Logger.getLogger(NotificacionService.class);
//	private static final String auth = "key=AAAAS9MOMKA:APA91bF1D8hG5V2nS0e4u8gxSsLTHYuPBJTau4lsNhy2m167FQXZBCemfNLBOlGnJY2-8_9F7QyM6aSJL3rI_Wsuu47zjRaRB1pHo21-Q2NpmTbKIa7yDWVixhBtPm3iQDI7PzCYPeBo";
	
//	@POST
//	@Path("fire")
//	@Produces(MediaType.APPLICATION_JSON)
//	public Response fire(Object body) {
//	Gson gson = new Gson(); 
//	String json = gson.toJson(body);
//
//		try {
//			
//			 HttpClient client = HttpClient.newHttpClient();
//		     HttpRequest request = HttpRequest.newBuilder()
//		                .uri(URI.create("https://fcm.googleapis.com/fcm/send"))
//		                .header("Authorization",auth)
//		                .header("Content-Type", "application/json")
//		                .POST(HttpRequest.BodyPublishers.ofString(json))
//		                .build();
//		     
//		     HttpResponse<String> response = client.send(request,
//		                HttpResponse.BodyHandlers.ofString());
//		     
//		     System.out.println(response.body());
//		     
//			return Response.status(Status.OK).entity(response.body()).build();
//
//			
//		} catch (Exception e) {
//			log.error(e);
//		}
//		return null;
//		
//
//	}

	
}
