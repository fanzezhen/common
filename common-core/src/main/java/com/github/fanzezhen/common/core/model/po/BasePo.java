package com.github.fanzezhen.common.core.model.po;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class BasePo {
    private Long id;
    private Date createTime;
    private Long createUserId;
}
