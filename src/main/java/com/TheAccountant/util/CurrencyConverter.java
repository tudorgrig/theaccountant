package com.TheAccountant.util;

import com.TheAccountant.controller.exception.BadRequestException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Currency;

/**
 * Created by tudor.grigoriu on 26.07.2016.
 */
public class CurrencyConverter {

    private static final HttpClient httpClient = new DefaultHttpClient();

    public static Double getExchangeRateOnDay(String expenseCurrency, Currency defaultCurrency, String formatDate) throws IOException {
        String requestString = "http://api.fixer.io/" + formatDate + "?base=" + expenseCurrency + "&symbols=" + defaultCurrency.getCurrencyCode();
        HttpGet httpGet = new HttpGet(requestString);
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        try {
            String responseBody = httpClient.execute(httpGet, responseHandler);
            final JSONObject obj = new JSONObject(responseBody).getJSONObject("rates");
            if (obj != null) {
                Double rate = obj.getDouble(defaultCurrency.getCurrencyCode());
                return rate;
            }
            return null;
        }catch(HttpResponseException responseException){
            //in case currency strings are malformed
            throw new BadRequestException(responseException.getMessage());
        }
    }


}
