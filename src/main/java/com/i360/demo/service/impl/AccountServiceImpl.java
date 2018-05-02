package com.i360.demo.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.i360.demo.service.IAccountService;

@Service
public class AccountServiceImpl implements IAccountService {

	@Value("${remote.service.url}")
	private String url;

	@Autowired
	private RestTemplate restTemplate;


	@Override
	public String saveAccount(String input) {
		ResponseEntity<String> response = restTemplate.postForEntity(this.url, input, String.class);
		return response.getBody();
	}

	public String getAccount() {
		String value = this.restTemplate.getForEntity(this.url, String.class).getBody();
		return value;
	}


	@Override
	public String saveAuthorisedAccount(String input, String authorisation) {
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.AUTHORIZATION, authorisation);
		HttpEntity<String> request = new HttpEntity<String>("testinput", headers);
		String response = restTemplate.postForObject(this.url, request, String.class);
		return response;
	}

}
