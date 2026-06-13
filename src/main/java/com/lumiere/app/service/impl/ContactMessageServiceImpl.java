package com.lumiere.app.service.impl;

import com.lumiere.app.domain.ContactMessage;
import com.lumiere.app.domain.Customer;
import com.lumiere.app.domain.User;
import com.lumiere.app.domain.enumeration.ContactStatus;
import com.lumiere.app.domain.enumeration.NotificationType;
import com.lumiere.app.repository.ContactMessageRepository;
import com.lumiere.app.repository.CustomerRepository;
import com.lumiere.app.repository.UserRepository;
import com.lumiere.app.service.ContactMessageService;
import com.lumiere.app.service.MailService;
import com.lumiere.app.service.dto.ContactMessageDTO;
import com.lumiere.app.service.kafka.NotificationProducerService;
import com.lumiere.app.service.mapper.ContactMessageMapper;
import java.time.Instant;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.config.JHipsterProperties;

/**
 * Service Implementation for managing {@link com.lumiere.app.domain.ContactMessage}.
 */
@Service
@Transactional
public class ContactMessageServiceImpl implements ContactMessageService {

    private static final Logger LOG = LoggerFactory.getLogger(ContactMessageServiceImpl.class);

    private final ContactMessageRepository contactMessageRepository;

    private final ContactMessageMapper contactMessageMapper;

    private final MailService mailService;

    private final JHipsterProperties jHipsterProperties;

    private final NotificationProducerService notificationProducerService;

    private final UserRepository userRepository;

    private final CustomerRepository customerRepository;

