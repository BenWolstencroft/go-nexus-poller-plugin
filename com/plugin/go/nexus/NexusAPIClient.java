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

    private URLConnection getConnection(String endpoint, String searchValue) throws Exception {
        URL repo = new URL(getApiEndpoint(endpoint, searchValue));
        URLConnection connection = repo.openConnection();
        if (this.username != null && this.password != null) {
            String userpass = this.username + ':' + this.password;
            String authToken = "Basic " + new String(Base64.getEncoder().encode(userpass.getBytes()));
            connection.setRequestProperty("Authorization", authToken);
        }
        return connection;
    }

    private String getText(String endpoint, String searchValue) throws Exception {
        URLConnection connection = getConnection(endpoint, searchValue);
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

    public ListResult<Asset> assets() throws Exception {
        String text = getText(API_ASSETS, "");
        Gson g = new Gson();
        Type resultType = new TypeToken<ListResult<Asset>>() {}.getType();
        ListResult<Asset> r = g.fromJson(text, resultType);
        return r;
    }

    public Asset asset(String id) throws Exception {
        String text = getText(API_ASSET, id);
        Gson g = new Gson();
        Type resultType = new TypeToken<Asset>() {}.getType();
        Asset r = g.fromJson(text, resultType);
        return r;
    }

    public ListResult<Component> components() throws Exception {
        String text = getText(API_COMPONENTS, "");
        Gson g = new Gson();
        Type resultType = new TypeToken<ListResult<Component>>() {}.getType();
        ListResult<Component> r = g.fromJson(text, resultType);
        return r;
    }

    public Component component(String id) throws Exception {
        String text = getText(API_COMPONENT, id);
        Gson g = new Gson();
        Type resultType = new TypeToken<Component>() {}.getType();
        Component r = g.fromJson(text, resultType);
        return r;
    }

    public ListResult<Format> formats() throws Exception {
        String text = getText(API_FORMATS, "");
        Gson g = new Gson();
        Type resultType = new TypeToken<ListResult<Format>>() {}.getType();
        ListResult<Format> r = g.fromJson(text, resultType);
        return r;
    }

    public Format format(String format) throws Exception {
        String text = getText(API_FORMAT, format);
        Gson g = new Gson();
        Type resultType = new TypeToken<Format>() {}.getType();
        Format r = g.fromJson(text, resultType);
        return r;
    }

    public ReadOnly readOnly() throws Exception {
        String text = getText(API_READONLY, "");
        Gson g = new Gson();
        Type resultType = new TypeToken<ReadOnly>() {}.getType();
        ReadOnly r = g.fromJson(text, resultType);
        return r;
    }

    public List<Repository> repositories() throws Exception {
        String text = getText(API_REPOSITORIES, "");
        Gson g = new Gson();
        Type resultType = new TypeToken<List<Repository>>() {}.getType();
        List<Repository> r = g.fromJson(text, resultType);
        return r;
    }

    public ListResult<Script> scripts() throws Exception {
        String text = getText(API_SCRIPTS, "");
        Gson g = new Gson();
        Type resultType = new TypeToken<ListResult<Script>>() {}.getType();
        ListResult<Script> r = g.fromJson(text, resultType);
        return r;
    }

    public Script script(String name) throws Exception {
        String text = getText(API_SCRIPT, name);
        Gson g = new Gson();
        Type resultType = new TypeToken<Script>() {}.getType();
        Script r = g.fromJson(text, resultType);
        return r;
    }

    public ListResult<Component> search() throws Exception {
        throw new Exception();
    }

    public ListResult<Asset> searchAssets() throws Exception {
        throw new Exception();
    }

    public ListResult<Task> tasks() throws Exception {
        String text = getText(API_TASKS, "");
        Gson g = new Gson();
        Type resultType = new TypeToken<ListResult<Task>>() {}.getType();
        ListResult<Task> r = g.fromJson(text, resultType);
        return r;
    }

    public Task task(String id) throws Exception {
        String text = getText(API_TASK, id);
        Gson g = new Gson();
        Type resultType = new TypeToken<Task>() {}.getType();
        Task r = g.fromJson(text, resultType);
        return r;
    }
}