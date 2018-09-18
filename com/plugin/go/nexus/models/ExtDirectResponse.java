package plugin.go.nexus.models;

import java.util.Random;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class ExtDirectResponse<ResultType> extends ExtDirectBase {
    @SerializedName("result") public ExtDirectResult<ResultType> result;
}