package com.bench.service;

import com.bench.model.Transaction;
import com.bench.model.TransactionPage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Response;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;


/**
 * Service for handling transactions.
 *
 * Created by dlee.
 */
public class TransactionService {
    private AsyncHttpClient httpClient;
    private ObjectMapper om;

    public TransactionService(AsyncHttpClient httpClient, ObjectMapper om) {
        this.httpClient = httpClient;
        this.om = om;
    }

    /**
     * Makes multiple request to the API to retrieve the list of transactions.  Each API response returns a set of
     * transactions, so it'll make multiple calls until the number of transactions received matches the expected
     * total count value.
     *
     * @return List of transactions collected.
     * @throws Exception If the API request failed for any reasons.
     */
    public List<Transaction> getAllTransactions() throws Exception {
        List<Transaction> transactions = new ArrayList<>();
        boolean done = false;
        int page = 1;

        // make request until status is not 200 or retrieve the expected total count
        do {
            String endpoint = String.format("http://resttest.bench.co/transactions/%s.json", page);
            Response response = httpClient.prepareGet(endpoint).execute().get();

            // determine if request was successful
            if (response.getStatusCode() == 200) {
                TransactionPage tr = om.readValue(response.getResponseBody(), TransactionPage.class);
                transactions.addAll(tr.getTransactions());

                // determine if retrieved the expected total count
                if (transactions.size() >= tr.getTotalCount()) {
                    done = true;
                }
                page++;
            } else {
                throw new IOException("Unable to retrieve transactions from API.");
            }
        } while (!done);

        return transactions;
    }

    /**
     * Calculates the total balance by summing the amount value from each transaction.
     *
     * @param transactions List of transactions to calculate the total balance.
     * @return The total balance value.
     */
    public double calculateTotalBalance(List<Transaction> transactions) {
        return transactions.stream().mapToDouble(Transaction::getAmount).sum();
    }

    /**
     * Calculates the running daily balances.
     * The result will contain the day before the earliest date found from the list of transactions and any missing days
     * between the earliest and last dates from the list of transactions.
     *
     * @param transactions List of transactions to calculate the running daily balances.
     * @return The running daily balances.
     */
    public SortedMap<LocalDate, Double> calculateRunningDailyTotal(List<Transaction> transactions) {
        // for each day, total the amount for that day
        SortedMap<LocalDate, Double> runningTotalMap = new TreeMap<>();
        transactions.forEach(t -> {
            if (runningTotalMap.containsKey(t.getDate())) {
                // update with new total amount for the day
                double total = runningTotalMap.get(t.getDate());
                runningTotalMap.put(t.getDate(), total + t.getAmount());
            } else {
                runningTotalMap.put(t.getDate(), t.getAmount());
            }
        });

        // add start date entry and missing date entries between the start date entry and the last date entry found
        if (!runningTotalMap.isEmpty()) {
            LocalDate firstDate = runningTotalMap.firstKey();
            LocalDate lastDate = runningTotalMap.lastKey();

            LocalDate iteratorDate = firstDate.minusDays(1);    // add the start date
            while (!iteratorDate.equals(lastDate)) {
                if (!runningTotalMap.containsKey(iteratorDate)) {
                    runningTotalMap.put(iteratorDate, 0d);  // missing date, add entry to total running map
                }
                iteratorDate = iteratorDate.plusDays(1);
            }
        }

        // for each day, tally the running total amount
        double runningTotalAmount = 0d;
        for (Map.Entry<LocalDate, Double> entry : runningTotalMap.entrySet()) {
            LocalDate date = entry.getKey();
            Double amount = entry.getValue();
            runningTotalMap.put(date, amount + runningTotalAmount);
            runningTotalAmount += amount;
        }

        return runningTotalMap;
    }
}
