import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Main {
  public static void main(String[] args) throws Exception {
    String address = System.getenv().getOrDefault("UDP_ADDRESS", "127.0.0.1");
    int port = Integer.parseInt(System.getenv().getOrDefault("UDP_PORT", "5000"));

    InetAddress host = InetAddress.getByName(address);
    DatagramSocket socket = new DatagramSocket(port, host);
    byte[] buffer = new byte[2048];

    while (true) {
      DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

      socket.receive(packet);

      String message = new String(packet.getData(), 0, packet.getLength());

      System.out.printf(
          "UDP received from %s:%d on %s:%d -> %s%n",
          packet.getAddress().getHostAddress(),
          packet.getPort(),
          address,
          port,
          message
      );
    }
  }
}