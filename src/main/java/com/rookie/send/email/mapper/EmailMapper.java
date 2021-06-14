package com.rookie.send.email.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rookie.send.email.entity.Email;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EmailMapper extends BaseMapper<Email> {
//    List<String> getDbRecevierList(String );
}
