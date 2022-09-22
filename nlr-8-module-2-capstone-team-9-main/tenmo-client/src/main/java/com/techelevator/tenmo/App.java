package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.*;
import com.techelevator.util.BasicLogger;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import javax.naming.AuthenticationException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);
    private final AccountService accountService = new AccountService(API_BASE_URL);
    private final TransferService transferService = new TransferService(API_BASE_URL);
    private final UserService userService = new UserService(API_BASE_URL);
    private AuthenticatedUser currentUser;


    public static void main(String[] args) throws AuthenticationException {
        App app = new App();
        app.run();
    }

    private void run() throws AuthenticationException {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }

    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        }
        accountService.setAuthToken(currentUser.getToken());
        transferService.setAuthToken(currentUser.getToken());
        userService.setAuthToken(currentUser.getToken());

    }

    private void mainMenu() throws AuthenticationException {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } /*else if (menuSelection == 5) {
                requestBucks();
            } */ else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

    private void viewCurrentBalance() {
        // TODO Auto-generated method stub
        String authToken = currentUser.getToken();
        try {
            double balance = accountService.getBalanceByUserId(currentUser.getUser().getId());
            System.out.println("Your current balance is: $" + balance);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void viewTransferHistory() {
        // TODO Auto-generated method stub
        String authToken = currentUser.getToken();
        System.out.println("");
        System.out.println("-------------------------------------------");
        System.out.println("Transfers                                  ");
        System.out.println("-------------------------------------------");

        try {
            List<Transfer> transfers = Arrays.asList(transferService.getTransferHistory((currentUser.getUser().getId())));
            for (Transfer transferloop : transfers) {
                System.out.println(userService.getUsernameByUserId(currentUser.getUser().getId()));
                System.out.println(transferloop.toString2());
                System.out.println("");
            }

        } catch (Exception e) {
            e.getMessage();
        }
    }


    private void viewPendingRequests() {
        // TODO Auto-generated method stub
        String authToken = currentUser.getToken();
        System.out.println("-------------------------------------------");
        System.out.println("PendingTransfers                           ");
        System.out.println("ID          From/To                 Amount ");
        System.out.println("-------------------------------------------");
        try {

            Transfer[] pending = transferService.getPendingTransfers();
            for (int i = 0; i < pending.length; i++) {
                System.out.println(pending[i].toString());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    private void sendBucks() throws AuthenticationException {
        // TODO Auto-generated method stub
        String authToken = currentUser.getToken();


        int toUser = consoleService.promptForUserId();
        if (toUser == currentUser.getUser().getId()) {
            System.out.println("Sorry, you can't send money to yourself. Try again.");
            return;
        }

        double transferAmount = consoleService.promptForAmount();
        if (transferAmount <= 0) {
            System.out.println("Please enter an amount greater than 0. Try again.");
            return;
        }

         if (accountService.getBalanceByUserId(currentUser.getUser().getId()) < transferAmount) {
            System.out.println("Sorry, you don't have enough balance.");
            return;
        }

        Transfer newTransfer = new Transfer();
        newTransfer.setAmount(transferAmount);
        newTransfer.setAccountTo(toUser);
        newTransfer.setAccountFrom(currentUser.getUser().getId());
        consoleService.pause();
        System.out.println("--------------------------------------------\n");
        System.out.println("Transfer Details");
        System.out.println("--------------------------------------------\n");
        //System.out.println(newTransfer.toString());
        try {
            transferService.send(newTransfer);
            System.out.println("");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        mainMenu();
    }


    private void requestBucks() {
        // TODO Auto-generated method stub

    }

}
