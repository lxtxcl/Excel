package com.kuang;

import com.alibaba.excel.EasyExcel;
import com.kuang.entity.DemoData;
import com.kuang.entity.Student;
import com.kuang.listener.StudentReadListener;
import com.kuang.utils.EasyExcelUtil;
import org.junit.Test;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

public class EasyExcelTest {

    String path = "D:\\java\\gitlab\\ExcelTest\\";

    private List<DemoData> data() {
        List<DemoData> list = new ArrayList<DemoData>();
        for (int i = 0; i < 10; i++) {
            DemoData data = new DemoData();
            data.setName("字符串" + i);
            data.setDate(new Date());
            data.setDoubleData(0.56);
            list.add(data);
        }
        return list;
    }



    // 最简单的写
    @Test
    public void simpleWrite() {
        // 写法1
        String fileName = path + "EasyExcel.xlsx";
        // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
        // 如果这里想使用03 则 传入excelType参数即可
        List<Student> list=new ArrayList<>();
        list.add(new Student("1","xiaozhang","男","", new Date()));
        list.add(new Student("2","abc","nan","", new Date()));
        list.add(new Student("3","xiaozhang3","女","", new Date()));
        list.add(new Student("4","xiaozhang4","nan","", new Date()));
        EasyExcel.write(fileName, Student.class).sheet("模板").doWrite(list);
    }

    // 最简单的读
    @Test
    public void simpleRead() {
        String fileName = path + "EasyExcel.xlsx";
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
        StudentReadListener studentReadListener = new StudentReadListener();
        EasyExcel.read(fileName, Student.class, studentReadListener).sheet().doRead();
        List<Student> list=studentReadListener.getList();
        list.forEach(l->check(l));
        System.out.println(list);
    }
    static void check(Student s){
        String msg="";
        if(s.getName().length()>4){
            msg+="名字过长 ";
        }
        if(!s.getGender().equals("男")&&!s.getGender().equals("女")){
            msg+="性别填写错误 ";
        }
        s.setErrorMsg(msg);
    }
}