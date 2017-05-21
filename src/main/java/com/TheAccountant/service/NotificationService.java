package com.TheAccountant.service;

import com.TheAccountant.model.category.Category;
import com.TheAccountant.model.notification.Notification;

/**
 * Service interface for creating, saving and deleting {@link Notification} entities
 *
 * Created by Florin on 5/21/2017.
 */
public interface NotificationService {

    /**
     * Method that will create and return a {@link Notification} entity in case of the
     * threshold for the specified category is exceeded, or returns null otherwise
     *
     * @param category
     * @return
     */
    Notification registerThresholdNotification(Category category);
}
