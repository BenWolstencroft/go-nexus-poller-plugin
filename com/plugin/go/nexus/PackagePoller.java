/*
 * Copyright 2016 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package plugin.go.nexus;

import com.thoughtworks.go.plugin.api.logging.Logger;
import com.thoughtworks.go.plugin.api.material.packagerepository.PackageRevision;

import plugin.go.nexus.models.Asset;
import plugin.go.nexus.models.Component;

import java.text.SimpleDateFormat;
import java.util.*;

import static utils.Constants.PACKAGE_CONFIGURATION;
import static utils.Constants.REPOSITORY_CONFIGURATION;

public class PackagePoller {
    private NexusResultFilterer resultFilterer;
    private ConnectionHandler connectionHandler;

    private static Logger logger = Logger.getLoggerFor(PluginConfigHandler.class);

    public PackagePoller(ConnectionHandler connectionHandler, NexusResultFilterer resultFilterer) {
        this.connectionHandler = connectionHandler;
        this.resultFilterer = resultFilterer;
    }

    public Map handleCheckPackageConnection(Map requestBodyMap) {
        Map response = new HashMap();
        List messages = new ArrayList();
        Map packageRevisionResponse;

        try {
            packageRevisionResponse = handleLatestRevision(requestBodyMap);
            String revision = (String) packageRevisionResponse.get("revision");
            if (revision != null) {
                messages.add("Successfully found revision: " + revision);
                response.put("status", "success");
                response.put("messages", messages);
                return response;
            }
        } catch (RuntimeException e) {
            logger.info(e.getMessage());
        }

        messages.add("No packages found");
        response.put("status", "failure");
        response.put("messages", messages);
        return response;
    }

    public Map handleLatestRevision(Map request) {
        return pollForRevision(request, "", false);
    }

    public Map handleLatestRevisionSince(Map request) {
        Map revisionMap = (Map) request.get("previous-revision");
        Map data = (Map) revisionMap.get("data");
        String previousVersion = (String) data.get("VERSION");

        return pollForRevision(request, previousVersion, true);
    }

    private Map pollForRevision(Map request, String knownPackageRevision, boolean lastVersionKnown) {
        // Use the Connection Handler to get the collection of data
        Map repoConfigMap = (Map) request.get(REPOSITORY_CONFIGURATION);

        String repoUrl = parseValueFromEmbeddedMap(repoConfigMap, "REPO_URL");
        String repoName = parseValueFromEmbeddedMap(repoConfigMap, "REPO_NAME");
        String username = parseValueFromEmbeddedMap(repoConfigMap, "USERNAME");
        String password = parseValueFromEmbeddedMap(repoConfigMap, "PASSWORD");

        Map packageConfigMap = (Map) request.get(PACKAGE_CONFIGURATION);
        String packageId = parseValueFromEmbeddedMap(packageConfigMap, "PACKAGE_ID");
        String versionTo = parseValueFromEmbeddedMap(packageConfigMap, "POLL_VERSION_TO");
        String versionFrom = parseValueFromEmbeddedMap(packageConfigMap, "POLL_VERSION_FROM");
        String includePreReleaseSetting = parseValueFromEmbeddedMap(packageConfigMap, "INCLUDE_PRE_RELEASE");
        Boolean includePreRelease = false;
        if (includePreReleaseSetting != null && !includePreReleaseSetting.isEmpty() && includePreReleaseSetting.equals("yes")) {
            includePreRelease = true;
        }

        Component component = connectionHandler.getComponent(repoUrl, repoName, username, password, packageId, knownPackageRevision, versionFrom, versionTo, includePreRelease, resultFilterer);
        return parsePackageDataFromComponent(component, lastVersionKnown);
    }

    private Map parsePackageDataFromComponent(Component component, boolean lastVersionKnown) {
        Map packageRevisionMap = new HashMap();
        if (component == null || component.getPackageRevision(lastVersionKnown) == null) {
            return packageRevisionMap;
        }
        PackageRevision packageRevision = component.getPackageRevision(lastVersionKnown);
        packageRevisionMap.put("revision", packageRevision.getRevision());
        packageRevisionMap.put("timestamp", formatTimestamp(packageRevision.getTimestamp()));
        packageRevisionMap.put("user", packageRevision.getUser());
        packageRevisionMap.put("revisionComment", packageRevision.getRevisionComment());
        packageRevisionMap.put("trackbackUrl", packageRevision.getTrackbackUrl());
        packageRevisionMap.put("data", packageRevision.getData());

        return packageRevisionMap;
    }

    private String formatTimestamp(Date timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        String date = dateFormat.format(timestamp);
        return date;
    }

    private String parseValueFromEmbeddedMap(Map configMap, String fieldName) {
        if (configMap.get(fieldName) == null) return "";

        Map fieldMap = (Map) configMap.get(fieldName);
        String value = (String) fieldMap.get("value");
        return value;
    }

}
