package com.bench;

import com.bench.model.Transaction;
import com.bench.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Main class of running the standalone program.
 *
 * Created by dlee.
 */
public class Main {

    public static void main(String... args) {
        DecimalFormat df = new DecimalFormat("#.##");
        ObjectMapper om = new ObjectMapper();

        try (AsyncHttpClient httpClient = new DefaultAsyncHttpClient()) {
            TransactionService ts = new TransactionService(httpClient, om);
            List<Transaction> transactions = ts.getAllTransactions();
            System.out.println("Total Balance: $" + df.format(ts.calculateTotalBalance(transactions)) + "\n");

            System.out.println("Running Daily Balances:");
            ts.calculateRunningDailyTotal(transactions).entrySet().forEach(e -> System.out.println(e.getKey() + ": $" + df.format(e.getValue())));
        } catch (Exception e) {
            System.err.println("Failed to retrieve transactions: " + e.getMessage());
        }
    }
}
