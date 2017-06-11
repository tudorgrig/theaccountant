package com.TheAccountant.service.impl;

import com.TheAccountant.dao.ExpenseDao;
import com.TheAccountant.dao.NotificationDao;
import com.TheAccountant.model.category.Category;
import com.TheAccountant.model.notification.Notification;
import com.TheAccountant.model.notification.NotificationCategory;
import com.TheAccountant.model.notification.NotificationPriority;
import com.TheAccountant.service.NotificationService;
import com.TheAccountant.service.exception.ServiceException;
import com.TheAccountant.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.logging.Logger;

/**
 * Created by Florin on 5/21/2017.
 */
@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    @Value("${threshold.medium.notification.percent}")
    private Double thresholdMediumNotificationPercent;

    @Autowired
    private ExpenseDao expenseDao;

    @Autowired
    private NotificationDao notificationDao;

    @Autowired
    private UserUtil userUtil;

    private static final Logger LOGGER = Logger.getLogger(NotificationServiceImpl.class.getName());

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
                } else {
                    // MEDIUM PRIORITY ALERT
                    try {
                        if (shouldCreateMediumPriorityNotification(category.getThreshold(), totalAmountSpent, thresholdMediumNotificationPercent)) {
                            String thresholdWarningMessage = this.createThresholdPercentCloseToLimitMessage(category, totalAmountSpent);
                            notification = this.createNotification(category, NotificationPriority.MEDIUM, thresholdWarningMessage);
                            notification = notificationDao.save(notification);
                        }
                    } catch (ServiceException e) {
                        LOGGER.warning(" ---- ERROR: " + e.getMessage());
                    }
                }
            }
        }
        return notification;
    }

    private boolean shouldCreateMediumPriorityNotification(Double categoryThreshold, Double totalAmountSpent,
                                                           Double thresholdMediumNotificationPercent) throws ServiceException {

        boolean shouldCreate = false;
        if (thresholdMediumNotificationPercent == null) {
            throw new ServiceException("Invalid medium threshold notification percent property value!");
        }
        if ((categoryThreshold - categoryThreshold * (thresholdMediumNotificationPercent/100)) < totalAmountSpent) {
            shouldCreate = true;
        }

        return shouldCreate;
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
        String message = "Threshold of value " + thresholdAmount +  " for category " + category.getName() + " has been exceeded! " +
                "Current amount spent: " + totalAmountSpent + " " + defaultCurrency + ".";

        return message;
    }

    /**
     * Create message that should be sent to the user when a threshold for a category is close to be
     * exceeded, using a percent to calculate if the total amount spent is close to that category threshold
     *
     * @param category
     * @param totalAmountSpent
     * @return
     */
    private String createThresholdPercentCloseToLimitMessage(Category category, Double totalAmountSpent) {
        String defaultCurrency = userUtil.extractLoggedAppUserFromDatabase().getDefaultCurrency().getCurrencyCode();
        String thresholdAmount = category.getThreshold() + " " + defaultCurrency;
        String message = "You are close to reaching threshold of " + thresholdAmount + " on category " + category.getName() + "!" +
                " Current amount spent: " + totalAmountSpent + " " + defaultCurrency + "!" +
                " Please take care of your expenses!";

        return message;
    }

    public void setThresholdMediumNotificationPercent(Double thresholdMediumNotificationPercent) {
        this.thresholdMediumNotificationPercent = thresholdMediumNotificationPercent;
    }

    @Override
    public Double getThresholdMediumNotificationPercent() {
        return thresholdMediumNotificationPercent;
    }
}
