import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Main {
  public static void main(String[] args) throws Exception {
    String address = System.getenv().getOrDefault("UDP_ADDRESS", "127.0.0.1");
    int port = Integer.parseInt(System.getenv().getOrDefault("UDP_PORT", "5000"));
    long intervalMs = Long.parseLong(System.getenv().getOrDefault("UDP_INTERVAL_MS", "1000"));

    DatagramSocket socket = new DatagramSocket();
    InetAddress host = InetAddress.getByName(address);
    int index = 1;

    while (true) {
      int distance = 1000 + index * 100;
      String name = String.format("target-%03d", index);
      String message = String.format("{\"distance\":%d,\"name\":\"%s\"}", distance, name);

      byte[] data = message.getBytes();
      DatagramPacket packet = new DatagramPacket(data, data.length, host, port);

      socket.send(packet);
      System.out.printf("UDP sent to %s:%d -> %s%n", address, port, message);

      index++;
      Thread.sleep(intervalMs);
    }
  }
}