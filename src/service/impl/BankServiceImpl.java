package service.impl;

import domain.Account;
import domain.Customer;
import domain.Transaction;
import domain.Type;
import repository.AccountRepository;
import repository.CustomerRepository;
import repository.TransactionRepository;
import service.BankService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import  java.time.LocalDateTime.*;

public class BankServiceImpl  implements BankService {
    private final AccountRepository accountRepository= new AccountRepository();
    private final TransactionRepository transactionRepository= new TransactionRepository();
    private final CustomerRepository customerRepository= new CustomerRepository();



// creating account ,creating account id and account number
    @Override
    public String openAccount(String name, String email, String accountType) {
        
        String customerId= UUID.randomUUID().toString();

        // create customer
        Customer c= new Customer(email, customerId, name);
        customerRepository.save(c);

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

    @Override
    public void withdraw(String accountNumber, double amount, String note) {
        Account account= accountRepository.findByNumber(accountNumber).
                orElseThrow(() -> new RuntimeException("Account not found"+ accountNumber));

       if(account.getBalance()  <0)
            new RuntimeException("Insufficient Balance");

        account.setBalance(account.getBalance() - amount);
        Transaction transaction = new Transaction(
                UUID.randomUUID().toString(),   // id
                Type.WITHDRAW,                   // type
                account.getAccountNumber(),     // accountNumber
                amount,                         // amount
                LocalDateTime.now(),            // timestamp
                note                          // note
        );
        transactionRepository.add(transaction);




    }

    @Override
    public void tranfer(String fromAccount, String toAccount, double amount, String note) {
        if(fromAccount.equals(toAccount))
            throw new RuntimeException("Cannot transfer to your own Account");
        Account fromAcc= accountRepository.findByNumber(fromAccount).
                orElseThrow(() -> new RuntimeException("Account not found"+ fromAccount));

        Account toAcc= accountRepository.findByNumber(toAccount).
                orElseThrow(()->new RuntimeException("Account not found"+ toAccount));
        if(fromAcc.getBalance()  <0)
            new RuntimeException("Insufficient Balance");

        fromAcc.setBalance(fromAcc.getBalance() - amount);

        toAcc.setBalance(toAcc.getBalance() + amount);

        Transaction fromTransaction = new Transaction(
                UUID.randomUUID().toString(),   // id
                Type.TRANSFER_OUT,                   // type
                fromAcc.getAccountNumber(),     // accountNumber
                amount,                         // amount
                LocalDateTime.now(),            // timestamp
                note                          // note
        );

        transactionRepository.add(fromTransaction);

        Transaction toTransaction = new Transaction(
                UUID.randomUUID().toString(),   // id
                Type.TRANSFER_IN,                   // type
                toAcc.getAccountNumber(),     // accountNumber
                amount,                         // amount
                LocalDateTime.now(),            // timestamp
                note                          // note
        );
        transactionRepository.add(fromTransaction);





    }

    @Override
    public List<Transaction> getStatement(String account) {
        return transactionRepository.findByAccount(account).stream().
                sorted(Comparator.comparing(Transaction::getAccountNumber)).collect(Collectors.toList());
    }

    @Override
    public List<Account> searchAccountByCustomerName(String q) {
        String query= (q==null)? "": q.toLowerCase();
        List<Account> result= new ArrayList<>();
        for(Customer c: customerRepository.findAll()){
            if(c.getName().toLowerCase().contains(query)) {
                result.addAll(accountRepository.findByCustomerId(c.getId()));
            }
            result.sort(Comparator.comparing(Account::getAccountNumber));

        }
        return result;
    }

    private String getAccountNumber() {
        int size= accountRepository.findAll().size()+1;
        return String.format("AC%06d", size);

    }
}
