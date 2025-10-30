package CustomerService.service;

import CustomerService.dto.ApiResponse;
import CustomerService.dto.EvaluationResponse;
import CustomerService.dto.StaffRatingSummary;
import CustomerService.dto.StaffTicketEvaluation;

import java.util.Optional;
import java.util.List;

public interface EvaluationService {

    EvaluationResponse createEvaluation(Long customerId, Long ticketId, int score, String comment);

    Optional<EvaluationResponse> getByTicketId(Long ticketId);

    StaffRatingSummary getStaffRatingSummary(Long staffId);

    List<StaffTicketEvaluation> getEvaluationsByStaff(Long staffId);
}


