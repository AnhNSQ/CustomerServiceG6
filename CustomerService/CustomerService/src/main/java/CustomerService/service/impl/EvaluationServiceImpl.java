package CustomerService.service.impl;

import CustomerService.dto.EvaluationResponse;
import CustomerService.dto.StaffRatingSummary;
import CustomerService.dto.StaffTicketEvaluation;
import CustomerService.entity.Customer;
import CustomerService.entity.Evaluation;
import CustomerService.entity.Ticket;
import CustomerService.repository.CustomerRepository;
import CustomerService.repository.EvaluationRepository;
import CustomerService.repository.TicketRepository;
import CustomerService.service.EvaluationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EvaluationServiceImpl implements EvaluationService {

    private final EvaluationRepository evaluationRepository;
    private final TicketRepository ticketRepository;
    private final CustomerRepository customerRepository;

    @Override
    public EvaluationResponse createEvaluation(Long customerId, Long ticketId, int score, String comment) {
        // Validate ticket
        Ticket ticket = ticketRepository.findByIdWithCustomer(ticketId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy ticket"));

        // Ensure ticket belongs to customer
        if (ticket.getCustomer() == null || !ticket.getCustomer().getCustomerId().equals(customerId)) {
            throw new RuntimeException("Bạn không có quyền đánh giá ticket này");
        }

        // Only allow evaluation when ticket is CLOSED
        if (ticket.getStatus() != Ticket.Status.CLOSED) {
            throw new RuntimeException("Chỉ có thể đánh giá ticket đã đóng");
        }

        // Ensure not evaluated yet
        if (evaluationRepository.existsByTicket_TicketId(ticketId)) {
            throw new RuntimeException("Ticket này đã được đánh giá");
        }

        // Validate score range
        if (score < 1 || score > 5) {
            throw new RuntimeException("Điểm đánh giá phải từ 1 đến 5");
        }

        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy customer"));

        Evaluation evaluation = new Evaluation(ticket, customer, score, comment);
        Evaluation saved = evaluationRepository.save(evaluation);

        return EvaluationResponse.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EvaluationResponse> getByTicketId(Long ticketId) {
        return evaluationRepository.findByTicketId(ticketId)
            .map(EvaluationResponse::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public StaffRatingSummary getStaffRatingSummary(Long staffId) {
        Double avg = evaluationRepository.getAverageScoreForStaff(staffId);
        List<Evaluation> evals = evaluationRepository.findByStaffId(staffId);
        double average = avg != null ? avg : 0.0;
        return new StaffRatingSummary(staffId, average, evals != null ? evals.size() : 0);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StaffTicketEvaluation> getEvaluationsByStaff(Long staffId) {
        return evaluationRepository.findByStaffId(staffId)
            .stream()
            .map(StaffTicketEvaluation::from)
            .toList();
    }
}
