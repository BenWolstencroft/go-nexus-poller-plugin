package plugin.go.nexus;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import plugin.go.nexus.models.Component;
import plugin.go.nexus.models.Version;

public class NexusResultFilterer {

    public Component filterComponents(List<Component> components, String knownVersion, String versionFrom, String versionTo, Boolean includePreRelease) {
        // FILTER OUT ANY BELOW THE LOWEST VERSION
        String lowestVersionString = calculateVersion(knownVersion, versionFrom);
        final Version lowestVersion = new Version(lowestVersionString);

        components = components.stream().filter((Component component) -> {
            Version version = component.getVersion();
            return version.compareTo(lowestVersion) == 1;
        }).collect(Collectors.toList());

        if (versionTo != null && !versionTo.isEmpty()) {
            // FILTER OUT ANY ABOVE THE HIGHEST VERSION
            final Version highestVersion = new Version(versionTo);
            components = components.stream().filter((Component component) -> {
                Version version = component.getVersion();
                return version.compareTo(highestVersion) == -1;
            }).collect(Collectors.toList());
        }

        if (!includePreRelease) {
            // FILTER OUT ANY PRERELEASE VERSIONS
            components = components.stream().filter((Component component) -> {
                Version version = component.getVersion();
                return !version.isPreRelease();
            }).collect(Collectors.toList());
        }

        // SORT BY VERSION DESC
        components.sort(Component.VersionComparator);
        components.sort(Collections.reverseOrder());

        // RETURN THE TOP ONE
        return components.get(0);
    }

    // public String getQuery(String packageId, String knownVersion, String versionFrom, String versionTo, String includePreRelease) {
    //     StringBuilder query = new StringBuilder();
    //     query.append(String.format("&versions='%s'", calculateVersion(knownVersion, versionFrom)));
    //     query.append("&includePrerelease=").append(convertIncludePreRelease(includePreRelease));
    //     query.append("&includeAllVersions=true");//has to be true, filter gets applied later
    //     if (versionTo != null && !versionTo.isEmpty()) {
    //         query.append("&$filter=Version%20lt%20'").append(versionTo).append("'");
    //     }
    //     query.append("&$orderby=Version%20desc&$top=1");
    //     return query.toString();
    // }

    private String calculateVersion(String knownVersion, String versionFrom) {
        if (knownVersion != null && !knownVersion.isEmpty()) return knownVersion;
        if (versionFrom != null && !versionFrom.isEmpty()) return versionFrom;
        return "0.0.1";
    }
}

