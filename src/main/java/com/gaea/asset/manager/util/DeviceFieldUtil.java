package com.gaea.asset.manager.util;

import com.gaea.asset.manager.code.vo.CodeVO;
import com.gaea.asset.manager.device.vo.DeviceVO;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class DeviceFieldUtil {
    public static boolean isEqual(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.equals(b);
    }

    // 장비 히스토리 요약용 필드 정의
    public static class DeviceField {
        public final String label;
        public final Function<DeviceVO, String> getter;

        public DeviceField(String label, Function<DeviceVO, String> getter) {
            this.label = label;
            this.getter = getter;
        }
    }

    /**
     * 장비 히스토리 필드 생성
     * @param deviceTypeList
     * @param usageDivisionList
     * @return
     */
    public static List<DeviceField> getDeviceSummaryFields (List<CodeVO> deviceTypeList, List<CodeVO> usageDivisionList) {
        // 코드 -> 코드명 매핑 처리(장비유형, 사용용도)
        Function<DeviceVO, String> deviceType = device -> deviceTypeList.stream()
                .filter(code -> code.getCode().equals(device.getDeviceTypeCode()))
                .map(CodeVO::getCodeName)
                .findFirst()
                .orElse(device.getDeviceType());
        Function<DeviceVO, String> usageDivision = device -> usageDivisionList.stream()
                .filter(code -> code.getCode().equals(device.getUsageDivisionCode()))
                .map(CodeVO::getCodeName)
                .findFirst()
                .orElse(device.getUsageDivision());

        return Arrays.asList(
            new DeviceField("장비담당자", DeviceVO::getUserName),
            new DeviceField("장비유형", deviceType),
            new DeviceField("용도구분", usageDivision),
            new DeviceField("사용용도", DeviceVO::getUsagePurpose),
            new DeviceField("사용/보관 위치", DeviceVO::getArchiveLocation),
            new DeviceField("기존 장비관리번호", DeviceVO::getOldDeviceId),
            new DeviceField("제조사", DeviceVO::getManufacturer),
            new DeviceField("모델명", DeviceVO::getModelName),
            new DeviceField("제조년도", DeviceVO::getManufactureDate),
            new DeviceField("CPU", DeviceVO::getCpuSpec),
            new DeviceField("메모리", DeviceVO::getMemorySize),
            new DeviceField("SSD/HDD", DeviceVO::getStorageInfo),
            new DeviceField("OS", DeviceVO::getOperatingSystem),
            new DeviceField("인치", DeviceVO::getScreenSize),
            new DeviceField("GPU", DeviceVO::getGpuSpec),
            new DeviceField("구매일자", DeviceVO::getPurchaseDate),
            new DeviceField("반납일자", DeviceVO::getReturnDate),
            new DeviceField("비고", DeviceVO::getRemarks)
        );
    }
}
