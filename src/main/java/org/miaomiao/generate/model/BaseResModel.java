package org.miaomiao.generate.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseResModel {

    private Integer code;

    private String content;

    private Object detail;

}
