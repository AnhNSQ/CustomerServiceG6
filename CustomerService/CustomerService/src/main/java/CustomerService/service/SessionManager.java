package CustomerService.service;

import jakarta.servlet.http.HttpSession;

/**
 * Interface cho việc quản lý session
 * Tuân thủ Single Responsibility Principle (SRP)
 */
public interface SessionManager {
    
    /**
     * Lưu thông tin customer vào session
     */
    void setCustomerSession(HttpSession session, Long customerId, String name, String email, Object roles);
    
    /**
     * Lưu thông tin staff vào session
     */
    void setStaffSession(HttpSession session, Long staffId, String name, String email, Object roles);
    
    /**
     * Lấy customer ID từ session
     */
    Long getCustomerId(HttpSession session);
    
    /**
     * Lấy staff ID từ session
     */
    Long getStaffId(HttpSession session);
    
    /**
     * Xóa session
     */
    void invalidateSession(HttpSession session);
    
    /**
     * Kiểm tra customer đã đăng nhập
     */
    boolean isCustomerLoggedIn(HttpSession session);
    
    /**
     * Kiểm tra staff đã đăng nhập
     */
    boolean isStaffLoggedIn(HttpSession session);
}
