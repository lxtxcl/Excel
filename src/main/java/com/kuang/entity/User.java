package com.kuang.entity;


import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelCollection;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentFontStyle;
import lombok.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @dec 用户实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class User {

    /**
     * 用户名
     */
    @Excel(name = "用户名",orderNum = "2")
    private String username;

    /**
     * 姓名
     */

    @Excel(name = "姓名",orderNum = "1",groupName = "人")
    private String name;

    /**
     * 年龄
     */

    @Excel(name = "年龄",orderNum = "3",groupName = "人")
    private Integer age;

    /**
     * 性别,0表示男，1表示女
     */

    @Excel(name = "性别", replace = {"男_1", "女_2"},orderNum = "4")
    private String sex;

    /**
     * 籍贯
     */
    @Excel(name = "籍贯",orderNum = "5")
    private String address;

    @ExcelCollection(name = "nums",orderNum = "6")
    private List<User_> list ;


}

