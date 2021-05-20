package com.rookie.send.email.util;

/**
 * @Author rookie
 * @Date 2021-05-20 7:19
 * @Description
 **/
public class EmailTiltlePool {


    public static String getRandomTitle(String emailKey) {
        return EmailUtil.convertTextModel(AddresserPool.getRandomTitle(),"hygiene","notice");
    }
}
