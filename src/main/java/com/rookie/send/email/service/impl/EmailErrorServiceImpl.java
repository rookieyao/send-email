package com.rookie.send.email.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rookie.send.email.entity.Email;
import com.rookie.send.email.entity.EmailError;
import com.rookie.send.email.mapper.EmailErrorMapper;
import com.rookie.send.email.mapper.EmailMapper;
import com.rookie.send.email.param.EmailParam;
import com.rookie.send.email.service.EmailErrorService;
import com.rookie.send.email.util.AddresserPool;
import com.rookie.send.email.util.EmailTiltlePool;
import com.rookie.send.email.util.EmailUtil;
import com.rookie.send.email.util.ReceiverPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
@Service
public class EmailErrorServiceImpl extends ServiceImpl<EmailErrorMapper, EmailError> implements EmailErrorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailErrorServiceImpl.class) ;
    @Autowired
    EmailErrorMapper emailErrorMapper;

    @Autowired
    EmailMapper emailMapper;

    @Override
    public void sendEmail(List<Email> emails) {
        Map<String, EmailParam> address = AddresserPool.getAddressByApi();
        String addresser = getAddresser(address);

        for(Email recevier: emails){
            // 发送文本邮件
            try {
                LOGGER.info("start to send exception email：{}", recevier);
                EmailUtil.sendEmail01(addresser,recevier.getEmail(), EmailTiltlePool.getRandomTitle(null), ReceiverPool.getTextBody(null), address);
                LOGGER.info("start to update exception email`status：{}", recevier);
                recevier.setStatus(1);
                emailMapper.updateById(recevier);

                LOGGER.info("sended to {},start to sleep 30s", recevier);
                TimeUnit.SECONDS.sleep(30);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public String getAddresser(Map<String, EmailParam> address){
        Set<String> addresser = address.keySet();
        for(String key:addresser){
            if (key !=null){
                return key;
            }
        }
        return null;
    }
}
