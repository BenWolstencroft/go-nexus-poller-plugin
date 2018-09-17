package plugin.go.nexus.models;

import java.util.Comparator;

public class Version implements Comparable<Version> {
    Integer major = 0;
    Integer minor = 0;
    Integer build = 0;
    Integer revision = 0;
    String semantic = null;

    public Version(String semanticVersion) {
        String[] semanticSplit = semanticVersion.split("-");
        String[] versionSplit = semanticSplit[0].split("\\.");
        if (versionSplit.length > 0) {
            this.major = Integer.parseInt(versionSplit[0]);
        }
        if (versionSplit.length > 1) {
            this.minor = Integer.parseInt(versionSplit[1]);
        }
        if (versionSplit.length > 2) {
            this.build = Integer.parseInt(versionSplit[2]);
        }
        if (versionSplit.length > 3) {
            this.revision = Integer.parseInt(versionSplit[3]);
        }
        if (semanticSplit.length > 1) {
            this.semantic = semanticSplit[1];
        }
    }

    public String toString() {
        String semanticString = "";
        if (this.semantic != null) {
            semanticString = "-" + semantic;
        }
        return this.major + "." + this.minor + "." + this.build + "." + this.revision + semanticString;
    }

    public Boolean isPreRelease() {
        if (this.semantic != null) {
            return true;
        }
        return false;
    }

    public int compareTo(Version other) {
        Version r1Version = this;
        Version r2Version = other;
        if (r1Version.major > r2Version.major) {
            return 1;
        }
        else if (r1Version.major < r2Version.major) {
            return -1;
        }
        else {
            if (r1Version.minor > r2Version.minor) {
                return 1;
            } else if (r1Version.minor < r2Version.minor) {
                return -1;
            } else {
                if (r1Version.build > r2Version.build) {
                    return 1;
                } else if (r1Version.build < r2Version.build) {
                    return -1;
                } else {
                    if (r1Version.revision > r2Version.revision) {
                        return 1;
                    } else if (r1Version.revision < r2Version.revision) {
                        return -1;
                    } else {
                        if (r1Version.semantic == null && r2Version.semantic != null) {
                            return 1;
                        } else if (r1Version.semantic != null && r2Version.semantic == null) {
                            return -1;
                        } else {
                            return 0;
                        }
                    }
                }
            }
        }
    };

    public static final Comparator<Version> VersionComparator = new Comparator<Version>() {
        public int compare (Version version1, Version version2) {
            return version1.compareTo((version2));
        }
    };

}