package com.bench.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.time.LocalDate;

/**
 * Unit test for Transaction class.
 *
 * Created by dlee.
 */
public class TransactionTest {
    private String testPayload;
    private ObjectMapper om = new ObjectMapper();

    @Before
    public void setUp() throws Exception {
        InputStream is = TransactionTest.class.getClassLoader().getResourceAsStream("model/Transaction.json");
        testPayload = IOUtils.toString(is, Charset.defaultCharset());
    }

    @Test
    public void testDeserialization() throws Exception {
        Transaction t = om.readValue(testPayload, Transaction.class);
        Assert.assertNotNull("Expected to be not NULL", t);

        Transaction expected = new Transaction(LocalDate.of(2013, 12, 20), "Ledger X", 100.50, "Company ABC");
        Assert.assertEquals("Incorrect deserialization", expected, t);
    }

    @Test
    public void testGetters() throws Exception {
        Transaction t = om.readValue(testPayload, Transaction.class);
        Assert.assertEquals("Incorrect local date value.", LocalDate.of(2013, 12, 20), t.getDate());
        Assert.assertEquals("Incorrect ledger value.", "Ledger X", t.getLedger());
        Assert.assertTrue("Incorrect amount value.", Double.compare(100.5d, t.getAmount()) == 0);
        Assert.assertEquals("Incorrect company value.", "Company ABC", t.getCompany());
    }
}
