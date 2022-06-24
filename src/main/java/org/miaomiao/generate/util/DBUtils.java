package org.miaomiao.generate.util;

import org.apache.commons.collections4.CollectionUtils;
import org.miaomiao.generate.model.ColumnType;
import org.miaomiao.generate.config.DBConfiguration;
import org.miaomiao.generate.model.BasicInfo;
import org.miaomiao.generate.model.ColumnInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;


public class DBUtils {

    private static final Logger logger = LoggerFactory.getLogger(DBUtils.class);

    private static final String SQL = "SELECT * FROM ";

    public static DBConfiguration dbConfiguration;
    private static Map<String, BasicInfo> tableInfoMap = new HashMap<>();

    private static Connection conn;

    /**
     * 获取数据库连接
     *
     * @return
     */
    public static Connection getConnection() {
        if (conn == null) {
            try {
                Connection connection = DriverManager.getConnection(dbConfiguration.url, dbConfiguration.username, dbConfiguration.pwd);
                conn = connection;
                logger.info("获取数据库连接成功");
                return connection;
            } catch (SQLException e) {
                logger.error("获取数据库连接失败", e);
            }
        }
        return conn;
    }

    public static void closeConnection() {
        Connection conn = getConnection();
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                logger.error("close connection failure", e);
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
//                closeConnection(conn);
            } catch (SQLException e) {
                logger.error("close ResultSet failure", e);
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
        String comment;
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
            if (!CollectionUtils.isEmpty(tableNames)) {
                List<String> collect = new ArrayList<>();
                tableNames.forEach(tableName -> {
                    if (tableNames.contains(tableName)) {
                        collect.add(tableName);
                    }
                });
                return collect;
            }
        } catch (SQLException e) {
            logger.error("getTableNames failure", e);
        } finally {
            try {
                rs.close();
//                closeConnection(conn);
            } catch (SQLException e) {
                logger.error("close ResultSet failure", e);
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
            logger.error("getColumnNames failure", e);
        } finally {
            if (pStemt != null) {
                try {
                    pStemt.close();
//                    closeConnection(conn);
                } catch (SQLException e) {
                    logger.error("getColumnNames close pstem and connection failure", e);
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
            logger.error("getColumnTypes failure", e);
        } finally {
            if (pStemt != null) {
                try {
                    pStemt.close();
//                    closeConnection(conn);
                } catch (SQLException e) {
                    logger.error("getColumnTypes close pstem and connection failure", e);
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
//                    closeConnection(conn);
                } catch (SQLException e) {
                    logger.error("getColumnComments close ResultSet and connection failure", e);
                }
            }
        }
        return columnComments;
    }

    /**
     * 扫描表信息存储到数据模型种
     */
    public static void scanInfoToModel(BasicInfo basicInfo) {
        List<String> tableNames = dbConfiguration.tableNames;
        tableNames.forEach(tableName -> {
            //获取表注释
            String tableComment = getCommentByTableName(tableName);
            //获取实体类名称
            String entityName = CovertUtils.underline2Camel(tableName, true);
            String entityNameStartByLowCase = CovertUtils.underline2Camel(tableName, false);
            //设置一些常用的参数数据,包含:以大写开头的实体类名称、表名称、表注释、以小写字母开头的实体类名称
            basicInfo.setEntityName(entityName);
            basicInfo.setTableName(tableName);
            basicInfo.setTableComment(tableComment);
            basicInfo.setEntityStartByLowCase(entityNameStartByLowCase);


            //获取字段类型
            List<String> columnTypes = getColumnTypes(tableName);
            //获取字段注释
            List<String> columnComments = getColumnComments(tableName);
            //获取字段名字
            List<String> columnNames = getColumnNames(tableName);

            //传递哥各个字段的信息到ColumnInfo对象中存储
            List<ColumnInfo> columnInfos = new ArrayList<>();
            Set<String> importPackages = new HashSet<>();
            for (int i = 0, columnTypesSize = columnTypes.size(); i < columnTypesSize; i++) {
                String columnType = columnTypes.get(i);
                String columnName = columnNames.get(i);
                String columnComment = columnComments.get(i);
                ColumnInfo.ColumnInfoBuilder builder = ColumnInfo.builder();
                ColumnInfo columnInfo = builder.columnName(columnName)
                        .columnType(columnType)
                        .javaName(CovertUtils.underline2Camel(columnName, false))
                        .javaType(ColumnType.valueOf(columnType).getFieldType())
                        .columnComment(columnComment)
                        .build();
                columnInfos.add(columnInfo);
                //这里我们放入一些需要导包的数据类型的包路径
                importPackages.add(ColumnType.valueOf(columnType).getPackageName());
            }
            //设定详细的包信息/列信息
            List<String> importInfos = importPackages.stream().filter(str -> !str.isEmpty()).collect(Collectors.toList());
            basicInfo.setImportPackages(importInfos);
            basicInfo.setColumnInfos(columnInfos);
            tableInfoMap.put(tableName, basicInfo);
        });
    }

    public static Map<String, BasicInfo> getTableInfoMap() {
        return tableInfoMap;
    }
}
