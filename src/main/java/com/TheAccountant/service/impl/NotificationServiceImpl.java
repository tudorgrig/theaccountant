package com.TheAccountant.service.impl;

import com.TheAccountant.dao.ExpenseDao;
import com.TheAccountant.dao.NotificationDao;
import com.TheAccountant.model.category.Category;
import com.TheAccountant.model.notification.Notification;
import com.TheAccountant.model.notification.NotificationCategory;
import com.TheAccountant.model.notification.NotificationPriority;
import com.TheAccountant.service.NotificationService;
import com.TheAccountant.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;

/**
 * Created by Florin on 5/21/2017.
 */
@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private ExpenseDao expenseDao;

    @Autowired
    private NotificationDao notificationDao;

    @Autowired
    private UserUtil userUtil;

    @Override
    public Notification registerThresholdNotification(Category category) {

        Notification notification = null;
        if (category.getThreshold() > 0) {
            Double totalAmountSpent = expenseDao.getTotalAmountByCategoryForCurrentMonth(category.getId());
            if (totalAmountSpent != null && totalAmountSpent > 0) {
                //HIGH PRIORITY ALERT
                if (totalAmountSpent >= category.getThreshold()) {
                    String thresholdExceededMessage = this.createThresholdExceededMessage(category, totalAmountSpent);
                    notification = this.createNotification(category, NotificationPriority.HIGH, thresholdExceededMessage);
                    notification = notificationDao.save(notification);
                }
            }
        }
        return notification;
    }

    private Notification createNotification(Category category,
                                            NotificationPriority notificationPriority, String message) {
        Notification notification = new Notification();
        notification.setCategory(NotificationCategory.THRESHOLD.name());
        notification.setCreationDate(new Timestamp(System.currentTimeMillis()));
        notification.setUser(userUtil.extractLoggedAppUserFromDatabase());
        notification.setMessage(message);
        notification.setPriority(notificationPriority.name());
        return notification;
    }

    /**
     * Create message that should be sent to the user when a threshold for a category is exceeded
     *
     * @param category
     * @param totalAmountSpent
     * @return
     */
    private String createThresholdExceededMessage(Category category, Double totalAmountSpent) {
        String defaultCurrency = userUtil.extractLoggedAppUserFromDatabase().getDefaultCurrency().getCurrencyCode();
        String thresholdAmount = category.getThreshold() + " " + defaultCurrency;
        String message = "The threshold of \"" + thresholdAmount + "\" for the category \"" + category.getName() + "\" " +
                " have been exceeded. Total amount spent on this category this month is \"" + totalAmountSpent + " " +
                defaultCurrency + "\"!";

        return message;
    }
}
