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

import io.github.dsheirer.buffer.INativeBufferFactory;
import io.github.dsheirer.source.SourceException;
import io.github.dsheirer.source.tuner.ITunerErrorListener;
import io.github.dsheirer.source.tuner.TunerType;
import io.github.dsheirer.source.tuner.usb.USBTunerController;

/**
 * Airspy HF+ and Discovery tuner controller.
 */
public class AirspyHfTunerController extends USBTunerController
{
    private static final long MINIMUM_FREQUENCY_HZ = 500_000;
    private static final long MAXIMUM_FREQUENCY_HZ = 260_000_000;

    public AirspyHfTunerController(int bus, String portAddress, ITunerErrorListener tunerErrorListener)
    {
        super(bus, portAddress, tunerErrorListener);
        setMinimumFrequency(MINIMUM_FREQUENCY_HZ);
        setMaximumFrequency(MAXIMUM_FREQUENCY_HZ);
        setUsableBandwidthPercentage(1.00);
        setMiddleUnusableHalfBandwidth(0);
    }

    @Override
    public int getBufferSampleCount()
    {
        //TODO:
        return 0;
    }

    @Override
    public long getTunedFrequency() throws SourceException
    {
        //TODO:
        return 0;
    }

    @Override
    public void setTunedFrequency(long frequency) throws SourceException
    {
        //TODO:

    }

    @Override
    public double getCurrentSampleRate() throws SourceException
    {
        //TODO:
        return 0;
    }

    @Override
    public TunerType getTunerType()
    {
        return TunerType.AIRSPY_HF_PLUS;
    }

    @Override
    protected INativeBufferFactory getNativeBufferFactory()
    {
        //TODO:
        return null;
    }

    @Override
    protected int getTransferBufferSize()
    {
        //TODO:
        return 0;
    }

    @Override
    protected void deviceStart() throws SourceException
    {
        //TODO:
    }

    @Override
    protected void deviceStop()
    {
        //TODO:
    }
}
