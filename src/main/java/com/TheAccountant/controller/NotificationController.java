package com.TheAccountant.controller;

import com.TheAccountant.controller.exception.BadRequestException;
import com.TheAccountant.dao.NotificationDao;
import com.TheAccountant.model.notification.Notification;
import com.TheAccountant.model.user.AppUser;
import com.TheAccountant.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by tudor.grigoriu on 3/5/2017.
 */
@RestController
@RequestMapping(value = "/notifications")
public class NotificationController {

    @Autowired
    private NotificationDao notificationDao;

    @Autowired
    private UserUtil userUtil;

    @RequestMapping(params = {"limit", "offset"},method = RequestMethod.GET)
    public ResponseEntity<List<Notification>> findNotifications(@RequestParam("limit") Integer limit, @RequestParam("offset") Integer offset) {

        AppUser user = userUtil.extractLoggedAppUserFromDatabase();
        if (user == null) {
            throw new BadRequestException("User not found");
        }
        return new ResponseEntity<>(notificationDao.fetchAll(user.getUserId(), limit, offset), HttpStatus.OK);
    }
}
