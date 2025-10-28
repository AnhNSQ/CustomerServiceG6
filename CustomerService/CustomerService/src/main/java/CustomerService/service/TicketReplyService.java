package CustomerService.service;

import CustomerService.entity.TicketReply;

import java.util.List;

/**
 * Interface cho TicketReplyService - xử lý nghiệp vụ phản hồi ticket
 */
public interface TicketReplyService {
    
    /**
     * Tạo ticket reply mới
     */
    TicketReply createReply(Long ticketId, TicketReply.SenderType senderType, Long senderId, String message);
    
    /**
     * Lấy tất cả replies của một ticket
     */
    List<TicketReply> getRepliesByTicketId(Long ticketId);
    
    /**
     * Lấy reply theo ID
     */
    TicketReply getReplyById(Long replyId);
}


