package com.TheAccountant.controller;

import com.TheAccountant.controller.exception.BadRequestException;
import com.TheAccountant.controller.exception.NotFoundException;
import com.TheAccountant.converter.NotificationConverter;
import com.TheAccountant.dao.NotificationDao;
import com.TheAccountant.dto.charge.ChargeDTO;
import com.TheAccountant.dto.notification.NotificationDTO;
import com.TheAccountant.model.notification.Notification;
import com.TheAccountant.model.payment.PaymentType;
import com.TheAccountant.model.user.AppUser;
import com.TheAccountant.service.PaymentService;
import com.TheAccountant.service.exception.ServiceException;
import com.TheAccountant.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

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

    @Autowired
    private NotificationConverter notificationConverter;

    @Autowired
    private PaymentService paymentService;

    @RequestMapping(params = {"limit", "offset"},method = RequestMethod.GET)
    public ResponseEntity<List<NotificationDTO>> findNotifications(@RequestParam("limit") Integer limit, @RequestParam("offset") Integer offset) {

        ChargeDTO chargeResult = null;
        try {
            chargeResult = paymentService.getPaymentStatus(PaymentType.USER_LICENSE);
        } catch (ServiceException e) {
            throw new BadRequestException(e.getMessage());
        }
        if (chargeResult.getPaymentApproved() == false) {
            throw new BadRequestException("Notifications are allowed only for paid accounts!");
        }

        AppUser user = userUtil.extractLoggedAppUserFromDatabase();
        if (user == null) {
            throw new BadRequestException("User not found");
        }
        return new ResponseEntity<>(notificationConverter.convertList(notificationDao.fetchAll(user.getUserId(), limit, offset)), HttpStatus.OK);
    }

    @RequestMapping(value = "/getTotal", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Long>> getTotalNotifications() {

        Map<String, Long> resultDTO = new HashMap<>();

        AppUser user = userUtil.extractLoggedAppUserFromDatabase();
        if (user == null) {
            throw new BadRequestException("User not found");
        }
        long totalNotifications = notificationDao.countByUserUserIdAndSeen(user.getUserId(), false);
        resultDTO.put("total", totalNotifications);

        return new ResponseEntity<>(resultDTO, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @Transactional
    public ResponseEntity<String> markNotificationAsSeen(@PathVariable("id") Long id) {
        try {
            AppUser user = userUtil.extractLoggedAppUserFromDatabase();
            Notification notification = notificationDao.findOne(id);
            if (notification == null) {
                throw new EmptyResultDataAccessException("Notification not found", 1);
            }
            if (!(user.getUsername().equals(notification.getUser().getUsername()))) {
                return new ResponseEntity<>("Unauthorized request", HttpStatus.BAD_REQUEST);
            }
            notification.setSeen(true);
            notificationDao.saveAndFlush(notification);
        } catch (EmptyResultDataAccessException emptyResultDataAccessException) {
            throw new NotFoundException("Notification not found");
        }
        return new ResponseEntity<>("Notification updated", HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @Transactional
    public ResponseEntity<String> deleteNotification(@PathVariable("id") Long id) {
        try {
            AppUser user = userUtil.extractLoggedAppUserFromDatabase();
            Notification notificiation = notificationDao.findOne(id);
            if (notificiation == null) {
                throw new EmptyResultDataAccessException("Notification not found", 1);
            }
            if (!(user.getUsername().equals(notificiation.getUser().getUsername()))) {
                return new ResponseEntity<>("Unauthorized request", HttpStatus.BAD_REQUEST);
            }
            notificationDao.delete(id);
            notificationDao.flush();
        } catch (EmptyResultDataAccessException emptyResultDataAccessException) {
            throw new NotFoundException("Notification not found");
        }
        return new ResponseEntity<>("Notification deleted", HttpStatus.NO_CONTENT);
    }
}
