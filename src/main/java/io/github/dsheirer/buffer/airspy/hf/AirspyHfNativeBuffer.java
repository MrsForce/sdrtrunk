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

package io.github.dsheirer.buffer.airspy.hf;

import io.github.dsheirer.buffer.INativeBuffer;
import io.github.dsheirer.sample.complex.ComplexSamples;
import io.github.dsheirer.sample.complex.InterleavedComplexSamples;
import java.util.Iterator;

/**
 * Native buffer implementation for Airspy HF+ & Discovery tuners.
 */
public class AirspyHfNativeBuffer implements INativeBuffer
{
    private static final float SCALE = 1.0f / 32768.0f;
    private long mTimestamp;
    private short[] mInterleavedSamples;

    /**
     * Constructs an instance.
     * @param timestamp of the first sample of the buffer
     * @param interleavedSamples pairs of 16-bit complex samples.
     */
    public AirspyHfNativeBuffer(long timestamp, short[] interleavedSamples)
    {
        mTimestamp = timestamp;
        mInterleavedSamples = interleavedSamples;
    }

    @Override
    public Iterator<ComplexSamples> iterator()
    {
        return new IteratorComplexSamples();
    }

    @Override
    public Iterator<InterleavedComplexSamples> iteratorInterleaved()
    {
        return new IteratorInterleaved();
    }

    @Override
    public int sampleCount()
    {
        return mInterleavedSamples.length / 2;
    }

    @Override
    public long getTimestamp()
    {
        return mTimestamp;
    }

    /**
     * Scalar implementation of complex samples buffer iterator
     */
    public class IteratorComplexSamples implements Iterator<ComplexSamples>
    {
        private boolean mHasNext = true;

        @Override
        public boolean hasNext()
        {
            return mHasNext;
        }

        @Override
        public ComplexSamples next()
        {
            float[] i = new float[mInterleavedSamples.length / 2];
            float[] q = new float[mInterleavedSamples.length / 2];

            for(int x = 0; x < mInterleavedSamples.length; x += 2)
            {
                int index = mInterleavedSamples.length / 2;
                i[index] = mInterleavedSamples[x] * SCALE;
                q[index] = mInterleavedSamples[x + 1] * SCALE;
            }

            mHasNext = false;

            return new ComplexSamples(i, q, mTimestamp);
        }
    }

    /**
     * Scalar implementation of interleaved sample buffer iterator.
     */
    public class IteratorInterleaved implements Iterator<InterleavedComplexSamples>
    {
        private boolean mHasNext = true;

        @Override
        public boolean hasNext()
        {
            return mHasNext;
        }

        @Override
        public InterleavedComplexSamples next()
        {
            float[] samples = new float[mInterleavedSamples.length];

            for(int x = 0; x < mInterleavedSamples.length; x++)
            {
                samples[x] = mInterleavedSamples[x] * SCALE;
            }

            mHasNext = false;

            return new InterleavedComplexSamples(samples, mTimestamp);
        }
    }
}
