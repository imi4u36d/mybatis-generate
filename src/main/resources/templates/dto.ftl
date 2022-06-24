package ${packageUrl};

/**
 * @说明: ${tableComment}接口
 * @作者: ${author} powered By 妙妙
 * @创建时间: ${curTime}
 */
@Data
<#if swaggerEable == true>
@ApiModel("${tableComment}")
</#if>
public class ${entityName} {

<#list columnInfos as col>
    <#if swaggerEable == true>
    @ApiModelProperty(value = "${col.columnComment}")
    <#else>
    /**
    * ${col.columnComment}")
    */
    </#if>
    private ${col.javaType} ${col.javaName};

</#list>

}