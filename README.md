# 开箱即用的代码生成工具

一年涨薪10W。老板的贴心小棉袄，同事的内卷神器。🍺

该项目可以自动生成基于mybatis框架的基本操作代码，从此再也不用担心天天CRUD了，
同事直呼内行。🤡 🤡

## 支持平台 🎉：
mac windows linux

## 主要模块包含：
1. controller
2. service
3. serviceImpl
4. domain
5. mapper
6. xml
7. DTO
### 辅助模块包含：
1. 统一DTO封装
2. 统一模块返回

## 使用方法：
修改ApplicationStarter基本参数并运行即可。

你可能需要下面的配置来正确使用PageHelper分页插件

1.pom 文件中配置如下
```
<dependency>
<groupId>com.github.pagehelper</groupId>
<artifactId>pagehelper-spring-boot-starter</artifactId>
<version>1.4.2</version> //
</dependency>
<!-- 为保证兼容性，请使用适配你springBoot版本的PageHelper版本 -->
```
2. 在yaml文件中配置
```
#pagehelper分页插件配置
pagehelper:
helperDialect: mysql
reasonable: true
supportMethodsArguments: true
params: count=countSql
```
3. 在ApplicationStarter启动类中修改你的数据库、包、等信息
4. 执行main方法🍺

## 未来开发计划
1. DTO封装 ✅
2. 统一返回封装 ✅
3. 接口测试JSON生成
4. 自动包文件夹生成
5. 可视化界面操作


## 可能采用的方案
1. 分页使用分页框架 ✅
2. 脚手架自动生成
3. IDEA插件封装