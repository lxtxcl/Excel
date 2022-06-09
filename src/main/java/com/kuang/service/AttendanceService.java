package com.kuang.service;

import com.kuang.entity.Attendance;
import com.kuang.entity.Employee;
import com.kuang.entity.LeaveType;
import com.kuang.utils.DateUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class AttendanceService {

    /*
     * 返回员工列表
     * */
    public List<Employee> getEmployees() {
        List<Employee> list = new ArrayList<>();
        list.add(Employee.builder().id("1").name("李一").build());
        list.add(Employee.builder().id("2").name("李二").build());
        list.add(Employee.builder().id("3").name("李三").build());
        list.add(Employee.builder().id("4").name("李四").build());
        list.add(Employee.builder().id("5").name("李五").build());
        list.add(Employee.builder().id("6").name("李六").build());
        list.add(Employee.builder().id("7").name("李七").build());
        list.add(Employee.builder().id("8").name("李八").build());
        return list;
    }

    public Map<String, Employee> getEmployeeMap() {
        List<Employee> list = getEmployees();
        Map<String, Employee> map = new HashMap<>();
        list.forEach(i -> {
            map.put(i.getId(), i);
        });
        return map;
    }

    /*
     * 返回请假类型列表
     * */
    public List<LeaveType> getLeaveTypes() {
        List<LeaveType> list = new ArrayList<>();
        list.add(LeaveType.builder().id("J01").name("法定年假").build());
        list.add(LeaveType.builder().id("A24").name("奖励假").build());
        list.add(LeaveType.builder().id("J02").name("福利年假").build());
        list.add(LeaveType.builder().id("C01").name("事假").build());
        list.add(LeaveType.builder().id("A02").name("倒休假").build());
        list.add(LeaveType.builder().id("A03").name("婚假").build());
        list.add(LeaveType.builder().id("A06").name("丧家").build());
        list.add(LeaveType.builder().id("A07").name("产假").build());
        list.add(LeaveType.builder().id("A08").name("看护假").build());
        list.add(LeaveType.builder().id("A09").name("哺乳假").build());
        list.add(LeaveType.builder().id("A10").name("工伤假").build());
        list.add(LeaveType.builder().id("B01").name("病假").build());
        list.add(LeaveType.builder().id("A11").name("公假").build());
        list.add(LeaveType.builder().id("A13").name("因公出差").build());
        list.add(LeaveType.builder().id("L01").name("N出").build());
        list.add(LeaveType.builder().id("L02").name("N培训").build());
        return list;
    }

    public Map<String, LeaveType> getLeaveTypeMap() {
        List<LeaveType> list = getLeaveTypes();
        Map<String, LeaveType> map = new HashMap<>();
        list.forEach(i -> {
            map.put(i.getName(), i);
        });
        return map;
    }

    public boolean isEmpty(String s) {
        if (s == null || s.isEmpty()) {
            return true;
        }
        return false;
    }

    public void setErrorMsg(String msg, Attendance attendance) {
        if (isEmpty(attendance.getErrorMsg())) {
            attendance.setErrorMsg(msg);
        } else {
            attendance.setErrorMsg(attendance.getErrorMsg() + ";" + msg);
        }
    }

    public void checkImportInfo(List<Attendance> list) {
        //checkAttendanceInfoEmpty(list);
        checkId(list);
        checkLeaveType(list);
        checkLeaveMode(list);
        checkLeaveReason(list);
    }

    private void checkLeaveReason(List<Attendance> list) {

        list.forEach(i -> {
            if (!isEmpty(i.getLeaveReason())) {
                if (!i.getLeaveReason().equals("1") && !i.getLeaveReason().equals("2") && !i.getLeaveReason().equals("3")) {
                    setErrorMsg("请假理由错误", i);
                }
            }
        });

    }

    private void checkLeaveMode(List<Attendance> list) {
        list.forEach(i -> {
            if (!i.getLeaveMode().equals("1") && !i.getLeaveMode().equals("2") && !i.getLeaveMode().equals("3") && !i.getLeaveMode().equals("4")) {
                setErrorMsg("请假模式错误", i);
            } else {
               // try {
                    Date date = DateUtils.parseDate(i.getLeaveDate());

                    Date startDate = null;
                   // try {
                        Date start = null;

                        if (!isEmpty(i.getStartDate())) {
                            startDate = DateUtils.parse(i.getStartDate(), "yyyy-MM-dd HH:mm");
                            if (!org.apache.commons.lang3.time.DateUtils.isSameDay(date, startDate)) {
                                setErrorMsg("开始时间与请假日期不符", i);
                            }
                        } else {
                            switch (i.getLeaveMode()) {
                                case "1":
                                case "2":
                                case "4":
                                    //start=LocalDateTime.of(date, LocalTime.of(9,0));
                                    start = DateUtils.plus(date, 6, ChronoUnit.HOURS);
                                    break;
                                case "3":
                                    //start=LocalDateTime.of(date, LocalTime.of(13,0));
                                    start = DateUtils.plus(date, 13, ChronoUnit.HOURS);
                                    break;
                            }
                            i.setStartDate(DateUtils.format(start, "yyyy-MM-dd HH:mm"));
                        }
                    //   } catch (Exception e) {
                    //      setErrorMsg("开始时间日期格式有误", i);
                   // }

                    //try {
                        Date end = null;
                        if (!isEmpty(i.getEndDate())) {
                            Date endDate = DateUtils.parse(i.getEndDate(), "yyyy-MM-dd HH:mm");
                            if (!org.apache.commons.lang3.time.DateUtils.isSameDay(date, endDate)) {
                                setErrorMsg("结束时间与请假日期不符", i);
                            } else {
                                if (startDate != null) {
                                    if (startDate.after(endDate)) {
                                        setErrorMsg("结束时间早于开始时间", i);
                                    }
                                }
                            }
                        } else {
                            switch (i.getLeaveMode()) {
                                case "1":
                                case "3":
                                case "4":
                                    //end=LocalDateTime.of(date, LocalTime.of(18,0));
                                    end = DateUtils.plus(date, 18, ChronoUnit.HOURS);
                                    break;
                                case "2":
                                    //end=LocalDateTime.of(date, LocalTime.of(12,0));
                                    end = DateUtils.plus(date, 12, ChronoUnit.HOURS);
                                    break;
                            }
                            i.setEndDate(DateUtils.format(end, "yyyy-MM-dd HH:mm"));
                        }
                    //} catch (Exception e) {
                    //    setErrorMsg("结束时间日期格式有误", i);
                    //}
                //} catch (Exception e) {
                //    setErrorMsg("请假日期有误", i);
                //}


            }

        });

    }

    /*
     *校验请假类型
     * */
    private void checkLeaveType(List<Attendance> list) {
        Map<String, LeaveType> map = getLeaveTypeMap();
        list.forEach(i -> {
            if (!map.containsKey(i.getLeaveType())) {
                setErrorMsg("请假类型错误", i);

            }
        });
    }

    /*
     * 校验必填数据是否为空
     * */
    public void checkAttendanceInfoEmpty(List<Attendance> list) {
        list.forEach(i -> {
            if (isEmpty(i.getUserId()) || isEmpty(i.getLeaveType()) || isEmpty(i.getLeaveMode()) || isEmpty(i.getLeaveDate())) {
                setErrorMsg("必填项存在空", i);
            }
        });
    }

    /*
     * 校验员工工号是否有效
     * */
    public void checkId(List<Attendance> list) {
        Map<String, Employee> employees = getEmployeeMap();
        list.forEach(i -> {
            if (employees.containsKey(i.getUserId())) {
                if (!isEmpty(i.getUserName())) {
                    if (!i.getUserName().equals(employees.get(i.getUserId()).getName())) {
                        setErrorMsg("员工工号与姓名不符", i);
                    }
                } else {
                    i.setUserName(employees.get(i.getUserId()).getName());
                }
            } else {
                setErrorMsg("员工工号不存在", i);
            }
        });
    }

}
