package plugin.go.nexus.models;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Component {
    @SerializedName("id") final public String id = null;
    @SerializedName("repository") final public String repository = null;
    @SerializedName("format") final public String format = null;
    @SerializedName("group") final public String group = null;
    @SerializedName("name") final public String name = null;
    @SerializedName("version") final public String version = null;
    @SerializedName("assets") final public List<Asset> assets = null;
}