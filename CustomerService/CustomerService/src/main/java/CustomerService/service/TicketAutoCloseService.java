package CustomerService.service;

/**
 * Service để tự động đóng các ticket không có hoạt động từ customer sau 2 ngày
 */
public interface TicketAutoCloseService {
    
    /**
     * Đóng tự động các ticket không có reply từ customer sau 2 ngày
     */
    void closeInactiveTickets();
}

