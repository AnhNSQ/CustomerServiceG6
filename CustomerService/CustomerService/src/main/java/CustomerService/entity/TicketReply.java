package CustomerService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "ticket_reply")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketReply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_reply_id")
    private Long ticketReplyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @Enumerated(EnumType.STRING)
    @Column(name = "sender_type", nullable = false)
    private SenderType senderType;

    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @Column(name = "message", nullable = false, columnDefinition = "nvarchar(MAX)")
    private String message;

    @Column(name = "image_url", columnDefinition = "nvarchar(500)")
    private String imageURL;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Enum cho SenderType theo ERD
    public enum SenderType {
        CUSTOMER, STAFF
    }

    // Constructor để tạo ticket reply mới
    public TicketReply(Ticket ticket, SenderType senderType, Long senderId, String message) {
        this.ticket = ticket;
        this.senderType = senderType;
        this.senderId = senderId;
        this.message = message;
    }
    
    // Constructor với imageURL
    public TicketReply(Ticket ticket, SenderType senderType, Long senderId, String message, String imageURL) {
        this.ticket = ticket;
        this.senderType = senderType;
        this.senderId = senderId;
        this.message = message;
        this.imageURL = imageURL;
    }
}
