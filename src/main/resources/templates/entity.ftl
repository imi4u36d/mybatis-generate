package ${packageUrl};

import lombok.Data;
<#list importPackages as package>
import ${package};
</#list>

/**
 * @说明: ${tableComment}实体
 * @作者: ${author} powered By 妙妙
 * @创建时间: ${curTime}
 */
@Data
public class ${entityName} {

<#list columnInfos as col>
    /**
     * ${col.columnComment}"
     */
    private ${col.javaType} ${col.javaName};
</#list>

}