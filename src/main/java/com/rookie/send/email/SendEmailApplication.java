package com.rookie.send.email;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

@SpringBootApplication
public class SendEmailApplication {

    /**
     * 信任所有证数：请求HTTPS网址时需要，如果请求HTTP网址可直接删除trustAllHttpsCertificates()与class miTM
     * @throws Exception
     */
    private static void trustAllHttpsCertificates() throws Exception {
        javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
        javax.net.ssl.TrustManager tm = new miTM();
        trustAllCerts[0] = tm;
        javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext.getInstance("TLS");
        sc.init(null, trustAllCerts, null);
        javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    }
    static class miTM implements javax.net.ssl.TrustManager, javax.net.ssl.X509TrustManager {
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }
        public boolean isServerTrusted(java.security.cert.X509Certificate[] certs) {
            return true;
        }
        public boolean isClientTrusted(java.security.cert.X509Certificate[] certs) {
            return true;
        }
        public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) throws java.security.cert.CertificateException {
            return;
        }
        public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) throws java.security.cert.CertificateException {
            return;
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(SendEmailApplication.class, args);
//        Map<String, String> map = ProxyIP.getIpAddress();
//        String ip = map.get("ip");
//        String prot = map.get("prot");
//        System.out.println(ip + ":" + prot);

        // 如果爬虫请求HTTPS网址，必须加入这两行
//        System.setProperty("jdk.http.auth.proxying.disabledSchemes", "");
//        System.setProperty("jdk.http.auth.tunneling.disabledSchemes", "");
//
//        // 要请求的网址
//        final String targetUrl = "https://pv.sohu.com/cityjson?ie=utf-8";
//
//        // 代理IP
//        final String httpsIpport = "tunnel.xiaozhudaili:15678";
//        // 代理用户名和密码
//        final String username = "13530975365";
//        final String password = "qwertyu1234";
//
//        // 发送请求次数
//        int requestTime = 5;
//
//        for(int i = 0; i < requestTime; i++) {
//            final int reqNo = i;
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//
//                        // 信任所有证书，当请求HTTPS网址时需要，该部分必须在获取connection前调用
//                        trustAllHttpsCertificates();
//                        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
//                            public boolean verify(String urlHostName, SSLSession session) {
//                                return true;
//                            }
//                        });
//
//                        URL link = new URL(targetUrl);
//
//                        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress((httpsIpport.split(":"))[0], Integer.parseInt((httpsIpport.split(":"))[1])));
//                        HttpURLConnection connection = (HttpURLConnection)link.openConnection(proxy);
//
//                        // Java系统自带的鉴权模式，请求HTTPS网址时需要
//                        Authenticator.setDefault(new Authenticator() {
//                            public PasswordAuthentication getPasswordAuthentication() {
//                                return new PasswordAuthentication(username, password.toCharArray());
//                            }
//                        });
//
//                        connection.setRequestMethod("GET");
//                        connection.setDoInput(true);
//                        connection.setDoOutput(true);
//                        connection.setUseCaches(false);
//
//                        // 设置超时时间为60秒
//                        connection.setConnectTimeout(60000);
//
//                        connection.connect();
//
//                        String line = null;
//                        StringBuilder html = new StringBuilder();
//                        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
//                        while((line = reader.readLine()) != null){
//                            html.append(line);
//                        }
//                        try {
//                            if (reader != null) {
//                                reader.close();
//                            }
//                        } catch (Exception e) {
//                        }
//
//                        connection.disconnect();
//
//                        // 输出结果
//                        System.out.println(reqNo + " [OK]" + "→→→→→" + targetUrl + "  " + connection.getResponseCode() + "   " + html.toString());
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        System.err.println(reqNo + " [ERR]" + "→→→→→" + e.getMessage());
//                    }
//                }
//            }).start();
//        }

    }

}
