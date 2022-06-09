package com.kuang.utils;

import cn.afterturn.easypoi.cache.manager.POICacheManager;
import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.ExcelXorHtmlUtil;
import cn.afterturn.easypoi.excel.entity.ExcelToHtmlParams;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.afterturn.easypoi.excel.entity.result.ExcelImportResult;
import cn.afterturn.easypoi.word.WordExportUtil;
import cn.afterturn.easypoi.word.parse.ParseWord07;
import com.kuang.entity.Attendance;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.apache.poi.xssf.usermodel.XSSFDataValidationConstraint;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Excel导入导出工具类
 */
@Component
public class ExcelUtils {

    /**
     * excel 导出
     *
     * @param list     数据列表
     * @param fileName 导出时的excel名称
     * @param response
     */
    public static void exportExcel(List<Map<String, Object>> list, String fileName, HttpServletResponse response) throws IOException {
        defaultExport(list, fileName, response);
    }

    /**
     * 默认的 excel 导出
     *
     * @param list     数据列表
     * @param fileName 导出时的excel名称
     * @param response
     */
    private static void defaultExport(List<Map<String, Object>> list, String fileName, HttpServletResponse response) throws IOException {
        //把数据添加到excel表格中
        Workbook workbook = ExcelExportUtil.exportExcel(list, ExcelType.HSSF);
        downLoadExcel(fileName, response, workbook);
    }

    /**
     * excel 导出
     *
     * @param list         数据列表
     * @param pojoClass    pojo类型
     * @param fileName     导出时的excel名称
     * @param response
     * @param exportParams 导出参数（标题、sheet名称、是否创建表头，表格类型）
     */
    private static void defaultExport(List<?> list, Class<?> pojoClass, String fileName, HttpServletResponse response, ExportParams exportParams) throws IOException {
        //把数据添加到excel表格中
        Workbook workbook = ExcelExportUtil.exportExcel(exportParams, pojoClass, list);
        downLoadExcel(fileName, response, workbook);
    }

    /**
     * excel 导出
     *
     * @param list         数据列表
     * @param pojoClass    pojo类型
     * @param fileName     导出时的excel名称
     * @param exportParams 导出参数（标题、sheet名称、是否创建表头，表格类型）
     * @param response
     */
    public static void exportExcel(List<?> list, Class<?> pojoClass, String fileName, ExportParams exportParams, HttpServletResponse response) throws IOException {
        defaultExport(list, pojoClass, fileName, response, exportParams);
    }

    /**
     * excel 导出
     *
     * @param list      数据列表
     * @param title     表格内数据标题
     * @param sheetName sheet名称
     * @param pojoClass pojo类型
     * @param fileName  导出时的excel名称
     * @param response
     */
    public static void exportExcel(List<?> list, String title, String sheetName, Class<?> pojoClass, String fileName, HttpServletResponse response) throws IOException {
        defaultExport(list, pojoClass, fileName, response, new ExportParams(title, sheetName, ExcelType.XSSF));
    }



    /**
     * excel 导出
     *
     * @param list           数据列表
     * @param title          表格内数据标题
     * @param sheetName      sheet名称
     * @param pojoClass      pojo类型
     * @param fileName       导出时的excel名称
     * @param isCreateHeader 是否创建表头
     * @param response
     */
    public static void exportExcel(List<?> list, String title, String sheetName, Class<?> pojoClass, String fileName, boolean isCreateHeader, HttpServletResponse response) throws IOException {
        ExportParams exportParams = new ExportParams(title, sheetName, ExcelType.XSSF);
        exportParams.setCreateHeadRows(isCreateHeader);
        defaultExport(list, pojoClass, fileName, response, exportParams);
    }

