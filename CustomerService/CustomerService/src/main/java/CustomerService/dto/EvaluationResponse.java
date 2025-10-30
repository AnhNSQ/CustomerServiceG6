package CustomerService.dto;

import CustomerService.entity.Evaluation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationResponse {
    private Long evaluationId;
    private Long ticketId;
    private Long customerId;
    private Integer score;
    private String comment;
    private LocalDateTime createdAt;

    public static EvaluationResponse fromEntity(Evaluation e) {
        return new EvaluationResponse(
            e.getEvaluationId(),
            e.getTicket() != null ? e.getTicket().getTicketId() : null,
            e.getCustomer() != null ? e.getCustomer().getCustomerId() : null,
            e.getScore(),
            e.getComment(),
            e.getCreatedAt()
        );
    }
}


