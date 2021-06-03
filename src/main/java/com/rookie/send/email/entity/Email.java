package com.rookie.send.email.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_email")
public class Email {

    private Long id;

    private String email;

    private Integer status;

    private String batchNum;

    private String msg;

}
