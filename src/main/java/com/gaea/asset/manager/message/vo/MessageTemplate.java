package com.gaea.asset.manager.message.vo;

import com.gaea.asset.manager.common.constants.CodeConstants;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum MessageTemplate {
    DEVICE_ASSIGNED(
            CodeConstants.MESSAGE_DEVICE_ASSIGNED,
            "장비 할당 알림",
            "<h3>장비가 할당되었습니다. 확인 부탁 드립니다.</h3>"
    ),
    STATUS_CHANGE_REQUESTED(
            CodeConstants.MESSAGE_STATUS_CHANGE_REQUESTED,
            "장비 상태 변경 요청 알림",
            "<h3>장비의 상태 변경이 요청되었습니다. 확인 부탁 드립니다.</h3>"
    DEVICE_CHANGE_REQUESTED(
            CodeConstants.MESSAGE_DEVICE_CHANGE_REQUESTED,
    ),
    STATUS_CHANGE_APPROVED(
            CodeConstants.MESSAGE_STATUS_CHANGE_APPROVED,
            "장비 상태 변경 승인 알림",
            "<h3>장비의 상태 변경이 승인되었습니다.</h3>"
    DEVICE_CHANGE_APPROVED(
            CodeConstants.MESSAGE_DEVICE_CHANGE_APPROVED,
    ),
    STATUS_CHANGE_REJECTED(
            CodeConstants.MESSAGE_STATUS_CHANGE_REJECTED,
            "장비 상태 변경 반려 알림",
            "<h3>장비의 상태 변경이 반려되었습니다.</h3>"
    DEVICE_CHANGE_REJECTED(
            CodeConstants.MESSAGE_DEVICE_CHANGE_REJECTED,
    );

    private final String subject;
    private final String template;
    private final String messageCode;

    MessageTemplate(String messageCode, String subject, String template) {
        this.messageCode = messageCode;
        this.subject = subject;
        this.template = template;
    }

    public String formatBody() {
        return String.format(template);
    }

    public static MessageTemplate fromCode(String code) {
        return Arrays.stream(values())
                .filter(template -> template.messageCode.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid message code: " + code));
    }

}
