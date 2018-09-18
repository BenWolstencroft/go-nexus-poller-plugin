package plugin.go.nexus.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.annotations.SerializedName;

public class AssetData {
    private static String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    @SerializedName("id") public String id;
    @SerializedName("name") public String name;
    @SerializedName("format") public String format;
    @SerializedName("contentType") public String contentType;
    @SerializedName("size") public Integer size;
    @SerializedName("repositoryName") public String repositoryName;
    @SerializedName("containingRepositoryName") public String containingRepositoryName;
    @SerializedName("blobCreated") public String blobCreated;
    @SerializedName("createdBy") public String createdBy;

    public Date getBlobCreated() {
        SimpleDateFormat parser = new SimpleDateFormat(DATE_FORMAT);
        try {
            Date date = parser.parse(this.blobCreated);
            return date;
        } catch (ParseException e) {
            return null;
        }
    }
}