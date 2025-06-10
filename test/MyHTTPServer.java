package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;


public class MyHTTPServer extends Thread implements HTTPServer{
    private final int port;
    private ServerSocket serverSocket;
    private volatile boolean running = true;

    private final Map<String, Servlet> getServlets = new HashMap<>();
    private final Map<String, Servlet> postServlets = new HashMap<>();
    private final Map<String, Servlet> deleteServlets = new HashMap<>();


    public MyHTTPServer(int port,int nThreads){
        this.port = port;
    }

    @Override
    public void addServlet(String httpCommanmd, String uri, Servlet s){
        switch (httpCommanmd.toUpperCase()){
            case "GET":
                this.getServlets.put(uri,s);
                break;
            case "POST":
                this.postServlets.put(uri,s);
                break;
            case  "DELETE":
                this.deleteServlets.put(uri,s);
            default:
                throw new IllegalArgumentException("Unsupported http method : "+httpCommanmd);
        }
    }

    @Override
    public void removeServlet(String httpCommanmd, String uri){
        switch (httpCommanmd.toUpperCase()){
            case "GET":
                this.getServlets.remove(uri);
                break;
            case "POST":
                this.postServlets.remove(uri);
                break;
            case  "DELETE":
                this.deleteServlets.remove(uri);
            default:
                throw new IllegalArgumentException("Unsupported http method : "+httpCommanmd);
        }

    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void run(){
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(1000);
            while (running){
                try {
                    Socket clientSocket = serverSocket.accept();
                    handleClient(clientSocket);
                }
                catch (SocketTimeoutException e){

                }
            }
        }
        catch (IOException e) {
            System.err.println("Error while starting or running the server: " + e.getMessage());
        }
    }


    private void handleClient(Socket clientSocket) {
        try (
                BufferedReader reader = new BufferedReader(new
                        InputStreamReader(clientSocket.getInputStream()));
                OutputStream out = clientSocket.getOutputStream()
        ) {
            RequestParser.RequestInfo requestInfo = RequestParser.parseRequest(reader);
            if (requestInfo != null) {
                Servlet servlet = findServlet(requestInfo.getHttpCommand(), requestInfo.getUri());
                if (servlet != null) {
                    servlet.handle(requestInfo, out);
                } else {
                    String response = "HTTP/1.1 404 Not Found\r\n\r\n";
                    out.write(response.getBytes());
                }
                out.flush();
            }
        } catch (IOException e) {
            System.err.println("Error while handling client request: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error while closing client socket: " + e.getMessage());
            }
        }
    }

    private Servlet findServlet(String httpCommand, String uri) {
        int questionMark = uri.indexOf('?');
        String matchUri = questionMark != -1 ? uri.substring(0, questionMark) : uri;

        Map<String, Servlet> servletMap;
        switch (httpCommand.toUpperCase()) {
            case "GET":
                servletMap = getServlets;
                break;
            case "POST":
                servletMap = postServlets;
                break;
            case "DELETE":
                servletMap = deleteServlets;
                break;
            default:
                return null;
        }

        String matchingUri = "";
        Servlet matchingServlet = null;

        for (String registeredUri : servletMap.keySet()) {
            if (matchUri.startsWith(registeredUri) && registeredUri.length() > matchingUri.length()) {
                matchingUri = registeredUri;
                matchingServlet = servletMap.get(registeredUri);
            }
        }

        return matchingServlet;
    }

    @Override
    public void close() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }

            for (Servlet s : getServlets.values()) s.close();
            for (Servlet s : postServlets.values()) s.close();
            for (Servlet s : deleteServlets.values()) s.close();

        } catch (IOException e) {
            System.err.println("Error while closing the server socket or servlets: " + e.getMessage());
        }
    }

}
