package br.gov.mg.bomdestino.coreservice.routes;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import br.gov.mg.bomdestino.coreservice.model.Auth;

@Component
public class SturRoute extends RouteBuilder {
	
	@Value("${server.port}")
	private int serverPort;
	
	@Value("${server.host}")
	private String host;

	@Value("${stur.username}")
	private String sturUsername;
	
	@Value("${stur.password}")
	private String sturPassword;

	@Value("${stur.token}")
	private String sturToken;
	
	@Override
	public void configure() throws Exception {
		restConfiguration().host(host).port(serverPort).bindingMode(RestBindingMode.auto);
		
		rest("/stur")
			.post("/auth")
				.type(Auth.class)
				.consumes("application/json")
				.produces("application/json")
				.route().routeId("rest-auth")
				.to("direct:stur-auth")
			.endRest()
			
			.get("/iptu")
				.route().routeId("rest-all-iptu")
				.to("direct:call-iptu-rest-all")
			.endRest()
			
			.get("/itr")
				.route().routeId("rest-all-itr")
				.to("direct:call-itr-rest-all")
			.endRest()
			
			.get("/iptu/cnpj/{cnpj}")
				.route().routeId("rest-itr-by-cnpj")
				.to("direct:iptu-cnpj-service")
			.endRest()
			
			.get("/iptu/cpf/{cpf}")
				.route().routeId("rest-itr-by-cpf")
				.to("direct:iptu-cpf-service")
			.endRest();
		

		from("direct:stur-auth")
			.process(new Processor() {
				
				@Override
				public void process(Exchange exchange) throws Exception {
				   Message in = exchange.getIn();
			        Auth authBody = new Auth(sturUsername, sturPassword);
			        in.setBody(authBody);
				}
			})
			.marshal().json(JsonLibrary.Jackson)
			.setHeader(Exchange.HTTP_METHOD, constant(HttpMethod.POST))
			.setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
		.to("http://{{stur.url}}/auth?bridgeEndpoint=true&throwExceptionOnFailure=false");

		from("direct:call-iptu-rest-all")
			.routeId("iptu-service")
			
			.onException(Exception.class)
				.handled(true)
				.setBody(constant("[]"))
			.end()
			
			.removeHeaders("CamelHttp*")
			.setHeader("Authorization", constant(sturToken))
			.setHeader(Exchange.HTTP_METHOD, constant(HttpMethod.GET))
		.to("http://{{stur.url}}/iptu");
		
		from("direct:call-itr-rest-all")
			.routeId("itr-service")
			.to("direct:stur-auth").unmarshal().json(JsonLibrary.Jackson)
			.onException(Exception.class)
				.handled(true)
				.setBody(constant("[]"))
			.end()
			
			.removeHeaders("CamelHttp*")
			.setHeader("Authorization", constant(sturToken))
			.setHeader(Exchange.HTTP_METHOD, constant(HttpMethod.GET))
		.to("http://{{stur.url}}/itr");
		
		from("direct:iptu-cnpj-service")
			.routeId("iptu-cnpj-service")
			
			.onException(Exception.class)
				.handled(true)
				.setBody(constant("[]"))
			.end()
			.removeHeaders("CamelHttp*")
			.setHeader("Authorization", constant(sturToken))
			.setHeader(Exchange.HTTP_METHOD, constant(HttpMethod.GET))
			.toD("http://{{stur.url}}/iptu/cnpj/${header.cnpj}");
		
		from("direct:iptu-cpf-service")
			.routeId("iptu-cpf-service")
			
			.onException(Exception.class)
				.handled(true)
				.setHeader(Exchange.HTTP_RESPONSE_CODE).constant(HttpStatus.NO_CONTENT)
			.end()
			.removeHeaders("CamelHttp*")
			.setHeader("Authorization", constant(sturToken))
			.setHeader(Exchange.HTTP_METHOD, constant(HttpMethod.GET))
		.toD("http://{{stur.url}}/iptu/cpf/${header.cpf}");
	}

}
