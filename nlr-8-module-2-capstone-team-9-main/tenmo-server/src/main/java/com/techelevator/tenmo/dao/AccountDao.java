package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;

import java.util.List;

public interface AccountDao {

    List<Account> getAllAccounts();

    double getBalanceByUserId(int userId);

    Account getAccountByUserId (int userId);

    Account findUserByAccountId(int accountId);

    void updateBalance(Account account);


}
