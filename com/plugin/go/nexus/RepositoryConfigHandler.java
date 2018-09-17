/*
 * Copyright 2017 ThoughtWorks, Inc.
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static utils.Constants.REPOSITORY_CONFIGURATION;

public class RepositoryConfigHandler extends PluginConfigHandler {

    private ConnectionHandler connectionHandler;

    public RepositoryConfigHandler(ConnectionHandler connectionHandler) {
        this.connectionHandler = connectionHandler;
    }

    public Map handleConfiguration() {
        Map repositoryConfig = new HashMap();

        repositoryConfig.put("REPO_URL", createConfigurationField("Nexus Url", "0", false, true, true));
        repositoryConfig.put("REPO_NAME", createConfigurationField("Repository Name", "1", false, true, true));
        repositoryConfig.put("USERNAME", createConfigurationField("Username", "2", false, false, false));
        repositoryConfig.put("PASSWORD", createConfigurationField("Password (use only with https)", "3", true, false, false));

        return repositoryConfig;
    }

    public List handleValidateConfiguration(Map request) {
        List validationList = new ArrayList();

        Map configMap = (Map) request.get(REPOSITORY_CONFIGURATION);
        Map urlMap = (Map) configMap.get("REPO_URL");
        Map nameMap = (Map) configMap.get("REPO_NAME");

        Object repoUrl = urlMap.get("value");
        if (repoUrl == null || repoUrl.equals("")) {
            Map errors = new HashMap();
            errors.put("key", "REPO_URL");
            errors.put("message", "Url cannot be empty");
            validationList.add(errors);
        }

        Object repoName = nameMap.get("value");
        if (repoName == null || repoName.equals("")) {
            Map errors = new HashMap();
            errors.put("key", "REPO_NAME");
            errors.put("message", "Name cannot be empty");
            validationList.add(errors);
        }

        return validationList;
    }

    public Map handleCheckRepositoryConnection(Map request) {
        Map configMap = (Map) request.get(REPOSITORY_CONFIGURATION);

        String repoUrl = parseValueFromEmbeddedMap(configMap, "REPO_URL");
        String repoName = parseValueFromEmbeddedMap(configMap, "REPO_NAME");
        String username = parseValueFromEmbeddedMap(configMap, "USERNAME");
        String password = parseValueFromEmbeddedMap(configMap, "PASSWORD");

        return connectionHandler.checkConnectionToUrlWithMetadata(repoUrl, repoName, username, password);
    }

    private String parseValueFromEmbeddedMap(Map configMap, String fieldName) {
        Map fieldMap = (Map) configMap.get(fieldName);
        String value = null;
        if (fieldMap != null) {
            value = (String) fieldMap.get("value");
        }
        return value;
    }


}
