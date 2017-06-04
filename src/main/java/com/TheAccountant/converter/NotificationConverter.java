package com.TheAccountant.converter;

import com.TheAccountant.dto.notification.NotificationDTO;
import com.TheAccountant.model.notification.Notification;
import org.dozer.DozerBeanMapperSingletonWrapper;
import org.dozer.Mapper;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by tudor.grigoriu on 6/4/2017.
 */
public class NotificationConverter {

    public NotificationDTO convertTo(Notification notification) {
        if(notification == null){
            return null;
        }
        NotificationDTO destObject = new NotificationDTO();
        Mapper mapper = DozerBeanMapperSingletonWrapper.getInstance();
        mapper.map(notification, destObject);
        return destObject;
    }

    public List<NotificationDTO> convertList(List<Notification> notifications) {
        return notifications.stream().map(notification -> convertTo(notification)).collect(Collectors.toList());
    }
}
