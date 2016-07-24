package com.myMoneyTracker.dto.currency;

/**
 * Object that will be used to set or get the default currency of a user.
 * 
 * @author Florin
 */
public class DefaultCurrencyDTO {
    
    private String value;

    public DefaultCurrencyDTO() {}
    
    public DefaultCurrencyDTO(String value) {
        this.value = value;
    }
    
    public String getValue() {
    
        return value;
    }

    public void setValue(String value) {
    
        this.value = value;
    }
}
