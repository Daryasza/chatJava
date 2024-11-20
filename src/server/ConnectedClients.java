package server;

import java.io.PrintWriter;

//record class
public record ConnectedClients(Integer port, PrintWriter out) {}
