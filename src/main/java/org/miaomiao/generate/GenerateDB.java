package org.miaomiao.generate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.miaomiao.generate.utils.underline2Camel;


public class GenerateDB {

    private final static Logger LOGGER = LoggerFactory.getLogger(GenerateDB.class);

    //    private static final String DRIVER = "com.mysql.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/miaomiao?useUnicode=true&characterEncoding=utf8&nullCatalogMeansCurrent=true";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";

    /**
     * 是否启用swagger2支持
     */
    private static final boolean swaggerEnable = true;

    /**
     * 指定表名称
     */

    private static final List<String> TABLENAMES =
            Arrays.asList("test_table");

    /**
     * 你的包名
     */
    private static final String BASEPACKAGE = "com.miaomiao.miaomiaoservice";

    /**
     * 文件生成绝对路径
     */
    private static final String BASEABSOLUTEDIRECTORY = "/Users/wangzhuo/Downloads";

    private static final String BASEABSOLUTEXMLDIRECTORY = BASEABSOLUTEDIRECTORY + "/xml";
    private static final String BASEABSOLUTECONTROLLERDIRECTORY = BASEABSOLUTEDIRECTORY + "/controller";
    private static final String BASEABSOLUTEDOMAINDIRECTORY = BASEABSOLUTEDIRECTORY + "/domain";
    private static final String BASEABSOLUTEDTODIRECTORY = BASEABSOLUTEDIRECTORY + "/dto";

    private static final String BASEABSOLUTEJSONDIRECTORY = BASEABSOLUTEDIRECTORY + "/json";
    private static final String BASEABSOLUTESERVICEDIRECTORY = BASEABSOLUTEDIRECTORY + "/service";
    private static final String BASEABSOLUTESERVICEIMPLDIRECTORY = BASEABSOLUTEDIRECTORY + "/service/impl";
    private static final String BASEABSOLUTEMAPPERDIRECTORY = BASEABSOLUTEDIRECTORY + "/mapper";

    private static final String SQL = "SELECT * FROM ";// 数据库操作

    public static void main(String[] args) {
        createDir();
        createFileByTable();
    }

    private static void createFileByTable() {
        List<String> tableNames = getTableNames();
        tableNames.forEach(tableName -> {
            String tableComment = getCommentByTableName(tableName);
            String domainName = underline2Camel(tableName, true);
            try {
                createJavaDomainFile(tableName, BASEPACKAGE + ".domain", domainName, tableComment);
                createJavaDTOFile(tableName, BASEPACKAGE + ".dto", domainName, tableComment);
                creteJavaJSONFile(domainName, tableName);
                createJavaControllerFile(BASEPACKAGE + ".controller", domainName, tableComment);
                createJavaServiceFile(BASEPACKAGE + ".service", domainName);
                createJavaServiceImplFile(BASEPACKAGE + ".service.impl", domainName);
                createJavaMapperFile(BASEPACKAGE + ".mapper", domainName);
                createSQLXMLFile(domainName, tableName);
            } catch (IOException e) {
                LOGGER.error("generateDB error!");
                e.printStackTrace();
            }
        });
    }

    //TODO 暂只支持请求模型 暂时未支持自动生成分页支持
    private static void createJavaDTOFile(String tableName, String packageURL, String className, String tableComment) throws IOException {
        List<String> columnTypes = getColumnTypes(tableName);
        if (CollectionUtils.isEmpty(columnTypes)) {
            return;
        }
        List<String> comments = getColumnComments(tableName);
        List<String> columnNames = getColumnNames(tableName);

        //创建 .java 文件
        File file = new File(BASEABSOLUTEDTODIRECTORY + File.separator + className + "DTO.java");
        FileWriter fileWriter = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(fileWriter);

        //包路径
        String packageBuilder = "package " +
                packageURL +
                ";" +
                System.getProperty("line.separator") +
                System.getProperty("line.separator");
        bw.write(packageBuilder);

        //引入包名称
        List<ColumnType> columnTypeList = ColumnType.getColumnTypeList();
        StringBuilder importBuilder = new StringBuilder();
        columnTypes.forEach(type -> columnTypeList.forEach(column -> {
            String name = column.name();
            if (type.equalsIgnoreCase(name)) {
                if (StringUtils.hasText(column.getPackageName())) {
                    importBuilder.append("import ");
                    importBuilder.append(column.getPackageName());
                    importBuilder.append(";");
                    importBuilder.append(System.getProperty("line.separator"));
                }
            }
        }));
        importBuilder.append("import lombok.Data;");
        if (swaggerEnable) {
            importBuilder.append(System.getProperty("line.separator"));
            importBuilder.append("import io.swagger.annotations.ApiModel;");
            importBuilder.append("import io.swagger.annotations.ApiModelProperty;");
            importBuilder.append(System.getProperty("line.separator"));

        }
        importBuilder.append(System.getProperty("line.separator"));
        importBuilder.append(System.getProperty("line.separator"));
        bw.write(importBuilder.toString());


        //生成代码
        StringBuilder codeBuilder = new StringBuilder();
        //生成类名 class
        codeBuilder.append("/**");
        codeBuilder.append(System.getProperty("line.separator"));

        //对应实体表名称
        codeBuilder.append("* ");
        codeBuilder.append("classDefined: ").append(tableName).append("DTO请求数据模型");
        codeBuilder.append(System.getProperty("line.separator"));

        //创建时间
        codeBuilder.append("* ");
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String createdTime = dateTimeFormatter.format(now);
        codeBuilder.append("createdTime: ").append(createdTime);
        codeBuilder.append(System.getProperty("line.separator"));

        //创建人
        codeBuilder.append("* ");
        codeBuilder.append("createdBy: Miaowu-Generate");
        codeBuilder.append(System.getProperty("line.separator"));

        codeBuilder.append("**/");
        codeBuilder.append(System.getProperty("line.separator"));

        codeBuilder.append("@Data");
        if (swaggerEnable) {
            codeBuilder.append(System.getProperty("line.separator"));
            codeBuilder.append("@ApiModel(\"");
            codeBuilder.append(tableComment);
            codeBuilder.append("\")");
        }
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append("public class ").append(className).append("DTO").append(" {");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append(System.getProperty("line.separator"));
        for (int i = 0, columnNamesSize = columnNames.size(); i < columnNamesSize; i++) {
            String columnName = columnNames.get(i);
            String columnType = columnTypes.get(i);
            String fieldType = Enum.valueOf(ColumnType.class, columnType.toUpperCase()).getFieldType();
            //注释
            if (StringUtils.hasLength(comments.get(i))) {
                if (swaggerEnable) {
                    codeBuilder.append("    @ApiModelProperty(\"");
                    codeBuilder.append(comments.get(i));
                    codeBuilder.append("\")");
                    codeBuilder.append(System.getProperty("line.separator"));
                } else {
                    codeBuilder.append("    /**");
                    codeBuilder.append(System.getProperty("line.separator"));
                    codeBuilder.append("    * ");
                    codeBuilder.append(comments.get(i));
                    codeBuilder.append(System.getProperty("line.separator"));
                    codeBuilder.append("    **/");
                    codeBuilder.append(System.getProperty("line.separator"));
                }
            }
            //字段
            codeBuilder.append("    ");
            codeBuilder.append("private ");
            codeBuilder.append(fieldType).append(" ");
            codeBuilder.append(underline2Camel(columnName, false)).append(";");
            codeBuilder.append(System.getProperty("line.separator"));
            codeBuilder.append(System.getProperty("line.separator"));
        }
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append("}");
        bw.write(codeBuilder.toString());
        //转文件保存
        bw.close();
    }


