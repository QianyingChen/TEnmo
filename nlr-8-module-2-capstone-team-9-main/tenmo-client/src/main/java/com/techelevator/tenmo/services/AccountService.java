package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.util.BasicLogger;
//import io.cucumber.java.bs.A;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import javax.naming.AuthenticationException;

public class AccountService extends AuthenticationService {

    private String authToken = null;
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }


    private final RestTemplate restTemplate = new RestTemplate();

    public AccountService(String baseUrl) {
        super(baseUrl + "");
    }

    public double getBalanceByUserId(int userId) throws AuthenticationException {
        Double accountBalance = null;
     try {
         ResponseEntity<Double> response = restTemplate.exchange(super.baseUrl + "/balance/" + userId, HttpMethod.GET, makeAuthEntity(), Double.class);
         accountBalance = response.getBody();
     } catch (RestClientResponseException | ResourceAccessException e) {
         BasicLogger.log(e.getMessage());
     }
     return accountBalance;
 }


    public int getAccountIdByUserId (int userId)  throws AuthenticationException {
        Account accounts = new Account();
        try{
            ResponseEntity<Account> response = restTemplate.exchange(super.baseUrl + "/users/" + userId, HttpMethod.GET, makeAuthEntity(), Account.class);
            accounts.setAccountId(response.getBody().getAccountId());
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
     return accounts.getAccountId();
        }


        public boolean updateBalance (Account updateAccount) {

            HttpEntity<Account> entity = makeAccountEntity(updateAccount);
            boolean success = false;
            try {
                restTemplate.put(super.baseUrl, updateAccount.getAccountId(), entity);
                success = true;
            } catch (RestClientResponseException | ResourceAccessException e) {
                BasicLogger.log(e.getMessage());

            }
            return success;

        }

    private HttpEntity<Account> makeAccountEntity(Account account) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(this.authToken);
        return new HttpEntity<>(account, headers);
    }

    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(this.authToken);
        return new HttpEntity<>(headers);
    }


}

