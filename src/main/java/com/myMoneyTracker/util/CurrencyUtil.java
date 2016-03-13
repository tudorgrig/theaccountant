package com.myMoneyTracker.util;

import java.util.Currency;

/**
 * Created by Floryn on 08.02.2016.
 */
public class CurrencyUtil {


    /**
     * Returns the <code>Currency</code> instance for the given currency code.
     * @param code the ISO 4217 code of the currency
     * @return the <code>Currency</code> instance for the given currency code
     * or null if any exceptions occur
     */
    public static Currency getCurrency(String code){
        if(code == null){
            return null;
        }
        try{
            Currency currency = Currency.getInstance(code);
            return currency;
        } catch (Exception e){
            return null;
        }
    }
}
