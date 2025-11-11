package CustomerService.service.impl;

import CustomerService.entity.Ticket;
import CustomerService.entity.TicketReply;
import CustomerService.repository.TicketRepository;
import CustomerService.repository.TicketReplyRepository;
import CustomerService.service.TicketAutoCloseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service để tự động đóng các ticket không có hoạt động từ customer sau 2 ngày
 */
@Service
@Slf4j
@Transactional
public class TicketAutoCloseServiceImpl implements TicketAutoCloseService {
    
    private final TicketRepository ticketRepository;
    private final TicketReplyRepository ticketReplyRepository;
    
    public TicketAutoCloseServiceImpl(TicketRepository ticketRepository, TicketReplyRepository ticketReplyRepository) {
        this.ticketRepository = ticketRepository;
        this.ticketReplyRepository = ticketReplyRepository;
    }
    
    /**
     * Scheduled task chạy mỗi ngày vào lúc 2 giờ sáng để đóng các ticket không hoạt động
     */
    @Override
    @Scheduled(cron = "0 0 2 * * ?") // Chạy mỗi ngày lúc 2 giờ sáng
    @Transactional
    public void closeInactiveTickets() {
        log.info("Bắt đầu quét các ticket không hoạt động để tự động đóng");
        
        try {
            LocalDateTime twoDaysAgo = LocalDateTime.now().minusDays(2);
            
            // Lấy tất cả tickets có status IN_PROGRESS
            List<Ticket> allTickets = ticketRepository.findAll();
            List<Ticket> inProgressTickets = allTickets.stream()
                .filter(t -> t.getStatus() == Ticket.Status.IN_PROGRESS)
                .toList();
            
            int closedCount = 0;
            
            for (Ticket ticket : inProgressTickets) {
                // Lấy replies của ticket
                List<TicketReply> replies = ticketReplyRepository.findByTicketIdOrderByCreatedAtAsc(ticket.getTicketId());
                
                // Lấy thời gian tham chiếu: ưu tiên reopenedAt nếu có, nếu không thì dùng createdAt
                LocalDateTime referenceTime = ticket.getReopenedAt() != null 
                    ? ticket.getReopenedAt() 
                    : ticket.getCreatedAt();
                
                if (replies.isEmpty()) {
                    // Nếu không có reply nào, check nếu ticket được tạo/mở lại hơn 2 ngày
                    if (referenceTime != null && referenceTime.isBefore(twoDaysAgo)) {
                        ticket.setStatus(Ticket.Status.CLOSED);
                        ticket.setClosedAt(LocalDateTime.now());
                        ticketRepository.save(ticket);
                        log.info("Đóng ticket {} vì không có reply nào sau 2 ngày (reference time: {})", 
                            ticket.getTicketId(), referenceTime);
                        closedCount++;
                    }
                } else {
                    // Tìm reply cuối cùng từ customer
                    TicketReply lastCustomerReply = replies.stream()
                        .filter(r -> r.getSenderType() == TicketReply.SenderType.CUSTOMER)
                        .reduce((first, second) -> second)
                        .orElse(null);
                    
                    if (lastCustomerReply == null) {
                        // Chỉ có reply từ staff, check nếu ticket được tạo/mở lại hơn 2 ngày
                        if (referenceTime != null && referenceTime.isBefore(twoDaysAgo)) {
                            ticket.setStatus(Ticket.Status.CLOSED);
                            ticket.setClosedAt(LocalDateTime.now());
                            ticketRepository.save(ticket);
                            log.info("Đóng ticket {} vì không có reply từ customer sau 2 ngày (reference time: {})", 
                                ticket.getTicketId(), referenceTime);
                            closedCount++;
                        }
                    } else {
                        // Có reply từ customer, check nếu reply cuối cùng hơn 2 ngày
                        if (lastCustomerReply.getCreatedAt() != null && lastCustomerReply.getCreatedAt().isBefore(twoDaysAgo)) {
                            ticket.setStatus(Ticket.Status.CLOSED);
                            ticket.setClosedAt(LocalDateTime.now());
                            ticketRepository.save(ticket);
                            log.info("Đóng ticket {} vì customer không reply sau 2 ngày (last reply: {})", 
                                ticket.getTicketId(), lastCustomerReply.getCreatedAt());
                            closedCount++;
                        }
                    }
                }
            }
            
            log.info("Đã đóng tự động {} ticket không hoạt động", closedCount);
            
        } catch (Exception e) {
            log.error("Lỗi khi đóng tự động ticket: ", e);
        }
    }
}

