package com.rookie.send.email.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
import java.util.*;

/**
 * 邮箱发送工具类
 */
public class EmailUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailUtil.class) ;

    public static void main1(String[] args) throws Exception {
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
            }
        }
        while (iterator.hasNext()){
            EmailParam emailParam = iterator.next(); // 发送人对象信息实体

            Properties props = getProperties(emailParam);

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

    final static String url = "https://www.yxa1024.com/getAccountApi.aspx?uid=93673&type=1&token=b5cea9e77db991ee92cb089f3fda44c6&count=100";
    /** 通过api获取发送账号 */
    public static void initSenderEmailsByApi(){
        String[] senderEmailsByApi = ProxyServer.getProxyLine(url).split("<br>");
        for (String email:senderEmailsByApi){
            LOGGER.info("通过api获取的邮箱信息为:{}",email);
            String[] lineContent = email.trim().split("----");
            Map<String, EmailParam> sendMapByApi = FileUtil.getSendMapByApi(lineContent);
            AddresserPool.unUsedAddresserPoolList.add(sendMapByApi);
        }
    }

    public static Map<String, EmailParam> getOneSenderEmailsByApi(){
        String url = "https://www.yxa1024.com/getAccountApi.aspx?uid=93673&type=1&token=b5cea9e77db991ee92cb089f3fda44c6&count=1";
        String email = ProxyServer.getProxyLine(url).split("<br>")[0];
        String[] lineContent = email.trim().split("----");
        return FileUtil.getSendMapByApi(lineContent);
    }

    static Object object = new Object();
    /** foxmail 邮箱发送邮件属性配置类获取方法*/
    public static synchronized Properties getProperties(EmailParam emailParam){
//使用代理请求网易得时候，可能会出现异常，多试几次应该就可以了，其他邮箱可能也有这样得情况
        Properties props = System.getProperties();

        props.setProperty("mail.host", emailParam.getEmailHost());
        props.setProperty("mail.transport.protocol", emailParam.getEmailProtocol());
        props.put("mail.smtp.port", "587");  // linux如果报错可以尝试587端口
        props.setProperty("mail.smtp.auth", emailParam.getEmailAuth());
        //开启SSL
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.socketFactory.port","587");
        props.put("mail.smtp.socketFactory.fallback","false");

        props.setProperty("proxySet", "true");
        String socksIp  = ProxyServer.getProxyLine(ProxyServer.ServerGetProxyUrl);;

// ip可以复用，但是效果不是很好
//        if(ConcurrentHashMapUtil.checkCacheName(emailParam.getEmailSender())){ // 如果该账号存在，说明发过一次，并且还在有效期内，直接获取。如果不存在，说明失效了，
//            LOGGER.info("{}发过邮件，使用的IP还在有效期内，准备使用之前的IP发送邮件!",emailParam.getEmailSender());
//            socksIp = ConcurrentHashMapUtil.get(emailParam.getEmailSender()).toString();
//        }else{  // 否则通过api获取代理ip
//
//            socksIp = ProxyServer.getProxyLine(ProxyServer.ServerGetProxyUrl);
//            ConcurrentHashMapUtil.put(emailParam.getEmailSender(),socksIp);
//            LOGGER.info("{}没发过邮件，准备通过API接口获取新的IP，获取的新IP为:{}",emailParam.getEmailSender(),socksIp);
//        }

        LOGGER.info("使用代理发送邮件，代理信息为:{}",socksIp);
        props.setProperty("mail.smtp.socks.host", ProxyServer.getIp(socksIp));
        props.setProperty("mail.smtp.socks.port", String.valueOf(ProxyServer.getPort(socksIp)));

        return props;
    }

    public static String getRandomIp(){
        long timeSeed = System.nanoTime(); // to get the current date time value

        double randSeed = Math.random() * 1000; // random number generation

        long midSeed = (long) (timeSeed * randSeed); // mixing up the time and

        String s = midSeed + "";
        String subStr = s.substring(0, 9);
        return subStr;
    }

    public static Properties getQQProperties(EmailParam emailParam){

        Properties props = System.getProperties();
        props.put("mail.smtp.ssl.enable", true);
        props.put("mail.smtp.port", 465);//设置端口
        props.setProperty("proxySet", "true");
        String socksIp = ProxyServer.getProxyLine(ProxyServer.ServerGetProxyUrl);
        LOGGER.info("使用代理发送邮件，代理信息为:{}",socksIp);

        props.setProperty("mail.host", emailParam.getEmailHost());
        props.setProperty("mail.transport.protocol", emailParam.getEmailProtocol());

        props.setProperty("mail.smtp.auth", emailParam.getEmailAuth());


        return props;
    }

    public static Properties getOtherProperties(EmailParam emailParam){

        //使用代理请求网易得时候，可能会出现异常，多试几次应该就可以了，其他邮箱可能也有这样得情况
        Properties props = System.getProperties();

//        props.put("mail.smtp.ssl.enable", true);
//        props.put("mail.smtp.port", 465);//设置端口
//        props.setProperty("proxySet", "true");
//        String socksIp = ProxyServer.getProxyLine(ProxyServer.ServerGetProxyUrl);
//        LOGGER.info("使用代理发送邮件，代理信息为:{}",socksIp);
//        props.setProperty("mail.smtp.socks.host", ProxyServer.getIp(socksIp));
//        props.setProperty("mail.smtp.socks.port", String.valueOf(ProxyServer.getPort(socksIp)));
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
