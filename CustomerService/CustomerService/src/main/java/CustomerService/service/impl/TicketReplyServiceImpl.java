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
    public TicketReply createReply(Long ticketId, TicketReply.SenderType senderType, Long senderId, String message, String imageURL) {
        log.info("Tạo ticket reply cho ticket {}, senderType: {}, senderId: {}", ticketId, senderType, senderId);

        Ticket ticket = ticketRepository.findByIdWithCustomer(ticketId)
            .orElseThrow(() -> new RuntimeException("Ticket not found with ID: " + ticketId));

        TicketReply reply;
        if (imageURL != null && !imageURL.trim().isEmpty()) {
            reply = new TicketReply(
                ticket,
                senderType,
                senderId,
                message,
                imageURL
            );
        } else {
            reply = new TicketReply(
                ticket,
                senderType,
                senderId,
                message
            );
        }

        TicketReply savedReply = ticketReplyRepository.save(reply);
        log.info("Tạo ticket reply thành công với ID: {}", savedReply.getTicketReplyId());
        
        return savedReply;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TicketReply> getRepliesByTicketId(Long ticketId) {
        log.info("Lấy danh sách replies cho ticket {}", ticketId);

        ticketRepository.findByIdWithCustomer(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found with ID: " + ticketId));

        return ticketReplyRepository.findByTicketIdOrderByCreatedAtAsc(ticketId);
    }
}

