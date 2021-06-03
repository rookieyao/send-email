package com.rookie.send.email.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rookie.send.email.entity.Email;
import com.rookie.send.email.entity.EmailError;

import java.util.List;

public interface EmailErrorService extends IService<EmailError> {
    void sendEmail(List<Email> emails);
}
