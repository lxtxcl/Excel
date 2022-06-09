package com.kuang.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.afterturn.easypoi.excel.entity.result.ExcelImportResult;
import com.alibaba.excel.EasyExcel;
import com.kuang.entity.*;
import com.kuang.listener.StudentReadListener;
import com.kuang.service.AttendanceService;
import com.kuang.utils.EasyExcelUtil;
import com.kuang.utils.ExcelUtils;
import com.kuang.utils.PoiUtil;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class TestController {

    @Autowired
    AttendanceService attendanceService;


    @PostMapping("/excel-read")
    @ResponseBody
    public void readExcel(MultipartFile file, int sheetNo) {
        String fileName = file.getOriginalFilename();
        System.out.println(fileName);
        try {
            InputStream is = file.getInputStream();
            Workbook workbook = PoiUtil.getWorkBook(fileName, is);
            /*Workbook workbook = new XSSFWorkbook(is);*/
            Sheet sheet = workbook.getSheetAt(sheetNo);

            // 读取标题所有内容
            Row rowTitle = sheet.getRow(0);

            List<Student2> list = new ArrayList<>();
            // 读取商品列表数据
            int rowCount = sheet.getPhysicalNumberOfRows();
            System.out.println(rowCount);
            for (int rowNum = 2; rowNum < rowCount; rowNum++) {

                Row rowData = sheet.getRow(rowNum);
                if (rowData != null) {// 行不为空
                    Student2 student2 = Student2.builder().id(PoiUtil.getCellValue(rowData.getCell(0)))
                            .name(PoiUtil.getCellValue(rowData.getCell(1)))
                            .gender(PoiUtil.getCellValue(rowData.getCell(2)))
                            .build();
                    list.add(student2);
                }
            }
            System.out.println(list);
            is.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @GetMapping("/excel-write")
    @ResponseBody
    public void writeExcel(HttpServletResponse response) {
        // 创建新的Excel 工作簿, 只有对象变了
        Workbook workbook = new XSSFWorkbook();

        // 如要新建一名为"会员登录统计"的工作表，其语句为：
        Sheet sheet = workbook.createSheet("狂神观众统计表");

        XSSFCellStyle style = (XSSFCellStyle) workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setColor(IndexedColors.BLUE.getIndex());
        style.setFont(font);
        // 创建行（row 1）
        Row row1 = sheet.createRow(0);

        // 创建单元格（col 1-1）
        Cell cell11 = row1.createCell(0);
        cell11.setCellValue("今日新增关注");
        cell11.setCellStyle(style);
        // 创建单元格（col 1-2）
        Cell cell12 = row1.createCell(1);
        cell12.setCellValue(666);

        // 创建行（row 2）
        Row row2 = sheet.createRow(1);

        // 创建单元格（col 2-1）
        Cell cell21 = row2.createCell(0);
        cell21.setCellValue("统计时间");

        //创建单元格（第三列）
        Cell cell22 = row2.createCell(1);
        String dateTime = new DateTime().toString("yyyy-MM-dd HH:mm:ss");
        cell22.setCellValue(dateTime);

        try {
            PoiUtil.write(workbook, response, "test");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/easyExcel-read")
    @ResponseBody
    public void readEasyExcel(MultipartFile file, int sheetNo) {
        String fileName = file.getOriginalFilename();
        System.out.println(fileName);
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
        StudentReadListener studentReadListener = new StudentReadListener();
        EasyExcel.read(fileName, Student.class, studentReadListener).sheet(sheetNo).doRead();
        List<Student> list = studentReadListener.getList();
        List<Student2> rst = new ArrayList<>();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        list.forEach(l -> {
            check(l);
            rst.add(Student2.builder()
                    .id(l.getId())
                    .name(l.getName())
                    .gender(l.getGender())
                    .birthday(df.format(l.getBirthday()))
                    .errorMsg(l.getErrorMsg())
                    .build());
        });

        System.out.println(rst);

    }

    static void check(Student s) {
        String msg = "";
        if (s.getName().length() > 4) {
            msg += "名字过长 ";
        }
        if (!s.getGender().equals("男") && !s.getGender().equals("女")) {
            msg += "性别填写错误 ";
        }
        s.setErrorMsg(msg);
    }

    @GetMapping("/easyExcel-write")
    @ResponseBody
    public void writeEasyExcel(HttpServletResponse response) {
        List<Student> list = new ArrayList<>();
        list.add(new Student("1", "xiaozhang", "男", "", new Date()));
        list.add(new Student("2", "abc", "nan", "", new Date()));
        list.add(new Student("3", "xiaozhang3", "女", "", new Date()));
        list.add(new Student("4", "xiaozhang4", "nan", "", new Date()));
        EasyExcelUtil.excelHelper(response, "easyExcelWtire", "员工列表", list, "", Student.class);
    }

    @PostMapping(value = "/easyPOI-read")
    @ResponseBody
    public void importExcel(MultipartFile file) throws IOException {
        List<User> list = ExcelUtils.importExcel(file, User.class);
        list.forEach(i -> System.out.println(i));

    }

    /**
     * 导出数据，使用map接收
     *
     * @param response
     * @throws IOException
     */
    @PostMapping("/easyPOI-write")
    @ResponseBody
    public void exportExcel(HttpServletResponse response) throws IOException {
        List<User> list = new ArrayList<>();
        list.add(User.builder().age(1).username("1").address("1").sex("1").name("1").list(new ArrayList<User_>() {{
            add(User_.builder().name("asd").age(1).build());
        }}).build());
        list.add(User.builder().age(2).username("2").address("2").sex("2").name("2").list(new ArrayList<User_>() {{
            add(User_.builder().name("asd").age(1).build());
        }}).build());
        ExcelUtils.exportExcel(list, "test", "test", User.class, "easyPOITest", response);
    }

    /**
     * 导出数据，使用map接收
     *
     * @param response
     * @throws IOException
     */
    @PostMapping("/easyPOI-write-attendance")
    @ResponseBody
    public void exportAttendance(HttpServletResponse response) throws IOException {
        List<Attendance> attendances = new ArrayList<>();
        ExportParams exportAttendanceParams = new ExportParams();
        exportAttendanceParams.setSheetName("请假申请导入");
        Map<String, Object> reportAttendanceExportMap = new HashMap<>();
        reportAttendanceExportMap.put("title", exportAttendanceParams);
        reportAttendanceExportMap.put("entity", Attendance.class);
        reportAttendanceExportMap.put("data", attendances);

        List<LeaveType> leaveTypes = attendanceService.getLeaveTypes();
        ExportParams exportLeaveTpyeParams = new ExportParams();
        exportLeaveTpyeParams.setSheetName("请假类型");
        Map<String, Object> reportLeaveTypeExportMap = new HashMap<>();
        reportLeaveTypeExportMap.put("title", exportLeaveTpyeParams);
        reportLeaveTypeExportMap.put("entity", LeaveType.class);
        reportLeaveTypeExportMap.put("data", leaveTypes);

        List<Map<String, Object>> sheetsList = new ArrayList<>();
        sheetsList.add(reportAttendanceExportMap);
        sheetsList.add(reportLeaveTypeExportMap);
        List<Attendance> list = new ArrayList<>();
        Workbook workbook = ExcelExportUtil.exportExcel(sheetsList, ExcelType.HSSF);
        /*String  fileName = URLEncoder.encode("员工报表导出", "UTF-8");
        ExcelUtils.downLoadExcel("easyPOITest-Attendance"+fileName,response,workBook)*/
        ;

        //把数据添加到excel表格中
        //Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(null,"attendance",ExcelType.XSSF), Attendance.class, list);

        Font font = workbook.createFont();
        short index = HSSFColor.HSSFColorPredefined.RED.getIndex();
        font.setColor(index);
        font.setFontName("等线");
        Sheet sheet = workbook.getSheetAt(0);
        Row row = sheet.getRow(0);


        HSSFCellStyle hssfCellStyle = (HSSFCellStyle) workbook.createCellStyle();
        hssfCellStyle.setFont(font);
        hssfCellStyle.setAlignment(HorizontalAlignment.CENTER);
        hssfCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        row.getCell(0).setCellStyle(hssfCellStyle);
        row.getCell(2).setCellStyle(hssfCellStyle);
        row.getCell(3).setCellStyle(hssfCellStyle);
        row.getCell(4).setCellStyle(hssfCellStyle);
        /*short index = HSSFColor.HSSFColorPredefined.RED.getIndex();
        font.setColor(index);
        font.setFontName("等线");
        Sheet sheet = workbook.getSheetAt(0);
        Row row=sheet.getRow(0);


        XSSFCellStyle xssfCellStyle= (XSSFCellStyle) workbook.createCellStyle();
        xssfCellStyle.setFont(font);
        xssfCellStyle.setAlignment(HorizontalAlignment.CENTER);
        xssfCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        row.getCell(0).setCellStyle(xssfCellStyle);
        row.getCell(2).setCellStyle(xssfCellStyle);
        row.getCell(3).setCellStyle(xssfCellStyle);
        row.getCell(4).setCellStyle(xssfCellStyle);*/
        ExcelUtils.selectListNoTile(workbook, 3, 3, new String[]{"全天", "上半个班次", "下半个班次", "弹性"}, 0);
        ExcelUtils.selectListNoTile(workbook, 7, 7, new String[]{"其他", "个人事务", "探亲访友"}, 0);
        String fileName = ExcelUtils.processFileName("-请假表单");
        ExcelUtils.downLoadExcel("easyPOITest-Attendance" + fileName, response, workbook);
    }

    @PostMapping(value = "/easyPOI-read-attendance")
    @ResponseBody
    public void importAttendance(MultipartFile file) throws Exception {


        ExcelImportResult<Attendance> result = ExcelUtils.excelImportVerify(1, 0, true, file, Attendance.class);
/*        System.out.println("是否校验失败: " + result.isVerfiyFail());
        System.out.println("校验失败的集合:" + JSONObject.toJSONString(result.getFailList()));
        System.out.println("校验通过的集合:" + JSONObject.toJSONString(result.getList()));*/
        //返回数据格式错误信息
        if (result.getFailList().size() > 0) {
            System.out.println("格式错误");
            List<Attendance> failList = result.getFailList();
            /*for (Attendance entity : failList) {
                String msg = entity.getErrorMsg();
                System.out.println(entity.getRowNum() + ":" + msg);
            }*/
            List<Import_ErrorMsg<Attendance>> importErrorMsgs = getErrorMsg(failList);
            importErrorMsgs.forEach(importErrorMsg -> {
                System.out.println(importErrorMsg.getData() + ":");
                importErrorMsg.getExceptionMessages().entrySet().forEach(e -> {
                    System.out.println("   " + e.getKey() + ":" + e.getValue());
                });
            });
        } else {
            System.out.println("格式正确");
            List<Attendance> list = result.getList();
            attendanceService.checkImportInfo(list);

            //数据校验发现错误是返回错误信息
            System.out.println("数据异常：");
            List<Attendance> collectError = list.stream()
                    .filter(i -> i.getErrorMsg() != null && !i.getErrorMsg().isEmpty())
                    .collect(Collectors.toList());
            List<Import_ErrorMsg<Attendance>> importErrorMsgs = getErrorMsg(collectError);
            importErrorMsgs.forEach(importErrorMsg -> {
                System.out.println(importErrorMsg.getData() + ":");
                importErrorMsg.getExceptionMessages().entrySet().forEach(e -> {
                    System.out.println("   " + e.getKey() + ":" + e.getValue());
                });
            });

            System.out.println("数据无异常：");
            List<Attendance> collectCheck = list.stream()
                    .filter(i -> i.getErrorMsg() == null || i.getErrorMsg().isEmpty())
                    .collect(Collectors.toList());
            collectCheck.forEach(System.out::println);
        }

        //List<Attendance> list = ExcelUtils.importExcelNoTitile(file, Attendance.class);
        //list.forEach(System.out::println);

    }

    /*提取错误信息放入map*/
    public List<Import_ErrorMsg<Attendance>> getErrorMsg(List<Attendance> collectError) {
        List<Import_ErrorMsg<Attendance>> improtErrorMsgs = new ArrayList<>();
        collectError.forEach(i -> {
            Map<String, String> key_error = new HashMap<>();
            List<String> errors = Arrays.asList(i.getErrorMsg().split(","));
            errors.forEach(e -> {
                putErrorMsg(e, key_error);
            });
            Import_ErrorMsg<Attendance> import_errorMsg = Import_ErrorMsg
                    .<Attendance>builder()
                    .data(i)
                    .exceptionMessages(key_error)
                    .build();
            improtErrorMsgs.add(import_errorMsg);
        });
        return improtErrorMsgs;
    }

    /*错误信息对应字段*/
    public void putErrorMsg(String error, Map<String, String> key_error) {
        switch (error) {
            case "工号不能为空":
            case "员工工号不存在":
                appendErrorMsg(error, "UserId", key_error);
                break;
            case "员工工号与姓名不符":
                appendErrorMsg(error, "UserName", key_error);
            case "请假类别不能为空":
            case "请假类型错误":
                appendErrorMsg(error, "LeaveType", key_error);
                break;
            case "请假模式不能为空":
            case "请假模式错误":
                appendErrorMsg(error, "LeaveMode", key_error);
                break;
            case "请假日期不能为空":
            case "请假日期格式错误":
                appendErrorMsg(error, "LeaveDate", key_error);
                break;

            case "开始时间日期格式错误":
            case "开始时间与请假日期不符":
                appendErrorMsg(error, "StartDate", key_error);
                break;
            case "结束时间日期格式错误":
            case "结束时间与请假日期不符":
            case "结束时间早于开始时间":
                appendErrorMsg(error, "EndDate", key_error);
                break;
            case "请假理由错误":
                appendErrorMsg(error, "LeaveReason", key_error);
                break;
        }
    }

    /*导入校验发现错误后返回map中拼接错误信息*/
    public void appendErrorMsg(String msg, String key, Map<String, String> map) {

        if (map.get(key) != null && !map.get(key).isEmpty()) {
            String error = map.get(key);
            map.put(key, error + ";" + msg);
        } else {
            map.put(key, msg);
        }
    }

    @PostMapping(value = "/easyPOI-write-attendance-model")
    @ResponseBody
    public void importAttendanceModel(HttpServletResponse response) throws Exception {
        String name = "easyPOITest-Attendance.xls";
        //String fileName="easyPOITest-Attendance.xls";
        ExcelUtils.downLoadExcelModel(name, response, name);

    }

}
