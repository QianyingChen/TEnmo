package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import javax.security.auth.login.AccountNotFoundException;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcAccountDao implements AccountDao {
    private JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;   }


    @Override
    public List<Account> getAllAccounts() {
        List<Account> allAccounts = new ArrayList<>();
        String sql = "SELECT account_id, user_id, balance " +
                "FROM account;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while (results.next()) {
            allAccounts.add(mapRowToAccount(results));
        }
        return allAccounts;
    }


    @Override
    public double getBalanceByUserId(int userId) {
        double balance = 0;
        String sql = "SELECT balance FROM account " +
                     "JOIN tenmo_user on tenmo_user.user_id = account.user_id " +
                     "where tenmo_user.user_id = ?";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, userId);
        if(result.next()){
          balance = result.getDouble("balance");
        }
        return balance;
    }

// new update
    @Override
    public Account getAccountByUserId(int userId) {

        Account account = null;
        String query = "select * from account where user_id = ? ;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(query, userId);
        if(results.next()){
            account = mapRowToAccountWithoutHash(results);
        }
       return account;
    }


    @Override
    public void updateBalance(Account account) {
        String sql = "update account set balance = ? where account_id = ?";
        jdbcTemplate.update(sql, account.getBalance(), account.getAccountId());
    }


    @Override
    public Account findUserByAccountId(int accountId) {
        Account account = null;
        String query = "select * from account where account_id = ? ;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(query, accountId);
        if(results.next()){
            account = mapRowToAccountWithoutHash(results);
        }
        return account;    }


    private Account mapRowToAccount(SqlRowSet rs) {
       Account account = new Account();
       account.setAccountId(rs.getInt("account_id"));
       account.setUserId(rs.getInt("user_id"));
       account.setBalance(rs.getDouble("balance"));
        return account;
    }

    private Account mapRowToAccountWithoutHash(SqlRowSet rs) {
        Account account = new Account();
        account.setAccountId(rs.getInt("account_id"));
        account.setUserId(rs.getInt("user_id"));
        account.setBalance(rs.getDouble("balance"));
        return account;
    }

}
