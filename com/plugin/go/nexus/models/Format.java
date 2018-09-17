package plugin.go.nexus.models;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Format {
    @SerializedName("format") final public String format = null;
    @SerializedName("multipleUpload") final public String multipleUpload = null;
    @SerializedName("componentFields") final public List<String> componentFields = null;
    @SerializedName("assetFields") final public List<AssetField> assetFields = null;
}