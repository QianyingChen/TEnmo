package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AccountController {
    private AccountDao accountDao;
//    private UserDao userDao;

    public AccountController(AccountDao accountDao) {
        this.accountDao = accountDao;
//        this.userDao = userDao;
    }

//    @RequestMapping(path = "/account/users", method = RequestMethod.GET)
//    public List<User> listUsers() {
//        return userDao.findAll();}

    //Get all accounts
    @RequestMapping(path = "/account", method = RequestMethod.GET)
    public List<Account> getAllAccounts(){
        return accountDao.getAllAccounts();
    }

    //Get Account information(like accId,userId, and balance) By UserID
    @RequestMapping(path = "/account/{userId}", method = RequestMethod.GET)
    public Account getAccountByUserId(@PathVariable int userId){
        return accountDao.getAccountByUserId(userId);
    }


    //Get balance By UserId
    @RequestMapping(path = "/balance/{userId}", method = RequestMethod.GET)
    public double getBalanceByUserId(@PathVariable int userId){
        return accountDao.getBalanceByUserId(userId);
    }


}
