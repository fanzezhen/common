package com.github.fanzezhen.common.core.enums.file;

import lombok.Getter;

/**
 * 文件类型
 *
 * @author fanzezhen
 * @createTime 2024/1/23 10:54
 * @since 3.0.0
 */
@Getter
public enum FileTypeEnum {
    /**
     * xlsx
     */
    XLSX(".xlsx");
    private final String code;

    FileTypeEnum(String code) {
        this.code = code;
    }
}
