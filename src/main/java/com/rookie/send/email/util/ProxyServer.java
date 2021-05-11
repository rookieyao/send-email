package com.rookie.send.email.util;

import com.alibaba.fastjson.JSON;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ProxyServer {

    public static final String ServerGetProxyUrl = "smtp.163.com";

    public static void main(String[] args) {
        String v = getProxyLine(ServerGetProxyUrl);
        System.out.println(getIp(v));
        System.out.println(getPort(v));
    }

    public static String getProxyLine(String proxyServerUrl) {
        try {

            // 访问目标网页
            URL url = new URL(proxyServerUrl);
            URLConnection conn = url.openConnection();
            // 读取返回数据
            InputStream in = conn.getInputStream();
            Map maps = (Map) JSON.parse(IO2String(in));

            if ((Integer) maps.get("code") == 200) {
                List<String> proxyLines = (List<String>) maps.get("result");
                if (proxyLines.isEmpty()) {
                    return null;
                }

                Random random = new Random();
                return proxyLines.get(random.nextInt(proxyLines.size()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getIp(String parseLine) {
        if (StringUtils.isEmpty(parseLine)) {
            return null;
        }

        String[] split = parseLine.split(",");

        return split[0].split(":")[0];
    }

    public static Integer getPort(String parseLine) {
        if (StringUtils.isEmpty(parseLine)) {
            return null;
        }

        String[] split = parseLine.split(",");

        return Integer.parseInt(split[0].split(":")[1]);
    }

    /**
     * 将输入流转换成字符串
     *
     * @param inStream
     * @return
     * @throws IOException
     */
    public static String IO2String(InputStream inStream) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inStream.read(buffer)) != -1) {
            result.write(buffer, 0, len);
        }
        String str = result.toString(StandardCharsets.UTF_8.name());
        return str;
    }
}
