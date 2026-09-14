package service.impl;

import domain.Account;
import domain.Transaction;
import domain.Type;
import repository.AccountRepository;
import repository.TransactionRepository;
import service.BankService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import  java.time.LocalDateTime.*;

public class BankServiceImpl  implements BankService {
    private final AccountRepository accountRepository= new AccountRepository();
    private final TransactionRepository transactionRepository= new TransactionRepository();



// creating account ,creating account id and account number
    @Override
    public String openAccount(String name, String email, String accountType) {
        
        String customerId= UUID.randomUUID().toString();
        String accountNumber = getAccountNumber();

        Account account= new Account(accountNumber, accountType, (double) 0,customerId );
        //Save
        
        accountRepository.save(account);
        

        return accountNumber;
    }

    @Override
    public List<Account> listAccounts() {
        return accountRepository.findAll().stream()
                .sorted(Comparator.comparing(Account::getAccountNumber))
                .collect(Collectors.toList());
    }

    @Override
    public void deposit(String accountNumber, double amount, String note) {
        Account account= accountRepository.findByNumber(accountNumber).
                orElseThrow(() -> new RuntimeException("Account not found"+ accountNumber));
        account.setBalance(account.getBalance() + amount);

//        Transaction transaction= new Transaction(
//        account.getAccountNumber(),
//                amount, UUID.randomUUID().toString(),note, LocalDateTime.now(), Type.DEPOSIT
//                );
        Transaction transaction = new Transaction(
                UUID.randomUUID().toString(),   // id
                Type.DEPOSIT,                   // type
                account.getAccountNumber(),     // accountNumber
                amount,                         // amount
                LocalDateTime.now(),            // timestamp
                note                            // note
        );
        transactionRepository.add(transaction);

    }

    private String getAccountNumber() {
        int size= accountRepository.findAll().size()+1;
        return String.format("AC%06d", size);

    }
}
