package com.i360.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.i360.demo.service.IAccountService;

@RestController
@RequestMapping("/account/v1")
public class AccountController {

	@Autowired
	private  IAccountService service;

	@PostMapping("/")
	public ResponseEntity<?> createAccountWithAuthorisation(@RequestBody String account,
			@RequestHeader(HttpHeaders.AUTHORIZATION) String authorisation) {
		return new ResponseEntity<String>(service.saveAuthorisedAccount(account, authorisation), HttpStatus.OK);
	}

	@GetMapping("/")
	public ResponseEntity<String> fetchAccounts() {
		return new ResponseEntity<String>(service.getAccount(), HttpStatus.OK);
	}

}
