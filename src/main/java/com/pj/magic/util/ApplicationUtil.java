package com.pj.magic.util;

public class ApplicationUtil {

    public static boolean isServer() {
        return ApplicationUtil.class.getClassLoader().getResourceAsStream("server.properties") != null;
    }
    
}
