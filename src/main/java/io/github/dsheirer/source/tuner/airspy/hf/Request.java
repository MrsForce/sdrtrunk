package io.github.dsheirer.source.tuner.airspy.hf;

/**
 * Requests/Commands supported by the Airspy HF
 */
public enum Request
{
    INVALID(0),
    RECEIVER_MODE(1),
    SET_FREQUENCY(2),
    GET_SAMPLE_RATES(3),
    SET_SAMPLE_RATE(4),
    CONFIG_READ(5),
    CONFIG_WRITE(6),
    GET_SERIAL_NUMBER_BOARD_ID(7),
    SET_USER_OUTPUT(8),
    GET_VERSION_STRING(9),
    SET_HF_AGC(10),
    SET_HF_AGC_THRESHOLD(11),
    SET_HF_ATT(12),
    SET_HF_LNA(13),
    GET_SAMPLE_RATE_ARCHITECTURES(14),
    GET_FILTER_GAIN(15),
    GET_FREQUENCY_DELTA(16),
    SET_VCTCXO_CALIBRATION(17);

    private int mValue;

    /**
     * Constructs an instance
     * @param value of the entry
     */
    Request(int value)
    {
        mValue = value;
    }

    /**
     * Value associated with the entry
     * @return value cast to a byte.
     */
    public byte getValue()
    {
        return (byte)mValue;
    }
}
