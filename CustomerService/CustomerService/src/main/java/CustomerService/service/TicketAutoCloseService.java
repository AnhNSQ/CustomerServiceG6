package CustomerService.service;

public interface TicketAutoCloseService {
    
    /**
     * Đóng tự động các ticket không có reply từ customer sau 2 ngày
     */
    void closeInactiveTickets();
}

