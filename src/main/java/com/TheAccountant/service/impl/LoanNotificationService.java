package com.TheAccountant.service.impl;

import com.TheAccountant.dao.LoanDao;
import com.TheAccountant.dao.NotificationDao;
import com.TheAccountant.model.loan.Loan;
import com.TheAccountant.model.notification.Notification;
import com.TheAccountant.model.notification.NotificationCategory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by tudor.grigoriu on 3/19/2017.
 */
public class LoanNotificationService {

    private static final String PREFIX = "Loan active! ";
    private static final String TO_RECEIVE_FROM = "To receive from ";
    private static final String TO_GIVE_TO = "To give to ";
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
        stringBuilder.append(loan.getReceiving() == Boolean.TRUE ? TO_RECEIVE_FROM : TO_GIVE_TO);
        stringBuilder.append(loan.getCounterparty().getName());
        stringBuilder.append(StringUtils.SPACE);
        stringBuilder.append(loan.getAmount());
        stringBuilder.append(StringUtils.SPACE + loan.getCurrency());
        return stringBuilder.toString();
    }

}
