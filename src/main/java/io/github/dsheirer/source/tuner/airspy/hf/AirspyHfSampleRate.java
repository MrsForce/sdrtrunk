package io.github.dsheirer.source.tuner.airspy.hf;

import java.text.DecimalFormat;

/**
 * Airspy HF sample rate.
 */
public class AirspyHfSampleRate
{
    private static final DecimalFormat KILOHERTZ_FORMATTER = new DecimalFormat("0.0");
    private int mValue;

    public AirspyHfSampleRate(int value)
    {
        mValue = value;
    }

    /**
     * Value of sample rate
     * @return rate in Hertz
     */
    public int getValue()
    {
        return mValue;
    }

    @Override
    public String toString()
    {
        return KILOHERTZ_FORMATTER.format(mValue / 1E3) + " kHz";
    }
}
