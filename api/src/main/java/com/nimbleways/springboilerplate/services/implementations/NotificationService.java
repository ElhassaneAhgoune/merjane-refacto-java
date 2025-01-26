package com.nimbleways.springboilerplate.services.implementations;

import java.time.LocalDate;

import com.nimbleways.springboilerplate.services.INotificationService;
import org.springframework.stereotype.Service;

// WARN: Should not be changed during the exercise
@Service
public class NotificationService implements INotificationService {
   @Override
    public void sendDelayNotification(int leadTime, String productName) {
    }

    @Override
    public void sendOutOfStockNotification(String productName) {
    }

    @Override
    public void sendExpirationNotification(String productName, LocalDate expiryDate) {
    }
}