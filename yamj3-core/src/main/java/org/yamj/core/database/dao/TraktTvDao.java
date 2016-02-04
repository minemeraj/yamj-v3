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
package org.yamj.core.database.dao;

import java.util.*;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.springframework.stereotype.Repository;
import org.yamj.core.database.model.dto.TraktMovieDTO;
import org.yamj.core.hibernate.HibernateDao;
import org.yamj.core.service.metadata.online.ImdbScanner;
import org.yamj.core.service.metadata.online.TheMovieDbScanner;
import org.yamj.core.service.metadata.online.TraktTvScanner;

@Repository("traktTvDao")
public class TraktTvDao extends HibernateDao {
    
    public Map<String,List<Long>> getUpdatedMovieIds(Date checkDate) {
        final Map<String,List<Long>> result = new HashMap<>();
        
        try (ScrollableResults scroll = currentSession().getNamedQuery("videoData.trakttv.updated.movies")
                .setDate("checkDate", checkDate)
                .setReadOnly(true)
                .scroll(ScrollMode.FORWARD_ONLY)
            )
        {
            String key;
            Long id;
            Object[] row;
            while (scroll.next()) {
                row = scroll.get();
                key = convertRowElementToString(row[0]);
                id = convertRowElementToLong(row[1]);
                
                List<Long> ids = result.get(key);
                if (ids==null) ids = new ArrayList<>();
                ids.add(id);
                result.put(key, ids);
            }
        }
        
        return result;
    }

    public Map<String,List<Long>> getUpdatedEpisodeIds(Date checkDate) {
        final Map<String,List<Long>> result = new HashMap<>();
        
        try (ScrollableResults scroll = currentSession().getNamedQuery("videoData.trakttv.updated.episodes")
                .setDate("checkDate", checkDate)
                .setReadOnly(true)
                .scroll(ScrollMode.FORWARD_ONLY)
            )
        {
            String key;
            Long id;
            Object[] row;
            while (scroll.next()) {
                row = scroll.get();
                key = convertRowElementToString(row[0]);
                id = convertRowElementToLong(row[1]);
                
                List<Long> ids = result.get(key);
                if (ids==null) ids = new ArrayList<>();
                ids.add(id);
                result.put(key, ids);
            }
        }
        
        return result;
    }

    public Map<Long,TraktMovieDTO> getWatchedMovies(Date checkDate) {
        final Map<Long,TraktMovieDTO> result = new HashMap<>();
        
        try (ScrollableResults scroll = currentSession().getNamedQuery("videoData.trakttv.watched.movies")
                .setDate("checkDate", checkDate)
                .setReadOnly(true)
                .scroll(ScrollMode.FORWARD_ONLY)
            )
        {
            Object[] row;
            Long id;
            String source;
            while (scroll.next()) {
                row = scroll.get();
                id = convertRowElementToLong(row[0]);
                
                TraktMovieDTO dto = result.get(id);
                if (dto == null) {
                    dto = new TraktMovieDTO();
                    dto.setId(id);
                    dto.setWatchedDate(convertRowElementToDate(row[4]));
                    dto.setIdentifier(convertRowElementToString(row[5]));
                }
                
                source = convertRowElementToString(row[1]);
                if (TraktTvScanner.SCANNER_ID.equals(source)) {
                    dto.setTrakt(convertRowElementToInteger(row[2]));
                } else if (ImdbScanner.SCANNER_ID.equals(source)) {
                    dto.setImdb(convertRowElementToString(row[2]));
                } else if (TheMovieDbScanner.SCANNER_ID.equals(source)) {
                    dto.setTmdb(convertRowElementToInteger(row[2]));
                }
                
                result.put(id, dto);
            }
        }
        
        return result;
    }
}
