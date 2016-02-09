package com.myMoneyTracker.util;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by tudor.grigoriu on 08.02.2016.
 */
public class YahooCurrencyConverterTest {

    @Test
    public void shouldConvertUSDToEuro() throws IOException {
        Float amount = YahooCurrencyConverter.convert("EUR", "USD", 1);
        assertTrue(amount > 1);
    }
}
