package com.i360.demo.integration;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.Matchers.*;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import com.i360.demo.Application;

@RunWith(SpringRunner.class)
@ActiveProfiles(value = "integration")
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class AccountControllerTests {

	@ClassRule
	public static WireMockClassRule mockHttpServer = new WireMockClassRule(9999);

	@Autowired
	private RestTemplate restTemplate;

	@BeforeClass
	public static void setup() throws Exception {
		mockHttpServer.start();
	}

	@Test
	public void test1() {
		stubFor(get(urlEqualTo("/remote")).willReturn(aResponse().withStatus(HttpStatus.OK.value())
				.withHeader("Content-Type", APPLICATION_JSON_VALUE).withBodyFile("resource.json")));

		ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8080/account/v1/", String.class);

		assertThat("Verify Status Code", response.getStatusCode().equals(HttpStatus.OK));
		assertEquals("{\"message\":\"mocked remote service response\"}", response.getBody());

	}

	@Test
	public void test_create_with_header() {
		stubFor(post(urlEqualTo("/remote")).withRequestBody(containing("testinput"))
				.withHeader(HttpHeaders.AUTHORIZATION, equalTo("Bearer xyz123"))
				.willReturn(aResponse().withStatus(200).withBodyFile("resource.json")));

		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.AUTHORIZATION, "Bearer xyz123");

		HttpEntity<String> request = new HttpEntity<String>("testinput", headers);

		String response = restTemplate.postForObject("http://localhost:8080/account/v1/", request, String.class);

		// assertThat("Verify Status Code",
		// response.getStatusCode().equals(HttpStatus.OK));
		assertEquals("{\"message\":\"mocked remote service response\"}", response);
	}

//	@Test
//	public void test_create() {
//		stubFor(post(urlEqualTo("/remote")).withRequestBody(containing("testinput"))
//				.willReturn(aResponse().withStatus(200).withBodyFile("resource.json")));
//
//		ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:8080/account/v1/", "testinput",
//				String.class);
//
//		assertThat("Verify Status Code", response.getStatusCode().equals(HttpStatus.OK));
//		assertEquals("{\"message\":\"mocked remote service response\"}", response.getBody());
//	}

	@AfterClass
	public static void teardown() throws Exception {
		mockHttpServer.stop();
	}

}
