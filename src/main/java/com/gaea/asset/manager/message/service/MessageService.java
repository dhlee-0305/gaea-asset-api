package com.gaea.asset.manager.message.service;

import com.gaea.asset.manager.message.vo.MessageVO;
import com.gaea.asset.manager.util.Header;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MessageService {
    @Value("${spring.mail.username}")
    private String from;

    private final JavaMailSender mailSender;

    public MessageService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public Header<MessageVO> sendEmail(MessageVO messageVO) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true,"UTF-8");

            helper.setFrom(from);
            helper.setTo(messageVO.getTo());
            helper.setSubject(messageVO.getSubject());
            helper.setText(messageVO.getText(), true);

            mailSender.send(message);

        } catch (Exception e) {
            return Header.ERROR("500", "등록 중 예외 발생: " + e.getMessage());
        }
        return Header.OK(messageVO);
    }
}
