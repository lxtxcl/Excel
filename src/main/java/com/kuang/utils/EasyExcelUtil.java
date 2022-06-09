package com.kuang.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.event.AnalysisEventListener;

import com.alibaba.fastjson.JSON;
import com.kuang.entity.Student;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.*;

public class EasyExcelUtil {

    public static void excelHelper(
            HttpServletResponse response,
            String fileName,
            String sheetName,
            List<?> data,
            String excludeTableName,
            Class<?> dataClass) {
        try {
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");
            response.setCharacterEncoding("utf-8");
            String name = URLEncoder.encode(fileName, "UTF-8");
            response.setHeader("Content-disposition", "attachment;filename=" + name + ".xlsx");
            // 根据用户传入字段
            // 去除不显示的列
            List<String> excludeTableNames = Arrays.asList(excludeTableName.split(","));
            Set<String> excludeColumnFiledNames = new HashSet<>(excludeTableNames);
            EasyExcel.write(response.getOutputStream(), dataClass)
                    .excludeColumnFiledNames(excludeColumnFiledNames)
                    .sheet(sheetName)
                    //.registerWriteHandler(new CustomHandler())
                    .doWrite(data);
        } catch (Exception e) {
            /*log.error("数据导出异常:", e);*/
            // 重置response
            response.reset();
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            PrintWriter writer = null;
            try {
                writer = response.getWriter();
                response.setStatus(HttpServletResponse.SC_OK);
                /*ResponseData resultData =
                        ResponseData.error(
                                "500", translateService.getLanguageByResource(MultiLanguage.ERROR_IN_EXPORT_FILE));
                writer.print(JSON.toJSONString(resultData));*/
            } catch (IOException ex) {
                /*log.error("returnJsonResponse has exception", ex);*/
            } finally {
                if (writer != null) {
                    writer.flush();
                    writer.close();
                }
            }
        }
    }
}
