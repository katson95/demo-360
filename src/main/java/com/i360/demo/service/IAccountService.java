package com.i360.demo.service;

public interface IAccountService {

	String saveAccount(String input);

	String getAccount();

	String saveAuthorisedAccount(String input, String authorisation);
}
