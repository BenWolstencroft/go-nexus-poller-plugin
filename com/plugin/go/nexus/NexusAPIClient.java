package plugin.go.nexus;

import java.util.List;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.thoughtworks.go.plugin.api.logging.Logger;
import plugin.go.nexus.models.*;

public class NexusAPIClient {
    private static Logger logger = Logger.getLoggerFor(ConnectionHandler.class);

    public static final String API_NAME = "Nexus Repository Manager REST API";
    public static final String API_REPOSITORIES = "REPOSITORIES";
    public static final String API_SEARCH = "SEARCH";

    private String url;
    private String name;
    private String username = null;
    private String password = null;

    public NexusAPIClient(String url, String name, String username, String password) {
        this.url = url;
        this.name = name;
        if (username != null && !username.isEmpty()) {
            this.username = username;
        }
        if (password != null && !password.isEmpty()) {
            this.password = password;
        }
    }

    private String getApiEndpoint(String endpointName, String queryString) {
        String endpoint = "";
        if (endpointName.equals(API_REPOSITORIES)) {
            endpoint = "/service/rest/v1/repositories";
        } else if (endpointName.equals(API_SEARCH)) {
            endpoint = "/service/rest/v1/search";
        }
        return this.url + endpoint + queryString;
    }

    private URLConnection getConnection(String endpointName, String queryString) throws Exception {
        URL repo = new URL(getApiEndpoint(endpointName, queryString));
        URLConnection connection = repo.openConnection();
        if (this.username != null && this.password != null) {
            String userpass = this.username + ':' + this.password;
            String authToken = "Basic " + new String(Base64.getEncoder().encode(userpass.getBytes()));
            connection.setRequestProperty("Authorization", authToken);
        }
        return connection;
    }

    private String getText(String endpointName, String queryString) throws Exception {
        URLConnection connection = getConnection(endpointName, queryString);
        BufferedReader in = new BufferedReader(
                                new InputStreamReader(
                                    connection.getInputStream()));

        StringBuilder response = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null)
            response.append(inputLine);
        in.close();
        return response.toString();
    }

    public List<Repository> getRepositories() throws Exception {
        String text = getText(API_REPOSITORIES, "");
        Gson g = new Gson();
        Type collectionType = new TypeToken<List<Repository>>() {}.getType();
        List<Repository> r = g.fromJson(text, collectionType);
        return r;
    }

}