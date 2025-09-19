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

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {
    @Value("${spring.mail.username}")
    private String from;
    @Value("${cors.allowed-origins}")
    private String frontendUrl;
    private final JavaMailSender mailSender;
    private final MessageMapper messageMapper;

    public void sendToDeviceOwner(String messageCode, Integer deviceNum) throws MessagingException {
        MessageTemplate template = MessageTemplate.fromCode(messageCode);
        String userId = messageMapper.getDeviceOwnerID(deviceNum);
        String userName = messageMapper.getUserName(deviceNum);
        String url = String.format("%s/device-management/devices/%d", frontendUrl, deviceNum);

        insertMessage(template, userId, userName, url);
    }

    public void sendToManager(String messageCode, String roleCode, Integer deviceNum) throws MessagingException {
        MessageTemplate template = MessageTemplate.fromCode(messageCode);
        List<String> managerId = messageMapper.getManagerID(roleCode);
        for (String userId : managerId) {
            String userName = messageMapper.getUserName(deviceNum);
            String url = String.format("%s/device-management/devices/%d", frontendUrl, deviceNum);
            insertMessage(template, userId, userName, url);
        }
    }

    public void sendToUser(String messageCode, String userId, String otpCode) throws MessagingException {
        MessageTemplate template = MessageTemplate.fromCode(messageCode);
        insertMessage(template, userId, otpCode);
    }

    public void insertMessage(MessageTemplate template, String userId, Object... args) throws MessagingException {
        String to = userId + "@gaeasoft.co.kr";
        String content = template.formatBody(args);
        MessageVO messageVO = MessageVO.builder()
                .recipient(to)
                .sender(from)
                .title(template.getSubject())
                .content(content)
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
