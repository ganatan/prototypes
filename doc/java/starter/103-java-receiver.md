**## Projet java-receiver**

**## Commandes essentielles**

```bash
javac Main.java
java Main
```

**## Dépendances**

Aucune dépendance externe.

**## Configuration locale**

Créer `.env` à la racine :

```env
UDP_ADDRESS=127.0.0.1
UDP_PORT=5000
```

Le fichier `.env` est optionnel.

Valeurs utilisées par défaut :

```text
UDP_ADDRESS=127.0.0.1
UDP_PORT=5000
```

**## src/Main.java**

```java
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
```

**## Compilation**

Se placer dans `src` :

```bash
cd src
javac Main.java
java Main
```

**## Création du JAR**

Toujours dans `src` :

```bash
jar --create --file java-receiver.jar --main-class Main Main.class
```

Exécuter le JAR :

```bash
java -jar java-receiver.jar
```

**## Résultat**

```text
UDP received from 127.0.0.1:54321 on 127.0.0.1:5000 -> {"distance":1100,"name":"target-001"}
UDP received from 127.0.0.1:54321 on 127.0.0.1:5000 -> {"distance":1200,"name":"target-002"}
UDP received from 127.0.0.1:54321 on 127.0.0.1:5000 -> {"distance":1300,"name":"target-003"}
```