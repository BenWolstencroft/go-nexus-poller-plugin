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

package plugin.go.nexus.unit;


import com.thoughtworks.go.plugin.api.material.packagerepository.PackageRevision;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import plugin.go.nexus.ConnectionHandler;
import plugin.go.nexus.NexusFeedDocument;
import plugin.go.nexus.NexusQueryBuilder;
import plugin.go.nexus.PackagePoller;
import plugin.go.nexus.builders.RequestBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;

public class PackagePollerTest {
    private static final String URL = "SOME_URL";
    private static final String NAME = "SOME_NAME";
    private static final String USERNAME = "SOME_USERNAME";
    private static final String PASSWORD = "SOME_PASSWORD";
    private static final String QUERYSTRING = "/GetUpdates()?packageIds='Zen.Log'&versions='0.0.1'&includePrerelease=true&includeAllVersions=true&$orderby=Version%20desc&$top=1";
    private static final String PACKAGE_ID = "Zen.Log";
    private Map sampleRequest;

    PackagePoller packagePoller;
    ConnectionHandler connectionHandler;

    @Before
    public void setup() {
        connectionHandler = mock(ConnectionHandler.class);
        packagePoller = new PackagePoller(connectionHandler, new NexusQueryBuilder());
    }

    public void setUpRequestWithPackageAndRepoConfigurations() {
        sampleRequest = new RequestBuilder().withRespositoryConfiguration(URL, NAME, USERNAME, PASSWORD).withPackageConfiguration(PACKAGE_ID).build();
    }

    @Test
    public void shouldGetLatestRevisionDataFromConnection() {
        setUpRequestWithPackageAndRepoConfigurations();
        Map data = new HashMap();
        data.put("VERSION", "3.5.0");
        PackageRevision packageRevision = new PackageRevision("REVISION", buildDate(), "USER", "REVISION_COMMENT", "TRACKBACK_URL", data);
        NexusFeedDocument mockDocument = mock(NexusFeedDocument.class);
        when(mockDocument.getPackageRevision(false)).thenReturn(packageRevision);
        when(connectionHandler.getNexusFeedDocument(URL, NAME, QUERYSTRING, USERNAME, PASSWORD)).thenReturn(mockDocument);

        Map revisionMap = packagePoller.handleLatestRevision(sampleRequest);

        verify(connectionHandler).getNexusFeedDocument(URL, NAME, QUERYSTRING, USERNAME, PASSWORD);
        Assert.assertEquals(packageRevision.getRevision(), revisionMap.get("revision"));
        Assert.assertThat((String) revisionMap.get("timestamp"), containsString("2016-09-27"));
        Assert.assertEquals(packageRevision.getUser(), revisionMap.get("user"));
        Assert.assertEquals(packageRevision.getRevisionComment(), revisionMap.get("revisionComment"));
        Assert.assertEquals(packageRevision.getData(), revisionMap.get("data"));
        Assert.assertEquals(packageRevision.getData().get("VERSION"), "3.5.0");
    }

    @Test
    public void shouldReturnEmptyMapIfNoPackageIsFound() {
        setUpRequestWithPackageAndRepoConfigurations();
        when(connectionHandler.getNexusFeedDocument(URL, NAME, QUERYSTRING, USERNAME, PASSWORD)).thenReturn(null);
        Map revisionMap = packagePoller.handleLatestRevision(sampleRequest);
        Assert.assertTrue(revisionMap.isEmpty());
    }

    @Test
    public void shouldFailIfPackageIsNull() {
        setUpRequestWithPackageAndRepoConfigurations();
        when(connectionHandler.getNexusFeedDocument(URL, NAME, QUERYSTRING, USERNAME, PASSWORD)).thenReturn(null);

        Map revisionMap = packagePoller.handleCheckPackageConnection(sampleRequest);

        verify(connectionHandler).getNexusFeedDocument(URL, NAME, QUERYSTRING, USERNAME, PASSWORD);

        Assert.assertEquals("failure", revisionMap.get("status"));
        Assert.assertEquals(((List) revisionMap.get("messages")).get(0), "No packages found");
    }

