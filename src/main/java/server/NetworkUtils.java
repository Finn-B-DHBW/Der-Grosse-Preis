package server;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public final class NetworkUtils {

    private NetworkUtils() {
    }

    /**
     * Ermittelt die LAN-IPv4-Adresse dieses Rechners, damit sich Spieler
     * im selben Netzwerk verbinden koennen. Faellt auf "localhost" zurueck.
     */
    public static String getLocalIpAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue;
                }
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (address instanceof Inet4Address && !address.isLinkLocalAddress()) {
                        return address.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            // fall through to localhost
        }
        return "localhost";
    }
}
