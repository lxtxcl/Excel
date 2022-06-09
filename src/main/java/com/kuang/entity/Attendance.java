package com.kuang.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelIgnore;
import cn.afterturn.easypoi.handler.inter.IExcelDataModel;
import cn.afterturn.easypoi.handler.inter.IExcelModel;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class Attendance implements IExcelDataModel, IExcelModel {


    @Excel(name = "工号",orderNum = "0")
    @NotBlank(message = "工号不能为空")
    private String userId;

    @Excel(name = "姓名",orderNum = "1")
    private String userName;

    @Excel(name = "请假类型",orderNum = "2")
    @NotBlank(message = "请假类别不能为空")
    private String leaveType;

    @Excel(name = "请假模式",orderNum = "3",replace = {"全天_1","上半个班次_2","下半个班次_3","弹性_4"})
    @NotBlank(message = "请假模式不能为空")
    private String leaveMode;

    @Excel(name = "请假日期",orderNum = "4")
    @NotBlank(message = "请假日期不能为空")
    @Pattern(regexp = "^[1-9]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])$",message = "请假日期格式错误")
    private String leaveDate;

    @Excel(name = "开始时间",orderNum = "5")
    @Pattern(regexp = "^[1-9]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])\\s(20|21|22|23|[0-1]\\d):[0-5]\\d$",message = "开始时间日期格式错误")
    private String startDate;

    @Excel(name = "结束时间",orderNum = "6")
    @Pattern(regexp = "^[1-9]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])\\s(20|21|22|23|[0-1]\\d):[0-5]\\d$",message = "结束时间日期格式错误")
    private String endDate;

    @Excel(name = "请假理由",orderNum = "7",replace = {"其他_1","个人事务_2","探亲访友_3"})
    private String leaveReason;


    @Excel(name = "详细",orderNum = "8")
    private String details;


    private String errorMsg;
    /**
     * 行号
     */
    private int rowNum;

    @Override
    public int getRowNum() {
        return rowNum;
    }

    @Override
    public void setRowNum(int rowNum) {
        this.rowNum=rowNum;
    }
}
