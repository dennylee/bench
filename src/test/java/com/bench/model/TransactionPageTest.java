package com.bench.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Unit test for TransactionPage class.
 *
 * Created by dlee.
 */
public class TransactionPageTest {
    private String testPayload;
    private ObjectMapper om = new ObjectMapper();

    @Before
    public void setUp() throws Exception {
        InputStream is = TransactionPageTest.class.getClassLoader().getResourceAsStream("model/TransactionPage.json");
        testPayload = IOUtils.toString(is, Charset.defaultCharset());
    }

    @Test
    public void testDeserialization() throws Exception {
        TransactionPage tp = om.readValue(testPayload, TransactionPage.class);
        Assert.assertNotNull("Expected to be not NULL", tp);

        Transaction t1 = new Transaction(LocalDate.of(2010, 1, 13), "Ledger X", -50.75, "Company A");
        Transaction t2 = new Transaction(LocalDate.of(2000, 1, 10), "Ledger Y", 250.25, "Company B");
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(t1);
        transactions.add(t2);
        TransactionPage expected = new TransactionPage(10, 1, transactions);

        Assert.assertEquals("Incorrect deserialization", expected, tp);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorConstraints() throws Exception {
        new TransactionPage(10, 1, null);
    }

    @Test
    public void testGetters() throws Exception {
        Transaction t1 = new Transaction(LocalDate.of(2010, 1, 13), "Ledger X", -50.75, "Company A");
        Transaction t2 = new Transaction(LocalDate.of(2000, 1, 10), "Ledger Y", 250.25, "Company B");
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(t1);
        transactions.add(t2);

        TransactionPage actual = om.readValue(testPayload, TransactionPage.class);

        Assert.assertTrue("Incorrect total count value.", Integer.compare(10, actual.getTotalCount()) == 0);
        Assert.assertTrue("Incorrect page value.", Integer.compare(1, actual.getPage()) == 0);
        Assert.assertEquals("Incorrect transactions value.", transactions, actual.getTransactions());
    }
}
