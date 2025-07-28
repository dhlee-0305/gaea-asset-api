package com.gaea.asset.manager.message;

import com.gaea.asset.manager.message.service.MessageService;
import com.gaea.asset.manager.message.vo.MessageVO;
import com.gaea.asset.manager.util.Header;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "메세지 관리 API", description = "메세지 관리 API 입니다.")
public class MessageController {
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/messages")
    @Operation(summary = "메세지 등록", description = "메세지 등록 API")
    Header<MessageVO> sendEmail(@RequestBody MessageVO messageVO) {
        return messageService.sendEmail(messageVO);
    }
}
