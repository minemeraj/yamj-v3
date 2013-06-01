package org.yamj.core.remote.service;

import org.yamj.common.remote.service.GitHubService;
import org.yamj.core.tools.web.PoolingHttpClient;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpGet;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yamj.common.tools.ClassTools;
import org.yamj.common.tools.DateTimeTools;

@Service("githubService")
public class GitHubServiceImpl implements GitHubService {

    private static final Logger LOG = LoggerFactory.getLogger(GitHubServiceImpl.class);
    private static final String GH_API = "https://api.github.com/repos/";
    private static final String GH_OWNER = "YAMJ";
    private static final String GH_REPO = "yamj-v3";
    private static final String USER_AGENT = "User-Agent";
    private static final String ACCEPT = "Accept";
    private static final String GH_USER_AGENT = "GitHubJava/2.1.0";
    private static final String GH_ACCEPT = "application/vnd.github.beta+json";
    private static final int DAY_AS_MILLIS = 24 * 60 * 60 * 1000;
    @Autowired
    private PoolingHttpClient httpClient;

    @Override
    public String pushDate(String owner, String repository) {
        if (StringUtils.isBlank(owner) || StringUtils.isBlank(repository)) {
            LOG.error("Owner '{}' or repository '{}' cannot be blank", owner, repository);
            throw new IllegalArgumentException("Owner or repository cannot be blank");
        }

        String returnDate = "";

        StringBuilder url = new StringBuilder(GH_API);
        url.append(owner).append("/").append(repository);

        try {
            HttpGet httpGet = new HttpGet(url.toString());
            httpGet.setHeader(USER_AGENT, GH_USER_AGENT);
            httpGet.addHeader(ACCEPT, GH_ACCEPT);

            URL newUrl = new URL(url.toString());
            httpGet.setURI(newUrl.toURI());

            String jsonData = httpClient.requestContent(httpGet);

            // This is ugly and a bit of a hack, but I don't need to unmarshal the whole object just for a date.
            int posStart = jsonData.indexOf("pushed_at");
            posStart = jsonData.indexOf("20", posStart);
            int posEnd = jsonData.indexOf('\"', posStart);
            returnDate = jsonData.substring(posStart, posEnd);
            LOG.info("Date: '{}'", returnDate);
        } catch (IOException ex) {
            LOG.warn("Unable to get GitHub information, error: {}", ex.getMessage());
            LOG.warn(ClassTools.getStackTrace(ex));
            return returnDate;
        } catch (RuntimeException ex) {
            LOG.warn("Unable to get GitHub information, error: {}", ex.getMessage());
            LOG.warn(ClassTools.getStackTrace(ex));
            return returnDate;
        } catch (URISyntaxException ex) {
            LOG.warn("Unable to get GitHub information, error: {}", ex.getMessage());
            LOG.warn(ClassTools.getStackTrace(ex));
            return returnDate;
        }
        return returnDate;
    }

    @Override
    public String pushDate() {
        return pushDate(GH_OWNER, GH_REPO);
    }

    @Override
    public boolean checkInstallationDate(String buildDate, int maxAgeDays) {
        return checkInstallationDate(GH_OWNER, GH_REPO, buildDate, maxAgeDays);
    }

    @Override
    public boolean checkInstallationDate(String owner, String repository, String buildDate, int maxAgeDays) {
        String ghDate = pushDate(owner, repository);
        LOG.debug("GitHub Date: {}", ghDate);
        LOG.debug("Build Date : {}", buildDate);

        DateTime dt1 = DateTimeTools.parseDate(ghDate, DateTimeTools.ISO8601_FORMAT);
        DateTime dt2 = DateTimeTools.parseDate(buildDate, DateTimeTools.BUILD_FORMAT);
        long diff = DateTimeTools.getDuration(dt1, dt2);
        LOG.debug("Difference : {}", diff, DateTimeTools.formatDurationColon(diff));
        if (diff > (maxAgeDays * DAY_AS_MILLIS)) {
            LOG.warn("Your installation is older than () days! Please update it", maxAgeDays);
            return Boolean.FALSE;
        } else {
            LOG.debug("Your installation is only {} old.", DateTimeTools.formatDurationText(diff));
            return Boolean.TRUE;
        }
    }
}
