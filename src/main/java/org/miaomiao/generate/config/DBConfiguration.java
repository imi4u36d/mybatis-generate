package org.miaomiao.generate.config;

import lombok.Data;

import java.util.List;

@Data
public class DBConfiguration {

    public String url;

    public String username;

    public String pwd;

    public List<String> tableNames;
}
