package com.rookie.send.email.util;

import com.rookie.send.email.model.SendEmailModel;
import com.rookie.send.email.param.EmailParam;

import java.util.Map;

public interface Addresser {

    Map<String, EmailParam> getAnSendEmailParam();
}
