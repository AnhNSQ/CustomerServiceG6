package CustomerService.controller;

import CustomerService.dto.ApiResponse;
import CustomerService.dto.CheckoutRequest;
import CustomerService.dto.OrderResponse;
import CustomerService.service.OrderService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/checkout")
@RequiredArgsConstructor
@Slf4j
public class CheckoutController {

    private final OrderService orderService;

    /**
     * Trang checkout
     */
    @GetMapping
    public String checkoutPage(@RequestParam(required = false) String items, Model model, HttpSession session) {
        try {
            Long customerId = (Long) session.getAttribute("customerId");
            
            if (customerId == null) {
                log.warn("Unauthorized access to checkout - redirecting to login");
                return "redirect:/login";
            }
            
            log.info("Loading checkout page for customer ID: {}", customerId);
            
            // Get selected items from URL parameters
            if (items == null || items.trim().isEmpty()) {
                log.warn("No items selected for checkout - redirecting to cart");
                return "redirect:/cart";
            }
            
            model.addAttribute("selectedItems", items);
            
            return "checkout";
            
        } catch (Exception e) {
            log.error("Error loading checkout page: ", e);
            return "redirect:/cart";
        }
    }

    /**
     * API tạo đơn hàng
     */
    @PostMapping("/api/orders")
    @ResponseBody
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @RequestBody CheckoutRequest request,
            HttpSession session) {
        try {
            Long customerId = (Long) session.getAttribute("customerId");
            
            if (customerId == null) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Không có quyền truy cập"));
            }
            
            log.info("Creating order for customer {} with {} items", 
                    customerId, request.getCartItemIds().size());
            
            OrderResponse order = orderService.createOrder(customerId, request);
            
            return ResponseEntity.ok(ApiResponse.success(order, "Đặt hàng thành công"));
            
        } catch (Exception e) {
            log.error("Error creating order: ", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * API lấy tất cả đơn hàng của customer
     */
    @GetMapping("/api/orders")
    @ResponseBody
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getCustomerOrders(
            HttpSession session) {
        try {
            Long customerId = (Long) session.getAttribute("customerId");
            
            if (customerId == null) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Không có quyền truy cập"));
            }
            
            List<OrderResponse> orders = orderService.getOrdersByCustomerId(customerId);
            
            return ResponseEntity.ok(ApiResponse.success(orders, "Lấy danh sách đơn hàng thành công"));
            
        } catch (Exception e) {
            log.error("Error getting customer orders: ", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * API lấy đơn hàng theo ID
     */
    @GetMapping("/api/orders/{orderId}")
    @ResponseBody
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(
            @PathVariable Long orderId,
            HttpSession session) {
        try {
            Long customerId = (Long) session.getAttribute("customerId");
            
            if (customerId == null) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Không có quyền truy cập"));
            }
            
            Optional<OrderResponse> order = orderService.getOrderById(orderId);
            
            if (order.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Không tìm thấy đơn hàng"));
            }
            
            // Verify order belongs to customer
            if (!order.get().getCustomerId().equals(customerId)) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Không có quyền truy cập đơn hàng này"));
            }
            
            return ResponseEntity.ok(ApiResponse.success(order.get(), "Lấy đơn hàng thành công"));
            
        } catch (Exception e) {
            log.error("Error getting order: ", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * API cập nhật trạng thái đơn hàng
     */
    @PutMapping("/api/orders/{orderId}/status")
    @ResponseBody
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam String status,
            HttpSession session) {
        try {
            Long customerId = (Long) session.getAttribute("customerId");
            
            if (customerId == null) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Không có quyền truy cập"));
            }
            
            OrderResponse order = orderService.updateOrderStatus(orderId, status);
            
            // Verify order belongs to customer
            if (!order.getCustomerId().equals(customerId)) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Không có quyền cập nhật đơn hàng này"));
            }
            
            return ResponseEntity.ok(ApiResponse.success(order, "Cập nhật trạng thái đơn hàng thành công"));
            
        } catch (Exception e) {
            log.error("Error updating order status: ", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
}
