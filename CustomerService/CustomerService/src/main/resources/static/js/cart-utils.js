/**
 * Cart Utilities - Shared functions for cart management across all pages
 */

/**
 * Update the cart count badge in the header
 */
async function updateCartCount() {
    try {
        const response = await fetch('/api/cart/count', {
            method: 'GET',
            credentials: 'same-origin'
        });
        
        if (response.ok) {
            const result = await response.json();
            
            if (result.success) {
                const cartCountElement = document.getElementById('cartCount');
                if (cartCountElement) {
                    // Update the count
                    cartCountElement.textContent = result.data || 0;
                    
                    // Show or hide the badge based on count
                    if (result.data && result.data > 0) {
                        cartCountElement.style.display = 'inline-block';
                    } else {
                        cartCountElement.style.display = 'none';
                    }
                }
            } else {
                // If not logged in or error, hide the badge
                const cartCountElement = document.getElementById('cartCount');
                if (cartCountElement) {
                    cartCountElement.style.display = 'none';
                }
            }
        } else {
            // On 401 or other errors, hide the badge
            const cartCountElement = document.getElementById('cartCount');
            if (cartCountElement) {
                cartCountElement.style.display = 'none';
            }
        }
    } catch (error) {
        console.error('Error updating cart count:', error);
        // Hide badge on error
        const cartCountElement = document.getElementById('cartCount');
        if (cartCountElement) {
            cartCountElement.style.display = 'none';
        }
    }
}

/**
 * Show toast notification
 */
function showToast(message, type = 'info') {
    const container = document.getElementById('toastContainer');
    if (!container) return;
    
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    toast.textContent = message;
    
    container.appendChild(toast);
    
    setTimeout(() => toast.classList.add('show'), 100);
    setTimeout(() => {
        toast.classList.remove('show');
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}

/**
 * Add item to cart and update count
 */
async function addToCart(productId, quantity = 1) {
    try {
        showToast('Đang thêm sản phẩm vào giỏ hàng...', 'info');
        
        const response = await fetch('/api/cart/add', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            credentials: 'same-origin',
            body: JSON.stringify({
                productId: productId,
                quantity: quantity
            })
        });
        
        const result = await response.json();
        
        if (result.success) {
            showToast('Đã thêm sản phẩm vào giỏ hàng!', 'success');
            await updateCartCount(); // Update the count badge
        } else {
            showToast('Lỗi: ' + (result.message || 'Không thể thêm sản phẩm vào giỏ hàng'), 'error');
        }
    } catch (error) {
        console.error('Error adding to cart:', error);
        showToast('Lỗi khi thêm sản phẩm vào giỏ hàng', 'error');
    }
}

/**
 * Initialize cart count when page loads
 */
document.addEventListener('DOMContentLoaded', function() {
    // Update cart count on page load
    updateCartCount();
    
    // Also update cart count when visibility changes (in case user switches tabs)
    document.addEventListener('visibilitychange', function() {
        if (!document.hidden) {
            updateCartCount();
        }
    });
    
    // Update cart count every 10 seconds to keep it in sync
    setInterval(updateCartCount, 10000);
});

/**
 * Allow manual triggering of cart count update from other scripts
 */
window.updateCartCount = updateCartCount;

