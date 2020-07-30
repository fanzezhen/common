package com.github.fanzezhen.common.core.model.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.fanzezhen.common.core.util.DateUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PageDto<T extends G, G> extends Page<G> {
    private T param;
    private String start;
    private String end;
    private String startDate;
    private String endDate;

    public void setStartDate(String startDate) {
        this.startDate = DateUtil.addTimeToDateString(startDate, "00:00:00");
    }

    public void setEndDate(String endDate) {
        this.endDate = DateUtil.addTimeToDateString(endDate, "23:59:59");
    }

}
