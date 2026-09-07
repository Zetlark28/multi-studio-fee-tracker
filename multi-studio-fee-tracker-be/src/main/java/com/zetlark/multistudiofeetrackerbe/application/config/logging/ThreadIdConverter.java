package com.zetlark.multistudiofeetrackerbe.application.config.logging;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * Logback conversion word "tid" exposing the numeric id of the thread that emitted the log event.
 * Logback's ILoggingEvent only carries the thread name, so the numeric id must be read from the
 * current thread at conversion time (safe because conversion runs synchronously on that thread).
 */
public class ThreadIdConverter extends ClassicConverter {

    @Override
    public String convert(ILoggingEvent event) {
        return String.valueOf(Thread.currentThread().threadId());
    }
}
