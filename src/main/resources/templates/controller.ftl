package ${packageUrl};


import com.github.pagehelper.PageInfo;
import ${entityUrl}.${entityName};
import ${dtoUrl}.BaseResponseDto;
import ${dtoUrl}.${entityName}Dto;
import ${serviceUrl}.${entityName}Service;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import com.miaomiao.miaomiaoservice.utils.Result;
<#if swaggerEnable == true>
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiOperation;
</#if>

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
    public BaseResponseDto<List<${entityName}Dto>> list(@RequestBody @Nullable ${entityName} ${entityStartByLowCase}) {
        List<${entityName}> list = ${entityStartByLowCase}Service.list(${entityStartByLowCase});
        List<${entityName}Dto> resList = list.stream().map(${entityName}::toDto).collect(Collectors.toList());
        return Result.success("查询成功", resList);
    }

    /**
     * 通过id查询对象
     * TODO 这里记录不存在需要使用异常处理
     */
    <#if swaggerEnable == true>
    @ApiOperation("通过id查询对象")
    </#if>
    @GetMapping("/selById/{id}")
    public BaseResponseDto<${entityName}Dto> selById(<#if swaggerEnable == true>@ApiParam(name = "id", value = "需要查询数据的id")</#if> @PathVariable Long id) {
        ${entityName} ${entityStartByLowCase} = ${entityStartByLowCase}Service.selById(id);
        if (Objects.nonNull(mArticle)){
            return Result.success("查询成功",${entityStartByLowCase}.toDto());
        }
        return Result.fail("记录不存在");
    }

    /**
     * 新增
     */
    <#if swaggerEnable == true>
    @ApiOperation("新增")
    </#if>
    @PostMapping("/add")
    public BaseResponseDto<String> add(@RequestBody ${entityName} ${entityStartByLowCase}) {
        Integer res = ${entityStartByLowCase}Service.add(${entityStartByLowCase});
        if (res == 1){
            return Result.success("新增成功");
        }
        return Result.fail("新增失败");
    }

    /**
     * 更新
     */
    <#if swaggerEnable == true>
    @ApiOperation("更新")
    </#if>
    @PutMapping("/update")
    public BaseResponseDto<String> update(@RequestBody ${entityName} ${entityStartByLowCase}) {
        ${entityName} selById = ${entityStartByLowCase}Service.selById(${entityStartByLowCase}.getId());
        if (Objects.isNull(selById)) {
            return Result.fail("记录不存在");
        }
        Integer res = ${entityStartByLowCase}Service.update(${entityStartByLowCase});
        if (res == 1) {
            return Result.success("更新成功");
        }
        return Result.fail("更新失败");
    }

    /**
     * 通过id删除
     */
    <#if swaggerEnable == true>
    @ApiOperation("通过id删除")
    </#if>
    @PutMapping("/delById/{id}")
    public BaseResponseDto<String> delById(<#if swaggerEnable == true>@ApiParam(name = "id", value = "需要删除数据的id")</#if> @PathVariable Long id) {
        Integer res = ${entityStartByLowCase}Service.delById(id);
        if (res == 1){
            return Result.success("删除成功");
        }
        return Result.fail("删除失败");
    }

    /**
     * 批量删除
     */
    <#if swaggerEnable == true>
    @ApiOperation("批量删除")
    </#if>
    @PutMapping("/delBatchByIdList")
    public BaseResponseDto<String> delBatchByIdList(@RequestBody List<Long> ids) {
        if (ids.size() <= 0) {
            return Result.fail("参数错误");
        }
        Integer res = ${entityStartByLowCase}Service.delBatchByIdList(ids);
        if (res == ids.size()) {
            return Result.success("删除成功");
        }
        return Result.fail("删除成功/部分记录不存在");
    }

    /**
     * 分页列表查询
     */
    <#if swaggerEnable == true>
    @ApiOperation("分页列表查询")
    </#if>
    @PostMapping("/page/{pageNum}/{pageSize}")
    public BaseResponseDto<PageInfo<${entityName}>> page(@RequestBody @Nullable ${entityName} ${entityStartByLowCase},<#if swaggerEnable == true>@ApiParam(name = "pageNum", value = "页码")</#if> @PathVariable int pageNum, <#if swaggerEnable == true>@ApiParam(name = "pageSize", value = "每页数量")</#if> @PathVariable int pageSize) {
        PageInfo<${entityName}> page = ${entityStartByLowCase}Service.page(${entityStartByLowCase}, pageNum, pageSize);
        return Result.success("查询成功", page);
    }

}