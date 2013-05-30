package com.yamj.core.service.artwork.common;

import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.TheMovieDbApi;
import com.omertron.themoviedbapi.model.MovieDb;
import com.yamj.common.tools.PropertyTools;
import com.yamj.core.database.model.IMetadata;
import com.yamj.core.service.artwork.ArtworkScannerService;
import com.yamj.core.service.artwork.fanart.IMovieFanartScanner;
import com.yamj.core.service.artwork.poster.IMoviePosterScanner;
import com.yamj.core.service.plugin.ImdbScanner;
import com.yamj.core.service.plugin.TheMovieDbScanner;
import java.net.URL;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("tmdbArtworkScanner")
public class TheMovieDbArtworkScanner implements 
    IMoviePosterScanner, IMovieFanartScanner, InitializingBean
{
    private static final Logger LOG = LoggerFactory.getLogger(TheMovieDbArtworkScanner.class);
    private static final String DEFAULT_POSTER_SIZE = "original";
    private static final String DEFAULT_FANART_SIZE = "original";
    private static final String DEFAULT_LANGUAGE = PropertyTools.getProperty("themoviedb.language", "en");

    @Autowired
    private ArtworkScannerService artworkScannerService;
    @Autowired
    private TheMovieDbApi tmdbApi;
    @Autowired
    private TheMovieDbScanner tmdbScanner;
    
    @Override
    public String getScannerName() {
        return TheMovieDbScanner.TMDB_SCANNER_ID;
    }

    @Override
    public void afterPropertiesSet() {
        // register this scanner
        artworkScannerService.registerMoviePosterScanner(this);
        artworkScannerService.registerMovieFanartScanner(this);
    }

    @Override
    public String getId(String title, int year) {
        return tmdbScanner.getMovieId(title, year);
    }

    @Override
    public String getPosterUrl(String title, int year) {
        String id = this.getId(title, year);
        return this.getPosterUrl(id);
    }

    @Override
    public String getFanartUrl(String title, int year) {
        String id = this.getId(title, year);
        return this.getFanartUrl(id);
    }

    @Override
    public String getPosterUrl(String id) {
        String url = null;
        if (StringUtils.isNumeric(id)) {
            try {
                MovieDb moviedb = tmdbApi.getMovieInfo(Integer.parseInt(id), DEFAULT_LANGUAGE);
                URL posterURL = tmdbApi.createImageUrl(moviedb.getPosterPath(), DEFAULT_POSTER_SIZE);
                url = posterURL.toString();
            } catch (MovieDbException error) {
                LOG.warn("Failed to get the poster URL for TMDb ID {}", id, error);
            }
        }
        return url;
    }

    @Override
    public String getFanartUrl(String id) {
        String url = null;
        if (StringUtils.isNumeric(id)) {
            try {
                MovieDb moviedb = tmdbApi.getMovieInfo(Integer.parseInt(id), DEFAULT_LANGUAGE);
                URL fanartURL = tmdbApi.createImageUrl(moviedb.getBackdropPath(), DEFAULT_FANART_SIZE);
                url = fanartURL.toString();
            } catch (MovieDbException error) {
                LOG.warn("Failed to get the fanart URL for TMDb ID {}", id, error);
            }
        }
        return url;
    }

    @Override
    public String getPosterUrl(IMetadata metadata) {
        String id = getId(metadata);
        if (StringUtils.isNotBlank(id)) {
            return getPosterUrl(id);
        }
        return null;
    }

    @Override
    public String getFanartUrl(IMetadata metadata) {
        String id = getId(metadata);
        if (StringUtils.isNotBlank(id)) {
            return getFanartUrl(id);
        }
        return null;
    }

    private String getId(IMetadata metadata) {
        // First look to see if we have a TMDb ID as this will make looking the film up easier
        String tmdbID = metadata.getSourcedbId(getScannerName());
        if (StringUtils.isNumeric(tmdbID)) {
            return tmdbID;
        }

        // Search based on IMDb ID
        String imdbID = metadata.getSourcedbId(ImdbScanner.IMDB_SCANNER_ID);
        if (StringUtils.isNotBlank(imdbID)) {
            MovieDb moviedb = null;
            try {
                moviedb = tmdbApi.getMovieInfoImdb(imdbID, DEFAULT_LANGUAGE);
            } catch (MovieDbException ex) {
                LOG.warn("Failed to get TMDb ID for " + imdbID + " - " + ex.getMessage());
            }
            
            if (moviedb != null) {
                tmdbID = String.valueOf(moviedb.getId());
                if (StringUtils.isNumeric(tmdbID)) {
                    metadata.setSourcedbId(getScannerName(), tmdbID);
                    return tmdbID;
                }
            }
        }

        // Search based on title and year
        String title = StringUtils.isBlank(metadata.getTitleOriginal())?metadata.getTitle():metadata.getTitleOriginal();
        tmdbID = getId(title, metadata.getYear());
        if (StringUtils.isNumeric(tmdbID)) {
            metadata.setSourcedbId(getScannerName(), tmdbID);
            return tmdbID;
        }

        LOG.warn("No TMDb id found for movie");
        return null;
    }
}
