package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RequestParser {

    public static RequestInfo parseRequest(BufferedReader reader) throws IOException {
        String httpMethod;
        String httpUri;
        Map<String,String> params = new HashMap<>();

        String requestLine = reader.readLine();
        if (requestLine.isEmpty() || requestLine == null){
            throw new IllegalArgumentException("The request line is invalid");
        }

        String [] requestParts = requestLine.split(" ");
        if (requestParts.length < 2){
            throw new IllegalArgumentException("Invalid request line");
        }

        httpMethod = requestParts[0];
        httpUri = requestParts[1];

        String [] httpUriParts = httpUri.split("\\?");
        String [] httpUriSegments = httpUriParts[0].split("/");
        httpUriSegments = Arrays.stream(httpUriSegments).filter(
                segment -> !segment.isEmpty()).toArray(String[]::new);

        if(httpUriParts.length > 1){
            String queryString = httpUriParts[1];
            String [] queryParams = queryString.split("&");
            for (String param : queryParams){
                String [] keyValue = param.split("=");
                if (keyValue.length == 2){
                    params.put(keyValue[0],keyValue[1]);
                }
            }
        }

        String line ;
        int contentLength = 0;
        while ((line = reader.readLine()) != null && !line.isEmpty()){
            if(line.startsWith("Content-Length:")){
                contentLength = Integer.parseInt(line.split(":")[1].trim());
            }
        }

        if (contentLength > 0){
            String paramLine = reader.readLine();
            if (paramLine!= null && paramLine.contains("=")){
                String [] paramParts = paramLine.split("=");
                if (paramParts.length == 2){
                    params.put(paramParts[0],paramParts[1]);
                }
            }
            reader.readLine();

            String contentLine = reader.readLine();

            if (contentLine != null) {
                String finalContent = contentLine + "\n";
                byte[] contentBytes = finalContent.getBytes();
                return new RequestInfo(httpMethod, httpUri, httpUriSegments, params, contentBytes);
            }

        }

        return new RequestInfo(httpMethod, httpUri, httpUriSegments, params, new byte[0]);

    }

    // RequestInfo given internal class
    public static class RequestInfo {
        private final String httpCommand;
        private final String uri;
        private final String[] uriSegments;
        private final Map<String, String> parameters;
        private final byte[] content;

        public RequestInfo(String httpCommand, String uri, String[] uriSegments, Map<String, String> parameters, byte[] content) {
            this.httpCommand = httpCommand;
            this.uri = uri;
            this.uriSegments = uriSegments;
            this.parameters = parameters;
            this.content = content;
        }

        public String getHttpCommand() {
            return httpCommand;
        }

        public String getUri() {
            return uri;
        }

        public String[] getUriSegments() {
            return uriSegments;
        }

        public Map<String, String> getParameters() {
            return parameters;
        }

        public byte[] getContent() {
            return content;
        }
    }
}
