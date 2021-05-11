package com.rookie.send.email.util;

import com.alibaba.fastjson.JSON;
import com.rookie.send.email.param.EmailParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * 邮箱发送工具类
 */
public class EmailUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailUtil.class) ;

    public static void main(String[] args) throws Exception {
//        sendEmail01("dzyaly@aliyun.com","复杂邮件","自定义图片：<img src='cid:gzh.jpg'/>,网络图片：<img src='http://pic37.nipic.com/20140113/8800276_184927469000_2.png'/>") ;
    }

    /**
     * 邮箱发送模式01：纯文本格式
     */
    public static void sendEmail01 (String addresser, String receiver, String title, String body, Map<String, EmailParam> address) throws Exception {
        Iterator<EmailParam> iterator = address.values().iterator();
        if(receiver == null){
            return;
        }else{
            if(1 == 1){
                LOGGER.info("{} STARTING TO SEND EMAIL,RECEIVER:{},TITLE:{},BODY:{},address:{}",addresser, receiver,title,body, JSON.toJSONString(address));
                return;
            }
        }
        while (iterator.hasNext()){
            EmailParam emailParam = iterator.next(); // 发送人对象信息实体

            Properties props = getOtherProperties(emailParam);

            //使用JavaMail发送邮件的5个步骤
            //1、创建session
            Session session = Session.getInstance(props);
            //开启Session的debug模式，这样就可以查看到程序发送Email的运行状态
            session.setDebug(true);
            //2、通过session得到transport对象
            Transport ts = session.getTransport();
            //3、使用邮箱的用户名和密码连上邮件服务器，发送邮件时，发件人需要提交邮箱的用户名和密码给smtp服务器，用户名和密码都通过验证之后才能够正常发送邮件给收件人。
            ts.connect(emailParam.getEmailHost(), emailParam.getEmailSender(), emailParam.getPassword());
            //4、创建邮件
            // Message message = createEmail01(session,receiver,title,body);
            Message message = createEmail01(session,receiver,title,body,emailParam);
            //5、发送邮件
            ts.sendMessage(message, message.getAllRecipients());
            ts.close();
        }

    }

    public static Properties getProperties(EmailParam emailParam){
        Properties props = new Properties();
        // 设置代理服务器  todo
//            props.setProperty("proxySet", "true");
//            props.setProperty("socksProxyHost", "192.168.155.1");
//            props.setProperty("socksProxyPort", "1081");
        props.setProperty("mail.host", emailParam.getEmailHost());
        props.setProperty("mail.transport.protocol", emailParam.getEmailProtocol());
        props.put("mail.smtp.port", "587");  // linux如果报错可以尝试587端口
        props.setProperty("mail.smtp.auth", emailParam.getEmailAuth());
        //开启SSL
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.socketFactory.port","587");
        props.put("mail.smtp.socketFactory.fallback","false");

        return props;
    }
    public static Properties getOtherProperties(EmailParam emailParam){
        Properties props = new Properties();
        props.setProperty("mail.host", emailParam.getEmailHost());
        props.setProperty("mail.transport.protocol", emailParam.getEmailProtocol());
        props.setProperty("mail.smtp.auth", emailParam.getEmailAuth());
        return props;
    }
