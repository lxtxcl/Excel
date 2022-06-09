package com.kuang.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class LeaveType {

    @Excel(name = "请假类型",orderNum = "0")
    String id;
    @Excel(name = "假别名称",orderNum = "1")
    String name;
}
