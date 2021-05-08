package com.rookie.send.email.param;

import lombok.Data;

/**
 * 邮箱发送参数配置
 */
@Data
public class EmailParam {
    /**
     * 邮箱服务器地址
     */
    // public static final String emailHost = "smtp.mxhichina.com" ; 阿里云企业邮箱配置（账号+密码）
    // public static final String emailHost = "smtp.aliyun.com" ; 阿里云个人邮箱配置（账号+密码）
    private  String emailHost = "smtp.163.com" ; // 网易邮箱配置（账号+授权码）
    /**
     * 邮箱协议
     */
    private  String emailProtocol = "smtp" ;
    /**
     * 邮箱发件人
     */
    private  String emailSender = "13530975365@163.com" ;
    /**
     * 邮箱授权码
     */
    private  String password = "RXMQEYFDVJPCADTC";
    /**
     * 邮箱授权
     */
    private  String emailAuth = "true" ;
    /**
     * 邮箱昵称
     */
    private  String emailNick = "rookie" ;

}
