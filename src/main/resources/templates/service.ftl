package ${packageUrl};


import com.github.pagehelper.PageInfo;
import ${entityUrl}.${entityName};

import java.util.List;

/**
* @说明: ${tableComment}业务接口层
* @作者: ${author} powered By 妙妙
* @创建时间: ${curTime}
**/
public interface ${entityName}Service {

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
    Integer delBatchByIdList(List<Long> ids);

    /**
    * 使用对象进行筛选后分页
    *
    * @return 使用对象进行筛选后分页
    */
    PageInfo<${entityName}> page(${entityName} ${entityStartByLowCase}, int pageNum, int pageSize);

    /**
    * 数据总条数
    *
    * @return 数据总条数
    */
    Integer total(${entityName} ${entityStartByLowCase});

}