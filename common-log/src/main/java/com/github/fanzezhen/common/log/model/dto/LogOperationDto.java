package com.github.fanzezhen.common.log.model.dto;

import com.github.fanzezhen.common.log.foundation.entity.LogOperation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author zezhen.fan
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class LogOperationDto extends LogOperation {
    private List<LogOperationDetailDto> detailDtoList;
}
