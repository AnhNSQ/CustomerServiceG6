package CustomerService.dto;

import CustomerService.entity.TicketReply;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketReplyResponse {
    private Long ticketReplyId;
    private Long ticketId;
    private TicketReply.SenderType senderType;
    private Long senderId;
    private String message;
    private String imageURL;
    private LocalDateTime createdAt;
    
    public static TicketReplyResponse fromEntity(TicketReply reply) {
        TicketReplyResponse dto = new TicketReplyResponse();
        dto.setTicketReplyId(reply.getTicketReplyId());
        dto.setTicketId(reply.getTicket().getTicketId());
        dto.setSenderType(reply.getSenderType());
        dto.setSenderId(reply.getSenderId());
        dto.setMessage(reply.getMessage());
        dto.setImageURL(reply.getImageURL());
        dto.setCreatedAt(reply.getCreatedAt());
        return dto;
    }
}


