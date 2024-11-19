package server;

import java.io.PrintWriter;

public class ConnectedClients {
    private Integer port;
    private PrintWriter out;

    public ConnectedClients(Integer port, PrintWriter out) {
        this.port = port;
        this.out = out;
    }

    public Integer getPort() {
        return port;
    }

    public PrintWriter getOut() {
        return out;
    }

}
