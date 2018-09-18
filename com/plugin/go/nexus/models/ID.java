package plugin.go.nexus.models;

import java.util.Base64;

public class ID {
    public String repository;
    public String id;

    public ID(String apiId) {
        byte[] data = Base64.getMimeDecoder().decode(apiId);
        String idData = new String(data);
        String[] idDataSplit = idData.split("\\:");
        this.repository = idDataSplit[0];
        this.id = idDataSplit[1];
    }
}