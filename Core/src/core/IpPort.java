package core;

import java.io.Serializable;
import java.net.InetAddress;

public class IpPort implements Serializable {
    InetAddress ip;
    int port;

    public IpPort(InetAddress ip, int port) {
        this.ip = ip;
        this.port = port;
    }
}