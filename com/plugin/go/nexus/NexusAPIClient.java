package plugin.go.nexus;

import java.util.List;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.HashMap;
import java.util.stream.*;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.thoughtworks.go.plugin.api.logging.Logger;
import plugin.go.nexus.models.*;

public class NexusAPIClient {
    private static Logger logger = Logger.getLoggerFor(ConnectionHandler.class);

    public static final String API_NAME = "Nexus Repository Manager REST API";
    public static final String API_ROOT = "/service/rest/v1";

    public static final String API_ASSETS = "/assets";
    public static final String API_ASSET = "/assets/{searchValue}";
    public static final String API_COMPONENTS = "/components";
    public static final String API_COMPONENT = "/components/{searchValue}";
    public static final String API_FORMATS = "/formats/upload-specs";
    public static final String API_FORMAT = "/formats/{format}/upload-specs";
    public static final String API_READONLY = "/read-only";
    public static final String API_REPOSITORIES = "/repositories";
    public static final String API_SCRIPTS = "/script";
    public static final String API_SCRIPT = "/script/{searchValue}";
    public static final String API_SEARCH = "/search";
    public static final String API_SEARCHASSETS = "/search/assets";
    public static final String API_TASKS = "/tasks";
    public static final String API_TASK = "/tasks/{searchValue}";

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

    private String getApiEndpoint(String endpoint, String searchValue) {
        String endpointWithValue = endpoint.replace("{searchValue}", searchValue);
        return this.url + API_ROOT + endpointWithValue;
    }

    private URLConnection getConnection(String endpoint, String searchValue, HashMap<String, String> query, String continuationToken) throws Exception {
        String url = getApiEndpoint(endpoint, searchValue);
        if (query == null) {
            query = new HashMap<String, String>();
        }
        if (continuationToken != null && !continuationToken.isEmpty()) {
            query.put("continuationToken", continuationToken);
        }
        String queryString = "";
        if (query.size() > 0) {
            queryString = "?" + query
                .entrySet()
                .stream()
                .map((p) -> {
                    String out = "";
                    try {
                        out = URLEncoder.encode(p.getKey(), "UTF-8") + "=" + URLEncoder.encode(p.getValue(), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        logger.info("Exception: " + e.getMessage());
                    }
                    return out;
                })
                .reduce((p1, p2) -> p1 + "&" + p2)
                .orElse("");
        }
        URL repo = new URL(url + queryString);
        URLConnection connection = repo.openConnection();
        if (this.username != null && this.password != null) {
            String userpass = this.username + ':' + this.password;
            String authToken = "Basic " + new String(Base64.getEncoder().encode(userpass.getBytes()));
            connection.setRequestProperty("Authorization", authToken);
        }
        return connection;
    }

    private String getText(String endpoint, String searchValue, HashMap<String, String> query, String continuationToken) throws Exception {
        URLConnection connection = getConnection(endpoint, searchValue, query, continuationToken);
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

    public ListResult<Asset> assets(String continuationToken) throws Exception {
        String text = getText(API_ASSETS, "", null, continuationToken);
        Gson g = new Gson();
        Type resultType = new TypeToken<ListResult<Asset>>() {}.getType();
        ListResult<Asset> r = g.fromJson(text, resultType);
        return r;
    }

    public Asset asset(String id) throws Exception {
        String text = getText(API_ASSET, id, null, null);
        Gson g = new Gson();
        Type resultType = new TypeToken<Asset>() {}.getType();
        Asset r = g.fromJson(text, resultType);
        return r;
    }

    public ListResult<Component> components(String continuationToken) throws Exception {
        String text = getText(API_COMPONENTS, "", null, continuationToken);
        Gson g = new Gson();
        Type resultType = new TypeToken<ListResult<Component>>() {}.getType();
        ListResult<Component> r = g.fromJson(text, resultType);
        return r;
    }

    public Component component(String id) throws Exception {
        String text = getText(API_COMPONENT, id, null, null);
        Gson g = new Gson();
        Type resultType = new TypeToken<Component>() {}.getType();
        Component r = g.fromJson(text, resultType);
        return r;
    }

    public ListResult<Format> formats() throws Exception {
        String text = getText(API_FORMATS, "", null, null);
        Gson g = new Gson();
        Type resultType = new TypeToken<ListResult<Format>>() {}.getType();
        ListResult<Format> r = g.fromJson(text, resultType);
        return r;
    }

    public Format format(String format) throws Exception {
        String text = getText(API_FORMAT, format, null, null);
        Gson g = new Gson();
        Type resultType = new TypeToken<Format>() {}.getType();
        Format r = g.fromJson(text, resultType);
        return r;
    }

    public ReadOnly readOnly() throws Exception {
        String text = getText(API_READONLY, "", null, null);
        Gson g = new Gson();
        Type resultType = new TypeToken<ReadOnly>() {}.getType();
        ReadOnly r = g.fromJson(text, resultType);
        return r;
    }

    public List<Repository> repositories() throws Exception {
        String text = getText(API_REPOSITORIES, "", null, null);
        Gson g = new Gson();
        Type resultType = new TypeToken<List<Repository>>() {}.getType();
        List<Repository> r = g.fromJson(text, resultType);
        return r;
    }

    public ListResult<Script> scripts() throws Exception {
        String text = getText(API_SCRIPTS, "", null, null);
        Gson g = new Gson();
        Type resultType = new TypeToken<ListResult<Script>>() {}.getType();
        ListResult<Script> r = g.fromJson(text, resultType);
        return r;
    }

    public Script script(String name) throws Exception {
        String text = getText(API_SCRIPT, name, null, null);
        Gson g = new Gson();
        Type resultType = new TypeToken<Script>() {}.getType();
        Script r = g.fromJson(text, resultType);
        return r;
    }

    public ListResult<Component> search(String name, String continuationToken) throws Exception {
        HashMap<String, String> query = new HashMap<String, String>();
        query.put("repository", this.name);
        query.put("name", name);
        String text = getText(API_SEARCH, null, query, continuationToken);
        Gson g = new Gson();
        Type resultType = new TypeToken<ListResult<Component>>() {}.getType();
        ListResult<Component> r = g.fromJson(text, resultType);
        return r;
    }

    public ListResult<Asset> searchAssets(String name, String continuationToken) throws Exception {
        HashMap<String, String> query = new HashMap<String, String>();
        query.put("repository", this.name);
        query.put("name", name);
        String text = getText(API_SEARCHASSETS, null, query, continuationToken);
        Gson g = new Gson();
        Type resultType = new TypeToken<ListResult<Asset>>() {}.getType();
        ListResult<Asset> r = g.fromJson(text, resultType);
        return r;
    }

    public ListResult<Task> tasks(String type) throws Exception {
        HashMap<String, String> query = new HashMap<String, String>();
        if (type != null && !type.isEmpty()) {
            query.put("type", type);
        }
        String text = getText(API_TASKS, "", query, null);
        Gson g = new Gson();
        Type resultType = new TypeToken<ListResult<Task>>() {}.getType();
        ListResult<Task> r = g.fromJson(text, resultType);
        return r;
    }

    public Task task(String id) throws Exception {
        String text = getText(API_TASK, id, null, null);
        Gson g = new Gson();
        Type resultType = new TypeToken<Task>() {}.getType();
        Task r = g.fromJson(text, resultType);
        return r;
    }
}