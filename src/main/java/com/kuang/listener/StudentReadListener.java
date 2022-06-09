package com.kuang.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.kuang.entity.Student;
import javafx.scene.input.DataFormat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class StudentReadListener extends AnalysisEventListener<Student> {
    List<Student> list;
    // 每读一样，会调用该invoke方法一次

    public void remove(){
        list=new ArrayList<>();
    }

    @Override
    public void invoke(Student data, AnalysisContext context) {
        System.out.println("data = " + data);

        list.add(data);
    }

    // 全部读完之后，会调用该方法
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // TODO......
    }

    public StudentReadListener() {
        this.list = new ArrayList<>();
    }

    public List<Student> getList(){
        return list;
    }

}