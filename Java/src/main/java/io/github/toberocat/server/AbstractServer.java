package io.github.toberocat.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class AbstractServer implements Runnable {

    private final ServerSocket server;
    private final ObjectMapper mapper;
    private final LinkedHashMap<String, BiConsumer<List<Object>, Consumer<Package>>> methods;
    private Socket connected;
    private boolean running;

    public AbstractServer(int port) {
        ServerSocket server1;
        try {
            server1 = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
            server1 = null;
        }
        this.server = server1;
        this.running = true;
        this.mapper = new ObjectMapper();
        this.methods = new LinkedHashMap<>();
    }

    public void sendEvent(Package pack) {
        if (connected == null || connected.isClosed()) return;
        PrintWriter outputPipeline = null;
        try {
            outputPipeline = new PrintWriter(connected.getOutputStream());
            outputPipeline.write(packToString(pack));
            outputPipeline.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createServer() {
        new Thread(this).start();
    }

    private Package parsePackage(@NotNull String raw) {
        return mapper.readValue(raw, Package.class);
    }

    private String packToString(Package pack) {
        return mapper.writeValueAsString(pack);
    }


    private void worker() throws IOException {
        Socket socket = server.accept();
        connected = socket;

        if (socket == null) return;
        System.out.println("Python connected");


        PrintWriter outputPipeline = new PrintWriter(socket.getOutputStream());
        InputStream is = socket.getInputStream();
        while (!socket.isClosed()) {
            byte[] buffer = new byte[1024];
            int read = is.read(buffer);
            if (read == -1) continue;

            Package pack = parsePackage(new String(buffer, 0, read));
            if (methods.containsKey(pack.getId())) {
                methods.get(pack.getId()).accept(pack.getData(), (reply) -> {
                    outputPipeline.write(packToString(reply));
                    outputPipeline.flush();
                });
            }

        }

        outputPipeline.close();
        is.close();
        socket.close();
        connected = null;
    }

    @Override
    public void run() {
        while (running) {
            try {
                worker();
            } catch (IOException e) {
                System.out.println("Python disconnected");
                connected = null;
            }
        }
    }

    public void addMethod(String id, BiConsumer<List<Object>, Consumer<Package>> action) {
        methods.put(id, action);
    }
}
