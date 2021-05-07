package com.rookie.send.email.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * 动态采集代理IP
 * @author Administrator
 *
 */
public class ProxyIP {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyIP.class) ;

	public static Map<String, String> getIpAddress() {
        Map<String, String> maps = new HashMap<String, String>();
        for(int i = 1 ; i < 20; ++i) {
            try {
                Document doc = Jsoup.connect("http://www.xicidaili.com/nn/" + i)
                        .data("query", "Java")
                        .userAgent("Netscape/5")
                        .cookie("auth", "token")
                        .timeout(3000)
                        .get();
                String regex = "((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))";
                Elements elements = doc.select("td:matches(" + regex + ")");
                for(int j = 0; j < elements.size(); ++j) {
                    Element e = (Element) elements.get(j);
                    Element e1 = e.nextElementSibling();
                    String ip = e.text();
                    String prot = e1.text();
                    if(isPing(ip)) {
//                        System.out.println(ip + " " + prot);
                        maps.put("ip", ip);
                        maps.put("prot", prot);
                        return maps; 
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
	
    public static boolean isPing(String ip) {
        boolean status = false;
        if(ip != null) {
            try {
                status = InetAddress.getByName(ip).isReachable(3000);
            }
            catch(IOException e) {
            	System.err.println(e.getMessage());
            }
        }
        return status;
    }
	
}
