package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.dto.ProductType;
import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ProductService productService;

    private Order mockOrder;
    private Product mockProduct;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockProduct = new Product();
        mockProduct.setAvailable(10);
        mockProduct.setType(ProductType.valueOf("NORMAL"));
        mockProduct.setLeadTime(5);
        mockProduct.setSeasonStartDate(LocalDate.now().minusDays(1));
        mockProduct.setSeasonEndDate(LocalDate.now().plusDays(1));
        mockProduct.setExpiryDate(LocalDate.now().plusMonths(1));

        mockOrder = new Order();
        mockOrder.setId(1L);
        mockOrder.setItems(Set.of(mockProduct));
    }

   @Test
    void testProcessOrder_normalProduct() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(mockOrder));

        productService.processOrder(1L);
        verify(productRepository, times(1)).save(mockProduct);
        verify(notificationService, never()).sendDelayNotification(anyInt(), any());
    }

    @Test
    void testHandleSeasonalProduct_productWithinSeason() {
        mockProduct.setType(ProductType.valueOf("SEASONAL"));
        mockProduct.setAvailable(5);
        mockProduct.setSeasonStartDate(LocalDate.now().minusDays(1));
        mockProduct.setSeasonEndDate(LocalDate.now().plusDays(1));

        productService.handleSeasonalProduct(mockProduct);

        // Verify the product availability is decremented during the season
        verify(productRepository, times(1)).save(mockProduct);
    }

    @Test
    void testHandleSeasonalProduct_productOutOfSeason() {
        mockProduct.setType(ProductType.valueOf("SEASONAL"));
        mockProduct.setAvailable(0);
        mockProduct.setSeasonStartDate(LocalDate.now().minusDays(10));
        mockProduct.setSeasonEndDate(LocalDate.now().minusDays(1));

        productService.handleSeasonalProduct(mockProduct);

        // Verify that the out-of-stock notification was sent
        verify(notificationService, times(1)).sendOutOfStockNotification(mockProduct.getName());
        verify(productRepository, times(1)).save(mockProduct);
    }

    @Test
    void testHandleExpiredProduct_validProduct() {
        mockProduct.setAvailable(10);
        mockProduct.setExpiryDate(LocalDate.now().plusDays(10));

        productService.handleExpiredProduct(mockProduct);

        verify(productRepository, times(1)).save(mockProduct);
    }


    @Test
    void testProcessOrder_orderNotFound() {

        when(orderRepository.findById(343L)).thenReturn(Optional.empty());

        try {
            productService.processOrder(1L);
        } catch (Exception e) {
            assert(e instanceof EntityNotFoundException);
        }
    }
}