    /**
     * 获取数据库连接
     *
     * @return
     */
    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            LOGGER.error("get connection failure", e);
        }
        return conn;
    }

    /**
     * 关闭数据库连接
     *
     * @param conn
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                LOGGER.error("close connection failure", e);
            }
        }
    }

    /**
     * 获得某表的建表语句
     *
     * @param tableName
     * @return 表的注释
     */
    public static String getCommentByTableName(String tableName) {
        Connection conn = getConnection();
        ResultSet rs = null;
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SHOW CREATE TABLE " + tableName);
            if (rs != null && rs.next()) {
                String createDDL = rs.getString(2);
                return parse(createDDL);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                rs.close();
                stmt.close();
                closeConnection(conn);
            } catch (SQLException e) {
                LOGGER.error("close ResultSet failure", e);
            }
        }
        return "";
    }


    /**
     * 返回注释信息
     *
     * @param all
     * @return
     */

    public static String parse(String all) {
        String comment = null;
        int index = all.indexOf("COMMENT='");
        if (index < 0) {
            return "";
        }
        comment = all.substring(index + 9);
        comment = comment.substring(0, comment.length() - 1);
        return comment;
    }

    /**
     * 获取数据库下的所有表名
     */
    public static List<String> getTableNames() {
        List<String> tableNames = new ArrayList<>();
        Connection conn = getConnection();
        ResultSet rs = null;
        try {
            //获取数据库的元数据
            DatabaseMetaData db = conn.getMetaData();
            //从元数据中获取到所有的表名
            rs = db.getTables(null, null, null, null);
            while (rs.next()) {
                tableNames.add(rs.getString(3));
            }
            if (!CollectionUtils.isEmpty(TABLENAMES)) {
                List<String> collect = new ArrayList<>();
                TABLENAMES.forEach(tableName -> {
                    if (tableNames.contains(tableName)) {
                        collect.add(tableName);
                    }
                });
                return collect;
            }
        } catch (SQLException e) {
            LOGGER.error("getTableNames failure", e);
        } finally {
            try {
                rs.close();
                closeConnection(conn);
            } catch (SQLException e) {
                LOGGER.error("close ResultSet failure", e);
            }
        }
        return tableNames;
    }


    /**
     * 获取表中所有字段名称
     *
     * @param tableName 表名
     * @return
     */
    public static List<String> getColumnNames(String tableName) {
        List<String> columnNames = new ArrayList<>();
        //与数据库的连接
        Connection conn = getConnection();
        PreparedStatement pStemt = null;
        String tableSql = SQL + tableName;
        try {
            pStemt = conn.prepareStatement(tableSql);
            //结果集元数据
            ResultSetMetaData rsmd = pStemt.getMetaData();
            //表列数
            int size = rsmd.getColumnCount();
            for (int i = 0; i < size; i++) {
                columnNames.add(rsmd.getColumnName(i + 1));
            }
        } catch (SQLException e) {
            LOGGER.error("getColumnNames failure", e);
        } finally {
            if (pStemt != null) {
                try {
                    pStemt.close();
                    closeConnection(conn);
                } catch (SQLException e) {
                    LOGGER.error("getColumnNames close pstem and connection failure", e);
                }
            }
        }
        return columnNames;
    }

    /**
     * 获取表中所有字段类型
     *
     * @param tableName
     * @return
     */
    public static List<String> getColumnTypes(String tableName) {
        List<String> columnTypes = new ArrayList<>();
        //与数据库的连接
        Connection conn = getConnection();
        PreparedStatement pStemt = null;
        String tableSql = SQL + tableName;
        try {
            pStemt = conn.prepareStatement(tableSql);
            //结果集元数据
            ResultSetMetaData rsmd = pStemt.getMetaData();
            //表列数
            int size = rsmd.getColumnCount();
            for (int i = 0; i < size; i++) {
                columnTypes.add(rsmd.getColumnTypeName(i + 1));
            }
        } catch (SQLException e) {
            LOGGER.error("getColumnTypes failure", e);
        } finally {
            if (pStemt != null) {
                try {
                    pStemt.close();
                    closeConnection(conn);
                } catch (SQLException e) {
                    LOGGER.error("getColumnTypes close pstem and connection failure", e);
                }
            }
        }
        return columnTypes;
    }

    /**
     * 获取表中字段的所有注释
     *
     * @param tableName
     * @return
     */
    public static List<String> getColumnComments(String tableName) {
        //与数据库的连接
        Connection conn = getConnection();
        PreparedStatement pStemt;
        String tableSql = SQL + tableName;
        List<String> columnComments = new ArrayList<>();//列名注释集合
        ResultSet rs = null;
        try {
            pStemt = conn.prepareStatement(tableSql);
            rs = pStemt.executeQuery("show full columns from " + tableName);
            while (rs.next()) {
                columnComments.add(rs.getString("Comment"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                    closeConnection(conn);
                } catch (SQLException e) {
                    LOGGER.error("getColumnComments close ResultSet and connection failure", e);
                }
            }
        }
        return columnComments;
    }

    private static void createDir() {
        File domainDir = new File(BASEABSOLUTEDOMAINDIRECTORY);
        File dtoDir = new File(BASEABSOLUTEDTODIRECTORY);
        File controllerDir = new File(BASEABSOLUTECONTROLLERDIRECTORY);
        File serviceDir = new File(BASEABSOLUTESERVICEDIRECTORY);
        File implDir = new File(BASEABSOLUTESERVICEIMPLDIRECTORY);
        File mapperDir = new File(BASEABSOLUTEMAPPERDIRECTORY);
        File xmlDir = new File(BASEABSOLUTEXMLDIRECTORY);
        File jsonDir = new File(BASEABSOLUTEJSONDIRECTORY);
        List<File> files = Arrays.asList(domainDir, dtoDir, controllerDir, serviceDir, implDir, mapperDir, xmlDir, jsonDir);
        files.forEach(one -> {
            if (!one.exists()) {
                if (one.mkdir()) {
                    LOGGER.info(one.getName() + "文件目录已经生成完毕");
                } else {
                    LOGGER.warn(one.getName() + "文件目录未正常生成请检查");
                }
            } else {
                LOGGER.warn("请先删除" + one.getName() + "目录再执行生成!");
            }
        });
    }

    private static void createSQLXMLFile(String domainName, String tableName) throws IOException {
        String XMLName = domainName + "Mapper";
        String id = domainName + "Result";
        String domainNameStartByLowerCase = underline2Camel(domainName, false);
        String vo = "sel" + domainNameStartByLowerCase + "Vo";
        List<String> columnNames = getColumnNames(tableName);


        File file = new File(BASEABSOLUTEXMLDIRECTORY + File.separator + domainName + "Mapper.xml");
        FileWriter fileWriter = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(fileWriter);
        String baseStartFileBuilder = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\" >" +
                System.getProperty("line.separator");
        bw.write(baseStartFileBuilder);


        StringBuilder nameSpaceBeginTagBuilder = new StringBuilder();
        nameSpaceBeginTagBuilder.append("<mapper namespace=\"" + BASEPACKAGE + ".mapper." + XMLName + "\">\n");
        nameSpaceBeginTagBuilder.append(System.getProperty("line.separator"));
        bw.write(nameSpaceBeginTagBuilder.toString());

        StringBuilder xmlBuilder = new StringBuilder();
        //crud

        xmlBuilder.append("    <resultMap type=\"" + domainName + "\" id=\"" + id + "\">");
        xmlBuilder.append(System.getProperty("line.separator"));
        for (String propertyName : columnNames) {
            String columnName = underline2Camel(propertyName, false);
            //字段
            xmlBuilder.append("        ");
            xmlBuilder.append("<result property=\"").append(columnName).append("\"   column=\"").append(propertyName).append("\" />\n");
        }
        xmlBuilder.append("    </resultMap>");
        xmlBuilder.append(System.getProperty("line.separator"));
        xmlBuilder.append(System.getProperty("line.separator"));


        xmlBuilder.append("    <sql id=\"").append(vo).append("\">\n");
        xmlBuilder.append("        select ");
        StringBuilder columnBuilder = new StringBuilder();
        for (String columnName : columnNames) {
            columnBuilder.append(columnName);
            columnBuilder.append(", ");
        }
        xmlBuilder.append(columnBuilder.substring(0, columnBuilder.length() - 2));
        xmlBuilder.append(" from ").append(tableName);
        xmlBuilder.append(System.getProperty("line.separator"));
        xmlBuilder.append("    </sql>");
        xmlBuilder.append(System.getProperty("line.separator"));
        xmlBuilder.append(System.getProperty("line.separator"));

        xmlBuilder.append("    <select id=\"list\" parameterType=\"").append(domainName).append("\" resultMap=\"").append(id).append("\">\n");
        xmlBuilder.append("        <include refid=\"").append(vo).append("\"/>\n");
        xmlBuilder.append("        <where>\n");
        for (String columnName : columnNames) {
            String testName = underline2Camel(columnName, false);
            xmlBuilder.append("            <if test=\"")
                    .append(testName).append(" != null  and ")
                    .append(testName)
                    .append(" != ''\"> and ")
                    .append(columnName)
                    .append(" = #{")
                    .append(testName)
                    .append("}</if>\n");
        }
        xmlBuilder.append("        </where>\n");
        xmlBuilder.append("    </select>");
        xmlBuilder.append(System.getProperty("line.separator"));
        xmlBuilder.append(System.getProperty("line.separator"));

        xmlBuilder.append("    <select id=\"page\" parameterType=\"").append(domainName).append("\" resultMap=\"").append(id).append("\">\n");
        xmlBuilder.append("        <include refid=\"").append(vo).append("\"/>\n");
        xmlBuilder.append("        <where>\n");
        for (String columnName : columnNames) {
            String testName = underline2Camel(columnName, false);
            xmlBuilder.append("            <if test=\"")
                    .append(domainNameStartByLowerCase + "." + testName).append(" != null  and ")
                    .append(domainNameStartByLowerCase + "." + testName)
                    .append(" != ''\"> and ")
                    .append(columnName)
                    .append(" = #{")
                    .append(domainNameStartByLowerCase + "." + testName)
                    .append("}</if>\n");
        }
        xmlBuilder.append("        </where>\n");
        xmlBuilder.append("        limit #{page},#{pageSize}\n");
        xmlBuilder.append("    </select>");
        xmlBuilder.append(System.getProperty("line.separator"));
        xmlBuilder.append(System.getProperty("line.separator"));

        xmlBuilder.append("    <select id=\"total\" parameterType=\"").append(domainName).append("\" resultType=\"Integer\">\n");
        xmlBuilder.append("        select count(*) from " + tableName + "\n");
        xmlBuilder.append("        <where>\n");
        for (String columnName : columnNames) {
            String testName = underline2Camel(columnName, false);
            xmlBuilder.append("            <if test=\"")
                    .append(testName).append(" != null  and ")
                    .append(testName)
                    .append(" != ''\"> and ")
                    .append(columnName)
                    .append(" = #{")
                    .append(testName)
                    .append("}</if>\n");
        }
        xmlBuilder.append("        </where>\n");
        xmlBuilder.append("    </select>");
        xmlBuilder.append(System.getProperty("line.separator"));
        xmlBuilder.append(System.getProperty("line.separator"));

        xmlBuilder.append("    <select id=\"selById\" parameterType=\"Long\" resultMap=\"" + id + "\">\n");
        xmlBuilder.append("        <include refid=\"" + vo + "\"/>\n" +
                "        where id = #{id}\n");
        xmlBuilder.append("    </select>\n");
        xmlBuilder.append(System.getProperty("line.separator"));

        xmlBuilder.append("    <insert id=\"add\" parameterType=\"" + domainName + "\">\n");
        xmlBuilder.append("        insert into " + tableName + "\n");
        xmlBuilder.append("        <trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">\n");
        for (String columnName : columnNames) {
            String testName = underline2Camel(columnName, false);
            xmlBuilder.append("            <if test=\"" + testName + " != null\">" + columnName + ",</if>\n");
        }
        xmlBuilder.append("        </trim>\n");

        xmlBuilder.append("        <trim prefix=\"values (\" suffix=\")\" suffixOverrides=\",\">\n");
        for (String columnName : columnNames) {
            String testName = underline2Camel(columnName, false);
            xmlBuilder.append("            <if test=\"" + testName + " != null\">#{" + testName + "},</if>\n");
        }
        xmlBuilder.append("        </trim>\n");
        xmlBuilder.append("    </insert>\n");
        xmlBuilder.append(System.getProperty("line.separator"));

        xmlBuilder.append("    <update id=\"update\" parameterType=\"" + domainName + "\">\n");
        xmlBuilder.append("        update " + tableName + "\n");
        xmlBuilder.append("        <trim prefix=\"SET\" suffixOverrides=\",\">\n");
        for (String columnName : columnNames) {
            String testName = underline2Camel(columnName, false);
            xmlBuilder.append("            <if test=\"" + testName + " != null\">" + columnName + " = #{" + testName + "},</if>\n");
        }
        xmlBuilder.append("        </trim>\n");
        xmlBuilder.append("        where id = #{id}\n");
        xmlBuilder.append("    </update>\n");
        xmlBuilder.append(System.getProperty("line.separator"));

        xmlBuilder.append("    <delete id=\"delById\" parameterType=\"Long\">\n");
        xmlBuilder.append("        delete from " + tableName + " where id = #{id}\n");
        xmlBuilder.append("    </delete>\n");
        xmlBuilder.append(System.getProperty("line.separator"));

        xmlBuilder.append("    <delete id=\"delBatchByIdList\" parameterType=\"Long\">\n");
        xmlBuilder.append("        delete from " + tableName + " where id in \n");
        xmlBuilder.append("        <foreach item=\"id\" collection=\"array\" open=\"(\" separator=\",\" close=\")\">\n" +
                "            #{id}\n" +
                "        </foreach>\n");
        xmlBuilder.append("    </delete>\n");
        xmlBuilder.append(System.getProperty("line.separator"));

        bw.write(xmlBuilder.toString());
        xmlBuilder.append(System.getProperty("line.separator"));
        String nameSpaceEndTag = "</mapper>";
        bw.write(nameSpaceEndTag);

        bw.close();
    }

    private static void createJavaMapperFile(String packageURL, String domainName) throws IOException {
        String className = domainName + "Mapper";
        String domainNameStartByLowerCase = underline2Camel(domainName, false);
        File file = new File(BASEABSOLUTEMAPPERDIRECTORY + File.separator + domainName + "Mapper.java");
        FileWriter fileWriter = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(fileWriter);

        //包路径
        String packageBuilder = "package " +
                packageURL +
                ";" +
                System.getProperty("line.separator") +
                System.getProperty("line.separator");
        bw.write(packageBuilder);

        //引入包名称
        StringBuilder importBuilder = new StringBuilder();
        importBuilder.append("import " + BASEPACKAGE + ".domain.").append(domainName).append(";\n");
        importBuilder.append("import org.apache.ibatis.annotations.Mapper;\n");
        importBuilder.append("import org.apache.ibatis.annotations.Param;\n");
        importBuilder.append(System.getProperty("line.separator"));
        importBuilder.append(System.getProperty("line.separator"));
        importBuilder.append("import java.util.List;");
        importBuilder.append(System.getProperty("line.separator"));
        bw.write(importBuilder.toString());

        //生成代码
        StringBuilder codeBuilder = new StringBuilder();
        //生成类名 class
        codeBuilder.append("/**");
        codeBuilder.append(System.getProperty("line.separator"));

        //对应实体表名称
        codeBuilder.append("* ");
        codeBuilder.append("className: ").append(className);
        codeBuilder.append(System.getProperty("line.separator"));

        //创建时间
        codeBuilder.append("* ");
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String createdTime = dateTimeFormatter.format(now);
        codeBuilder.append("createdTime: ").append(createdTime);
        codeBuilder.append(System.getProperty("line.separator"));

        //创建人
        codeBuilder.append("* ");
        codeBuilder.append("createdBy: Miaowu-Generate");
        codeBuilder.append(System.getProperty("line.separator"));

        codeBuilder.append("**/");
        codeBuilder.append(System.getProperty("line.separator"));

        codeBuilder.append("@Mapper\n");
        codeBuilder.append("public interface ").append(className).append(" {");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append(System.getProperty("line.separator"));

        codeBuilder.append("    /**\n" +
                "     * 列表查询\n" +
                "     * @return 实体列表\n" +
                "     */");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append("    List<").append(domainName)
                .append("> list(")
                .append(domainName)
                .append(" ")
                .append(domainNameStartByLowerCase)
                .append(");");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append(System.getProperty("line.separator"));

        codeBuilder.append("    /**\n" +
                "     * 通过id查询对象\n" +
                "     * @return 实体对象\n" +
                "     */");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append("    ").append(domainName).append(" selById(Long id);");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append(System.getProperty("line.separator"));

        codeBuilder.append("    /**\n" +
                "     * 新增\n" +
                "     * @return 新增结果\n" +
                "     */");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append("    Integer add(").append(domainName).append(" ").append(domainNameStartByLowerCase).append(");");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append(System.getProperty("line.separator"));

        codeBuilder.append("    /**\n" +
                "     * 更新\n" +
                "     * @return 更新结果\n" +
                "     */");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append("    Integer update(").append(domainName).append(" ").append(domainNameStartByLowerCase).append(");");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append(System.getProperty("line.separator"));

        codeBuilder.append("    /**\n" +
                "     * 通过id删除单条记录\n" +
                "     * @return 删除结果\n" +
                "     */");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append("    Integer delById(Long id);");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append(System.getProperty("line.separator"));

        codeBuilder.append("    /**\n" +
                "     * 使用id列表批量删除\n" +
                "     * @return 批量删除结果\n" +
                "     */");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append("    Integer delBatchByIdList(Long[] ids);");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append(System.getProperty("line.separator"));

        codeBuilder.append("    /**\n" +
                "     * 使用对象进行筛查后分页\n" +
                "     * @return 筛选后分页结果\n" +
                "     */");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append("    List<")
                .append(domainName)
                .append("> page(@Param(\"")
                .append(domainNameStartByLowerCase)
                .append("\") ")
                .append(domainName).append(" ")
                .append(domainNameStartByLowerCase)
                .append(", @Param(\"page\") int page, @Param(\"pageSize\") int pageSize);");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append(System.getProperty("line.separator"));

        codeBuilder.append("    /**\n" +
                "     * 数据总条数\n" +
                "     * @return 数据总条数\n" +
                "     */");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append("    Integer total(")
                .append(domainName)
                .append(" ")
                .append(domainNameStartByLowerCase)
                .append(");");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append(System.getProperty("line.separator"));

        codeBuilder.append("}");

        bw.write(codeBuilder.toString());
        bw.close();
    }

    private static void createJavaServiceImplFile(String packageURL, String domainName) throws IOException {
        String className = domainName + "ServiceImpl";
        String domainNameStartByLowerCase = underline2Camel(domainName, false);
        File file = new File(BASEABSOLUTESERVICEIMPLDIRECTORY + File.separator + domainName + "ServiceImpl.java");
        FileWriter fileWriter = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(fileWriter);

        //包路径
        String packageBuilder = "package " +
                packageURL +
                ";" +
                System.getProperty("line.separator") +
                System.getProperty("line.separator");
        bw.write(packageBuilder);

        //引入包名称
        StringBuilder importBuilder = new StringBuilder();
        importBuilder.append("import " + BASEPACKAGE + ".domain.").append(domainName).append(";").append("\n");
//        importBuilder.append("import "+ BASEPACKAGE + ".Covert;").append("\n");
        importBuilder.append("import " + BASEPACKAGE + ".mapper.").append(domainName).append("Mapper;").append("\n");
        importBuilder.append("import " + BASEPACKAGE + ".service.").append(domainName).append("Service;").append("\n");
        importBuilder.append("import org.springframework.stereotype.Service;\n");
        importBuilder.append("import org.springframework.util.ObjectUtils;");
        importBuilder.append(System.getProperty("line.separator"));
        importBuilder.append(System.getProperty("line.separator"));

        importBuilder.append("import java.util.List;");
        importBuilder.append(System.getProperty("line.separator"));
        bw.write(importBuilder.toString());


        //生成代码
        StringBuilder codeBuilder = new StringBuilder();
        //生成类名 class
        codeBuilder.append("/**");
        codeBuilder.append(System.getProperty("line.separator"));

        //对应实体表名称
        codeBuilder.append("* ");
        codeBuilder.append("className: ").append(className);
        codeBuilder.append(System.getProperty("line.separator"));

        //创建时间
        codeBuilder.append("* ");
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String createdTime = dateTimeFormatter.format(now);
        codeBuilder.append("createdTime: ").append(createdTime);
        codeBuilder.append(System.getProperty("line.separator"));

        //创建人
        codeBuilder.append("* ");
        codeBuilder.append("createdBy: Miaowu-Generate");
        codeBuilder.append(System.getProperty("line.separator"));

        codeBuilder.append("**/");
        codeBuilder.append(System.getProperty("line.separator"));

        codeBuilder.append("@Service\n");
        codeBuilder.append("public class ").append(className).append(" implements " + domainName + "Service {");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append(System.getProperty("line.separator"));


        //注入Mapper============================================
        String serviceName = underline2Camel(domainName, false) + "Mapper";
        String serviceClassName = underline2Camel(domainName, true) + "Mapper";
        codeBuilder.append("    private final ").append(domainName).append("Mapper ").append(serviceName).append(";");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append(System.getProperty("line.separator"));

        //构造
        codeBuilder.append("    public ").append(className).append("(").append(serviceClassName).append(" ").append(serviceName).append(") {").append("\n");
        codeBuilder.append("        this.").append(serviceName).append(" = ").append(serviceName).append(";").append("\n");
        codeBuilder.append("    }").append("\n");
        codeBuilder.append(System.getProperty("line.separator"));

        codeBuilder.append("    /**\n" +
                "     * 列表查询\n" +
                "     * @return 实体列表\n" +
                "     */");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append("    public List<").append(domainName)
                .append("> list(")
                .append(domainName)
                .append(" ")
                .append(domainNameStartByLowerCase)
                .append(")");
        codeBuilder.append(" {\n" + "        return ").append(domainNameStartByLowerCase)
                .append("Mapper.list(")
                .append(domainNameStartByLowerCase)
                .append(");\n")
                .append("    }");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append(System.getProperty("line.separator"));

        codeBuilder.append("    /**\n" +
                "     * 通过id查询对象\n" +
                "     * @return 实体对象\n" +
                "     */");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append("    public ").append(domainName).append(" selById(Long id)");
        codeBuilder.append(" {\n" + "        return ")
                .append(domainNameStartByLowerCase)
                .append("Mapper.selById(id);\n")
                .append("    }");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append(System.getProperty("line.separator"));

        codeBuilder.append("    /**\n" +
                "     * 新增\n" +
                "     * @return 新增结果\n" +
                "     */");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append("    public Integer add(").append(domainName).append(" ").append(domainNameStartByLowerCase).append(")");
        codeBuilder.append(" {\n" + "        return ")
                .append(domainNameStartByLowerCase)
                .append("Mapper.add(")
                .append(domainNameStartByLowerCase)
                .append(");\n").append("    }");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append(System.getProperty("line.separator"));

        codeBuilder.append("    /**\n" +
                "     * 更新\n" +
                "     * @return 更新结果\n" +
                "     */");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append("    public Integer update(").append(domainName).append(" ").append(domainNameStartByLowerCase).append(")");
        codeBuilder.append(" {\n" + "        return ")
                .append(domainNameStartByLowerCase)
                .append("Mapper.update(")
                .append(domainNameStartByLowerCase)
                .append(");\n").append("    }");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append(System.getProperty("line.separator"));

        codeBuilder.append("    /**\n" +
                "     * 通过id删除单条记录\n" +
                "     * @return 删除结果\n" +
                "     */");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append("    public Integer delById(Long id)");
        codeBuilder.append(" {\n" + "        return ").append(domainNameStartByLowerCase).append("Mapper.delById(id);\n").append("    }");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append(System.getProperty("line.separator"));

        codeBuilder.append("    /**\n" +
                "     * 使用id列表批量删除\n" +
                "     * @return 批量删除结果\n" +
                "     */");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append("    public Integer delBatchByIdList(List<Long> ids)");
        codeBuilder.append(" {\n" + "        return ").append(domainNameStartByLowerCase).append("Mapper.delBatchByIdList(ids.toArray(new Long[0]));\n").append("    }");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append(System.getProperty("line.separator"));

        codeBuilder.append("    /**\n" +
                "     * 使用对象进行筛选后分页\n" +
                "     * @return 使用对象进行筛选后分页\n" +
                "     */\n");
        codeBuilder.append("    public List<").append(domainName)
                .append("> page(")
                .append(domainName)
                .append(" ")
                .append(domainNameStartByLowerCase)
                .append(", int page, int pageSize")
                .append(")");
        codeBuilder.append(" {\n")
                .append("    if (ObjectUtils.isEmpty(")
                .append(domainNameStartByLowerCase)
                .append(")){\n")
                .append("            ")
                .append(domainNameStartByLowerCase)
                .append(" = new ")
                .append(domainName)
                .append("();\n")
                .append("        }\n")
                .append("        return ").append(domainNameStartByLowerCase)
                .append("Mapper.page(")
                .append(domainNameStartByLowerCase)
                .append(", page, pageSize")
                .append("")
                .append(");\n")
                .append("    }");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append(System.getProperty("line.separator"));

        codeBuilder.append("    /**\n" +
                "     * 数据总条数\n" +
                "     * @return 数据总条数\n" +
                "     */");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append("    public Integer total(")
                .append(domainName)
                .append(" ")
                .append(domainNameStartByLowerCase)
                .append(")");
        codeBuilder.append(" {\n" + "        return ").append(domainNameStartByLowerCase)
                .append("Mapper.total(")
                .append(domainNameStartByLowerCase)
                .append(");\n")
                .append("    }");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append(System.getProperty("line.separator"));

        codeBuilder.append("}");

        bw.write(codeBuilder.toString());
        bw.close();
    }

    private static void createJavaServiceFile(String packageURL, String domainName) throws IOException {
        String className = domainName + "Service";
        String domainNameStartByLowerCase = underline2Camel(domainName, false);
        File file = new File(BASEABSOLUTESERVICEDIRECTORY + File.separator + domainName + "Service.java");
        FileWriter fileWriter = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(fileWriter);

        //包路径
        String packageBuilder = "package " +
                packageURL +
                ";" +
                System.getProperty("line.separator") +
                System.getProperty("line.separator");
        bw.write(packageBuilder);

        //引入包名称
        StringBuilder importBuilder = new StringBuilder();
        importBuilder.append("import " + BASEPACKAGE + ".domain." + domainName + ";");
        importBuilder.append(System.getProperty("line.separator"));
        importBuilder.append(System.getProperty("line.separator"));
        importBuilder.append("import java.util.List;");
        importBuilder.append(System.getProperty("line.separator"));
        bw.write(importBuilder.toString());

        //生成代码
        StringBuilder codeBuilder = new StringBuilder();
        //生成类名 class
        codeBuilder.append("/**");
        codeBuilder.append(System.getProperty("line.separator"));

        //对应实体表名称
        codeBuilder.append("* ");
        codeBuilder.append("className: ").append(className);
        codeBuilder.append(System.getProperty("line.separator"));

        //创建时间
        codeBuilder.append("* ");
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String createdTime = dateTimeFormatter.format(now);
        codeBuilder.append("createdTime: ").append(createdTime);
        codeBuilder.append(System.getProperty("line.separator"));

        //创建人
        codeBuilder.append("* ");
        codeBuilder.append("createdBy: Miaowu-Generate");
        codeBuilder.append(System.getProperty("line.separator"));

        codeBuilder.append("**/");
        codeBuilder.append(System.getProperty("line.separator"));

        codeBuilder.append("public interface ").append(className).append(" {");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append(System.getProperty("line.separator"));

        codeBuilder.append("    /**\n" +
                "     * 列表查询\n" +
                "     * @return 实体列表\n" +
                "     */");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append("    List<").append(domainName)
                .append("> list(")
                .append(domainName)
                .append(" ")
                .append(domainNameStartByLowerCase)
                .append(");");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append(System.getProperty("line.separator"));

        codeBuilder.append("    /**\n" +
                "     * 通过id查询对象\n" +
                "     * @return 实体对象\n" +
                "     */");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append("    ").append(domainName).append(" selById(Long id);");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append(System.getProperty("line.separator"));

        codeBuilder.append("    /**\n" +
                "     * 新增\n" +
                "     * @return 新增结果\n" +
                "     */");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append("    Integer add(").append(domainName).append(" ").append(domainNameStartByLowerCase).append(");");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append(System.getProperty("line.separator"));

        codeBuilder.append("    /**\n" +
                "     * 更新\n" +
                "     * @return 更新结果\n" +
                "     */");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append("    Integer update(").append(domainName).append(" ").append(domainNameStartByLowerCase).append(");");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append(System.getProperty("line.separator"));

        codeBuilder.append("    /**\n" +
                "     * 通过id删除单条记录\n" +
                "     * @return 删除结果\n" +
                "     */");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append("    Integer delById(Long id);");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append(System.getProperty("line.separator"));

        codeBuilder.append("    /**\n" +
                "     * 使用id列表批量删除\n" +
                "     * @return 批量删除结果\n" +
                "     */");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append("    Integer delBatchByIdList(List<Long> ids);");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append(System.getProperty("line.separator"));

        codeBuilder.append("    /**\n" +
                "     * 使用对象进行筛选后分页\n" +
                "     * @return 使用对象进行筛选后分页\n" +
                "     */");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append("    List<").append(domainName)
                .append("> page(")
                .append(domainName)
                .append(" ")
                .append(domainNameStartByLowerCase)
                .append(", int page, int pageSize")
                .append(");");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append(System.getProperty("line.separator"));

        codeBuilder.append("    /**\n" +
                "     * 数据总条数\n" +
                "     * @return 数据总条数\n" +
                "     */");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append("    Integer total(")
                .append(domainName)
                .append(" ")
                .append(domainNameStartByLowerCase)
                .append(");");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append(System.getProperty("line.separator"));

        codeBuilder.append("}");

        bw.write(codeBuilder.toString());
        bw.close();

    }

    private static void creteJavaJSONFile(String domainName, String tableName) throws IOException {
        File file = new File(BASEABSOLUTEJSONDIRECTORY + File.separator + domainName + ".json");
        FileWriter fileWriter = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(fileWriter);
        List<String> columnNames = getColumnNames(tableName);
        List<String> columnTypes = getColumnTypes(tableName);
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{");
        for (int i = 0; i < columnNames.size(); i++) {
            String columnName = columnNames.get(i);
            String columnType = columnTypes.get(i);
            //TODO 这里需要根据不同的类型生成不同的数据文件
            if (columnType.contains("VARCHAR")) {
                jsonBuilder.append("\"").append(utils.underline2Camel(columnName)).append("\"");
                jsonBuilder.append(":");
                jsonBuilder.append("\"字符串\"");
            } else {
                jsonBuilder.append("\"").append(utils.underline2Camel(columnName)).append("\"");
                jsonBuilder.append(":");
                jsonBuilder.append("\"10011\"");
            }
            if (i < columnNames.size() - 1) {
                jsonBuilder.append(",");
            }
        }
        jsonBuilder.append("}");
        //写入并关闭流
        bw.write(jsonBuilder.toString());
        bw.close();
    }

    private static void createJavaControllerFile(String packageURL, String domainName, String tableComment) throws IOException {
        String className = domainName + "Controller";
        String domainNameStartByLowerCase = underline2Camel(domainName, false);
        File file = new File(BASEABSOLUTECONTROLLERDIRECTORY + File.separator + domainName + "Controller.java");
        FileWriter fileWriter = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(fileWriter);

        //包路径
        String packageBuilder = "package " +
                packageURL +
                ";" +
                System.getProperty("line.separator") +
                System.getProperty("line.separator");
        bw.write(packageBuilder);

        //引入包名称
        StringBuilder importBuilder = new StringBuilder();
        importBuilder.append("import org.springframework.web.bind.annotation.*;");
        importBuilder.append(System.getProperty("line.separator"));
        importBuilder.append("import org.springframework.beans.BeanUtils;");
        importBuilder.append(System.getProperty("line.separator"));
        importBuilder.append("import org.springframework.util.ObjectUtils;");
        importBuilder.append(System.getProperty("line.separator"));
        importBuilder.append("import " + BASEPACKAGE + ".domain.").append(domainName).append(";");
        importBuilder.append(System.getProperty("line.separator"));
        importBuilder.append("import " + BASEPACKAGE + ".dto.").append(domainName).append("DTO").append(";");
        importBuilder.append(System.getProperty("line.separator"));
        importBuilder.append("import " + BASEPACKAGE + ".service.").append(domainName).append("Service;");

        if (swaggerEnable) {
            importBuilder.append(System.getProperty("line.separator"));
            importBuilder.append("import io.swagger.annotations.Api;");
            importBuilder.append(System.getProperty("line.separator"));
            importBuilder.append("import io.swagger.annotations.ApiOperation;");
        }
        importBuilder.append(System.getProperty("line.separator"));
        importBuilder.append(System.getProperty("line.separator"));
        importBuilder.append("import java.util.List;");
        importBuilder.append(System.getProperty("line.separator"));
        importBuilder.append("import java.util.Map;");
        importBuilder.append(System.getProperty("line.separator"));
        importBuilder.append("import java.util.HashMap;");

        importBuilder.append(System.getProperty("line.separator"));
        importBuilder.append("import org.springframework.lang.Nullable;");
        importBuilder.append(System.getProperty("line.separator"));
        importBuilder.append(System.getProperty("line.separator"));

        bw.write(importBuilder.toString());

        //生成代码
        StringBuilder codeBuilder = new StringBuilder();
        //生成类名 class
        codeBuilder.append("/**");
        codeBuilder.append(System.getProperty("line.separator"));

        //对应实体表名称
        codeBuilder.append("* ");
        codeBuilder.append("className: ").append(tableComment).append("控制器");
        codeBuilder.append(System.getProperty("line.separator"));

        //创建时间
        codeBuilder.append("* ");
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String createdTime = dateTimeFormatter.format(now);
        codeBuilder.append("createdTime: ").append(createdTime);
        codeBuilder.append(System.getProperty("line.separator"));

        //创建人
        codeBuilder.append("* ");
        codeBuilder.append("createdBy: Miaowu-Generate");
        codeBuilder.append(System.getProperty("line.separator"));

        codeBuilder.append("**/");
        codeBuilder.append(System.getProperty("line.separator"));

        if (swaggerEnable) {
            codeBuilder.append("@Api(\"");
            codeBuilder.append(tableComment);
            codeBuilder.append("\")\n");
        }
        codeBuilder.append("@RestController");
        codeBuilder.append(System.getProperty("line.separator"));
        //Mark===================================== configByUser
        codeBuilder.append("@RequestMapping(\"/api/").append(domainNameStartByLowerCase).append("\")");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append("public class ").append(className).append(" {");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append(System.getProperty("line.separator"));


        //注入Service============================================
        String serviceName = underline2Camel(domainName, false) + "Service";
        String serviceClassName = underline2Camel(domainName, true) + "Service";
        codeBuilder.append("    private final ").append(domainName).append("Service ").append(serviceName).append(";");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append(System.getProperty("line.separator"));

        //构造
        codeBuilder.append("    public ").append(className).append("(").append(serviceClassName).append(" ").append(serviceName).append(") {");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append("        this.").append(serviceName).append(" = ").append(serviceName).append(";");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append("    }");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append(System.getProperty("line.separator"));


        //crud
        if (swaggerEnable) {
            codeBuilder.append("    @ApiOperation(\"列表查询\")");
        } else {
            codeBuilder.append("    /**\n" +
                    "     * 列表查询\n" +
                    "     */");
        }
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append("    @GetMapping(\"/list\")");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append("    public List<")
                .append(domainName)
                .append("> list(@RequestBody @Nullable")
                .append(" ")
                .append(domainName)
                .append(" ")
                .append(domainNameStartByLowerCase)
                .append("){");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append("        return ")
                .append(serviceName)
                .append(".list(")
                .append(domainNameStartByLowerCase)
                .append(");");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append("    }");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append(System.getProperty("line.separator"));

        if (swaggerEnable) {
            codeBuilder.append("    @ApiOperation(\"分页列表查询\")");
        } else {
            codeBuilder.append("    /**\n" +
                    "     * 分页列表查询\n" +
                    "     */");
        }
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append("    @PostMapping(\"/list/{page}/{pageSize}\")");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append("    public Map<String, Object> page(@RequestBody @Nullable")
                .append(" ")
                .append(domainName)
                .append("DTO")
                .append(" reqDTO,")
                .append(" @PathVariable int page, @PathVariable int pageSize")
                .append("){");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append("        if (ObjectUtils.isEmpty(reqDTO)) {\n")
                .append("            reqDTO = new ")
                .append(domainName)
                .append("DTO();\n")
                .append("        }");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append("        ")
                .append(domainName)
                .append(" ")
                .append(domainNameStartByLowerCase)
                .append(" = new ")
                .append(domainName)
                .append("();");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append("        BeanUtils.copyProperties(reqDTO, ")
                .append(domainNameStartByLowerCase)
                .append(");");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append("        Integer total = ")
                .append(serviceName)
                .append(".total(")
                .append(domainNameStartByLowerCase)
                .append(");\n");
        codeBuilder.append("        List<")
                .append(domainName)
                .append("> contentList = ")
                .append(serviceName)
                .append(".page(")
                .append(domainNameStartByLowerCase)
                .append(", page, pageSize);\n");
        codeBuilder.append("        Map<String, Object> resMap = new HashMap<>();\n")
                .append("        resMap.put(\"totalEle\", total);\n")
                .append("        resMap.put(\"content\", contentList);\n")
                .append("        resMap.put(\"curSize\", contentList.size());\n")
                .append("        resMap.put(\"page\", page);\n")
                .append("        resMap.put(\"pageSize\", pageSize);\n")
                .append("        return resMap;");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append("    }");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append(System.getProperty("line.separator"));

        if (swaggerEnable) {
            codeBuilder.append("    @ApiOperation(\"通过id查询对象\")");
        } else {
            codeBuilder.append("    /**\n" +
                    "     * 通过id查询对象\n" +
                    "     */");
        }
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append("    @GetMapping(\"/selById/{id}\")");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append("    public ").append(domainName).append(" selById(@PathVariable Long id) {");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append("        return ").append(serviceName).append(".selById(id);");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append("    }");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append(System.getProperty("line.separator"));

        if (swaggerEnable) {
            codeBuilder.append("    @ApiOperation(\"通过对象新增\")");
        } else {
            codeBuilder.append("    /**\n" +
                    "     * 通过对象新增\n" +
                    "     */");
        }
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append("    @PostMapping(\"/add\")");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append("    public Integer add(@RequestBody ").append(domainName).append(" ").append(domainNameStartByLowerCase).append("){");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append("        return ").append(serviceName).append(".add(").append(domainNameStartByLowerCase).append(");");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append("    }");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append(System.getProperty("line.separator"));

        if (swaggerEnable) {
            codeBuilder.append("    @ApiOperation(\"通过对象更新\")");
        } else {
            codeBuilder.append("    /**\n" +
                    "     * 通过对象更新\n" +
                    "     */");
        }
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append("    @GetMapping(\"/update\")");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append("    public Integer update(@RequestBody ").append(domainName).append(" ").append(domainNameStartByLowerCase).append("){");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append("        return ").append(serviceName).append(".update(").append(domainNameStartByLowerCase).append(");");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append("    }");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append(System.getProperty("line.separator"));

        if (swaggerEnable) {
            codeBuilder.append("    @ApiOperation(\"通过id删除对象数据\")");
            codeBuilder.append(System.getProperty("line.separator"));
        } else {
            codeBuilder.append("    /**\n" +
                    "     * 通过id删除对象数据\n" +
                    "     */");
        }
        codeBuilder.append("    @GetMapping(\"/delById/{id}\")");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append("    public Integer delById(@PathVariable Long id){");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append("        return ").append(serviceName).append(".delById(id);");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append("    }");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append(System.getProperty("line.separator"));

        if (swaggerEnable) {
            codeBuilder.append("    @ApiOperation(\"通过id列表批量删除\")");
        } else {
            codeBuilder.append("    /**\n" +
                    "     * 通过id列表批量删除\n" +
                    "     */");
        }
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append("    @GetMapping(\"/delBatchByIdList\")");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append("    public Integer delBatchByIdList(@RequestBody List<Long> ids){");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append("        return ").append(serviceName).append(".delBatchByIdList(ids);");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append("    }");
        codeBuilder.append(System.getProperty("line.separator"));

        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append("}");
        //写入并关闭流
        bw.write(codeBuilder.toString());
        bw.close();
    }

    private static void createJavaDomainFile(String tableName, String packageURL, String className, String tableComment) throws IOException {
        List<String> columnTypes = getColumnTypes(tableName);
        if (CollectionUtils.isEmpty(columnTypes)) {
            return;
        }
        List<String> comments = getColumnComments(tableName);
        List<String> columnNames = getColumnNames(tableName);

        //创建 .java 文件
        File file = new File(BASEABSOLUTEDOMAINDIRECTORY + File.separator + className + ".java");
        FileWriter fileWriter = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(fileWriter);

        //包路径
        String packageBuilder = "package " +
                packageURL +
                ";" +
                System.getProperty("line.separator") +
                System.getProperty("line.separator");
        bw.write(packageBuilder);

        //引入包名称
        List<ColumnType> columnTypeList = ColumnType.getColumnTypeList();
        StringBuilder importBuilder = new StringBuilder();
        columnTypes.forEach(type -> columnTypeList.forEach(column -> {
            String name = column.name();
            if (type.equalsIgnoreCase(name)) {
                if (StringUtils.hasText(column.getPackageName())) {
                    importBuilder.append("import ");
                    importBuilder.append(column.getPackageName());
                    importBuilder.append(";");
                    importBuilder.append(System.getProperty("line.separator"));
                }
            }
        }));
        importBuilder.append("import lombok.Data;");
//        if (swaggerEnable) {
//            importBuilder.append(System.getProperty("line.separator"));
//            importBuilder.append("import io.swagger.annotations.ApiModel;");
//            importBuilder.append("import io.swagger.annotations.ApiModelProperty;");
//            importBuilder.append(System.getProperty("line.separator"));
//
//        }
        importBuilder.append(System.getProperty("line.separator"));
        importBuilder.append(System.getProperty("line.separator"));
        bw.write(importBuilder.toString());


        //生成代码
        StringBuilder codeBuilder = new StringBuilder();
        //生成类名 class
        codeBuilder.append("/**");
        codeBuilder.append(System.getProperty("line.separator"));

        //对应实体表名称
        codeBuilder.append("* ");
        codeBuilder.append("tableName: ").append(tableName);
        codeBuilder.append(System.getProperty("line.separator"));

        //创建时间
        codeBuilder.append("* ");
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String createdTime = dateTimeFormatter.format(now);
        codeBuilder.append("createdTime: ").append(createdTime);
        codeBuilder.append(System.getProperty("line.separator"));

        //创建人
        codeBuilder.append("* ");
        codeBuilder.append("createdBy: Miaowu-Generate");
        codeBuilder.append(System.getProperty("line.separator"));

        codeBuilder.append("**/");
        codeBuilder.append(System.getProperty("line.separator"));

        codeBuilder.append("@Data");
//        if (swaggerEnable) {
//            codeBuilder.append(System.getProperty("line.separator"));
//            codeBuilder.append("@ApiModel(\"");
//            codeBuilder.append(tableComment);
//            codeBuilder.append("\")");
//        }
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append("public class ").append(className).append(" {");
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append(System.getProperty("line.separator"));
        for (int i = 0, columnNamesSize = columnNames.size(); i < columnNamesSize; i++) {
            String columnName = columnNames.get(i);
            String columnType = columnTypes.get(i);
            String fieldType = Enum.valueOf(ColumnType.class, columnType.toUpperCase()).getFieldType();
            //注释
            if (StringUtils.hasLength(comments.get(i))) {
//                if (swaggerEnable) {
//                    codeBuilder.append("    @ApiModelProperty(\"");
//                    codeBuilder.append(comments.get(i));
//                    codeBuilder.append("\")");
//                    codeBuilder.append(System.getProperty("line.separator"));
//                } else {
                codeBuilder.append("    /**");
                codeBuilder.append(System.getProperty("line.separator"));
                codeBuilder.append("    * ");
                codeBuilder.append(comments.get(i));
                codeBuilder.append(System.getProperty("line.separator"));
                codeBuilder.append("    **/");
                codeBuilder.append(System.getProperty("line.separator"));
//                }
            }
            //字段
            codeBuilder.append("    ");
            codeBuilder.append("private ");
            codeBuilder.append(fieldType).append(" ");
            codeBuilder.append(underline2Camel(columnName, false)).append(";");
            codeBuilder.append(System.getProperty("line.separator"));
            codeBuilder.append(System.getProperty("line.separator"));
        }
        codeBuilder.append(System.getProperty("line.separator"));
        codeBuilder.append("}");
        bw.write(codeBuilder.toString());
        //转文件保存
        bw.close();
    }
}
