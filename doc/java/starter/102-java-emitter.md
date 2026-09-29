**## Projet java-emitter**

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
UDP_INTERVAL_MS=1000
```

Le fichier `.env` est optionnel.

Valeurs utilisées par défaut :

```text
UDP_ADDRESS=127.0.0.1
UDP_PORT=5000
UDP_INTERVAL_MS=1000
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
```

**## Compilation**

Se placer dans `src` :

```bash
cd src
javac Main.java
java Main
```

**## Résultat**

```text
UDP sent to 127.0.0.1:5000 -> {"distance":1100,"name":"target-001"}
UDP sent to 127.0.0.1:5000 -> {"distance":1200,"name":"target-002"}
UDP sent to 127.0.0.1:5000 -> {"distance":1300,"name":"target-003"}
```

**## Configuration CI/CD**

Priorité de configuration :

```text
variables d'environnement
→ valeurs par défaut
```

En local :

```text
variables d'environnement
→ java-emitter
```

Avec Docker / OpenShift :

```text
ConfigMap / Secret
→ variables d'environnement
→ java-emitter
```

En CI/CD, les variables sont injectées par l'environnement :

```text
UDP_ADDRESS
UDP_PORT
UDP_INTERVAL_MS
```