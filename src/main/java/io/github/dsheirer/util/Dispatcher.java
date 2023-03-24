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
package io.github.dsheirer.util;

import io.github.dsheirer.sample.Listener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Threaded processor for receiving elements from a separate producer thread and forwarding those buffers to a
 * registered listener on this consumer/dispatcher thread.
 */
public class Dispatcher<E> implements Listener<E>
{
    private final static Logger mLog = LoggerFactory.getLogger(Dispatcher.class);
    private static final long OVERFLOW_LOG_EVENT_WAIT_PERIOD = TimeUnit.SECONDS.toMillis(10);
    private LinkedTransferQueue<E> mQueue;
    private Listener<E> mListener;
    private AtomicBoolean mRunning = new AtomicBoolean();
    private AtomicInteger mQueueSize = new AtomicInteger();
    private int mMaxQueueSize;
    private int mQueueProcessingBatchSize;
    private long mRunInterval;
    private String mDispatcherName;
    private ScheduledFuture<?> mScheduledFuture;
    private long mLastOverflowLogEvent;

    /**
     * Constructs an instance
     * @param maxQueueSize of the internal queue before overflow starts to occur
     * @param batchSize is the maximum number of enqueued elements to batch process per run interval
     */
    public Dispatcher(int maxQueueSize, int batchSize, long runInterval, String dispatcherName)
    {
        mMaxQueueSize = maxQueueSize;
        mQueueProcessingBatchSize = batchSize;
        mRunInterval = runInterval;
        mDispatcherName = dispatcherName;
        mQueue = new LinkedTransferQueue<>();
    }

    /**
     * Listener to receive the queued buffers each time this processor runs.
     */
    protected Listener<E> getListener()
    {
        return mListener;
    }

    /**
     * Sets or changes the listener to receive buffers from this processor.
     * @param listener to receive buffers
     */
    public void setListener(Listener<E> listener)
    {
        mListener = listener;
    }

    /**
     * Primary input method for adding buffers to this processor.  Note: incoming buffers will be ignored if this
     * processor is in a stopped state.  You must invoke start() to allow incoming buffers and initiate buffer
     * processing.
     *
     * @param e to enqueue for distribution to a registered listener
     */
    public void receive(E e)
    {
        if(mRunning.get())
        {
            if(mQueueSize.get() < mMaxQueueSize)
            {
                mQueue.add(e);
                mQueueSize.incrementAndGet();
            }
            else
            {
                if(System.currentTimeMillis() > (mLastOverflowLogEvent + OVERFLOW_LOG_EVENT_WAIT_PERIOD))
                {
                    mLastOverflowLogEvent = System.currentTimeMillis();
                    mLog.warn("Dispatcher - temporary buffer overflow for thread [" + mDispatcherName +
                        "] - throwing away samples - " + " processor flag:" + (mRunning.get() ? "running" : "stopped"));
                }
            }
       }
    }

    /**
     * Starts this buffer processor and allows queuing of incoming buffers.
     */
    public void start()
    {
        if(mRunning.compareAndSet(false, true))
        {
            mQueue.clear();

            if(mScheduledFuture != null)
            {
                mScheduledFuture.cancel(true);
            }

            mScheduledFuture = ThreadPool.SCHEDULED.scheduleAtFixedRate(new Processor(), 0, mRunInterval,
                    TimeUnit.MILLISECONDS);
        }
    }

    /**
     * Stops this buffer processor and waits up to two seconds for the processing thread to terminate.
     */
    public void stop()
    {
        if(mRunning.compareAndSet(true, false))
        {
            if(mScheduledFuture != null)
            {
                mScheduledFuture.cancel(true);
                mScheduledFuture = null;
                mQueue.clear();
            }
        }
    }

    /**
     * Indicates if this processor is currently running
     */
    public boolean isRunning()
    {
        return mRunning.get();
    }

    /**
     * Processes the transfer queue up to the maximum batch size specified.
     */
    private void process()
    {
        List<E> elements = new ArrayList<>();
        mQueue.drainTo(elements, mQueueProcessingBatchSize);

//        mLog.info("Processing [" + elements.size()+ "] of [" + mQueueSize.get() + "] for [" + mDispatcherName + "]");

        for(E element: elements)
        {
            if(mRunning.get() && mListener != null)
            {
                try
                {
                    mListener.receive(element);
                }
                catch(Throwable t)
                {
                    mLog.error("Unexpected error while processing queue elements to send to listener [" +
                            mListener.getClass() + "]", t);
                }
            }

            mQueueSize.decrementAndGet();
        }

        elements.clear();
    }

    /**
     * Processor to invoke the process() method.
     */
    class Processor implements Runnable
    {
        @Override
        public void run()
        {
            try
            {
                process();
            }
            catch(Throwable t)
            {
                mLog.error("Unexpected error thrown from the Dispatcher processor thread", t);
            }
        }
    }
}