//
//    /**
//     * 邮箱发送模式02：复杂格式
//     */
//    public static void sendEmail02 (String receiver, String title, String body) throws Exception {
//        Properties prop = new Properties();
//        prop.setProperty("mail.host", EmailParam.emailHost);
//        prop.setProperty("mail.transport.protocol", EmailParam.emailProtocol);
//        prop.setProperty("mail.smtp.auth", EmailParam.emailAuth);
//        //使用JavaMail发送邮件的5个步骤
//        //1、创建session
//        Session session = Session.getInstance(prop);
//        //开启Session的debug模式，这样就可以查看到程序发送Email的运行状态
//        session.setDebug(true);
//        //2、通过session得到transport对象
//        Transport ts = session.getTransport();
//        //3、使用邮箱的用户名和密码连上邮件服务器，发送邮件时，发件人需要提交邮箱的用户名和密码给smtp服务器，用户名和密码都通过验证之后才能够正常发送邮件给收件人。
//        ts.connect(EmailParam.emailHost, EmailParam.emailSender, EmailParam.password);
//        //4、创建邮件
//        // Message message = createEmail01(session,receiver,title,body);
//        Message message = createEmail02(session,receiver,title,body);
//        //5、发送邮件
//        ts.sendMessage(message, message.getAllRecipients());
//        ts.close();
//    }
//
    /**
     * 创建文本邮件
     */
    private static MimeMessage createEmail01(Session session, String receiver, String title, String body, EmailParam emailParam)
            throws Exception {
        //创建邮件对象
        MimeMessage message = new MimeMessage(session);
        //指明邮件的发件人
        String nick = javax.mail.internet.MimeUtility.encodeText(emailParam.getEmailNick());
        message.setFrom(new InternetAddress(nick+"<"+emailParam.getEmailSender()+">"));
        //指明邮件的收件人
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(receiver));
        //邮件的标题
        message.setSubject(title);
        //邮件的文本内容
        message.setContent(body, "text/html;charset=UTF-8");
        //返回创建好的邮件对象
        return message;
    }
//
//    private static MimeMessage createEmail02 (Session session, String receiver, String title, String body)
//            throws Exception {
//        //创建邮件对象
//        MimeMessage message = new MimeMessage(session);
//        //指明邮件的发件人
//        String nick = javax.mail.internet.MimeUtility.encodeText(EmailParam.emailNick);
//        message.setFrom(new InternetAddress(nick+"<"+EmailParam.emailSender+">"));
//        //指明邮件的收件人
//        message.setRecipient(Message.RecipientType.TO, new InternetAddress(receiver));
//        //邮件的标题
//        message.setSubject(title);
//        //文本内容
//        MimeBodyPart text = new MimeBodyPart() ;
//        text.setContent(body,"text/html;charset=UTF-8");
//        //图片内容
//        MimeBodyPart image = new MimeBodyPart();
//        image.setDataHandler(new DataHandler(new FileDataSource("ware-email-send/src/gzh.jpg")));
//        image.setContentID("gzh.jpg");
//        //附件内容
//        MimeBodyPart attach = new MimeBodyPart();
//        DataHandler file = new DataHandler(new FileDataSource("ware-email-send/src/gzh.zip"));
//        attach.setDataHandler(file);
//        attach.setFileName(file.getName());
//        //关系:正文和图片
//        MimeMultipart multipart1 = new MimeMultipart();
//        multipart1.addBodyPart(text);
//        multipart1.addBodyPart(image);
//        multipart1.setSubType("related");
//        //关系:正文和附件
//        MimeMultipart multipart2 = new MimeMultipart();
//        multipart2.addBodyPart(attach);
//        // 全文内容
//        MimeBodyPart content = new MimeBodyPart();
//        content.setContent(multipart1);
//        multipart2.addBodyPart(content);
//        multipart2.setSubType("mixed");
//        // 封装 MimeMessage 对象
//        message.setContent(multipart2);
//        message.saveChanges();
//        // 本地查看文件格式
//        message.writeTo(new FileOutputStream("F:\\MixedMail.eml"));
//        //返回创建好的邮件对象
//        return message;
//    }

    /**
     * 文本邮箱模板
     */
    public static String convertTextModel (String body, String param1, String param2){
        return String.format(body,param1,param2) ;
    }
    /**
     * 图片邮箱模板
     */
    public static String convertImageModel (String body, String param1){
        return String.format(body,param1) ;
    }
    /**
     * 文件邮箱模板
     */
    public static String convertFileModel (String body, String param1){
        return String.format(body,param1) ;
    }

}
