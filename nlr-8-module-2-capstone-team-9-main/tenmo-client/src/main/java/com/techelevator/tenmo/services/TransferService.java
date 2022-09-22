package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import javax.naming.AuthenticationException;
import java.util.ArrayList;
import java.util.List;

public class TransferService extends AuthenticationService {

    private String authToken = null;

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    private final RestTemplate restTemplate = new RestTemplate();

    public TransferService(String baseUrl) {
        super(baseUrl + "transfers");
    }

    public Transfer[] getTransferHistory(int userId) {
        Transfer[] transfers = null;
        try {
            ResponseEntity<Transfer[]> response = restTemplate.exchange(super.baseUrl + "/" + userId, HttpMethod.GET, makeAuthEntity(), Transfer[].class);
            transfers = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());

        }
        return transfers;
    }


    public boolean updateTransfer(Transfer updatedTransfer) {
        HttpEntity<Transfer> entity = makeTransferEntity(updatedTransfer);
        boolean success = false;
        try {
            restTemplate.put(super.baseUrl, updatedTransfer.getTransferId(), entity);
            success = true;
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return success;
    }

    public Transfer[] getPendingTransfers() {
        Transfer[] transfers = null;
        try {
            ResponseEntity<Transfer[]> response = restTemplate.exchange(super.baseUrl + "/pending", HttpMethod.GET, makeAuthEntity(), Transfer[].class);
            transfers = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            //BasicLogger.log(e.getMessage());

        }
        return transfers;
    }

    public Transfer send(Transfer transfer) {
        try {
            ResponseEntity<Transfer> response = restTemplate.exchange(super.baseUrl, HttpMethod.POST, makeTransferEntity(transfer), Transfer.class);
            transfer = response.getBody();
            System.out.println(transfer.toString());
        } catch (RestClientResponseException | ResourceAccessException e) {
            System.out.println("*****");
            System.out.println(e.getMessage());
//            BasicLogger.log(e.getMessage());
            System.out.println("*****");
        }
        return transfer;
    }


    private HttpEntity<Transfer> makeTransferEntity(Transfer transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(this.authToken);
        return new HttpEntity<>(transfer, headers);
    }

    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(this.authToken);
        return new HttpEntity<>(headers);
    }


}
