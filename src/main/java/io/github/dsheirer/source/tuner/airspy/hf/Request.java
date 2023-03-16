package io.github.dsheirer.source.tuner.airspy.hf;

/**
 * Requests/Commands supported by the Airspy HF
 */
public enum Request
{
    INVALID(0, 0),
    RECEIVER_MODE(1, 0),
    SET_FREQUENCY(2, 0),
    GET_SAMPLE_RATES(3, 4),
    SET_SAMPLE_RATE(4, 0),
    CONFIG_READ(5, 0),
    CONFIG_WRITE(6, 0),
    GET_SERIAL_NUMBER_BOARD_ID(7, 0),
    SET_USER_OUTPUT(8, 0),
    GET_VERSION_STRING(9, 0),
    SET_HF_AGC(10, 0),
    SET_HF_AGC_THRESHOLD(11, 0),
    SET_HF_ATT(12, 0),
    SET_HF_LNA(13, 0),
    GET_SAMPLE_RATE_ARCHITECTURES(14, 0),
    GET_FILTER_GAIN(15, 0),
    GET_FREQUENCY_DELTA(16, 0),
    SET_VCTCXO_CALIBRATION(17, 0);

    private int mValue;
    private int mLength;

    /**
     * Constructs an instance
     * @param value of the entry
     */
    Request(int value, int length)
    {
        mValue = value;
        mLength = length;
    }

    /**
     * Value associated with the entry
     * @return value cast to a byte.
     */
    public byte getValue()
    {
        return (byte)mValue;
    }

    /**
     * Length of the byte array to read or write.
     * @return
     */
    public int getLength()
    {
        return mLength;
    }
}
