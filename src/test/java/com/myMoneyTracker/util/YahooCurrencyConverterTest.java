package com.myMoneyTracker.util;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

/**
 * Created by Floryn on 08.02.2016.
 */
public class YahooCurrencyConverterTest {

    @Test
    public void shouldConvertUSDToEuro() throws IOException {
        Float amount = YahooCurrencyConverter.convert("EUR", "USD", 1);
        assertTrue(amount > 1);
    }
}