    public ContactMessageServiceImpl(
        ContactMessageRepository contactMessageRepository,
        ContactMessageMapper contactMessageMapper,
        MailService mailService,
        JHipsterProperties jHipsterProperties,
        NotificationProducerService notificationProducerService,
        UserRepository userRepository,
        CustomerRepository customerRepository
    ) {
        this.contactMessageRepository = contactMessageRepository;
        this.contactMessageMapper = contactMessageMapper;
        this.mailService = mailService;
        this.jHipsterProperties = jHipsterProperties;
        this.notificationProducerService = notificationProducerService;
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public ContactMessageDTO save(ContactMessageDTO contactMessageDTO) {
        LOG.debug("Request to save ContactMessage : {}", contactMessageDTO);
        ContactMessage contactMessage = contactMessageMapper.toEntity(contactMessageDTO);
        if (contactMessage.getCreatedAt() == null) {
            contactMessage.setCreatedAt(Instant.now());
        }
        contactMessage = contactMessageRepository.save(contactMessage);
        return contactMessageMapper.toDto(contactMessage);
    }

    @Override
    public ContactMessageDTO update(ContactMessageDTO contactMessageDTO) {
        LOG.debug("Request to update ContactMessage : {}", contactMessageDTO);
        ContactMessage contactMessage = contactMessageMapper.toEntity(contactMessageDTO);
        contactMessage = contactMessageRepository.save(contactMessage);
        return contactMessageMapper.toDto(contactMessage);
    }

    @Override
    public Optional<ContactMessageDTO> partialUpdate(ContactMessageDTO contactMessageDTO) {
        LOG.debug("Request to partially update ContactMessage : {}", contactMessageDTO);

        return contactMessageRepository
            .findById(contactMessageDTO.getId())
            .map(existingContactMessage -> {
                contactMessageMapper.partialUpdate(existingContactMessage, contactMessageDTO);
                return existingContactMessage;
            })
            .map(contactMessageRepository::save)
            .map(contactMessageMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ContactMessageDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all ContactMessages");
        return contactMessageRepository.findAllByOrderByCreatedAtDesc(pageable).map(contactMessageMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ContactMessageDTO> findByStatus(ContactStatus status, Pageable pageable) {
        LOG.debug("Request to get ContactMessages by status: {}", status);
        return contactMessageRepository.findByStatusOrderByCreatedAtDesc(status, pageable).map(contactMessageMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ContactMessageDTO> findOne(Long id) {
        LOG.debug("Request to get ContactMessage : {}", id);
        return contactMessageRepository.findById(id).map(contactMessageMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete ContactMessage : {}", id);
        contactMessageRepository.deleteById(id);
    }

    @Override
    public ContactMessageDTO submitContactMessage(ContactMessageDTO contactMessageDTO) {
        LOG.debug("Request to submit contact message from: {}", contactMessageDTO.getEmail());

        // Set status to NEW and created date
        contactMessageDTO.setStatus(ContactStatus.NEW);
        contactMessageDTO.setCreatedAt(Instant.now());

        ContactMessage contactMessage = contactMessageMapper.toEntity(contactMessageDTO);
        contactMessage = contactMessageRepository.save(contactMessage);

        // Gửi notification cho admin qua Kafka
        String adminMessage = String.format("Có tin nhắn liên hệ mới từ %s: %s", 
            contactMessageDTO.getFullName() != null ? contactMessageDTO.getFullName() : "Khách hàng",
            contactMessageDTO.getSubject() != null ? contactMessageDTO.getSubject() : "Không có chủ đề");
        notificationProducerService.sendAdminNotification(
            NotificationType.NEW_CONTACT,
            adminMessage,
            "/admin/contact-messages/" + contactMessage.getId()
        );

        // Send email notification to admin
        try {
            String adminEmail = jHipsterProperties.getMail().getFrom();
            String subject = "Tin nhắn liên hệ mới: " + contactMessageDTO.getSubject();
            String content = buildContactNotificationEmail(contactMessageDTO);
            mailService.sendEmail(adminEmail, subject, content, false, true);
            LOG.info("Sent notification email to admin for new contact message: {}", contactMessage.getId());
        } catch (Exception e) {
            LOG.error("Failed to send notification email for contact message", e);
            // Don't fail the request if email fails
        }

        return contactMessageMapper.toDto(contactMessage);
    }

    @Override
    public ContactMessageDTO markAsRead(Long id) {
        LOG.debug("Request to mark ContactMessage as read: {}", id);
        ContactMessage contactMessage = contactMessageRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("ContactMessage not found: " + id));

        if (contactMessage.getStatus() == ContactStatus.NEW) {
            contactMessage.setStatus(ContactStatus.READ);
            contactMessage = contactMessageRepository.save(contactMessage);
        }

        return contactMessageMapper.toDto(contactMessage);
    }

    @Override
    public ContactMessageDTO markAsReplied(Long id, String adminNote) {
        LOG.debug("Request to mark ContactMessage as replied: {}", id);
        ContactMessage contactMessage = contactMessageRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("ContactMessage not found: " + id));

        contactMessage.setStatus(ContactStatus.REPLIED);
        if (adminNote != null) {
            contactMessage.setAdminNote(adminNote);
        }
        contactMessage = contactMessageRepository.save(contactMessage);

        // Gửi notification cho customer nếu tìm thấy customer từ email
        if (contactMessage.getEmail() != null) {
            try {
                Optional<User> userOpt = userRepository.findOneByEmailIgnoreCase(contactMessage.getEmail());
                if (userOpt.isPresent()) {
                    Optional<Customer> customerOpt = customerRepository.findByUserId(userOpt.get().getId());
                    if (customerOpt.isPresent()) {
                        String customerMessage = String.format("Chúng tôi đã phản hồi tin nhắn liên hệ của bạn về: %s", 
                            contactMessage.getSubject() != null ? contactMessage.getSubject() : "tin nhắn của bạn");
                        notificationProducerService.sendCustomerNotification(
                            customerOpt.get().getId(),
                            NotificationType.CONTACT_REPLY,
                            customerMessage,
                            "/contact-messages/" + contactMessage.getId()
                        );
                    }
                }
            } catch (Exception e) {
                LOG.error("Failed to send notification to customer for contact reply", e);
                // Không throw exception để không ảnh hưởng đến business logic
            }
        }

        return contactMessageMapper.toDto(contactMessage);
    }

    /**
     * Build HTML email content for contact notification.
     */
    private String buildContactNotificationEmail(ContactMessageDTO contactMessageDTO) {
        StringBuilder html = new StringBuilder();
        html.append("<html><body style='font-family: Arial, sans-serif;'>");
        html.append("<h2>Tin nhắn liên hệ mới</h2>");
        html.append("<div style='background-color: #f5f5f5; padding: 20px; border-radius: 5px;'>");
        html.append("<p><strong>Họ và tên:</strong> ").append(escapeHtml(contactMessageDTO.getFullName())).append("</p>");
        html.append("<p><strong>Email:</strong> ").append(escapeHtml(contactMessageDTO.getEmail())).append("</p>");
        html.append("<p><strong>Chủ đề:</strong> ").append(escapeHtml(contactMessageDTO.getSubject())).append("</p>");
        html.append("<p><strong>Tin nhắn:</strong></p>");
        html.append("<p style='white-space: pre-wrap;'>").append(escapeHtml(contactMessageDTO.getMessage())).append("</p>");
        html.append("</div>");
        html.append("</body></html>");
        return html.toString();
    }

    private String escapeHtml(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;");
    }
}

