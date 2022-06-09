package com.kuang.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.*;
import org.apache.poi.ss.formula.functions.T;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class User_ {


    /**
     * 姓名
     */

    @Excel(name = "姓名")
    private String name;

    /**
     * 年龄
     */

    @Excel(name = "年龄")
    private Integer age;


}
