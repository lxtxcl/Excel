package com.kuang.entity;

import lombok.*;

import java.util.Map;
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class Import_ErrorMsg<T> {

   T data;
   Map<String,String> exceptionMessages;


}
