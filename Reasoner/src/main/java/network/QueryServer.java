package network;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reasoner.Reasoner;
import reasoner.ReasonerTrigger;

import java.io.IOException;

public class QueryServer {
    int port;
    Server server;

    public QueryServer(int port, Reasoner reasoner){
        ServerBuilder<?> builder = ServerBuilder.forPort(port);
        this.port = port;
        this.server = builder.addService(new QueryServiceImpl(new ReasonerTrigger(reasoner))).build();
    }

    public void start() throws IOException {
        server.start();
        System.out.println("Started Server, listening on " + port);
    }
}
