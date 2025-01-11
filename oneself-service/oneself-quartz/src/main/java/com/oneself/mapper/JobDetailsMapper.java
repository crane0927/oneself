package com.oneself.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oneself.model.entity.JobDetails;
import com.oneself.model.vo.QuartzTaskVO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author liuhuan
 * date 2025/1/4
 * packageName com.oneself.mapper
 * interfaceName JobDetailsMapper
 * description
 * version 1.0
 */
@Mapper
@Repository
public interface JobDetailsMapper extends BaseMapper<JobDetails> {

    List<QuartzTaskVO> getQuartzTasks();
}
