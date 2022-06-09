package com.kuang.entity;


import lombok.*;

import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class Student2 {



    private String id;

    private String name;

    private String gender;

    private String errorMsg;

    private String birthday;
}
