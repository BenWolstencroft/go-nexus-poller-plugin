package plugin.go.nexus.models;

import com.google.gson.annotations.SerializedName;

public class ExtDirectBase {
    public static String ACTION_COREUI_COMPONENT = "coreui_Component";
    public static String METHOD_READASSET = "readAsset";
    public static String TYPE = "rpc";

    @SerializedName("tid") public Integer tid;
    @SerializedName("action") public String action;
    @SerializedName("method") public String method;
    @SerializedName("type") public String type;
}