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

import io.github.dsheirer.controller.NamingThreadFactory;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Application-wide static thread pools for scheduled and ad-hoc threaded tasks.
 */
public class ThreadPool
{
    private final static Logger mLog = LoggerFactory.getLogger(ThreadPool.class);
    public static ExecutorService CACHED = Executors.newCachedThreadPool(new NamingThreadFactory("sdrtrunk cached"));
    public static ScheduledExecutorService SCHEDULED;
    private static int sScheduledPoolThreadCount;
    static
    {
        sScheduledPoolThreadCount = Math.max(4, Runtime.getRuntime().availableProcessors());
        SCHEDULED = Executors.newScheduledThreadPool(sScheduledPoolThreadCount,
                new NamingThreadFactory("sdrtrunk scheduled"));
    }

    /**
     * Application-wide shared thread pools and scheduled executor service.
     */
    public ThreadPool()
    {
    }

    public static void logSettings()
    {
        mLog.info("Application thread pools created SCHEDULED[" + sScheduledPoolThreadCount + "] and CACHED");
    }
}
