package com.bench.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * An immutable model of a page of transactions.
 *
 * Created by dlee.
 */
public final class TransactionPage {
    private final int totalCount;
    private final int page;
    private final List<Transaction> transactions;

    @JsonCreator
    public TransactionPage(
            @JsonProperty("totalCount") int totalCount,
            @JsonProperty("page") int page,
            @JsonProperty("transactions") List<Transaction> transactions) {
        Objects.requireNonNull(transactions, "Cannot be NULL.");
        this.totalCount = totalCount;
        this.page = page;
        this.transactions = transactions;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public int getPage() {
        return page;
    }

    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TransactionPage that = (TransactionPage) o;

        if (totalCount != that.totalCount) return false;
        if (page != that.page) return false;
        return transactions.equals(that.transactions);

    }

    @Override
    public int hashCode() {
        int result = totalCount;
        result = 31 * result + page;
        result = 31 * result + transactions.hashCode();
        return result;
    }
}
