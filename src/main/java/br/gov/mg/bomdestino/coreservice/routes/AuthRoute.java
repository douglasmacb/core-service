package br.gov.mg.bomdestino.coreservice.routes;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
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
			.host(host)
			.port(serverPort)
			.bindingMode(RestBindingMode.auto);
		
		rest("/core")
			.post("/auth")
				.type(Auth.class)
				.consumes("application/json")
				.produces("application/json")
				.route().routeId("rest-core-auth")
				.to("direct:core-auth")
			.endRest();
		
		from("direct:core-auth")
			.setHeader(Exchange.HTTP_METHOD, constant(HttpMethod.POST))
			.setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
		.to("http://{{auth.service.url}}/auth?bridgeEndpoint=true&throwExceptionOnFailure=false");
	}

}
