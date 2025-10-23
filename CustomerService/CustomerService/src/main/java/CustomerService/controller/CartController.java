package CustomerService.controller;

import CustomerService.dto.AddToCartRequest;
import CustomerService.dto.ApiResponse;
import CustomerService.dto.CartItemResponse;
import CustomerService.dto.CartResponse;
import CustomerService.dto.UpdateCartItemRequest;
import CustomerService.service.CartService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Slf4j
public class CartController {

    private final CartService cartService;

    /**
     * Thêm sản phẩm vào giỏ hàng
     */
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<CartItemResponse>> addToCart(
            @Valid @RequestBody AddToCartRequest request,
            HttpSession session) {
        
        try {
            Long customerId = getCustomerIdFromSession(session);
            if (customerId == null) {
                return ResponseEntity.status(401)
                    .body(ApiResponse.error("Customer not logged in"));
            }

            CartItemResponse cartItem = cartService.addToCart(customerId, request);
            
            log.info("Product {} added to cart for customer {}", request.getProductId(), customerId);
            
            return ResponseEntity.ok(ApiResponse.success(cartItem, "Product added to cart successfully"));
            
        } catch (Exception e) {
            log.error("Error adding product to cart: ", e);
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(404)
                    .body(ApiResponse.error(e.getMessage()));
            }
            if (e.getMessage().contains("Insufficient") || e.getMessage().contains("not available")) {
                return ResponseEntity.status(400)
                    .body(ApiResponse.error(e.getMessage()));
            }
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Lấy thông tin giỏ hàng
     */
    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getCart(HttpSession session) {
        try {
            Long customerId = getCustomerIdFromSession(session);
            if (customerId == null) {
                return ResponseEntity.status(401)
                    .body(ApiResponse.error("Customer not logged in"));
            }

            CartResponse cart = cartService.getCart(customerId);
            
            return ResponseEntity.ok(ApiResponse.success(cart, "Cart retrieved successfully"));
            
        } catch (Exception e) {
            log.error("Error getting cart: ", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Cập nhật số lượng sản phẩm trong giỏ hàng
     */
    @PutMapping("/update")
    public ResponseEntity<ApiResponse<CartItemResponse>> updateCartItem(
            @Valid @RequestBody UpdateCartItemRequest request,
            HttpSession session) {
        
        try {
            Long customerId = getCustomerIdFromSession(session);
            if (customerId == null) {
                return ResponseEntity.status(401)
                    .body(ApiResponse.error("Customer not logged in"));
            }

            CartItemResponse cartItem = cartService.updateCartItem(customerId, request);
            
            log.info("Cart item updated for customer {} product {}", customerId, request.getProductId());
            
            return ResponseEntity.ok(ApiResponse.success(cartItem, "Cart item updated successfully"));
            
        } catch (Exception e) {
            log.error("Error updating cart item: ", e);
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(404)
                    .body(ApiResponse.error(e.getMessage()));
            }
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Xóa sản phẩm khỏi giỏ hàng
     */
    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<ApiResponse<Void>> removeFromCart(
            @PathVariable @NotNull Long productId,
            HttpSession session) {
        
        try {
            Long customerId = getCustomerIdFromSession(session);
            if (customerId == null) {
                return ResponseEntity.status(401)
                    .body(ApiResponse.error("Customer not logged in"));
            }

            if (productId == null || productId <= 0) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Invalid product ID"));
            }

            cartService.removeFromCart(customerId, productId);
            
            log.info("Product {} removed from cart for customer {}", productId, customerId);
            
            return ResponseEntity.ok(ApiResponse.success(null, "Product removed from cart successfully"));
            
        } catch (Exception e) {
            log.error("Error removing product from cart: ", e);
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(404)
                    .body(ApiResponse.error(e.getMessage()));
            }
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Xóa tất cả sản phẩm khỏi giỏ hàng
     */
    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<Void>> clearCart(HttpSession session) {
        try {
            Long customerId = getCustomerIdFromSession(session);
            if (customerId == null) {
                return ResponseEntity.status(401)
                    .body(ApiResponse.error("Customer not logged in"));
            }

            cartService.clearCart(customerId);
            
            log.info("Cart cleared for customer {}", customerId);
            
            return ResponseEntity.ok(ApiResponse.success(null, "Cart cleared successfully"));
            
        } catch (Exception e) {
            log.error("Error clearing cart: ", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Lấy số lượng items trong giỏ hàng
     */
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Integer>> getCartItemCount(HttpSession session) {
        try {
            Long customerId = getCustomerIdFromSession(session);
            if (customerId == null) {
                return ResponseEntity.status(401)
                    .body(ApiResponse.error("Customer not logged in"));
            }

            Integer count = cartService.getCartItemCount(customerId);
            
            return ResponseEntity.ok(ApiResponse.success(count, "Cart item count retrieved successfully"));
            
        } catch (Exception e) {
            log.error("Error getting cart item count: ", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Kiểm tra xem sản phẩm có trong giỏ hàng không
     */
    @GetMapping("/check/{productId}")
    public ResponseEntity<ApiResponse<Boolean>> isProductInCart(
            @PathVariable @NotNull Long productId,
            HttpSession session) {
        
        try {
            Long customerId = getCustomerIdFromSession(session);
            if (customerId == null) {
                return ResponseEntity.status(401)
                    .body(ApiResponse.error("Customer not logged in"));
            }

            if (productId == null || productId <= 0) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Invalid product ID"));
            }

            boolean isInCart = cartService.isProductInCart(customerId, productId);
            
            return ResponseEntity.ok(ApiResponse.success(isInCart, "Product cart status retrieved successfully"));
            
        } catch (Exception e) {
            log.error("Error checking product in cart: ", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Lấy thông tin customer ID từ session
     */
    private Long getCustomerIdFromSession(HttpSession session) {
        Object customerIdObj = session.getAttribute("customerId");
        if (customerIdObj instanceof Long) {
            return (Long) customerIdObj;
        }
        return null;
    }
}
