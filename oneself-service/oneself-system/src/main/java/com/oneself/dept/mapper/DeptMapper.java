package com.oneself.dept.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oneself.dept.model.pojo.Dept;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author liuhuan
 * date 2025/1/18
 * packageName com.oneself.dept.mapper
 * interfaceName DeptMapper
 * description 部门表映射
 * version 1.0
 */
@Mapper
@Repository
public interface DeptMapper extends BaseMapper<Dept> {

}
