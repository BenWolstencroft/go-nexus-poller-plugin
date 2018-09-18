package plugin.go.nexus.models;

import java.util.List;
import java.util.Random;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class ExtDirectRequest extends ExtDirectBase {

    @SerializedName("data") public List<String> data;

    public static ExtDirectRequest createExtDirectRequest(String action, String method, String type, List<String> data) {
        ExtDirectRequest request = new ExtDirectRequest();
        request.action = action;
        request.method = method;
        request.type = type;
        request.data = data;
        request.tid = ((new Random()).nextInt(100) + 1);
        return request;
    }

    public String toJson() {
        Gson g = new Gson();
        return g.toJson(this);
    }
}