package com.TheAccountant.dto.notification;

import com.TheAccountant.model.notification.Notification;

import java.util.List;

/**
 * Created by Florin on 5/27/2017.
 */
public class NotificationEntityWrapperDTO <E> {

    private List<E> entityList;
    private Notification notification;

    public NotificationEntityWrapperDTO() {}

    public NotificationEntityWrapperDTO(List<E> entitiyList, Notification notification) {
        this.entityList = entitiyList;
        this.notification = notification;
    }

    public List<E> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<E> entityList) {
        this.entityList = entityList;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }
}
