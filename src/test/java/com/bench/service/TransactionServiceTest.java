package com.bench.service;

import com.bench.model.Transaction;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.BoundRequestBuilder;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Unit test for TransactionService.
 *
 * Created by dlee.
 */
public class TransactionServiceTest {
    private String payload1;
    private String payload2;

    @Mock
    private AsyncHttpClient httpClient;
    @Mock
    private Response response;
    @Mock
    private BoundRequestBuilder brb;
    @Mock
    private ListenableFuture lf;

    private ObjectMapper om = new ObjectMapper();
    private TransactionService ts;

    @Before
    public void setUp() throws Exception {
        httpClient = Mockito.mock(AsyncHttpClient.class);
        response = Mockito.mock(Response.class);
        brb = Mockito.mock(BoundRequestBuilder.class);
        lf = Mockito.mock(ListenableFuture.class);

        ts = new TransactionService(httpClient, om);

        payload1 = IOUtils.toString(TransactionServiceTest.class.getClassLoader().getResourceAsStream("service/page-1.json"), Charset.defaultCharset());
        payload2 = IOUtils.toString(TransactionServiceTest.class.getClassLoader().getResourceAsStream("service/page-2.json"), Charset.defaultCharset());
    }

    @Test(expected = IOException.class)
    public void testGetAllTransactionsWhenNotFoundResource() throws Exception {
        String endpoint = "http://resttest.bench.co/transactions/1.json";
        Mockito.when(httpClient.prepareGet(endpoint)).thenReturn(brb);
        Mockito.when(brb.execute()).thenReturn(lf);
        Mockito.when(response.getStatusCode()).thenReturn(404);
        Mockito.when(lf.get()).thenReturn(response);

        ts.getAllTransactions();
    }

    @Test(expected = IOException.class)
    public void testGetAllTransactionsWhenNoMoreFoundResource() throws Exception {
        Mockito.when(brb.execute()).thenReturn(lf);
        Mockito.when(lf.get()).thenReturn(response);

        String endpoint1 = "http://resttest.bench.co/transactions/1.json";
        Mockito.when(httpClient.prepareGet(endpoint1)).thenReturn(brb);

        String endpoint2 = "http://resttest.bench.co/transactions/2.json";
        Mockito.when(httpClient.prepareGet(endpoint2)).thenReturn(brb);

        Mockito.when(response.getStatusCode()).thenReturn(200).thenReturn(404);
        Mockito.when(response.getResponseBody()).thenReturn(payload1).thenReturn(payload2);

        ts.getAllTransactions();
    }

    @Test
    public void testGetAllTransactionsWhenExpectedTotalCountReached() throws Exception {
        Mockito.when(brb.execute()).thenReturn(lf);
        Mockito.when(lf.get()).thenReturn(response);

        String endpoint1 = "http://resttest.bench.co/transactions/1.json";
        Mockito.when(httpClient.prepareGet(endpoint1)).thenReturn(brb);

        String endpoint2 = "http://resttest.bench.co/transactions/2.json";
        Mockito.when(httpClient.prepareGet(endpoint2)).thenReturn(brb);

        Mockito.when(response.getStatusCode()).thenReturn(200).thenReturn(200);
        Mockito.when(response.getResponseBody()).thenReturn(payload1).thenReturn(payload2);

        List<Transaction> t =  ts.getAllTransactions();
        Assert.assertEquals("Expecting only 3 transactions.", 3, t.size());

        Transaction t1 = new Transaction(LocalDate.of(2000, 1, 1), "Ledger X", -100.55, "Company A");
        Transaction t2 = new Transaction(LocalDate.of(2013, 10, 10), "Ledger Y", -10.5, "Company B");
        Transaction t3 = new Transaction(LocalDate.of(2017, 3, 1), "Ledger X", 500, "Company C");
        List<Transaction> expected = new ArrayList<>();
        expected.add(t1);
        expected.add(t2);
        expected.add(t3);
        Assert.assertEquals("Expecting transaction list to equal.", expected, t);
    }

    @Test
    public void testCalculateTotalBalance() throws Exception {
        Transaction t1 = new Transaction(LocalDate.of(2000, 1, 1), "Ledger X", -100.55, "Company A");
        Transaction t2 = new Transaction(LocalDate.of(2013, 10, 10), "Ledger Y", -10.5, "Company B");
        Transaction t3 = new Transaction(LocalDate.of(2017, 3, 1), "Ledger X", 500, "Company C");
        List<Transaction> t = new ArrayList<>();
        t.add(t1);
        t.add(t2);
        t.add(t3);

        Assert.assertTrue("Total balance incorrect.", Double.compare(388.95d, ts.calculateTotalBalance(t)) == 0);
    }

    @Test
    public void testCalculateRunningDailyTotal() throws Exception {
        Transaction t1 = new Transaction(LocalDate.of(2000, 1, 3), "Ledger X", -100.55, "Company A");
        Transaction t2 = new Transaction(LocalDate.of(2000, 1, 1), "Ledger Y", -10.5, "Company B");
        Transaction t3 = new Transaction(LocalDate.of(2000, 1, 1), "Ledger X", 500, "Company C");
        List<Transaction> t = new ArrayList<>();
        t.add(t1);
        t.add(t2);
        t.add(t3);

        SortedMap<LocalDate, Double> expected = new TreeMap<>();
        expected.put(LocalDate.of(1999, 12, 31), 0d);
        expected.put(LocalDate.of(2000, 1, 1), 489.5d);
        expected.put(LocalDate.of(2000, 1, 2), 489.5d);
        expected.put(LocalDate.of(2000, 1, 3), 388.95d);

        SortedMap<LocalDate, Double> actual = ts.calculateRunningDailyTotal(t);
        Assert.assertEquals("Running daily total incorrect.", expected, actual);
    }

    @Test
    public void testCalculateRunningDailyTotalWhenNoTransactions() throws Exception {
        Assert.assertEquals("Expected empty map.", 0, ts.calculateRunningDailyTotal(new ArrayList<>()).size());
    }
}
