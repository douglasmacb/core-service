package br.gov.mg.bomdestino.coreservice.routes;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import br.gov.mg.bomdestino.coreservice.model.Auth;

@Component
public class AuthRoute extends RouteBuilder {

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
		
		rest("/core")
			.post("/auth")
				.type(Auth.class)
				.route().routeId("rest-core-auth")
				.to("direct:core-auth")
			.endRest()
			
			.get("/auth/validate/token")
				.route().routeId("rest-validate-token")
				.to("direct:validate-token")
			.endRest();
	
		from("direct:core-auth")
			.setHeader(Exchange.HTTP_METHOD, constant(HttpMethod.POST))
			.setHeader("Access-Control-Allow-Origin", constant("*"))
			.setHeader("Access-Control-Allow-Methods", constant("GET,PUT,POST,DELETE,PATCH,OPTIONS"))
			.setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
		.to("http://{{auth.service.url}}/auth?bridgeEndpoint=true&throwExceptionOnFailure=false");
		
		from("direct:validate-token")
			.routeId("validate-token")
			.removeHeaders("CamelHttp*")
			.setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
			.setHeader("Access-Control-Allow-Origin", constant("*"))
			.setHeader("Access-Control-Allow-Methods", constant("GET,PUT,POST,DELETE,PATCH,OPTIONS"))
			.setHeader(Exchange.HTTP_METHOD, constant(HttpMethod.GET))
		.toD("http://{{auth.service.url}}/auth/token/validate/${header.authorization}")
			.unmarshal().json(JsonLibrary.Gson)
		.end();
	}

}
