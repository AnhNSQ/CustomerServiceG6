package CustomerService.dto;

import CustomerService.entity.Evaluation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StaffTicketEvaluation {
    private Long ticketId;
    private String subject;
    private Integer score;
    private String comment;
    private LocalDateTime createdAt;

    public static StaffTicketEvaluation from(Evaluation e) {
        return new StaffTicketEvaluation(
            e.getTicket() != null ? e.getTicket().getTicketId() : null,
            e.getTicket() != null ? e.getTicket().getSubject() : null,
            e.getScore(),
            e.getComment(),
            e.getCreatedAt()
        );
    }
}


