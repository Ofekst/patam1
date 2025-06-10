package graph;

import java.io.IOException;
import java.io.OutputStream;

import graph.RequestParser.RequestInfo;

public interface Servlet {
    void handle(RequestInfo ri, OutputStream toClient) throws IOException;
    void close() throws IOException;
}

