package br.gov.mg.bomdestino.coreservice.routes;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class CitizeRoute extends RouteBuilder {
	@Value("${server.port}")
	private int serverPort;
	
	@Value("${server.host}")
	private String host;
	
	@Override
	public void configure() throws Exception {
		restConfiguration()
			.component("servlet")
			.bindingMode(RestBindingMode.json)
			.host(host)
			.port(serverPort)
			.enableCORS(true) 
	        .corsAllowCredentials(true) 
	        .corsHeaderProperty("Access-Control-Allow-Origin","*")
	        .corsHeaderProperty("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE,PATCH,OPTIONS")
	        .corsHeaderProperty("Access-Control-Allow-Headers","Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers, Authorization");
		
		rest("/servico")
			.get()
				.route().routeId("rest-service-get-all")
				.to("direct:service-get-all")
			.endRest()
			
			.get("/protocolo/{protocol}")
				.route().routeId("rest-service-get-by-protocol")
				.to("direct:service-get-by-protocol")
			.endRest()
			
			.get("/solicitacao")
				.route().routeId("rest-service-order-get-all")
					.to("direct:rest-service-order-get-all")
				.endRest()
		
			.get("/status")
				.route().routeId("rest-service-status-get-all")
					.to("direct:rest-service-status-get-all")
				.endRest()
						
			.patch("/solicitacao/{id}/status/{idStatus}")
				.route().routeId("rest-service-update-status")
					.to("direct:rest-service-update-status")
				.endRest()
			
			.post("/solicitacao")
				.route().routeId("rest-service-create")
				.to("direct:rest-service-create")
			.endRest();
	
		from("direct:service-get-all")
			.setHeader(Exchange.HTTP_METHOD, constant(HttpMethod.GET))
			.setHeader("Access-Control-Allow-Origin", constant("*"))
			.setHeader("Access-Control-Allow-Methods", constant("GET,PUT,POST,DELETE,PATCH,OPTIONS"))
			.setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
		.to("http://{{citizen.service.url}}/servico?bridgeEndpoint=true&throwExceptionOnFailure=false");
		
		from("direct:rest-service-order-get-all")
			.setHeader(Exchange.HTTP_METHOD, constant(HttpMethod.GET))
			.setHeader("Access-Control-Allow-Origin", constant("*"))
			.setHeader("Access-Control-Allow-Methods", constant("GET,PUT,POST,DELETE,PATCH,OPTIONS"))
			.setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
		.to("http://{{citizen.service.url}}/solicitacao?bridgeEndpoint=true&throwExceptionOnFailure=false");
		
		from("direct:rest-service-status-get-all")
			.setHeader(Exchange.HTTP_METHOD, constant(HttpMethod.GET))
			.setHeader("Access-Control-Allow-Origin", constant("*"))
			.setHeader("Access-Control-Allow-Methods", constant("GET,PUT,POST,DELETE,PATCH,OPTIONS"))
			.setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
		.to("http://{{citizen.service.url}}/status?bridgeEndpoint=true&throwExceptionOnFailure=false");
			
		from("direct:service-get-by-protocol")
			.routeId("route-service-get-by-protocol")
		
			.onException(Exception.class)
				.handled(true)
				.setHeader(Exchange.HTTP_RESPONSE_CODE).constant(HttpStatus.NO_CONTENT)
			.end()
			.removeHeaders("CamelHttp*")
			.setHeader("Access-Control-Allow-Origin", constant("*"))
			.setHeader("Access-Control-Allow-Methods", constant("GET,PUT,POST,DELETE,PATCH,OPTIONS"))
			.setHeader(Exchange.HTTP_METHOD, constant(HttpMethod.GET))
		.toD("http://{{citizen.service.url}}/solicitacao/protocol/${header.protocol}");
			
		from("direct:rest-service-update-status")
			.routeId("route-service-update-status")
		
			.onException(Exception.class)
				.handled(true)
				.setHeader(Exchange.HTTP_RESPONSE_CODE).constant(HttpStatus.NO_CONTENT)
			.end()
			.removeHeaders("CamelHttp*")
			.setHeader("Access-Control-Allow-Origin", constant("*"))
			.setHeader("Access-Control-Allow-Methods", constant("GET,PUT,POST,DELETE,PATCH,OPTIONS"))
			.setHeader(Exchange.HTTP_METHOD, constant(HttpMethod.PATCH))
		.toD("http://{{citizen.service.url}}/solicitacao/${header.id}/status/${header.idStatus}");
			
		from("direct:rest-service-create")
			.setHeader(Exchange.HTTP_METHOD, constant(HttpMethod.POST))
			.setHeader("Access-Control-Allow-Origin", constant("*"))
			.setHeader("Access-Control-Allow-Methods", constant("GET,PUT,POST,DELETE,PATCH,OPTIONS"))
			.setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
		.to("http://{{citizen.service.url}}/solicitacao?bridgeEndpoint=true&throwExceptionOnFailure=false");
		
	}
}
