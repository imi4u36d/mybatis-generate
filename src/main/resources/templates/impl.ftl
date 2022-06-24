package ${packageUrl};

import ${entityUrl}.${entityName};
import ${mapperUrl}.${entityName}Mapper;
import ${serviceUrl}.${entityName}Service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
* @说明: ${tableComment}相关接口实现类
* @作者: ${author} powered By 妙妙
* @创建时间: ${curTime}
**/
@Service
@Transactional
public class ${entityName}ServiceImpl implements ${entityName}Service {

    private final ${entityName}Mapper ${entityStartByLowCase}Mapper;

    public ${entityName}ServiceImpl(${entityName}Mapper ${entityStartByLowCase}Mapper) {
        this.${entityStartByLowCase}Mapper = ${entityStartByLowCase}Mapper;
    }

    /**
     * 列表查询
     *
     * @return 实体列表
     */
    public List<${entityName}> list(${entityName} ${entityStartByLowCase}) {
        return ${entityStartByLowCase}Mapper.list(${entityStartByLowCase});
    }

    /**
     * 列表查询
     *
     * @return 实体列表总数
     */
    @Override
    public Long totalSize() {
        return ${entityStartByLowCase}Mapper.totalSize();
    }

    /**
     * 通过id查询对象
     *
     * @return 实体对象
     */
    public ${entityName} selById(Long id) {
        ${entityName} ${entityStartByLowCase} = ${entityStartByLowCase}Mapper.selById(id);
        return ${entityStartByLowCase};
    }

    /**
     * 新增
     *
     * @return 新增结果
     */
    public Integer add(${entityName} ${entityStartByLowCase}) {
        return ${entityStartByLowCase}Mapper.add(${entityStartByLowCase});
    }

    /**
     * 更新
     *
     * @return 更新结果
     */
    public Integer update(${entityName} ${entityStartByLowCase}) {
        return ${entityStartByLowCase}Mapper.update(${entityStartByLowCase});
    }

    /**
     * 通过id删除单条记录
     *
     * @return 删除结果
     */
    public Integer delById(Long id) {
        return ${entityStartByLowCase}Mapper.delById(id);
    }

    /**
     * 使用id列表批量删除
     *
     * @return 批量删除结果
     */
    public Integer delBatchByIdList(List<Long> ids) {
        return ${entityStartByLowCase}Mapper.delBatchByIdList(ids.toArray(new Long[0]));
    }

    /**
    * 使用对象进行筛选后分页
    *
    * @return 使用对象进行筛选后分页
    */
    public List<${entityName}> page(${entityName} ${entityStartByLowCase}, int page, int pageSize) {
        if (ObjectUtils.isEmpty(${entityStartByLowCase})) {
        ${entityStartByLowCase} = new ${entityName}();
        }
        return ${entityStartByLowCase}Mapper.page(${entityStartByLowCase}, page, pageSize);
    }

    /**
    * 数据总条数
    *
    * @return 数据总条数
    */
    public Integer total(${entityName} ${entityStartByLowCase}) {
        return ${entityStartByLowCase}Mapper.total(${entityStartByLowCase});
    }

}