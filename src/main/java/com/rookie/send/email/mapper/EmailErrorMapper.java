package com.rookie.send.email.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rookie.send.email.entity.EmailError;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmailErrorMapper extends BaseMapper<EmailError> {
}
