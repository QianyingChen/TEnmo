package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.util.BasicLogger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;


    public class UserService extends AuthenticationService {

        private String authToken = null;
        public void setAuthToken(String authToken) {
            this.authToken = authToken;
        }

        private final RestTemplate restTemplate = new RestTemplate();

        public UserService(String baseUrl) {
            super(baseUrl + "users/");
        }

        public User[] listUsers() {
            User[] users = null;
            try {
                ResponseEntity<User[]> response = restTemplate.exchange(super.baseUrl, HttpMethod.GET, makeAuthEntity(), User[].class);
                users = response.getBody();
            } catch (RestClientResponseException | ResourceAccessException e) {
                BasicLogger.log(e.getMessage());

            }
            return users;
        }

        public String getUsernameByUserId (int userId){
            String username = "";
            try {
                ResponseEntity<String> response = restTemplate.exchange(super.baseUrl + "/" + userId, HttpMethod.GET, makeAuthEntity(), String.class);
                username = response.getBody();
            } catch (RestClientResponseException | ResourceAccessException e) {
                BasicLogger.log(e.getMessage());

            }
            return username;
        }

        private HttpEntity<Void> makeAuthEntity() {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(this.authToken);
            return new HttpEntity<>(headers);
        }
    }