package CustomerService.dto;

import lombok.Data;

@Data
public class EvaluationRequest {
    private Integer score; // 1-5
    private String comment;
}


