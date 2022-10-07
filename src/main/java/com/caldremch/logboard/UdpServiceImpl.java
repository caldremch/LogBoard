package com.caldremch.logboard;

import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by Leon on 2022/10/6
 */
@Service
public class UdpServiceImpl implements UdpService {

    public static String getIpv4() {
        Enumeration<NetworkInterface> IFaces = null;
        try {
            IFaces = NetworkInterface.getNetworkInterfaces();
            while (IFaces.hasMoreElements()) {
                NetworkInterface fInterface = IFaces.nextElement();
                if (!fInterface.isVirtual() && !fInterface.isLoopback() && fInterface.isUp()) {
                    Enumeration<InetAddress> adds = fInterface.getInetAddresses();
                    while (adds.hasMoreElements()) {
                        InetAddress address = adds.nextElement();
                        byte[] bs = address.getAddress();
                        if (bs.length == 4)
                            return address.getHostAddress();
                    }
                }
            }

        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        return "";

    }

    @Override
    public String getIPV4Address() {
        return getIpv4();
    }
}
