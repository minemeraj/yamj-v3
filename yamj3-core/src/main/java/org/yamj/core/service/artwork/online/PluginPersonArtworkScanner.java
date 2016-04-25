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
package org.yamj.core.service.artwork.online;

import java.util.List;
import org.yamj.core.database.model.Person;
import org.yamj.core.service.metadata.online.WrapperPerson;
import org.yamj.plugin.api.artwork.ArtworkDTO;
import org.yamj.plugin.api.artwork.PersonArtworkScanner;

public class PluginPersonArtworkScanner implements IPersonArtworkScanner {

    private final PersonArtworkScanner personArtworkScanner;
    
    public PluginPersonArtworkScanner(PersonArtworkScanner personArtworkScanner) {
        this.personArtworkScanner = personArtworkScanner;
    }
    
    @Override
    public String getScannerName() {
        return personArtworkScanner.getScannerName();
    }

    @Override
    public List<ArtworkDTO> getPhotos(Person person) {
        WrapperPerson wrapper = new WrapperPerson(person);
        wrapper.setScannerName(personArtworkScanner.getScannerName());
        return personArtworkScanner.getPhotos(wrapper);
    }
}
