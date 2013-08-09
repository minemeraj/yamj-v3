/*
 *      Copyright (c) 2004-2013 YAMJ Members
 *      https://github.com/organizations/YAMJ/teams
 *
 *      This file is part of the Yet Another Media Jukebox (YAMJ).
 *
 *      The YAMJ is free software: you can redistribute it and/or modify
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
 *      along with the YAMJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 *      Web: https://github.com/YAMJ/yamj-v3
 *
 */
package org.yamj.core.api.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author stuart.boston
 */
public class ApiEpisodeDTO {

    private Long seriesId;
    private Long season;
    private Long episode;
    private String title;
    @JsonIgnore
    private String cacheFilename;
    @JsonIgnore
    private String cacheDir;
    private String videoimage = "";

    //<editor-fold defaultstate="collapsed" desc="Setter Methods">
    public void setSeriesId(Long seriesId) {
        this.seriesId = seriesId;
    }

    public void setSeason(Long season) {
        this.season = season;
    }

    public void setEpisode(Long episode) {
        this.episode = episode;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCacheFilename(String cacheFilename) {
        this.cacheFilename = cacheFilename;
    }

    public void setCacheDir(String cacheDir) {
        this.cacheDir = cacheDir;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Getter Methods">
    public Long getSeriesId() {
        return seriesId;
    }

    public Long getSeason() {
        return season;
    }

    public Long getEpisode() {
        return episode;
    }

    public String getTitle() {
        return title;
    }

    public String getVideoimage() {
        if (StringUtils.isBlank(videoimage) && (StringUtils.isNotBlank(cacheDir) && StringUtils.isNotBlank(cacheFilename))) {
            this.videoimage = FilenameUtils.normalize(FilenameUtils.concat(this.cacheDir, this.cacheFilename), Boolean.TRUE);
        }
        return videoimage;
    }
    //</editor-fold>
}