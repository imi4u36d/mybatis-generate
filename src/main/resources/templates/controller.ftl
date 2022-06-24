package ${packageUrl};


<#--import ${dtoUrl}.BaseResponseDto;-->
import ${entityUrl}.${entityName};
import ${serviceUrl}.${entityName}Service;
<#--import ${utilUrl}.Results;-->
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
<#if swaggerEnable == true>
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiOperation;
</#if>

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @说明: ${tableComment}相关接口
 * @作者: ${author} powered By 妙妙
 * @创建时间: ${curTime}
 */
@RestController
<#if swaggerEnable == true>
@Api(tags = {"${tableComment}相关接口"})
</#if>
@RequestMapping("/api/${entityStartByLowCase}")
public class ${entityName}Controller {

    private final ${entityName}Service ${entityStartByLowCase}Service;

    public ${entityName}Controller(${entityName}Service ${entityStartByLowCase}Service) {
        this.${entityStartByLowCase}Service = ${entityStartByLowCase}Service;
    }

    /**
     * 查询列表
     */
    <#if swaggerEnable == true>
    @ApiOperation("查询列表")
    </#if>
    @PostMapping("/list")
    public Map<String, Object> list(@RequestBody @Nullable ${entityName} ${entityStartByLowCase}) {
        List<${entityName}> list = ${entityStartByLowCase}Service.list(${entityStartByLowCase});
        Long totalSize = ${entityStartByLowCase}Service.totalSize();
        Map<String, Object> resMap = new HashMap<>();
        resMap.put("data", list);
        resMap.put("totalSize", totalSize);
        return resMap;
    }

    /**
     * 通过id查询对象
     */
    <#if swaggerEnable == true>
    @ApiOperation("通过id查询对象")
    </#if>
    @GetMapping("/selById/{id}")
    public ${entityName} selById(<#if swaggerEnable == true>@ApiParam(name = "id", value = "需要查询数据的id")</#if> @PathVariable Long id) {
        return ${entityStartByLowCase}Service.selById(id);
    }

    /**
     * 新增
     */
    <#if swaggerEnable == true>
    @ApiOperation("新增")
    </#if>
    @PostMapping("/add")
    public Integer add(@RequestBody ${entityName} ${entityStartByLowCase}) {
        return ${entityStartByLowCase}Service.add(${entityStartByLowCase});
    }

    /**
     * 更新
     */
    <#if swaggerEnable == true>
    @ApiOperation("更新")
    </#if>
    @PutMapping("/update")
    public Integer update(@RequestBody ${entityName} ${entityStartByLowCase}) {
        return ${entityStartByLowCase}Service.update(${entityStartByLowCase});
    }

    /**
     * 通过id删除
     */
    <#if swaggerEnable == true>
    @ApiOperation("通过id删除")
    </#if>
    @PutMapping("/delById/{id}")
    public Integer delById(<#if swaggerEnable == true>@ApiParam(name = "id", value = "需要删除数据的id")</#if> @PathVariable Long id) {
        return ${entityStartByLowCase}Service.delById(id);
    }

    /**
     * 批量删除
     */
    <#if swaggerEnable == true>
    @ApiOperation("批量删除")
    </#if>
    @PutMapping("/delBatchByIdList")
    public Integer delBatchByIdList(@RequestBody List<Long> ids) {
        return ${entityStartByLowCase}Service.delBatchByIdList(ids);
    }

    /**
     * 分页列表查询
     */
    <#if swaggerEnable == true>
    @ApiOperation("分页列表查询")
    </#if>
    @PostMapping("/list/{page}/{pageSize}")
    public Map<String, Object> page(@RequestBody @Nullable ${entityName} ${entityStartByLowCase},<#if swaggerEnable == true>@ApiParam(name = "page", value = "页码")</#if> @PathVariable int page,<#if swaggerEnable == true>@ApiParam(name = "pageSize", value = "每页数量")</#if> @PathVariable int pageSize) {
        Integer total = ${entityStartByLowCase}Service.total(${entityStartByLowCase});
        List<${entityName}> contentList = ${entityStartByLowCase}Service.page(${entityStartByLowCase}, page, pageSize);
        Map<String, Object> resMap = new HashMap<>();
        resMap.put("totalEle", total);
        resMap.put("content", contentList);
        resMap.put("curSize", contentList.size());
        resMap.put("page", page);
        resMap.put("pageSize", pageSize);
        return resMap;
    }

}