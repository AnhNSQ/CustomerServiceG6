package CustomerService.service;

import CustomerService.dto.AddToCartRequest;
import CustomerService.dto.CartItemResponse;
import CustomerService.dto.CartResponse;
import CustomerService.dto.UpdateCartItemRequest;

/**
 * Interface cho dịch vụ quản lý Shopping Cart
 * Tuân thủ Single Responsibility Principle (SRP)
 */
public interface CartService {

    /**
     * Thêm sản phẩm vào giỏ hàng
     */
    CartItemResponse addToCart(Long customerId, AddToCartRequest request);

    /**
     * Lấy tất cả items trong giỏ hàng của customer
     */
    CartResponse getCart(Long customerId);

    /**
     * Cập nhật số lượng sản phẩm trong giỏ hàng
     */
    CartItemResponse updateCartItem(Long customerId, UpdateCartItemRequest request);

    /**
     * Xóa sản phẩm khỏi giỏ hàng
     */
    void removeFromCart(Long customerId, Long productId);

    /**
     * Xóa tất cả items khỏi giỏ hàng
     */
    void clearCart(Long customerId);

    /**
     * Lấy số lượng items trong giỏ hàng
     */
    Integer getCartItemCount(Long customerId);

    /**
     * Tính tổng giá trị giỏ hàng
     */
    Double getCartTotal(Long customerId);

    /**
     * Kiểm tra xem sản phẩm có trong giỏ hàng không
     */
    boolean isProductInCart(Long customerId, Long productId);

    /**
     * Lấy cart item cụ thể
     */
    CartItemResponse getCartItem(Long customerId, Long productId);
}
