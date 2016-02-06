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
package org.yamj.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.fasterxml.jackson.datatype.joda.JodaModule;

/**
 * This ensures that the Lazy Instantiated objects are fully instantiated before returning the JSON information to the API.
 * Also the JODA types will be registered.
 *
 * @author Stuart
 */
public class YamjObjectMapper extends ObjectMapper {
    private static final long serialVersionUID = 34607231867726798L;

    public YamjObjectMapper() {
        super();
        this.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        // https://github.com/FasterXML/jackson-module-hibernate
        Hibernate5Module hm = new Hibernate5Module();
        registerModule(hm);
        hm.configure(Hibernate5Module.Feature.FORCE_LAZY_LOADING, true);

        // https://github.com/FasterXML/jackson-datatype-joda
        registerModule(new JodaModule());
    }
}