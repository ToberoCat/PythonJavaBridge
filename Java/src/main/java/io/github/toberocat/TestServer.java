package io.github.toberocat;

import io.github.toberocat.server.Package;
import io.github.toberocat.server.Server;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

class TestServer {

    private static Server server;

    public static void main(String[] args) {
        server = new Server(1337);

        Runnable helloRunnable = () -> {
            server.sendEvent(new Package("join_world"));
            System.out.println("Send event");
        };

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(helloRunnable, 0, 3, TimeUnit.SECONDS);
    }
}