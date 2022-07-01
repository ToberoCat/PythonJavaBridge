package io.github.toberocat.server;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class Server extends AbstractServer {

    public Server(int port) {
        super(port);

        addMethod("no_response_interval_answer", ((objects, reply) -> {
            System.out.println("Received answer " + Arrays.toString(objects.toArray()));
        }));

        addMethod("ping", ((objects, reply) -> {
            reply.accept(new Package("pong", "Add data types"));
        }));

        createServer();
    }
}
