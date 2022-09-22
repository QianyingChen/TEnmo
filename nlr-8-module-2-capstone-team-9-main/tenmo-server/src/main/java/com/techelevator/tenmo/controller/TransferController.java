package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.JdbcTransferDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
//@PreAuthorize("isAuthenticated()")
public class TransferController {
    private TransferDao transferDao;
    private AccountDao accountDao;
    private UserDao userDao;

    public TransferController(TransferDao transferDao, AccountDao accountDao, UserDao userDao) {
        this.transferDao = transferDao;
        this.accountDao = accountDao;
        this.userDao = userDao;
    }

    @RequestMapping(path = "/transfers/{userId}", method = RequestMethod.GET)
    public List<Transfer> getAllTransfers(@PathVariable int userId) {
        return transferDao.getAllTransfers(userId);
    }

    @RequestMapping(path = "/transfers/pending", method = RequestMethod.GET)
    public List<Transfer> getPendingTransfers() {
        return transferDao.getPendingTransfers();
    }

    @RequestMapping(path = "/transfer/{userId}", method = RequestMethod.GET)
    public Transfer getTransfer(@PathVariable int userId) {
        return transferDao.getTransfer(userId);
    }


    @RequestMapping(path = "/transfers/update/{transferId}", method = RequestMethod.PUT)
    public void updateTransfer(@RequestBody Transfer transfer) {
        this.transferDao.updateTransfer(transfer);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/transfers", method = RequestMethod.POST)
    public Transfer sendTransfer(@RequestBody Transfer transfer) {
        Transfer send = null;
        //System.out.println(transfer.toString());
        Account sender = accountDao.getAccountByUserId(transfer.getAccountFrom());
        Account receiver = accountDao.getAccountByUserId(transfer.getAccountTo());
//        double amount = transfer.getAmount();
//        double senderBalance = accountDao.getBalanceByUserId(sender.getUserId());
//        if (sender.getBalance() > transfer.getAmount()) {
            sender.setBalance(sender.getBalance() - transfer.getAmount());
            receiver.setBalance(receiver.getBalance() + transfer.getAmount());
            accountDao.updateBalance(sender);
            accountDao.updateBalance(receiver);
            transfer.setTransferStatusId(2);
            transfer.setAccountFrom(sender.getAccountId());
            transfer.setAccountTo(receiver.getAccountId());
            transfer = transferDao.create(transfer);
            return transfer;
        }

}
