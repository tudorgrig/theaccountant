package com.TheAccountant.service.impl;

import com.TheAccountant.dao.LoanDao;
import com.TheAccountant.dao.NotificationDao;
import com.TheAccountant.model.loan.Loan;
import com.TheAccountant.model.notification.Notification;
import com.TheAccountant.model.notification.NotificationCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by tudor.grigoriu on 3/19/2017.
 */
public class LoanNotificationService {

    private static final String PREFIX = "You have a loan that is due today, with ";
    private static final String SUFFIX = ", on amount of ";
    @Autowired
    private LoanDao loanDao;

    @Autowired
    private NotificationDao notificationDao;

    @Scheduled(cron = "0 0 0 * * *") //everyday at midnight
    public void addLoanNotifications(){
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        List<Loan> loanList = loanDao.findAllActiveBeforeDate(currentTimestamp);
        loanList.forEach(loan -> createAndPersistNotification(loan, currentTimestamp));
        if(!loanList.isEmpty()){
            notificationDao.flush();
        }
    }

    private void createAndPersistNotification(Loan loan, Timestamp currentTimestamp) {
        Notification notification = new Notification();
        notification.setCategory(NotificationCategory.LOAN.name());
        notification.setSeen(false);
        notification.setUser(loan.getUser());
        notification.setCreationDate(currentTimestamp);
        notification.setMessage(createMessage(loan));
        notificationDao.save(notification);
    }

    private String createMessage(Loan loan) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(PREFIX);
        stringBuilder.append(loan.getCounterparty().getName());
        stringBuilder.append(SUFFIX);
        stringBuilder.append(resolveAmount(loan));
        //TODO: Add currency also
//        stringBuilder.append(resolveCurrency(loan));loan.getDefaultCurrency() || loan.getCurrency();
    }

    private double resolveAmount(Loan loan) {
        return loan.getDefaultCurrencyAmount() != null ? loan.getDefaultCurrencyAmount() : loan.getAmount();
    }
}
