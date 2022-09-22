package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.util.List;

public interface TransferDao {

    Transfer getTransfer(int transferId) ;

    List<Transfer> getAllTransfers(int userId);

    Transfer create(Transfer transfer);

    void updateTransfer(Transfer transfer);

    List <Transfer> getPendingTransfers();
}
