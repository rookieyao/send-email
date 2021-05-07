package com.rookie.send.email.service;

import com.rookie.send.email.model.SendEmailModel;

public interface EmailService {
    void sendEmail (String emailKey, SendEmailModel model) ;
}
