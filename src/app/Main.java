package app;

import exceptions.ValidationException;
import service.BankService;
import service.impl.BankServiceImpl;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);


        BankService bankService = new BankServiceImpl();

        boolean running = true;
        System.out.println("Welcome to console Bank");
        while (running) {
            System.out.println("""
                    1. Open Account
                    2. Deposit
                    3. Withdraw
                    4. Transfer
                    5. Account statement
                    6. search Account customer name
                    7. List of Account
                    0. Exit
                    """);
            System.out.println("CHOOSE: ");
            String choice = scanner.nextLine().trim();
            System.out.println("CHOICE: " + choice);


            try {
                switch (choice) {
                    case "1" -> openAccount(scanner, bankService);
                    case "2" -> Deposit(scanner, bankService);
                    case "3" -> withdraw(scanner, bankService);
                    case "4" -> Tranfer(scanner, bankService);
                    case "5" -> AccountStatement(scanner, bankService);
                    case "6" -> searchAccount(scanner, bankService);
                    case "7" -> listAccounts(scanner, bankService);
                    case "0" -> running = false;
                }


                }catch (RuntimeException e) {
                    System.out.println("Error: " + e.getMessage());
                }

        }
    }




    private static void openAccount(Scanner scanner, BankService bankService){
        System.out.println("customer name: ");
        String name= scanner.nextLine().trim();
        System.out.println("customer email: ");
        String email= scanner.nextLine().trim();
        System.out.println("Account type SAVING/CURRENT: ");
        String accountType= scanner.nextLine().trim();
        System.out.println("Intial deposit (optional, blank for 0 : ");
        String amountStr= scanner.nextLine().trim();
        if(amountStr.isBlank()) amountStr= "0";
        double initial= Double.valueOf(amountStr);
        if (initial < 0) {
            throw new ValidationException("Initial deposit cannot be negative");
        }

        String accountNumber= bankService.openAccount(name, email, accountType);

        if(initial>0)
            bankService.deposit(accountNumber, initial, "Initial Deposit ");
        System.out.println("Account opened: "+ accountNumber);
    }
//       String accountNumber= bankService.openAccount(name, email, accountType);
//
//       if(initial>0)
//           bankService.deposit(accountNumber, initial, "Initial Deposit ");
//        System.out.println("Account opened: "+ accountNumber);
//
//
//    }

    private static void Deposit(Scanner scanner, BankService bankService){
        System.out.println("Account Number: ");
        String accountNumber=scanner.nextLine().trim();
        System.out.println("Amount: ");
        double amount= Double.valueOf(scanner.nextLine().trim());
        bankService.deposit(accountNumber, amount, "deposit");
        System.out.println("Amount deposited ");

    }

    private static void withdraw(Scanner scanner, BankService bankService){
        System.out.println("Account Number: ");
        String accountNumber=scanner.nextLine().trim();
        System.out.println("Amount: ");
        double amount= Double.valueOf(scanner.nextLine().trim());
        bankService.withdraw(accountNumber, amount, "withdrawal");
        System.out.println("Amount Withdraw ");

    }

    private static void Tranfer(Scanner scanner, BankService bankService){
        System.out.println("From Account : ");
        String fromAccount=scanner.nextLine().trim();
        System.out.println("To Account : ");
        String toAccount=scanner.nextLine().trim();
        System.out.println("Amount: ");
        double amount= Double.valueOf(scanner.nextLine().trim());
        bankService.tranfer(fromAccount,toAccount, amount, "Transfer");
        System.out.println("Amount transer to "+fromAccount+" to "+toAccount);



    }

    private static void AccountStatement(Scanner scanner, BankService bankService){
        System.out.println("Account Number : ");
        String account=scanner.nextLine().trim();
        bankService.getStatement(account).forEach(t->{
            System.out.println(t.getTimestamp()+ " | "+ t.getType()+ " | "+t.getAmount()+ " | "+ t.getNote());
        });



    }




    private static void searchAccount(Scanner scanner, BankService bankService){
        System.out.println("Account Holder Name: ");
        String q= scanner.nextLine().trim();
        bankService.searchAccountByCustomerName(q).forEach(account ->
                System.out.println(account.getAccountNumber()+ " | "+ account.getBalance()+" | "+ account.getBalance()));


    }
    private static void listAccounts(Scanner scanner,BankService bankService) {
        bankService.listAccounts().forEach(a ->{
            System.out.println(a.getAccountNumber()+ " | "+ a.getAccountType()+ " | "+ a.getBalance());
        });
    }
}
