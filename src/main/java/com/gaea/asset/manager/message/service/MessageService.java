package com.gaea.asset.manager.message.service;

import com.gaea.asset.manager.message.vo.MessageTemplate;
import com.gaea.asset.manager.message.vo.MessageVO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {
    @Value("${spring.mail.username}")
    private String from;
    private final JavaMailSender mailSender;
    private final MessageMapper messageMapper;

    public void sendToDeviceOwner(String messageCode, Integer deviceNum) throws MessagingException {
        MessageTemplate template = MessageTemplate.fromCode(messageCode);
        String userId = messageMapper.getDeviceOwner(deviceNum);
        Integer empNum = messageMapper.getEmpNum(userId);
        insertMessage(template, empNum);
    }

    public void sendToManager(String messageCode, String roleCode) throws MessagingException {
        MessageTemplate template = MessageTemplate.fromCode(messageCode);
        Integer empNum = messageMapper.getManagerEmpNum(roleCode);
        insertMessage(template, empNum);
    }

    public void insertMessage(MessageTemplate template, Integer recipient) throws MessagingException {
        String to = messageMapper.getUserID(recipient) + "@gaeasoft.co.kr";
        MessageVO messageVO = MessageVO.builder()
                .recipient(to)
                .sender(from)
                .title(template.getSubject())
                .content(template.formatBody())
                .messageStatusCode(template.getMessageCode())
                .build();

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true,"UTF-8");

        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(messageVO.getTitle());
        helper.setText(messageVO.getContent(), true);

        mailSender.send(message);
        messageMapper.insertMessage(messageVO);
    }
}
