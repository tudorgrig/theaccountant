package com.TheAccountant.dao;

import com.TheAccountant.model.notification.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by tudor.grigoriu on 3/5/2017.
 */
public interface NotificationDao extends JpaRepository<Notification, Long> {

    @Query("SELECT DISTINCT n FROM Notification n WHERE n.user.username = ?1 ORDER BY creationDate DESC")
    List<Notification> fetchAll(String username);

    @Query(value = "SELECT DISTINCT n.* FROM notification n WHERE n.user_id = ?1 ORDER BY n.creationDate DESC LIMIT ?2 OFFSET ?3", nativeQuery = true)
    List<Notification> fetchAll(long userId, int limit, int offset);

    long countByUserUserIdAndSeen(long userId, boolean seen);

    @Query("SELECT DISTINCT n FROM Notification n WHERE n.user.username = ?1 and n.seen = ?2 ORDER BY creationDate DESC")
    List<Notification> findBySeen(String username, boolean seen);
}
