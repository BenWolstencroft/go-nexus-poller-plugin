package plugin.go.nexus.models;

import java.util.Date;
import java.util.List;
import java.util.Comparator;

import com.google.gson.annotations.SerializedName;
import com.thoughtworks.go.plugin.api.material.packagerepository.PackageRevision;

import plugin.go.nexus.NexusException;

public class Component implements Comparable<Component>  {
    @SerializedName("id") final public String id = null;
    @SerializedName("repository") final public String repository = null;
    @SerializedName("format") final public String format = null;
    @SerializedName("group") final public String group = null;
    @SerializedName("name") final public String name = null;
    @SerializedName("version") final public String version = null;
    @SerializedName("assets") final public List<Asset> assets = null;

    public Version getVersion() {
        return new Version(this.version);
    }

    public String getPackageLocation() {
        return assets.get(0).downloadUrl;
    }

    public String getAuthor() {
        return "Nexus Repository Manager";
    }

    public Date getPublishedDate() {
        return new Date();
    }

    public String getEntryTitle() {
        return this.name;
    }

    public String getPackageVersion() {
        return this.version;
    }

    public PackageRevision getPackageRevision(boolean lastVersionKnown) {
        if (assets.size() == 0) {
            if (lastVersionKnown) return null;
            else throw new NexusException("No such package found");
        }
        if (assets.size() > 1)
            throw new NexusException(String.format("Multiple entries in feed for %s %s", getEntryTitle(), getPackageVersion()));
        PackageRevision result = new PackageRevision(getPackageLabel(), getPublishedDate(), getAuthor(), getReleaseNotes(), getProjectUrl());
        result.addData("LOCATION", getPackageLocation());
        result.addData("VERSION", getPackageVersion());
        return result;
    }

    private String getReleaseNotes() {
        return "";
    }

    private String getProjectUrl() {
        return "";
    }

    private String getPackageLabel() {
        return getEntryTitle() + "-" + getPackageVersion();
    }

    public int compareTo(Component other) {
        Version version1 = this.getVersion();
        Version version2 = other.getVersion();

        return version1.compareTo(version2);
    }

    public static final Comparator<Component> VersionComparator = new Comparator<Component>() {
        public int compare(Component component1, Component component2) {
            return component1.compareTo(component2);
        }
    };
}