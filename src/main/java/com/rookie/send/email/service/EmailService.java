package com.rookie.send.email.service;

import com.rookie.send.email.model.SendEmailModel;

import java.util.concurrent.ArrayBlockingQueue;

public interface EmailService {
    void sendEmail (String emailKey, SendEmailModel model) ;

    void sendEmailByThread(ArrayBlockingQueue recevierQueue);
}
