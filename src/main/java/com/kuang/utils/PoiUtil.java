package com.kuang.utils;


import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.apache.poi.ss.usermodel.CellType.STRING;

@Component
public class PoiUtil {


    //校验文件是否合法
    public static void checkFile(MultipartFile file) throws IOException{
        //判断文件是否存在
        if(null == file){
            throw new FileNotFoundException("文件不存在！");
        }
        //获得文件名
        String fileName = file.getOriginalFilename();
        //判断文件是否是excel文件
        if(!fileName.endsWith("xls") && !fileName.endsWith("xlsx")){
            throw new IOException(fileName + "不是excel文件");
        }
    }

    //MultipartFile file
    public static Workbook getWorkBook(String fileName,InputStream is) {
        //获得文件名
        //String fileName = file.getOriginalFilename();
        //创建Workbook工作薄对象，表示整个excel
        Workbook workbook = null;
        try {
            //获取excel文件的io流
            //InputStream is = file.getInputStream();

            //根据文件后缀名不同(xls和xlsx)获得不同的Workbook实现类对象
            if(fileName.endsWith("xls")){
                //2003
                workbook = new HSSFWorkbook(is);
            }else if(fileName.endsWith("xlsx")){
                //2007
                workbook = new XSSFWorkbook(is);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return workbook;
    }

    public static String getCellValue(Cell cell){
        String cellValue = "";
        if (cell != null) {
            CellType cellType=cell.getCellTypeEnum();
            switch (cellType) {
                case STRING://字符串
                    //System.out.print("【STRING】");
                    cellValue = cell.getStringCellValue();
                    break;

                case BOOLEAN://布尔
                    //System.out.print("【BOOLEAN】");
                    cellValue = String.valueOf(cell.getBooleanCellValue());
                    break;

                case BLANK://空
                    //System.out.print("【BLANK】");
                    break;

                case NUMERIC:
                    //System.out.print("【NUMERIC】");
                    //cellValue = String.valueOf(cell.getNumericCellValue());

                    if (DateUtil.isCellDateFormatted(cell)) {//日期
                        //System.out.print("【日期】");
                        Date date = cell.getDateCellValue();
                        cellValue = new DateTime(date).toString("yyyy-MM-dd");
                    } else {
                        // 不是日期格式，则防止当数字过长时以科学计数法显示
                        //System.out.print("【转换成字符串】");
                        cell.setCellType(STRING);
                        cellValue = cell.toString();
                    }
                    break;

                case ERROR:
                    //System.out.print("【数据类型错误】");
                    break;
            }

            //System.out.println(cellValue);
        }
        return cellValue;
    }


    public static void write(Workbook workbook ,HttpServletResponse response,String fileName) throws IOException {

        //String fileName = "人员管理表";
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        workbook.write(os);
        //创建一个新分配的字节数组
        byte[] content = os.toByteArray();
        InputStream is = new ByteArrayInputStream(content);
        //设置页面不缓存
        /**response.reset();*/
        // 设置response参数，可以打开下载页面


        //文件类型为xlsx,编码为utf-8
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        //设置以下载方式打开文件
        response.setHeader("Content-Disposition", "attachment;filename="
                + new String((fileName + ".xlsx").getBytes(), "iso-8859-1"));
        //发送文件
        ServletOutputStream out = response.getOutputStream();

        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        try {
            //新
            bis = new BufferedInputStream(is);
            //旧
            bos = new BufferedOutputStream(out);
            byte[] buff = new byte[2048];
            int bytesRead;
            // 简单的读，循环方法，从输入流读取一些字节数，并将它们存储到缓冲区b
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(bis != null) {
                bis.close();
            }
            if (bos != null) {
                bos.close();
            }
        }


    }
}
