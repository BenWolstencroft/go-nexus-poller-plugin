package plugin.go.nexus.models;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class ListResult<T> {
    @SerializedName("items") final public List<T> items = null;
    @SerializedName("continuationToken") final public String continuationToken = null;
}