package com.TheAccountant.dto.notification;


import java.util.List;

/**
 * Created by Florin on 5/27/2017.
 */
public class NotificationEntityWrapperDTO <E> {

    private List<E> entityList;
    private NotificationDTO notification;

    public NotificationEntityWrapperDTO() {}

    public NotificationEntityWrapperDTO(List<E> entitiyList, NotificationDTO notification) {
        this.entityList = entitiyList;
        this.notification = notification;
    }

    public List<E> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<E> entityList) {
        this.entityList = entityList;
    }

    public NotificationDTO getNotification() {
        return notification;
    }

    public void setNotification(NotificationDTO notification) {
        this.notification = notification;
    }
}
