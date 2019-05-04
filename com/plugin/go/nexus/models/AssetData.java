package plugin.go.nexus.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.annotations.SerializedName;
import com.thoughtworks.go.plugin.api.logging.Logger;

public class AssetData {
    private static Logger logger = Logger.getLoggerFor(AssetData.class);

    private static String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    private static String DATE_ISO8106_TIMEZONE_MATCHER = "(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}[+-]\\d{2}):(\\d{2})";
    private static String DATE_UTIL_JAVA_TIMEZONE_FORMATTER = "$1$2";

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
        logger.debug("date format: [" + DATE_FORMAT + "]");
        SimpleDateFormat parser = new SimpleDateFormat(DATE_FORMAT);

        // The blobCreated datetime string received from nexus API is a ISO 8106 compliant offset datetime string which the java.util.Date class does not handle
        // The alternative to this adaptation is to replace the usage of java.util.Date with the newer java.time classes which support ISO 8106 by default but
        // only come with Java8, limiting the application of the plugin to environments running jvm 8+ and that's not a decision I am comfortable making for the
        // project.
        String javaUtilDateConformantBlobCreated = this.blobCreated.replaceAll(DATE_ISO8106_TIMEZONE_MATCHER, DATE_UTIL_JAVA_TIMEZONE_FORMATTER);
        try {
            logger.debug("blobCreated: [" + javaUtilDateConformantBlobCreated + "]");
            Date date = parser.parse(javaUtilDateConformantBlobCreated);
            logger.debug("date: " + date + "]");

            return date;
        } catch (ParseException e) {
            logger.info("Something went wrong while trying to convert the blobCreated string to a date object.", e);

            return null;
        }
    }
}