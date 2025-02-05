package com.nimbleways.springboilerplate.services.implementations;

import java.time.LocalDate;
import java.util.List;

import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import com.nimbleways.springboilerplate.services.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;

import javax.persistence.EntityNotFoundException;

@Service
public class ProductService  implements IProductService {

    @Autowired
   private  ProductRepository productRepository;

    @Autowired
    NotificationService notificationService;

    @Autowired
    private OrderRepository orderRepository;


    @Override
    public void processOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found for ID: " + orderId));
        order.getItems().forEach(product -> handleProductProcessing(product));

    }


    private void handleProductProcessing(Product product) {
        switch (product.getType()) {
            case  NORMAL -> processNormalProduct(product);
            case SEASONAL -> processSeasonalProduct(product);
            case EXPIRABLE -> processExpirableProduct(product);
            default -> throw new IllegalArgumentException("Unsupported product type: " + product.getType());
        }
    }

    private void processNormalProduct(Product product) {
        if (product.getAvailable() > 0) {
            decrementProductAvailability(product);
        } else {
            handleDelay(product.getLeadTime(), product);
        }
    }


    private void processSeasonalProduct(Product product) {
        boolean isWithinSeason = LocalDate.now().isAfter(product.getSeasonStartDate())
                && LocalDate.now().isBefore(product.getSeasonEndDate());

        if (isWithinSeason && product.getAvailable() > 0) {
            decrementProductAvailability(product);
        } else {
            handleSeasonalProduct(product);
        }
    }

    private void processExpirableProduct(Product product) {
        boolean isValidExpiry = product.getExpiryDate().isAfter(LocalDate.now());

        if (product.getAvailable() > 0 && isValidExpiry) {
            decrementProductAvailability(product);
        } else {
            handleExpiredProduct(product);
        }
    }

    private void decrementProductAvailability(Product product) {
        product.setAvailable(product.getAvailable() - 1);
        productRepository.save(product);
    }

    private void handleDelay(int leadTime, Product product) {
        if (leadTime > 0) {
            product.setLeadTime(leadTime);
            productRepository.save(product);
            notificationService.sendDelayNotification(leadTime, product.getName());
        }
    }


    public void handleSeasonalProduct(Product product) {
        LocalDate today = LocalDate.now();
        LocalDate seasonEndDate = product.getSeasonEndDate();
        LocalDate seasonStartDate = product.getSeasonStartDate();
        int leadTime = product.getLeadTime();

        if (isSeasonEnded(today, seasonEndDate, leadTime)) {
            notifyOutOfStock(product);
            product.setAvailable(0);
            productRepository.save(product);
        } else if (isSeasonNotStarted(today, seasonStartDate)) {
            notifyOutOfStock(product);
            productRepository.save(product);
        } else {
            handleDelay(leadTime, product);
        }
    }

    private boolean isSeasonEnded(LocalDate today, LocalDate seasonEndDate, int leadTime) {
        return today.plusDays(leadTime).isAfter(seasonEndDate);
    }

    private boolean isSeasonNotStarted(LocalDate today, LocalDate seasonStartDate) {
        return seasonStartDate.isAfter(today);
    }

    private void notifyOutOfStock(Product product) {
        notificationService.sendOutOfStockNotification(product.getName());
    }


    public void handleExpiredProduct(Product product) {
        if (isProductValid(product)) {
            decrementProductAvailability(product);
        } else {
            handleExpiredProduct(product);
        }
    }
    
    private boolean isProductValid(Product product) {
        return product.getAvailable() > 0 && product.getExpiryDate().isAfter(LocalDate.now());
    }
    
}