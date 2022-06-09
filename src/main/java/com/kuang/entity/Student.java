package com.kuang.entity;

// 使用lombok

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ColumnWidth(20)
@ContentFontStyle(fontHeightInPoints = 20)
@ToString
public class Student {


    /**
     * id
     */
    @ExcelProperty(value = "编号",index = 0)
    //@ExcelIgnore
    private String id;
    /**
     * 学生姓名
     */

    @ExcelProperty(value = {"a","学生姓名"}, index = 1)
    //@ColumnWidth(30)
    private String name;
    /**
     * 学生性别
     */
    @HeadStyle(fillPatternType = FillPatternType.SOLID_FOREGROUND ,fillForegroundColor = 5)
    @ExcelProperty(value = {"a","学生性别"}, index = 2)
    private String gender;

    @ExcelIgnore
    private String errorMsg;

    /**
     * 学生出生日期
     */
    @ExcelProperty(value = "学生出生日期", index = 3)
    //@ColumnWidth(20)
    @HeadStyle(fillPatternType = FillPatternType.THIN_VERT_BANDS ,fillForegroundColor = 10,horizontalAlignment= HorizontalAlignment.CENTER)
    @HeadFontStyle(color = Font.COLOR_RED,fontHeightInPoints = 10,italic = true,bold = true)
   /* @ContentStyle(fillPatternType = FillPatternType.SOLID_FOREGROUND ,fillForegroundColor = 10)
    @ContentFontStyle(fontHeightInPoints = 30,italic = true,color = Font.COLOR_RED,bold = true)*/
    @ContentLoopMerge(eachRow = 1,columnExtend = 5)
    @DateTimeFormat("yyyy-MM-dd")
    private Date birthday;
}