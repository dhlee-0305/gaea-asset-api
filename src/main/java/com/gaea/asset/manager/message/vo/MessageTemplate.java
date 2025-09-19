package com.gaea.asset.manager.message.vo;

import com.gaea.asset.manager.common.constants.CommonCode;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum MessageTemplate {
    DEVICE_ASSIGNED(
            CommonCode.MESSAGE_DEVICE_ASSIGNED,
            "[알림] 장비 할당",
            "<h4>%s님, 장비가 할당되었습니다.<br>확인 부탁 드립니다.</h4><br><a href='%s'>상세 내역 보기</a>"
    ),
    DEVICE_CHANGE_REQUESTED(
            CommonCode.MESSAGE_DEVICE_CHANGE_REQUESTED,
            "[알림] 장비 정보 변경 요청",
            "<h4>%s님의 장비 정보 변경이 요청되었습니다.<br>확인 부탁 드립니다.</h4><br><a href='%s'>상세 내역 보기</a>"
    ),
    DEVICE_CHANGE_APPROVED(
            CommonCode.MESSAGE_DEVICE_CHANGE_APPROVED,
            "[알림] 장비 정보 변경 승인",
            "<h4>%s님의 장비 정보 변경이 승인되었습니다.</h4><br><a href='%s'>상세 내역 보기</a>"
    ),
    DEVICE_CHANGE_REJECTED(
            CommonCode.MESSAGE_DEVICE_CHANGE_REJECTED,
            "[알림] 장비 정보 변경 반려",
            "<h4>%s님의 장비 정보 변경이 반려되었습니다.</h4><br><a href='%s'>상세 내역 보기</a>"
    ),
    RESET_PASSWORD_OTP(
            CommonCode.MESSAGE_RESET_PASSWORD_OTP,
            "[알림] 비밀번호 초기화 인증",
            "<h4>인증번호는 <h3>[%s]</h3>입니다. 5분 안에 입력해주세요.</h4>"
    );

    private final String subject;
    private final String template;
    private final String messageCode;

    MessageTemplate(String messageCode, String subject, String template) {
        this.messageCode = messageCode;
        this.subject = subject;
        this.template = template;
    }

    public String formatBody(Object... args) {
        return String.format(template, args);
    }

    public static MessageTemplate fromCode(String code) {
        return Arrays.stream(values())
                .filter(template -> template.messageCode.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid message code: " + code));
    }

}
