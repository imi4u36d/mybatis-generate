<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<mapper namespace="${mapperUrl}.${entityName}Mapper">

    <resultMap type="${entityName}" id="${entityName}Result">
        <#list columnInfos as col>
            <result property="${col.columnName}"   column="${col.javaName}" />
        </#list>
    </resultMap>

    <sql id="sel${entityStartByLowCase}Vo">
        select
        <#list columnInfos as col>
            ${col.columnName}<#if col_has_next>,</#if>
        </#list>
        from ${tableName}
    </sql>

    <select id="list" parameterType="${entityName}" resultMap="${entityName}Result">
        <include refid="sel${entityStartByLowCase}Vo"/>
        <where>
            <#list columnInfos as col>
                <if test="${col.javaName} != null  and ${col.javaName} != ''"> and ${col.columnName} = <#noparse>#</#noparse>{${col.javaName}}</if>
            </#list>
        </where>
        order by id desc
        <if test="pageSize != null  and pageSize != '' and currentPage != null  and currentPage != ''"> limit <#noparse>#</#noparse>{pageSize * (currentPage-1)} , <#noparse>#</#noparse>{pageSize}</if>
    </select>

    <select id="totalSize" resultType="java.lang.Long">
        select count(*) from ${tableName}
        <where>
            <#list columnInfos as col>
                <if test="${col.javaName} != null  and ${col.javaName} != ''"> and ${col.columnName} = <#noparse>#</#noparse>{${col.javaName}}</if>
            </#list>
        </where>
    </select>

    <select id="selById" parameterType="Long" resultMap="${entityName}Result">
        <include refid="sel${entityStartByLowCase}Vo"/>
        where id = <#noparse>#</#noparse>{id}
    </select>

    <insert id="add" parameterType="${entityName}">
        insert into ${tableName}
        <trim prefix="(" suffix=")" suffixOverrides=",">
            <#list columnInfos as col>
                <if test="${col.javaName} != null">${col.columnName},</if>
            </#list>
        </trim>
        <trim prefix="values (" suffix=")" suffixOverrides=",">
            <#list columnInfos as col>
                <if test="${col.javaName} != null"><#noparse>#</#noparse>{${col.javaName}},</if>
            </#list>
        </trim>
    </insert>

    <update id="update" parameterType="${entityName}">
        update ${tableName}
        <trim prefix="SET" suffixOverrides=",">
            <#list columnInfos as col>
                <if test="${col.javaName} != null">${col.columnName} = <#noparse>#</#noparse>{${col.javaName}},</if>
            </#list>
        </trim>
        where id = <#noparse>#</#noparse>{id}
    </update>

    <delete id="delById" parameterType="Long">
        delete from ${tableName} where id = <#noparse>#</#noparse>{id}
    </delete>

    <delete id="delBatchByIdList" parameterType="Long">
        delete from ${tableName} where id in
        <foreach item="id" collection="array" open="(" separator="," close=")">
            <#noparse>#</#noparse>{id}
        </foreach>
    </delete>

    <select id="page" parameterType="${entityName}" resultMap="${entityName}Result">
        <include refid="sel${entityStartByLowCase}Vo"/>
        <where>
            <#list columnInfos as col>
                <if test="${entityStartByLowCase}.${col.javaName} != null  and ${entityStartByLowCase}.${col.javaName} != ''"> and ${col.columnName} = <#noparse>#</#noparse>{${entityStartByLowCase}.${col.javaName}}</if>
            </#list>
        </where>
        limit <#noparse>#</#noparse>{page},<#noparse>#</#noparse>{pageSize}
    </select>

    <select id="total" parameterType="${entityName}" resultType="Integer">
        select count(*) from ${tableName}
        <where>
            <#list columnInfos as col>
                <if test="${col.javaName} != null  and ${col.javaName} != ''"> and ${col.columnName} = <#noparse>#</#noparse>{${col.javaName}}</if>
            </#list>
        </where>
    </select>

</mapper>