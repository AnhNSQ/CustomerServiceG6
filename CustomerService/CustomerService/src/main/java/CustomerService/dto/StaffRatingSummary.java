package CustomerService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StaffRatingSummary {
    private Long staffId;
    private double averageScore; // 0..5
    private int totalReviews;
}


