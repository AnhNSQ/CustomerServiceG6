package CustomerService.service.impl;

import CustomerService.dto.AddToCartRequest;
import CustomerService.dto.CartItemResponse;
import CustomerService.dto.CartResponse;
import CustomerService.dto.UpdateCartItemRequest;
import CustomerService.entity.CartItem;
import CustomerService.entity.Customer;
import CustomerService.entity.Product;
import CustomerService.repository.CartRepository;
import CustomerService.repository.CustomerRepository;
import CustomerService.repository.ProductRepository;
import CustomerService.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    @Override
    public CartItemResponse addToCart(Long customerId, AddToCartRequest request) {
        log.info("Adding product {} to cart for customer {}", request.getProductId(), customerId);

        // Kiểm tra customer tồn tại
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new RuntimeException("Customer not found"));

        // Kiểm tra product tồn tại và đang hoạt động
        Product product = productRepository.findByIdWithVendorAndCategory(request.getProductId())
            .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getStatus() != Product.ProductStatus.ACTIVE) {
            throw new RuntimeException("Product is not available");
        }

        // Kiểm tra số lượng có đủ không
        if (product.getQuantity() < request.getQuantity()) {
            throw new RuntimeException("Insufficient product quantity");
        }

        // Kiểm tra xem sản phẩm đã có trong giỏ hàng chưa
        CartItem existingCartItem = cartRepository.findByCustomerIdAndProductId(customerId, request.getProductId())
            .orElse(null);

        if (existingCartItem != null) {
            // Cập nhật số lượng nếu sản phẩm đã có trong giỏ hàng
            int newQuantity = existingCartItem.getQuantity() + request.getQuantity();
            if (newQuantity > product.getQuantity()) {
                throw new RuntimeException("Cannot add more items. Insufficient stock");
            }
            existingCartItem.updateQuantity(newQuantity);
            cartRepository.save(existingCartItem);
            log.info("Updated existing cart item quantity to {}", newQuantity);
            return convertToCartItemResponse(existingCartItem);
        } else {
            // Tạo cart item mới
            CartItem newCartItem = new CartItem(customer, product, request.getQuantity());
            CartItem savedCartItem = cartRepository.save(newCartItem);
            log.info("Created new cart item with ID {}", savedCartItem.getCartItemId());
            return convertToCartItemResponse(savedCartItem);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public CartResponse getCart(Long customerId) {
        log.info("Getting cart for customer {}", customerId);

        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new RuntimeException("Customer not found"));

        List<CartItem> cartItems = cartRepository.findByCustomerId(customerId);
        List<CartItemResponse> cartItemResponses = cartItems.stream()
            .map(this::convertToCartItemResponse)
            .collect(Collectors.toList());

        // Tính tổng số lượng items
        Integer totalItems = cartItems.stream()
            .mapToInt(CartItem::getQuantity)
            .sum();

        // Tính tổng giá trị
        BigDecimal totalAmount = cartItems.stream()
            .map(CartItem::getSubTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Tính phí ship (ví dụ: miễn phí ship nếu đơn hàng > 500k)
        BigDecimal shippingCost = totalAmount.compareTo(new BigDecimal("500000")) >= 0 
            ? BigDecimal.ZERO 
            : new BigDecimal("30000");

        BigDecimal grandTotal = totalAmount.add(shippingCost);

        CartResponse cartResponse = new CartResponse();
        cartResponse.setCustomerId(customerId);
        cartResponse.setCustomerName(customer.getName());
        cartResponse.setCartItems(cartItemResponses);
        cartResponse.setTotalItems(totalItems);
        cartResponse.setTotalAmount(totalAmount);
        cartResponse.setShippingCost(shippingCost);
        cartResponse.setGrandTotal(grandTotal);

        log.info("Cart retrieved for customer {} with {} items, total: {}", 
                customerId, totalItems, totalAmount);

        return cartResponse;
    }

    @Override
    public CartItemResponse updateCartItem(Long customerId, UpdateCartItemRequest request) {
        log.info("Updating cart item for customer {} product {}", customerId, request.getProductId());

        CartItem cartItem = cartRepository.findByCustomerIdAndProductId(customerId, request.getProductId())
            .orElseThrow(() -> new RuntimeException("Cart item not found"));

        // Kiểm tra số lượng có đủ không
        if (cartItem.getProduct().getQuantity() < request.getQuantity()) {
            throw new RuntimeException("Insufficient product quantity");
        }

        cartItem.updateQuantity(request.getQuantity());
        CartItem updatedCartItem = cartRepository.save(cartItem);

        log.info("Updated cart item quantity to {}", request.getQuantity());
        return convertToCartItemResponse(updatedCartItem);
    }

    @Override
    public void removeFromCart(Long customerId, Long productId) {
        log.info("Removing product {} from cart for customer {}", productId, customerId);

        if (!cartRepository.existsByCustomerIdAndProductId(customerId, productId)) {
            throw new RuntimeException("Cart item not found");
        }

        cartRepository.deleteByCustomerIdAndProductId(customerId, productId);
        log.info("Removed product {} from cart", productId);
    }

    @Override
    public void clearCart(Long customerId) {
        log.info("Clearing cart for customer {}", customerId);
        cartRepository.deleteByCustomerId(customerId);
        log.info("Cart cleared for customer {}", customerId);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getCartItemCount(Long customerId) {
        Long count = cartRepository.getTotalQuantityByCustomerId(customerId);
        return count != null ? count.intValue() : 0;
    }

    @Override
    @Transactional(readOnly = true)
    public Double getCartTotal(Long customerId) {
        Double total = cartRepository.getTotalAmountByCustomerId(customerId);
        return total != null ? total : 0.0;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isProductInCart(Long customerId, Long productId) {
        return cartRepository.existsByCustomerIdAndProductId(customerId, productId);
    }

    @Override
    @Transactional(readOnly = true)
    public CartItemResponse getCartItem(Long customerId, Long productId) {
        CartItem cartItem = cartRepository.findByCustomerIdAndProductId(customerId, productId)
            .orElseThrow(() -> new RuntimeException("Cart item not found"));
        return convertToCartItemResponse(cartItem);
    }

    /**
     * Chuyển đổi CartItem thành CartItemResponse
     */
    private CartItemResponse convertToCartItemResponse(CartItem cartItem) {
        CartItemResponse response = new CartItemResponse();
        response.setCartItemId(cartItem.getCartItemId());
        response.setProductId(cartItem.getProduct().getProductId());
        response.setProductName(cartItem.getProduct().getName());
        response.setProductDescription(cartItem.getProduct().getDescription());
        response.setUnitPrice(cartItem.getUnitPrice());
        response.setQuantity(cartItem.getQuantity());
        response.setSubTotal(cartItem.getSubTotal());
        response.setCreatedAt(cartItem.getCreatedAt());
        response.setUpdatedAt(cartItem.getUpdatedAt());
        return response;
    }
}
