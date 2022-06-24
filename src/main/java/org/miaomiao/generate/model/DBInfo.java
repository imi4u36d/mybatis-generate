package org.miaomiao.generate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DBInfo {

    private String url;

    private String userName;

    private String pwd;

}
