package server;

import java.io.PrintWriter;

//record class: creates private final fields, initialises them in constructor
// and creates getters: (ConnectedClients.port(), ConnectedClients.out())
public record ConnectedClients(Integer port, PrintWriter out) {}
