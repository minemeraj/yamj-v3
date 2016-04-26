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
package org.yamj.core.service.metadata.online;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yamj.core.database.model.Person;
import org.yamj.plugin.api.metadata.PersonScanner;

public class PluginPersonScanner implements IPersonScanner {

    private final Logger LOG = LoggerFactory.getLogger(PluginPersonScanner.class);
    private final PersonScanner personScanner;

    public PluginPersonScanner(PersonScanner personScanner) {
        this.personScanner = personScanner;
    }
    
    public PersonScanner getPersonScanner() {
        return personScanner;
    }

    @Override
    public String getScannerName() {
        return personScanner.getScannerName();
    }
    
    @Override
    public String getPersonId(Person person) {
        // create person wrapper
        WrapperPerson wrapper = new WrapperPerson(person);
        wrapper.setScannerName(personScanner.getScannerName());
        
        return getPersonId(wrapper, false);
    }

    private String getPersonId(WrapperPerson wrapper, boolean throwTempError) {
        return personScanner.getPersonId(wrapper, throwTempError);
    }

    @Override
    public ScanResult scanPerson(Person person, boolean throwTempError) {
        // create person wrapper
        WrapperPerson wrapper = new WrapperPerson(person);
        wrapper.setScannerName(personScanner.getScannerName());

        final String personId = getPersonId(wrapper, throwTempError);
        if (!personScanner.isValidPersonId(personId)) {
            LOG.debug("{} id not available '{}'", getScannerName(), wrapper.getName());
            return ScanResult.MISSING_ID;
        }

        final boolean scanned = personScanner.scanPerson(wrapper, throwTempError);
        if (!scanned) {
            LOG.error("Can't find {} informations for person '{}'", getScannerName(), wrapper.getName());
            return ScanResult.NO_RESULT;
        }
        
        return ScanResult.OK;
    }
}
