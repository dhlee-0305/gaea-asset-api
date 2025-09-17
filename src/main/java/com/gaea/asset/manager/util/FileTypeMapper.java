package com.gaea.asset.manager.util;

import com.gaea.asset.manager.common.constants.CommonCode;

public class FileTypeMapper {
    public static String getFileType(String postType) {
        return switch (postType) {
            case CommonCode.POST_TYPE_NOTICE -> "notice";
            default -> "etc";
        };
    }
}
