package com.caucho.hessian.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author chenyao
 * @date 2019/5/28 15:41
 * @description
 */
public class AddressUtil {
    private static InetAddress address;

    static {
        try {
            address = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public static String getIP() {
        return address.getHostAddress();
    }
}
