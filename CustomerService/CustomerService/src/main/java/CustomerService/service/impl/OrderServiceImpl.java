package CustomerService.service.impl;

import CustomerService.dto.CheckoutRequest;
import CustomerService.dto.OrderDetailResponse;
import CustomerService.dto.OrderResponse;
import CustomerService.entity.*;
import CustomerService.repository.*;
import CustomerService.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final CustomerRepository customerRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final OrderHistoryRepository orderHistoryRepository;

    @Override
    public OrderResponse createOrder(Long customerId, CheckoutRequest request) {
        log.info("Creating order for customer {} with {} items", customerId, request.getCartItemIds().size());

        // Validate customer
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new RuntimeException("Customer not found"));

        // Get selected cart items
        List<CartItem> cartItems = request.getCartItemIds().stream()
            .map(cartItemId -> cartRepository.findById(cartItemId))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
        
        if (cartItems.isEmpty()) {
            throw new RuntimeException("No cart items found");
        }

        // Validate all cart items belong to the customer
        boolean allItemsBelongToCustomer = cartItems.stream()
            .allMatch(item -> item.getCustomer().getCustomerId().equals(customerId));
        
        if (!allItemsBelongToCustomer) {
            throw new RuntimeException("Some cart items do not belong to the customer");
        }

        // Calculate totals
        BigDecimal subtotal = cartItems.stream()
            .map(CartItem::getSubTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal shippingCost = calculateShippingCost(subtotal);
        BigDecimal totalAmount = subtotal.add(shippingCost);

        // Create order
        Order order = new Order(
            customer,
            request.getShippingMethod() != null ? request.getShippingMethod() : "Giao hàng tiêu chuẩn",
            shippingCost,
            "3-5 ngày làm việc",
            request.getShippingAddress(),
            request.getRecipientName(),
            request.getRecipientPhone(),
            request.getPaymentMethod()
        );
        order.setTotalAmount(totalAmount);

        // Set additional notes if provided
        if (request.getNotes() != null && !request.getNotes().trim().isEmpty()) {
            order.setNotes(request.getNotes());
        }

        Order savedOrder = orderRepository.save(order);
        log.info("Order created with ID: {}", savedOrder.getOrderId());

        // Create initial order history
        OrderHistory initialHistory = new OrderHistory(
            savedOrder,
            "CREATED",
            "Đơn hàng được tạo bởi khách hàng " + customer.getName()
        );
        orderHistoryRepository.save(initialHistory);

        // Create order details
        List<OrderDetail> orderDetails = cartItems.stream()
            .map(cartItem -> {
                OrderDetail orderDetail = new OrderDetail(
                    savedOrder,
                    cartItem.getProduct(),
                    cartItem.getUnitPrice(),
                    cartItem.getQuantity()
                );
                return orderDetailRepository.save(orderDetail);
            })
            .collect(Collectors.toList());

        // Remove cart items after successful order creation
        cartItems.forEach(cartItem -> cartRepository.delete(cartItem));
        log.info("Removed {} cart items after order creation", cartItems.size());

        return convertToResponse(savedOrder, orderDetails);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderResponse> getOrderById(Long orderId) {
        log.info("Getting order by ID: {}", orderId);
        
        return orderRepository.findByIdWithCustomer(orderId)
            .map(order -> {
                List<OrderDetail> orderDetails = orderDetailRepository.findByOrderOrderId(orderId);
                return convertToResponse(order, orderDetails);
            });
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByCustomerId(Long customerId) {
        log.info("Getting orders for customer: {}", customerId);
        
        List<Order> orders = orderRepository.findByCustomerIdOrderByOrderDateDesc(customerId);
        
        return orders.stream()
            .map(order -> {
                List<OrderDetail> orderDetails = orderDetailRepository.findByOrderOrderId(order.getOrderId());
                return convertToResponse(order, orderDetails);
            })
            .collect(Collectors.toList());
    }

    @Override
    public OrderResponse updateOrderStatus(Long orderId, String status) {
        log.info("Updating order {} status to {}", orderId, status);
        
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));

        try {
            Order.OrderStatus newStatus = Order.OrderStatus.valueOf(status.toUpperCase());
            order.setOrderStatus(newStatus);
            
            Order savedOrder = orderRepository.save(order);
            List<OrderDetail> orderDetails = orderDetailRepository.findByOrderOrderId(orderId);
            
            return convertToResponse(savedOrder, orderDetails);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid order status: " + status);
        }
    }

    private BigDecimal calculateShippingCost(BigDecimal subtotal) {
        // Free shipping for orders over 500,000 VND
        if (subtotal.compareTo(new BigDecimal("500000")) >= 0) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal("30000"); // 30,000 VND shipping cost
    }

    private OrderResponse convertToResponse(Order order, List<OrderDetail> orderDetails) {
        OrderResponse response = new OrderResponse();
        response.setOrderId(order.getOrderId());
        response.setCustomerId(order.getCustomer().getCustomerId());
        response.setCustomerName(order.getCustomer().getName());
        response.setOrderDate(order.getOrderDate());
        response.setTotalAmount(order.getTotalAmount());
        response.setShippingMethod(order.getShippingMethod());
        response.setShippingCost(order.getCostEstimate());
        response.setEstimatedTime(order.getEstimatedTime());
        response.setShippingAddress(order.getShippingAddress());
        response.setRecipientName(order.getRecipientName());
        response.setRecipientPhone(order.getRecipientPhone());
        response.setOrderStatus(order.getOrderStatus().name());
        response.setShippingStatus(order.getShippingStatus().name());
        response.setPaymentMethod(order.getPaymentMethod());

        List<OrderDetailResponse> detailResponses = orderDetails.stream()
            .map(this::convertToDetailResponse)
            .collect(Collectors.toList());
        
        response.setOrderDetails(detailResponses);
        
        return response;
    }

    private OrderDetailResponse convertToDetailResponse(OrderDetail orderDetail) {
        OrderDetailResponse response = new OrderDetailResponse();
        response.setOrderDetailId(orderDetail.getOrderDetailId());
        response.setProductId(orderDetail.getProduct().getProductId());
        response.setProductName(orderDetail.getProduct().getName());
        response.setProductDescription(orderDetail.getProduct().getDescription());
        response.setUnitPrice(orderDetail.getUnitPrice());
        response.setQuantity(orderDetail.getQuantity());
        response.setSubTotal(orderDetail.getSubTotal());
        
        return response;
    }
}
