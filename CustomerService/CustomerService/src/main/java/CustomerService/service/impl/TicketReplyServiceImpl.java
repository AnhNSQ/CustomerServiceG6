package CustomerService.service.impl;

import CustomerService.entity.Ticket;
import CustomerService.entity.TicketReply;
import CustomerService.repository.TicketRepository;
import CustomerService.repository.TicketReplyRepository;
import CustomerService.service.TicketReplyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional
public class TicketReplyServiceImpl implements TicketReplyService {
    
    private final TicketReplyRepository ticketReplyRepository;
    private final TicketRepository ticketRepository;
    
    public TicketReplyServiceImpl(TicketReplyRepository ticketReplyRepository, TicketRepository ticketRepository) {
        this.ticketReplyRepository = ticketReplyRepository;
        this.ticketRepository = ticketRepository;
    }
    
    @Override
    @Transactional
    public TicketReply createReply(Long ticketId, TicketReply.SenderType senderType, Long senderId, String message) {
        log.info("Tạo ticket reply cho ticket {}, senderType: {}, senderId: {}", ticketId, senderType, senderId);
        
        // Kiểm tra ticket tồn tại
        Ticket ticket = ticketRepository.findByIdWithCustomer(ticketId)
            .orElseThrow(() -> new RuntimeException("Ticket not found with ID: " + ticketId));
        
        // Tạo reply
        TicketReply reply = new TicketReply(
            ticket,
            senderType,
            senderId,
            message
        );
        
        // Note: Không tự động chuyển status khi reply, status đã được set khi phân công ticket
        TicketReply savedReply = ticketReplyRepository.save(reply);
        log.info("Tạo ticket reply thành công với ID: {}", savedReply.getTicketReplyId());
        
        return savedReply;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TicketReply> getRepliesByTicketId(Long ticketId) {
        log.info("Lấy danh sách replies cho ticket {}", ticketId);
        
        // Kiểm tra ticket tồn tại
        ticketRepository.findByIdWithCustomer(ticketId)
            .orElseThrow(() -> new RuntimeException("Ticket not found with ID: " + ticketId));
        
        return ticketReplyRepository.findByTicketIdOrderByCreatedAtAsc(ticketId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public TicketReply getReplyById(Long replyId) {
        log.info("Lấy ticket reply với ID: {}", replyId);
        
        return ticketReplyRepository.findById(replyId)
            .orElseThrow(() -> new RuntimeException("Ticket reply not found with ID: " + replyId));
    }
}

