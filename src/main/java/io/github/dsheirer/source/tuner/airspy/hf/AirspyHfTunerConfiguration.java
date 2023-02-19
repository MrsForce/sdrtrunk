/*
 * *****************************************************************************
 * Copyright (C) 2014-2023 Dennis Sheirer
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 * ****************************************************************************
 */
package io.github.dsheirer.source.tuner.airspy.hf;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import io.github.dsheirer.source.tuner.TunerType;
import io.github.dsheirer.source.tuner.configuration.TunerConfiguration;

public class AirspyHfTunerConfiguration extends TunerConfiguration
{
    /**
     * Default constructor for JAXB
     */
    public AirspyHfTunerConfiguration()
    {
    }

    /**
     * Constructs an instance for the unique ID.
     * @param uniqueID of the tuner (ie serial number)
     */
    public AirspyHfTunerConfiguration(String uniqueID)
    {
        super(uniqueID);
    }

    @Override
    @JacksonXmlProperty(isAttribute = true, localName = "type", namespace = "http://www.w3.org/2001/XMLSchema-instance")
    public TunerType getTunerType()
    {
        return TunerType.AIRSPY_HF_PLUS;
    }
}