    @Test
    public void shouldFailIfPackageIsEmptyMap() {
        setUpRequestWithPackageAndRepoConfigurations();
        when(connectionHandler.getNexusFeedDocument(URL, NAME, QUERYSTRING, USERNAME, PASSWORD)).thenReturn(new NexusFeedDocument(null));

        Map revisionMap = packagePoller.handleCheckPackageConnection(sampleRequest);

        verify(connectionHandler).getNexusFeedDocument(URL, NAME, QUERYSTRING, USERNAME, PASSWORD);

        Assert.assertEquals("failure", revisionMap.get("status"));
        Assert.assertEquals(((List) revisionMap.get("messages")).get(0), "No packages found");
    }

    @Test
    public void shouldSucceedIfPackageExists() {
        setUpRequestWithPackageAndRepoConfigurations();
        String revision = "Zen.Log-1.1.4";
        PackageRevision packageRevision = new PackageRevision(revision, new Date(), "USER", "REVISION_COMMENT", "TRACKBACK_URL", new HashMap());
        NexusFeedDocument mockDocument = mock(NexusFeedDocument.class);
        when(mockDocument.getPackageRevision(false)).thenReturn(packageRevision);
        when(connectionHandler.getNexusFeedDocument(URL, NAME, QUERYSTRING, USERNAME, PASSWORD)).thenReturn(mockDocument);

        Map revisionMap = packagePoller.handleCheckPackageConnection(sampleRequest);

        verify(connectionHandler).getNexusFeedDocument(URL, NAME, QUERYSTRING, USERNAME, PASSWORD);

        Assert.assertEquals("success", revisionMap.get("status"));
        Assert.assertEquals(((List) revisionMap.get("messages")).get(0), "Successfully found revision: " + revision);
    }


    @Test
    public void shouldReturnEmptyMapIfNoNewerPackageExists() {
        String version = "1.1.4";
        Map sampleRequest = new RequestBuilder().withRespositoryConfiguration(URL, NAME, USERNAME, PASSWORD)
                .withPackageConfiguration(PACKAGE_ID)
                .withPreviousRevision(version)
                .build();
        String latestRevisionSinceQueryString = "/GetUpdates()?packageIds='Zen.Log'&versions='" + version + "'&includePrerelease=true&includeAllVersions=true&$orderby=Version%20desc&$top=1";
        NexusFeedDocument mockDocument = mock(NexusFeedDocument.class);
        when(mockDocument.getPackageRevision(true)).thenReturn(null);
        when(connectionHandler.getNexusFeedDocument(URL, NAME, latestRevisionSinceQueryString, USERNAME, PASSWORD)).thenReturn(mockDocument);

        Map revisionMap = packagePoller.handleLatestRevisionSince(sampleRequest);

        verify(connectionHandler).getNexusFeedDocument(URL, NAME, latestRevisionSinceQueryString, USERNAME, PASSWORD);

        Assert.assertTrue(revisionMap.isEmpty());
    }

    @Test
    public void shouldReturnPackageDataIfNewerPackageExists() {
        String version = "1.1.1";
        Map sampleRequest = new RequestBuilder().withRespositoryConfiguration(URL, NAME, USERNAME, PASSWORD)
                .withPackageConfiguration(PACKAGE_ID)
                .withPreviousRevision(version)
                .build();
        String revision = "Zen.Log-1.1.4";
        String latestRevisionSinceQueryString = "/GetUpdates()?packageIds='Zen.Log'&versions='" + version + "'&includePrerelease=true&includeAllVersions=true&$orderby=Version%20desc&$top=1";
        NexusFeedDocument mockDocument = mock(NexusFeedDocument.class);
        PackageRevision mockPackageRevision = mock(PackageRevision.class);
        when(mockDocument.getPackageRevision(true)).thenReturn(mockPackageRevision);
        when(mockPackageRevision.getRevision()).thenReturn(revision);
        when(mockPackageRevision.getTimestamp()).thenReturn(new Date());
        when(connectionHandler.getNexusFeedDocument(URL, NAME, latestRevisionSinceQueryString, USERNAME, PASSWORD)).thenReturn(mockDocument);

        Map revisionMap = packagePoller.handleLatestRevisionSince(sampleRequest);

        verify(connectionHandler).getNexusFeedDocument(URL, NAME, latestRevisionSinceQueryString, USERNAME, PASSWORD);

        Assert.assertEquals(revision, revisionMap.get("revision"));
    }

    private Date buildDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        try {
            date = dateFormat.parse("27/09/2016");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}
