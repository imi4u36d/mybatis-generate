package org.miaomiao.generate;

import org.miaomiao.generate.config.DBConfiguration;
import org.miaomiao.generate.model.BasicInfo;
import org.miaomiao.generate.model.FileType;
import org.miaomiao.generate.util.DBUtils;
import org.miaomiao.generate.util.FreemarkerUtils;

import java.util.Arrays;
import java.util.Map;

public class ApplicationStarter {


    public static void main(String[] args) {

        //设置数据库相关信息
        DBConfiguration dbConfiguration = new DBConfiguration();
        dbConfiguration.setUrl("jdbc:mysql://localhost:3306/miaomiao?serverTimezone=UTC");
        dbConfiguration.setUsername("test");
        dbConfiguration.setPwd("test");
        //设置你需要生成CRUD的表的名称
        dbConfiguration.setTableNames(Arrays.asList("m_article"));

        //是否开启swagger注解
        final Boolean swaggerEnable = true;

        //设置文件保存位置
        String savePath = "/Users/wangzhuo/Downloads";
        //作者信息
        final String author = "Wangzhuo";
        //基础包地址
        String packageUrl = "com.miaomiao.miaomiaoservice";
        //dto文件包地址
        String dtoUrl = packageUrl + ".dto";
        //entity文件包地址
        String entityUrl = packageUrl + ".domain";
        //service文件包地址
        String serviceUrl = packageUrl + ".service";
        //impl文件包地址
        String implUrl = serviceUrl + ".impl";
        //mapper文件包地址
        String mapperUrl = packageUrl + ".mapper";
        //util文件包地址
        String utilUrl = packageUrl + ".utils";

        BasicInfo.BasicInfoBuilder builder = new BasicInfo().toBuilder();
        BasicInfo basicInfo = builder.author(author).packageUrl(packageUrl).dtoUrl(dtoUrl).entityUrl(entityUrl).serviceUrl(serviceUrl)
                .implUrl(implUrl).mapperUrl(mapperUrl).utilUrl(utilUrl).swaggerEnable(swaggerEnable)
                .build();

        //配置数据库配置
        configDB(dbConfiguration);
        //扫描并注入表信息
        Map<String, BasicInfo> tableInfoList = scanTableInfo(basicInfo);

        //全部生成
        Arrays.stream(FileType.values()).forEach(fileType -> {
            tableInfoList.forEach((tableName, info) -> FreemarkerUtils.ftlToFile(info, fileType, savePath));
        });

        //关闭连接-结束程序
        DBUtils.closeConnection();

    }

    private static void configDB(DBConfiguration dbConfiguration) {
        DBUtils.dbConfiguration = dbConfiguration;
    }

    /**
     * 注入部分基本信息之后开始对表进行扫描装配表详细信息
     *
     * @param basicInfo
     * @return 表详细信息
     */
    private static Map<String, BasicInfo> scanTableInfo(BasicInfo basicInfo) {
        DBUtils.scanInfoToModel(basicInfo);
        //获取所有的tableInfo
        return DBUtils.getTableInfoMap();
    }
}
