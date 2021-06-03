package com.rookie.send.email.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rookie.send.email.entity.Email;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmailMapper extends BaseMapper<Email> {
}
