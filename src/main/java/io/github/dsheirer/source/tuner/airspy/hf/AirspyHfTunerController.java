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
import io.github.dsheirer.source.tuner.configuration.TunerConfiguration;
import io.github.dsheirer.source.tuner.usb.USBTunerController;
import io.github.dsheirer.util.ByteUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usb4java.LibUsb;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Airspy HF+ and Discovery tuner controller.
 */
public class AirspyHfTunerController extends USBTunerController
{
    private static final Logger mLog = LoggerFactory.getLogger(AirspyHfTunerController.class);
    private static final AirspyHfSampleRate DEFAULT_SAMPLE_RATE = new AirspyHfSampleRate(768_000);
    private static final long MINIMUM_FREQUENCY_HZ = 500_000;
    private static final long MAXIMUM_FREQUENCY_HZ = 260_000_000;
    private static final byte REQUEST_TYPE_IN = LibUsb.ENDPOINT_IN | LibUsb.REQUEST_TYPE_VENDOR | LibUsb.RECIPIENT_DEVICE;
    private static final byte REQUEST_TYPE_OUT = LibUsb.ENDPOINT_OUT | LibUsb.REQUEST_TYPE_VENDOR | LibUsb.RECIPIENT_DEVICE;
    private String mSerialNumber;
    private List<AirspyHfSampleRate> mAvailableSampleRates;
    private AirspyHfSampleRate mCurrentSampleRate = DEFAULT_SAMPLE_RATE;

    public AirspyHfTunerController(int bus, String portAddress, ITunerErrorListener tunerErrorListener)
    {
        super(bus, portAddress, tunerErrorListener);
        setMinimumFrequency(MINIMUM_FREQUENCY_HZ);
        setMaximumFrequency(MAXIMUM_FREQUENCY_HZ);
        setUsableBandwidthPercentage(1.00);
        setMiddleUnusableHalfBandwidth(0);
    }

    @Override
    public void apply(TunerConfiguration config) throws SourceException
    {
        super.apply(config);

        //TODO: apply tuner settings here.
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
        loadSampleRates();
        return mCurrentSampleRate.getValue();
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
        int status = LibUsb.setInterfaceAltSetting(getDeviceHandle(), 0, 1);

        if(status != LibUsb.SUCCESS)
        {
            throw new SourceException("Can't set Airspy HF interface 0 alternate setting 1 - " + LibUsb.errorName(status));
        }

        mSerialNumber = LibUsb.getStringDescriptor(getDeviceHandle(), getDeviceDescriptor().iSerialNumber());

        loadSampleRates();
    }

    @Override
    protected void deviceStop()
    {
        try
        {
//            setReceiverMode(false);
        }
        catch(Exception e)
        {
            mLog.error("Error stopping device", e);
        }
    }

    /**
     * LibUsb control transfer to request data from the tuner.
     * @param request to submit
     * @param length of bytes to read
     * @return byte array containing the response value
     * @throws IOException if there is an error or the request cannot be completed.
     */
    private byte[] read(Request request, int length, int bufferLength) throws IOException
    {
        ByteBuffer buffer = ByteBuffer.allocateDirect(bufferLength);
        int status = LibUsb.controlTransfer(getDeviceHandle(), REQUEST_TYPE_IN, request.getValue(), (short)0,
                (short)length, buffer, 0);

        if(status >= 0)
        {
            byte[] result = new byte[bufferLength];
            buffer.get(result);
            return result;
        }

        throw new IOException("Unable to complete read request [" + request.name() + "] - libusb status [" + status +
                "] - " + LibUsb.errorName(status));
    }

    /**
     * LibUsb control transfer to send data from the tuner.
     * @param request to submit
     * @return byte array containing the response value
     * @throws IOException if there is an error or the request cannot be completed.
     */
    private void write(Request request, ByteBuffer buffer) throws IOException
    {
        int status = LibUsb.controlTransfer(getDeviceHandle(), REQUEST_TYPE_OUT, request.getValue(), (short)0, (short)0,
                buffer, 0);

        if(status != LibUsb.SUCCESS)
        {
            throw new IOException("Unable to complete write request [" + request.name() + "] - libusb status: " + status);
        }
    }

    /**
     * Sets the receiver mode to start/stop the sample stream
     * @param started true to start and false to stop.
     * @throws SourceException if there is an error
     */
    private void setReceiverMode(boolean started) throws SourceException
    {
        int status = LibUsb.controlTransfer(getDeviceHandle(), REQUEST_TYPE_OUT, Request.RECEIVER_MODE.getValue(),
                (started ? (short)1 : (short)0), (byte)0, ByteBuffer.allocateDirect(0), 0);

        if(status != LibUsb.SUCCESS)
        {
            throw new SourceException("Unable to set receiver mode started to " + started + " - Error: " +
                    LibUsb.errorName(status));
        }
   }

    /**
     * Loads the sample rates from the tuner's firmware or uses the default sample rate of 768 kHz.
     */
    private void loadSampleRates()
    {
        if(mAvailableSampleRates == null)
        {
            mAvailableSampleRates = new ArrayList<>();

            try
            {
                //Read the first 4 bytes to get the count of sample rates.
                byte[] bytes = read(Request.GET_SAMPLE_RATES, 0, 4);

                int count = ByteUtil.toInteger(bytes, 0);

                if(count > 0)
                {
                    //Read the count (again) plus the number of 4-byte integer values.
                    bytes = read(Request.GET_SAMPLE_RATES, count, count * 4);

                    for(int x = 0; x < count; x++)
                    {
                        int sampleRate = ByteUtil.toInteger(bytes, (x * 4));
                        mAvailableSampleRates.add(new AirspyHfSampleRate(sampleRate));
                    }
                }
            }
            catch(Exception e)
            {
                mLog.error("Error reading sample rates from Airspy HF tuner", e);
            }

            //If we can't read sample rates from tuner, use the default sample rate.
            if(mAvailableSampleRates.isEmpty())
            {
                mAvailableSampleRates.add(DEFAULT_SAMPLE_RATE);
            }
        }
    }
}
