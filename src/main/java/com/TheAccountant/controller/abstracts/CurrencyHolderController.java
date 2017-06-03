package com.TheAccountant.controller.abstracts;

import com.TheAccountant.controller.exception.BadRequestException;
import com.TheAccountant.model.abstracts.CurrencyHolderEntity;
import com.TheAccountant.model.user.AppUser;
import com.TheAccountant.util.CurrencyConverter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Currency;

/**
 * Abstract Controller that should be extended by controllers working with entities that will contain
 * amounts based on a currency.
 *
 * Created by Florin on 5/20/2017.
 */
public abstract class CurrencyHolderController {

    protected static final long ONE_DAY = 24 * 60 * 60 * 1000;

    /**
     * Set default default currency and its value for the specified entity if the currency of the
     * entity is different than default currency
     *
     * @param entity
     * @param defaultCurrency
     */
    protected void setDefaultCurrencyAmount(CurrencyHolderEntity entity, Currency defaultCurrency){
        String entityCurrency = entity.getCurrency();
        Double amount = entity.getAmount();
        String formatDate = new SimpleDateFormat("yyyy-MM-dd").format(entity.getCreationDate().getTime());
        Double exchangeRateOnDay = null;
        try {
            if(entityCurrency.equals(defaultCurrency.getCurrencyCode())){
                entity.setDefaultCurrencyAmount(null);
                entity.setDefaultCurrency(null);
                return;
            }
            exchangeRateOnDay = CurrencyConverter.getExchangeRateOnDay(entityCurrency, defaultCurrency, formatDate);
            if(exchangeRateOnDay != null) {
                entity.setDefaultCurrency(defaultCurrency.getCurrencyCode());
                entity.setDefaultCurrencyAmount(amount * exchangeRateOnDay);
            }
        } catch (IOException e) {
            throw new BadRequestException(e);
        }
    }

    protected boolean shouldUpdateDefaultCurrencyAmount(CurrencyHolderEntity entity, AppUser user, CurrencyHolderEntity oldEntity) {
        boolean creationDateChanged = !entity.getCreationDate().equals(oldEntity.getCreationDate())
                && (entity.getCreationDate().getTime() - oldEntity.getCreationDate().getTime() >= ONE_DAY
                ||
                oldEntity.getCreationDate().getTime() - entity.getCreationDate().getTime() >= ONE_DAY);

        boolean currencyChanged = !entity.getCurrency().equals(oldEntity.getCurrency());
        boolean amountChanged = !entity.getAmount().equals(oldEntity.getAmount());
        if(creationDateChanged || currencyChanged || amountChanged){
            //if any of those fields changed, check if the user updated the entity with his own default currency.
            //if yes, no conversion is needed, if not conversion is needed between the entity currency
            // and the users default currency
            boolean hasDiffCurrencyThanDefault = !entity.getCurrency().equals(user.getDefaultCurrency().getCurrencyCode());
            if(!hasDiffCurrencyThanDefault){
                entity.setDefaultCurrency(null);
                entity.setDefaultCurrencyAmount(null);
            }
            return hasDiffCurrencyThanDefault;
        }
        return false;
    }

    protected boolean shouldUpdateDefaultCurrencyAmount(CurrencyHolderEntity entity, AppUser user) {
        boolean entityWasOnOldDefaultCurrency = entity.getDefaultCurrency() == null &&
                !entity.getCurrency().equals(user.getDefaultCurrency().getCurrencyCode());
        boolean userChangedDefaultCurrency = entity.getDefaultCurrency() != null &&
                !entity.getDefaultCurrency().equals(user.getDefaultCurrency().getCurrencyCode());
        return entityWasOnOldDefaultCurrency || userChangedDefaultCurrency;
    }
}
