package service;

import domain.Account;
import domain.Transaction;

import java.util.List;
import java.util.Map;

public interface BankService {

    String openAccount(String name, String email, String accountType);

    List<Account> listAccounts();


    void deposit(String accountNumber, double amount, String note);

    void withdraw(String accountNumber, double amount, String withdrawal);

    void tranfer(String fromAccount, String toAccount, double amount, String transfer);

    List<Transaction> getStatement(String account);

    List<Account> searchAccountByCustomerName(String q);
}
