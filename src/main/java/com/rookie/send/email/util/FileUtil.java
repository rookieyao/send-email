package com.rookie.send.email.util;

import com.rookie.send.email.param.EmailParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.regex.Pattern;

/**
 * @Author rookie
 * @Date 2021/5/8 10:38
 * @Description
 **/
public class FileUtil {


    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtil.class) ;
    final static String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
    final static Pattern regex = Pattern.compile(check);
    public static ArrayBlockingQueue<Map<String, EmailParam>> readSendersInfo(){

        ArrayBlockingQueue unUsedAddresserQueue = new ArrayBlockingQueue<Map<String, EmailParam>>(10000);
//        List<Map<String,EmailParam>> sendersInfoList = new ArrayList<>();

        try {
            // 读取指定路径下发送账号文件
            ClassPathResource resource = new ClassPathResource("file/senders.txt");
            InputStream inputStream = resource.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream,"GBK"));
            String lineTxt = null;
            while ((lineTxt = br.readLine()) != null) {
                String[] lineContent = lineTxt.split(",");
                if(lineContent.length == 5){
                    String sender = lineContent[0].trim();
                    if(regex.matcher(sender).matches()){
                        unUsedAddresserQueue.offer(getSendMap(lineContent));
//                        sendersInfoList.add(getSendMap(lineContent));
                    }else{
                        LOGGER.info("不合格的邮箱账号:{}",sender);
                    }
                }
            }
            br.close();
        } catch (Exception e) {
            System.err.println("read errors :" + e);
        }

        return unUsedAddresserQueue;
    }

    /** emailSender,emailHost,emailProtocol,password,emailNick  */
    private static Map<String, EmailParam> getSendMap(String[] lineContent) {

        Map<String,EmailParam> sendInfo = new HashMap<>();
        EmailParam emailParam = new EmailParam();
        emailParam.setEmailSender(lineContent[0].trim());emailParam.setEmailHost(lineContent[1].trim());
        emailParam.setEmailProtocol(lineContent[2].trim());emailParam.setPassword(lineContent[3].trim());
        emailParam.setEmailNick(lineContent[4].trim());
        sendInfo.put(lineContent[0].trim(),emailParam);
        return sendInfo;
    }
}
