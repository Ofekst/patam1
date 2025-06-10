package test;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class MainTrain { // RequestParser


    private static void testParseRequest() {
        // Test data
        String request = "GET /api/resource?id=123&name=test HTTP/1.1\n" +
                "Host: example.com\n" +
                "Content-Length: 5\n"+
                "\n" +
                "filename=\"hello_world.txt\"\n"+
                "\n" +
                "hello world!\n"+
                "\n" ;

        BufferedReader input=new BufferedReader(new InputStreamReader(new ByteArrayInputStream(request.getBytes())));
        try {
            RequestParser.RequestInfo requestInfo = RequestParser.parseRequest(input);

            // Test HTTP command
            if (!requestInfo.getHttpCommand().equals("GET")) {
                System.out.println("HTTP command test failed (-5)");
            }

            // Test URI
            if (!requestInfo.getUri().equals("/api/resource?id=123&name=test")) {
                System.out.println("URI test failed (-5)");
            }

            // Test URI segments
            String[] expectedUriSegments = {"api", "resource"};
            if (!Arrays.equals(requestInfo.getUriSegments(), expectedUriSegments)) {
                System.out.println("URI segments test failed (-5)");
                for(String s : requestInfo.getUriSegments()){
                    System.out.println(s);
                }
            }
            // Test parameters
            Map<String, String> expectedParams = new HashMap<>();
            expectedParams.put("id", "123");
            expectedParams.put("name", "test");
            expectedParams.put("filename","\"hello_world.txt\"");
            if (!requestInfo.getParameters().equals(expectedParams)) {
                System.out.println("Parameters test failed (-5)");
            }

            // Test content
            byte[] expectedContent = "hello world!\n".getBytes();
            if (!Arrays.equals(requestInfo.getContent(), expectedContent)) {
                System.out.println("Content test failed (-5)");
            }
            input.close();
        } catch (IOException e) {
            System.out.println("Exception occurred during parsing: " + e.getMessage() + " (-5)");
        }
    }

    public static void testServer() throws Exception{
        MyHTTPServer server = new MyHTTPServer(8080, 1);

        // Register a simple servlet
        server.addServlet("GET", "/api/message", new Servlet() {
            @Override
            public void handle(RequestParser.RequestInfo request, OutputStream output) throws IOException {
                String name = request.getParameters().getOrDefault("name", "Guest");
                String response = "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\n\r\nHello, " + name;
                output.write(response.getBytes());
                output.flush();
            }

            @Override
            public void close() throws IOException {
                System.out.println("Closing servlet for /api/message");
            }
        });

        // Start the server
        server.start();

        // Run test cases
        testSingleClient();
        testServerShutdown(server);
    }
    private static void testSingleClient() throws IOException {
        System.out.println("Running single client test...");

        String request = "GET /api/message?name=John HTTP/1.1\r\nHost: localhost\r\n\r\n";
        String expectedResponse = "Hello, John";

        try (Socket socket = new Socket("localhost", 8080);
             OutputStream outputStream = socket.getOutputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Send the request
            outputStream.write(request.getBytes());
            outputStream.flush();

            // Read the response headers
            StringBuilder responseHeaders = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                responseHeaders.append(line).append("\n");
            }

            // Read the body
            StringBuilder body = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }

            System.out.println("Server response: " + body.toString());
            if (!body.toString().contains(expectedResponse)) {
                System.err.println("Test Failed: Unexpected server response.");
            } else {
                System.out.println("Test Passed: Single client test.");
            }
        }
    }

    private static void testServerShutdown(MyHTTPServer server) {
        System.out.println("Testing server shutdown...");
        try {
            server.close();

            // Wait for a short time to ensure the server shuts down
            TimeUnit.SECONDS.sleep(2);

            // Attempting to connect should fail
            try (Socket socket = new Socket("localhost", 8080)) {
                System.err.println("Test Failed: Server is still accepting connections.");
            } catch (IOException e) {
                System.out.println("Test Passed: Server successfully shut down.");
            }

        } catch (Exception e) {
            System.err.println("Test Failed: Error during server shutdown test: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        testParseRequest(); // 40 points
        try{
            testServer(); // 60
        }catch(Exception e){
            System.out.println("your server throwed an exception (-60)");
        }
        System.out.println("done");
    }
}
