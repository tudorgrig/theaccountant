package com.myMoneyTracker.util;

import org.junit.Test;

import java.util.Currency;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by tudor.grigoriu on 26.04.2016.
 */
public class CurrencyUtilTest {

    @Test
    public void shouldGetValidCurrency(){
        Currency currency = CurrencyUtil.getCurrency("USD");
        assertNotNull(currency);
    }

    @Test
    public void shouldGetNullCurrency(){
        Currency currency = CurrencyUtil.getCurrency(null);
        assertNull(currency);
    }

    @Test
    public void shouldGetNullCurrency2(){
        Currency currency = CurrencyUtil.getCurrency("Florin");
        assertNull(currency);
    }
}
