package ${packageUrl};

<#if swaggerEnable == true>
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
</#if>
<#list importPackages as package>
import ${package};
</#list>

/**
 * @说明: ${tableComment}接口
 * @作者: ${author} powered By 妙妙
 * @创建时间: ${curTime}
 */
@Data
<#if swaggerEnable == true>
@ApiModel("${tableComment}")
</#if>
public class ${entityName}Dto {

<#list columnInfos as col>
    <#if swaggerEnable == true>
    @ApiModelProperty(value = "${col.columnComment}")
    <#else>
    /**
    * ${col.columnComment}")
    */
    </#if>
    private ${col.javaType} ${col.javaName};

</#list>

}