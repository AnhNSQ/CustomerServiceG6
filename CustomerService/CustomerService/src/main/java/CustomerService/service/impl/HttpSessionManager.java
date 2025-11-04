package CustomerService.service.impl;

import CustomerService.service.SessionManager;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Implementation cho SessionManager sử dụng HttpSession
 */
@Component
@Slf4j
public class HttpSessionManager implements SessionManager {
    
    private static final String CUSTOMER_ID_KEY = "customerId";
    private static final String CUSTOMER_NAME_KEY = "customerName";
    private static final String CUSTOMER_EMAIL_KEY = "customerEmail";
    
    private static final String STAFF_ID_KEY = "staffId";
    private static final String STAFF_NAME_KEY = "staffName";
    private static final String STAFF_EMAIL_KEY = "staffEmail";
    
    @Override
    public void setCustomerSession(HttpSession session, Long customerId, String name, String email, Object roles) {
        try {
            session.setAttribute(CUSTOMER_ID_KEY, customerId);
            session.setAttribute(CUSTOMER_NAME_KEY, name);
            session.setAttribute(CUSTOMER_EMAIL_KEY, email);
            log.info("Customer session set successfully for ID: {}", customerId);
        } catch (Exception e) {
            log.error("Error setting customer session: ", e);
            throw new RuntimeException("Failed to set customer session", e);
        }
    }
    
    @Override
    public void setStaffSession(HttpSession session, Long staffId, String name, String email, Object roles) {
        try {
            session.setAttribute(STAFF_ID_KEY, staffId);
            session.setAttribute(STAFF_NAME_KEY, name);
            session.setAttribute(STAFF_EMAIL_KEY, email);
            log.info("Staff session set successfully for ID: {}", staffId);
        } catch (Exception e) {
            log.error("Error setting staff session: ", e);
            throw new RuntimeException("Failed to set staff session", e);
        }
    }
    
    @Override
    public Long getCustomerId(HttpSession session) {
        return (Long) session.getAttribute(CUSTOMER_ID_KEY);
    }
    
    @Override
    public Long getStaffId(HttpSession session) {
        return (Long) session.getAttribute(STAFF_ID_KEY);
    }
    
    @Override
    public void invalidateSession(HttpSession session) {
        try {
            if (session != null && !isSessionInvalid(session)) {
                // Clear all session attributes before invalidation
                try {
                    session.removeAttribute(CUSTOMER_ID_KEY);
                    session.removeAttribute(CUSTOMER_NAME_KEY);
                    session.removeAttribute(CUSTOMER_EMAIL_KEY);
                    session.removeAttribute(STAFF_ID_KEY);
                    session.removeAttribute(STAFF_NAME_KEY);
                    session.removeAttribute(STAFF_EMAIL_KEY);
                } catch (Exception e) {
                    log.warn("Error clearing session attributes (session may already be invalid): {}", e.getMessage());
                }
                
                // Invalidate the session
                session.invalidate();
                log.info("Session invalidated successfully");
            } else {
                log.warn("Session is already invalid or null");
            }
        } catch (IllegalStateException e) {
            // Session is already invalidated, which is fine
            log.debug("Session was already invalidated: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error invalidating session: ", e);
            throw new RuntimeException("Failed to invalidate session", e);
        }
    }
    
    /**
     * Check if session is already invalid
     */
    private boolean isSessionInvalid(HttpSession session) {
        try {
            session.getAttribute("dummy"); // Try to access session
            return false;
        } catch (IllegalStateException e) {
            return true; // Session is invalid
        }
    }
    
    @Override
    public boolean isCustomerLoggedIn(HttpSession session) {
        return getCustomerId(session) != null;
    }
    
    @Override
    public boolean isStaffLoggedIn(HttpSession session) {
        return getStaffId(session) != null;
    }
}
