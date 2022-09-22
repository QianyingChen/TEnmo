package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao {
    private final JdbcTemplate jdbcTemplate;

    public JdbcTransferDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Transfer getTransfer(int transferId) {
        Transfer transfer = null;
        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount " +
                "FROM Transfer " +
                "WHERE transfer_id = ? ";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, transferId);
        if (results.next()) {
            transfer = mapRowToTransfer(results);
        }
        return transfer;

    }


    @Override
    public List<Transfer> getAllTransfers(int userId) {
        List<Transfer> allTransfers = new ArrayList<>();
        String sql = "SELECT account_from, account_to, amount\n" +
                "FROM public.transfer\n" +
                "\t\tjoin account on account.account_id = transfer.account_from \n" +
                "\t\tjoin tenmo_user on tenmo_user.user_id = account.user_id\n" +
                "\t\twhere tenmo_user.user_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
        while (results.next()) {
            Transfer transfer = mapRowToTransfer2(results);
            allTransfers.add(transfer);
        }
        return allTransfers;
    }


    @Override
    public Transfer create(Transfer transfer) {
        String sql = "INSERT INTO public.transfer(\n" +
                "\ttransfer_type_id, transfer_status_id, account_from, account_to, amount)\n" +
                "\tVALUES ( ?, ?, ?, ?, ?) RETURNING transfer_id;";
        Integer transferId = jdbcTemplate.queryForObject(sql, Integer.class,
                2, 2,
                transfer.getAccountFrom(), transfer.getAccountTo(), transfer.getAmount());

        return getTransfer(transferId);

    }


    @Override
    public void updateTransfer(Transfer transfer) {
        String query = "Update transfers set transfer_status_id = ? where transfer_id = ? RETURNING transfer_id;";
        int id = jdbcTemplate.queryForObject(query, Integer.class, transfer.getTransferStatusId(), transfer.getTransferId());
    }

    @Override
    public List<Transfer> getPendingTransfers() {
        List<Transfer> pending = new ArrayList<>();
        Transfer pendingTransfer;
        String sql = "SELECT transfer.transfer_id, transfer.transfer_type_id, transfer.transfer_status_id, transfer.account_from, transfer.account_to, transfer.amount\n" +
                "FROM transfer\n" +
                "join transfer_status \n" +
                "on transfer_status.transfer_status_id = transfer.transfer_status_id\n" +
                "where transfer_status.transfer_status_desc = 'Pending';";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while (results.next()) {
            pendingTransfer = mapRowToTransfer(results);
            pending.add(pendingTransfer);
        }
        return pending;
    }


    private Transfer mapRowToTransfer2(SqlRowSet rs) {
        Transfer transfer = new Transfer();
        transfer.setAccountFrom(rs.getInt("account_from"));
        transfer.setAccountTo(rs.getInt("account_to"));
        transfer.setAmount(rs.getDouble("amount"));
        return transfer;
    }

    private Transfer mapRowToTransfer(SqlRowSet rs) {
        Transfer transfer = new Transfer();
        transfer.setTransferId(rs.getInt("transfer_id"));
        transfer.setTransferTypeId(rs.getInt("transfer_type_id"));
        transfer.setTransferStatusId(rs.getInt("transfer_status_id"));
        transfer.setAccountFrom(rs.getInt("account_from"));
        transfer.setAccountTo(rs.getInt("account_to"));
        transfer.setAmount(rs.getDouble("amount"));
        return transfer;
    }

}
