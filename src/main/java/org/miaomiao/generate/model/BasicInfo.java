package org.miaomiao.generate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class BasicInfo {
    /**
     * 实体(驼峰大写开头)
     */
    private String entityName;

    /**
     * 数据库表名称
     */
    private String tableName;

    /**
     * 数据库表注释
     */
    private String tableComment;

    /**
     * 当前时间
     */
    private String curTime = nowTime();

    /**
     * 需要导入的包名
     */
    private List<String> importPackages;

    /**
     * 基础包地址
     */
    String packageUrl = "com.miaomiao";

    /**
     * dto文件包地址
     */
    String dtoUrl = "com.miaomiao";

    /**
     * entity文件包地址
     */
    String entityUrl = "com.miaomiao.entity";

    /**
     * service文件包地址
     */
    String serviceUrl = "com.miaomiao.service";

    /**
     * impl文件包地址
     */
    String implUrl = "com.miaomiao.service.impl";

    /**
     * mapper文件包地址
     */
    String mapperUrl = "com.miaomiao.mapper";

    /**
     * util文件包地址
     */
    String utilUrl = "com.miaomiao.util";

    /**
     * 说明
     */
    String illustrate;

    /**
     * 作者
     */
    String author;

    /**
     * 实体(驼峰首字母小写)
     */
    String entityStartByLowCase;

    /**
     * 字段信息
     */
    List<ColumnInfo> columnInfos;

    /**
     * 是否开启swagger支持
     */
    private Boolean swaggerEnable;

    private String nowTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }
}
