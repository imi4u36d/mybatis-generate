package ${packageUrl};

import ${dtoUrl}.${entityName}Dto;
import org.springframework.beans.BeanUtils;
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
     * ${col.columnComment}
     */
    private ${col.javaType} ${col.javaName};
</#list>
    public ${entityName}Dto toDto() {
        ${entityName}Dto ${entityStartByLowCase}Dto = new ${entityName}Dto();
        BeanUtils.copyProperties(this, ${entityStartByLowCase}Dto);
        return ${entityStartByLowCase}Dto;
    }

}