package plugin.go.nexus.models;

import com.google.gson.annotations.SerializedName;

public class ExtDirectResult<ResultType> {

    @SerializedName("success") public Boolean success;
    @SerializedName("data") public ResultType data;

}