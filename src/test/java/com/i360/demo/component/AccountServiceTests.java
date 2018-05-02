package com.i360.demo.component;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.instanceOf;

import java.io.IOException;

import org.apache.http.NoHttpResponseException;
import org.apache.http.client.ClientProtocolException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import com.i360.demo.Application;
import com.i360.demo.service.IAccountService;


@RunWith(SpringRunner.class)
@ActiveProfiles(value = "component")
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class AccountServiceTests {

	@ClassRule
	public static WireMockClassRule mockHttpServer = new WireMockClassRule(WireMockSpring.options().port(6067));

	@Rule
	public ExpectedException expected = ExpectedException.none();

	@Autowired
	private IAccountService service;

	
	@BeforeClass
    public static void setup() throws Exception {
        mockHttpServer.start();
    }

	@Test
    public void testHelloEndpoint() {
		stubFor(get("/remote")
                .willReturn(ResponseDefinitionBuilder.okForJson("{\"username\":\"john\",\"authorities\":[{\"authority\":\"ROLE_USER\"}]}")));
		assertThat(this.service.getAccount()).isEqualTo("Hello World!");
    }

	@Test
	public void test() throws Exception {
		stubFor(get(urlEqualTo("/remote"))
				.willReturn(aResponse().withHeader("Content-Type", "text/plain").withBody("Hello World!")));
		assertThat(this.service.getAccount()).isEqualTo("Hello World!");
	}

	@Test
	public void randomData() throws Exception {
		stubFor(get(urlEqualTo("/remote"))
				.willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE)));
		expected.expectCause(instanceOf(ClientProtocolException.class));
		assertThat(this.service.getAccount()).isEqualTo("Oops!");
	}

	@Test
	public void emptyResponse() throws Exception {
		stubFor(get(urlEqualTo("/remote"))
				.willReturn(aResponse().withFault(Fault.EMPTY_RESPONSE)));
		expected.expectCause(instanceOf(NoHttpResponseException.class));
		assertThat(this.service.getAccount()).isEqualTo("Oops!");
	}

	@Test
	public void malformed() throws Exception {
		stubFor(get(urlEqualTo("/remote"))
				.willReturn(aResponse().withFault(Fault.MALFORMED_RESPONSE_CHUNK)));
		// It's a different exception type than Jetty, but it's in the right ballpark
		expected.expectCause(instanceOf(IOException.class));
		expected.expectMessage("chunk");
		assertThat(this.service.getAccount()).isEqualTo("Oops!");
	}
	
	 @AfterClass
	    public static void teardown() throws Exception {
	        mockHttpServer.stop();
	    }

}
