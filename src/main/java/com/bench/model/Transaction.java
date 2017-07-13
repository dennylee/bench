package com.bench.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;
import java.time.LocalDate;

/**
 * An immutable object of transaction type.
 *
 * Created by dlee.
 */
public class Transaction {
    private final LocalDate date;
    private final String ledger;
    private final double amount;
    private final String company;

    @JsonCreator
    public Transaction(
            @JsonProperty("Date") @JsonDeserialize(using = LocalDateDeserializer.class) LocalDate date,
            @JsonProperty("Ledger") String ledger,
            @JsonProperty("Amount") double amount,
            @JsonProperty("Company") String company) {
        this.date = date;
        this.ledger = ledger;
        this.amount = amount;
        this.company = company;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getLedger() {
        return ledger;
    }

    public double getAmount() {
        return amount;
    }

    public String getCompany() {
        return company;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Transaction that = (Transaction) o;

        if (Double.compare(that.amount, amount) != 0) return false;
        if (!date.equals(that.date)) return false;
        if (!ledger.equals(that.ledger)) return false;
        return company.equals(that.company);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = date.hashCode();
        result = 31 * result + ledger.hashCode();
        temp = Double.doubleToLongBits(amount);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + company.hashCode();
        return result;
    }

    /**
     * Helps deserialize the string date value into the LocalDate Java object.
     */
    static class LocalDateDeserializer extends JsonDeserializer {
        @Override
        public Object deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            return LocalDate.parse(jsonParser.readValueAs(String.class));
        }
    }
}
