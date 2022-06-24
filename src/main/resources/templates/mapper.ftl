package ${packageUrl};

import ${entityUrl}.${entityName};
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @说明: ${tableComment}Mapper接口
* @作者: ${author} powered By 妙妙
* @创建时间: ${curTime}
**/
@Mapper
public interface ${entityName}Mapper {

    /**
    * 列表查询
    *
    * @return 实体列表
    */
    List<${entityName}> list(${entityName} ${entityStartByLowCase});

    /**
    * 列表查询
    *
    * @return 实体列表总数
    */
    Long totalSize();

    /**
    * 通过id查询对象
    *
    * @return 实体对象
    */
    ${entityName} selById(Long id);

    /**
    * 新增
    *
    * @return 新增结果
    */
    Integer add(${entityName} ${entityStartByLowCase});

    /**
    * 更新
    *
    * @return 更新结果
    */
    Integer update(${entityName} ${entityStartByLowCase});

    /**
    * 通过id删除单条记录
    *
    * @return 删除结果
    */
    Integer delById(Long id);

    /**
    * 使用id列表批量删除
    *
    * @return 批量删除结果
    */
    Integer delBatchByIdList(Long[] ids);

    /**
    * 使用对象进行筛查后分页
    *
    * @return 筛选后分页结果
    */
    List<${entityName}> page(@Param("${entityStartByLowCase}") ${entityName} ${entityStartByLowCase}, @Param("page") int page, @Param("pageSize") int pageSize);

    /**
    * 数据总条数
    *
    * @return 数据总条数
    */
    Integer total(${entityName} ${entityStartByLowCase});

}