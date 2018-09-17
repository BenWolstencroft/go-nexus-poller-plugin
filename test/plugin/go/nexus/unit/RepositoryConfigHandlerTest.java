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

package plugin.go.nexus.unit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import plugin.go.nexus.ConnectionHandler;
import plugin.go.nexus.RepositoryConfigHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static utils.Constants.REPOSITORY_CONFIGURATION;

public class RepositoryConfigHandlerTest {
    RepositoryConfigHandler repositoryConfigHandler;
    ConnectionHandler connectionHandler;

    @Before
    public void setUp() {
        connectionHandler = mock(ConnectionHandler.class);
        repositoryConfigHandler = new RepositoryConfigHandler(connectionHandler);
    }

    @Test
    public void shouldErrorWhenInvalidRepositoryConfiguration() {
        Map invalidBody = createRequestBodyWithCompleteMetadata("", "", "", "");

        List errorList = repositoryConfigHandler.handleValidateConfiguration(invalidBody);

        Assert.assertFalse(errorList.isEmpty());
    }

    @Test
    public void shouldErrorOutWhenRepoUrlIsNull() {
        Map invalidBody = createRequestBodyWithCompleteMetadata(null, "nuget-official", "", "");

        List errorList = repositoryConfigHandler.handleValidateConfiguration(invalidBody);

        Assert.assertFalse(errorList.isEmpty());
    }

    @Test
    public void shouldErrorOutWhenRepoNameIsNull() {
        Map invalidBody = createRequestBodyWithCompleteMetadata("https://repositories.pse.zen.co.uk", null, "", "");

        List errorList = repositoryConfigHandler.handleValidateConfiguration(invalidBody);

        Assert.assertFalse(errorList.isEmpty());
    }

    @Test
    public void shouldReturnEmptyErrorListWhenValidRepositoryConfigurations() {
        Map validBody = createRequestBodyWithCompleteMetadata("https://repositories.pse.zen.co.uk", "nuget-official", "", "");

        List errorList = repositoryConfigHandler.handleValidateConfiguration(validBody);

        Assert.assertTrue(errorList.isEmpty());
    }

    @Test
    public void shouldUseConnectionHandlerToCheckConnectionWithMetadata() {
        String SOME_URL = "https://repositories.pse.zen.co.uk";
        String SOME_NAME = "nuget-official";
        String SOME_USERNAME = "SomeUsername";
        String SOME_PASSWORD = "somePassword";

        repositoryConfigHandler.handleCheckRepositoryConnection(createRequestBodyWithCompleteMetadata(SOME_URL, SOME_NAME, SOME_USERNAME, SOME_PASSWORD));

        verify(connectionHandler).checkConnectionToUrlWithMetadata(SOME_URL, SOME_NAME, SOME_USERNAME, SOME_PASSWORD);
    }

    @Test
    public void shouldHandleCheckConnectionWhenOptionalMetadataIsNotProvided() {
        String SOME_URL = "https://repositories.pse.zen.co.uk";
        String SOME_NAME = "nuget-official";

        repositoryConfigHandler.handleCheckRepositoryConnection(createUrlRequestBody(SOME_URL, SOME_NAME));

        verify(connectionHandler).checkConnectionToUrlWithMetadata(SOME_URL, SOME_NAME, null, null);
    }

    private Map createRepoConfigMap(String url, String repoName) {
        Map fieldsMap = new HashMap();
        Map urlMap = new HashMap();
        urlMap.put("value", url);
        fieldsMap.put("REPO_URL", urlMap);
        Map nameMap = new HashMap();
        nameMap.put("value", repoName);
        fieldsMap.put("REPO_NAME", nameMap);
        return fieldsMap;
    }

    private Map createRepoConfigMapWithCompleteMetadata(String url, String repoName, String username, String password) {
        Map fieldsMap = createRepoConfigMap(url, repoName);
        Map usernameMap = new HashMap();
        usernameMap.put("value", username);
        fieldsMap.put("USERNAME", usernameMap);
        Map passwordMap = new HashMap();
        passwordMap.put("value", password);
        fieldsMap.put("PASSWORD", passwordMap);
        return fieldsMap;
    }

    private Map createUrlRequestBody(String url, String repoName) {
        Map fieldsMap = createRepoConfigMap(url, repoName);
        Map bodyMap = new HashMap();
        bodyMap.put(REPOSITORY_CONFIGURATION, fieldsMap);
        return bodyMap;
    }

    private Map createRequestBodyWithCompleteMetadata(String url, String repoName, String username, String password) {
        Map fieldsMap = createRepoConfigMapWithCompleteMetadata(url, repoName, username, password);
        Map bodyMap = new HashMap();
        bodyMap.put(REPOSITORY_CONFIGURATION, fieldsMap);
        return bodyMap;
    }


}