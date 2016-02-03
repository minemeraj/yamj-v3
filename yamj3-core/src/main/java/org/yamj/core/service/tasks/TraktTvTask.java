/*
 *      Copyright (c) 2004-2015 YAMJ Members
 *      https://github.com/organizations/YAMJ/teams
 *
 *      This file is part of the Yet Another Media Jukebox (YAMJ).
 *
 *      YAMJ is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU General Public License as published by
 *      the Free Software Foundation, either version 3 of the License, or
 *      any later version.
 *
 *      YAMJ is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU General Public License for more details.
 *
 *      You should have received a copy of the GNU General Public License
 *      along with YAMJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 *      Web: https://github.com/YAMJ/yamj-v3
 *
 */
package org.yamj.core.service.tasks;

import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.yamj.core.service.trakttv.TraktTvService;

/**
 * Task for periodical synchronization with Trakt.TV
 */
@Component
public class TraktTvTask implements ITask {

    private static final Logger LOG = LoggerFactory.getLogger(RecheckTask.class);

    @Autowired
    private ExecutionTaskService executionTaskService;
    @Autowired
    private TraktTvService traktTvService;
    
    @Value("${trakttv.push.enabled:false}")
    private boolean pushEnabled;
    @Value("${trakttv.pull.enabled:false}")
    private boolean pullEnabled;
    
    @Override
    public String getTaskName() {
        return "trakttv";
    }

    @PostConstruct
    public void init() {
        // just register task if push or pull is enabled
        if (pushEnabled || pullEnabled) {
            executionTaskService.registerTask(this);
        }
    }

    @Override
    public void execute(String options) throws Exception {
        if (traktTvService.isExpired()) {
            // nothing could be done if expired
            return;
        }
        LOG.debug("Execute Trakt.TV task");
        
        if (pullEnabled) {
            traktTvService.pullWatchedMovies();
            traktTvService.pullWatchedShows();
        } else {
            LOG.debug("Trakt.TV pulling is not enabled");
        }

        if (pushEnabled) {
            // TODO pushing events to Trakt.TV
        } else {
            LOG.debug("Trakt.TV pushing is not enabled");
        }

        LOG.debug("Finished Trakt.TV task");
    }
}