    /**
     * firstRow 开始行号(下标0开始)
     * lastRow  结束行号，最大65535
     * firstCol 区域中第一个单元格的列号 (下标0开始)
     * lastCol 区域中最后一个单元格的列号
     * dataArray 下拉内容
     * sheetHidden 影藏的sheet编号（例如1,2,3），多个下拉数据不能使用同一个
     * */
    public static void selectList(Workbook workbook,int firstCol,int lastCol,String[] strings ){
        Sheet sheet = workbook.getSheetAt(0);
        //生成下拉列表
        CellRangeAddressList cellRangeAddressList = new CellRangeAddressList(2, 90000, firstCol, lastCol);
        //生成下拉框内容
        DataValidationHelper dvHelper = sheet.getDataValidationHelper();
        DataValidationConstraint dvConstraint = dvHelper.createExplicitListConstraint(strings);
        DataValidation validation = dvHelper.createValidation(dvConstraint, cellRangeAddressList);
        //设置错误信息提示
        validation.setShowErrorBox(true);
        //对sheet页生效
        sheet.addValidationData(validation);

    }

    /**
     * firstRow 开始行号(下标0开始)
     * lastRow  结束行号，最大65535
     * firstCol 区域中第一个单元格的列号 (下标0开始)
     * lastCol 区域中最后一个单元格的列号
     * dataArray 下拉内容
     * sheetHidden 影藏的sheet编号（例如1,2,3），多个下拉数据不能使用同一个
     * */
    public static void selectListNoTile(Workbook workbook,int firstCol,int lastCol,String[] strings,int sheetNum){
        Sheet sheet = workbook.getSheetAt(sheetNum);
        //生成下拉列表
        CellRangeAddressList cellRangeAddressList = new CellRangeAddressList(1, 65535, firstCol, lastCol);
        //生成下拉框内容
        DataValidationHelper dvHelper = sheet.getDataValidationHelper();
        DataValidationConstraint dvConstraint = dvHelper.createExplicitListConstraint(strings);
        DataValidation validation = dvHelper.createValidation(dvConstraint, cellRangeAddressList);
        //设置错误信息提示
        validation.setShowErrorBox(false);
        //对sheet页生效
        sheet.addValidationData(validation);

    }
    /**
     * excel下载
     *
     * @param fileName 下载时的文件名称
     * @param response
     * @param workbook excel数据
     */
    public static void downLoadExcel(String fileName, HttpServletResponse response, Workbook workbook) throws IOException {
        try {
           /* response.setCharacterEncoding("UTF-8");
            response.setHeader("content-Type", "application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName + ".xlsx", "UTF-8"));*/
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            if(workbook.getClass().getName().equals(HSSFWorkbook.class.getName()) ){
                response.setHeader("Content-disposition", "attachment;"+"filename*=utf-8''"+fileName+ ".xls");
            }else if(workbook.getClass().getName().equals(XSSFWorkbook.class.getName())){
                response.setHeader("Content-disposition", "attachment;"+"filename*=utf-8''"+fileName+ ".xlsx");
            }

            response.setContentType("application/msexcel");
            response.setHeader("X-Frame-Options", "SAMEORIGIN");
            //response.setCharacterEncoding("UTF-8");
            workbook.write(response.getOutputStream());
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }
    /**
     * 本地excel模板下载
     *
     * @param name 下载时的文件路径以及文件名称
     * @param fileName 生成的文件名
     * @param response
     */
    public static void downLoadExcelModel(String name, HttpServletResponse response,String fileName) throws IOException {

        //文件所在目录路径
        URL url = Thread.currentThread().getContextClassLoader().getResource(name);
        String filePath=url.getPath();
        //得到该文件
        File file = new File(filePath);

        FileInputStream fileInputStream = new FileInputStream(file);
        //设置Http响应头告诉浏览器下载这个附件,下载的文件名也是在这里设置的
        //response.setHeader("Content-Disposition", "attachment;Filename=" + URLEncoder.encode(advertis, "UTF-8"));
        response.setHeader("Content-disposition", "attachment;"+"filename*=utf-8''"+ fileName );
       // response.setContentType("application/msexcel");
       // response.setHeader("X-Frame-Options", "SAMEORIGIN");
        OutputStream outputStream = response.getOutputStream();
        byte[] bytes = new byte[2048];
        int len = 0;
        while ((len = fileInputStream.read(bytes))>0){
            outputStream.write(bytes,0,len);
        }
        fileInputStream.close();
        outputStream.close();
    }
    /**
     * 设置导出文件名称
     * @param fileName
     * @return
     */
    public static String processFileName(String fileName){
        try {
                fileName = URLEncoder.encode(fileName,"UTF8");//其他浏览器
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return fileName;
    }

    /**
     * excel 导入
     *
     * @param file      excel文件
     * @param pojoClass pojo类型
     * @param <T>
     * @return
     */
    public static <T> List<T> importExcel(MultipartFile file, Class<T> pojoClass) throws IOException {
        return importExcel(file, 1, 1, pojoClass);
    }
    /**
     * excel 导入
     *
     * @param file      excel文件
     * @param pojoClass pojo类型
     * @param <T>
     * @return
     */
    public static <T> List<T> importExcelNoTitile(MultipartFile file, Class<T> pojoClass) throws IOException {
        return importExcel(file, 0, 1, pojoClass);
    }
    /**
     * excel 导入
     *
     * @param filePath   excel文件路径
     * @param titleRows  表格内数据标题行
     * @param headerRows 表头行
     * @param pojoClass  pojo类型
     * @param <T>
     * @return
     */
    public static <T> List<T> importExcel(String filePath, Integer titleRows, Integer headerRows, Class<T> pojoClass) throws IOException {
        if (StringUtils.isBlank(filePath)) {
            return null;
        }
        ImportParams params = new ImportParams();
        params.setTitleRows(titleRows);
        params.setHeadRows(headerRows);
        params.setNeedSave(true);
        params.setSaveUrl("/excel/");
        try {
            return ExcelImportUtil.importExcel(new File(filePath), pojoClass, params);
        } catch (NoSuchElementException e) {
            throw new IOException("模板不能为空");
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }


    /**
     * excel 导入
     *
     * @param file       上传的文件
     * @param titleRows  表格内数据标题行
     * @param headerRows 表头行
     * @param pojoClass  pojo类型
     * @param <T>
     * @return
     */
    public static <T> List<T> importExcel(MultipartFile file, Integer titleRows, Integer headerRows, Class<T> pojoClass) throws IOException {
        if (file == null) {
            return null;
        }
        try {
            return importExcel(file.getInputStream(), titleRows, headerRows, pojoClass);
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    /**
     * excel 导入
     *
     * @param inputStream 文件输入流
     * @param titleRows   表格内数据标题行
     * @param headerRows  表头行
     * @param pojoClass   pojo类型
     * @param <T>
     * @return
     */
    public static <T> List<T> importExcel(InputStream inputStream, Integer titleRows, Integer headerRows, Class<T> pojoClass) throws IOException {
        if (inputStream == null) {
            return null;
        }
        ImportParams params = new ImportParams();
        params.setTitleRows(titleRows);
        params.setHeadRows(headerRows);
        params.setSaveUrl("/excel/");
        params.setNeedSave(true);
        try {
            return ExcelImportUtil.importExcel(inputStream, pojoClass, params);
        } catch (NoSuchElementException e) {
            throw new IOException("excel文件不能为空");
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    /**
     * 导入时开启校验
     * @param headRows
     * @param TitleRows
     * @param needVerify
     * @param file
     * @return
     * @throws Exception
     */
    public static <T> ExcelImportResult<T> excelImportVerify(int headRows,int TitleRows,boolean needVerify,MultipartFile file, Class<T> pojoClass) throws Exception {
        ImportParams params = new ImportParams();
        // 表头设置为1行
        params.setHeadRows(headRows);
        // 标题行设置为0行，默认是0，可以不设置
        params.setTitleRows(TitleRows);
        // 开启Excel校验
        params.setNeedVerify(needVerify);
        ExcelImportResult<T> result = ExcelImportUtil.importExcelMore(file.getInputStream(),
                pojoClass, params);
        return result;
    }
}
