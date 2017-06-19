/*
 * Decompiled with CFR 0_118.
 */
package com.projectkorra.projectkorra.util.logging;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Filter;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.projectkorra.projectkorra.util.logging.LogFilter;
import com.projectkorra.projectkorra.util.logging.LogFormatter;

public class PKLogHandler
extends FileHandler {
    public PKLogHandler(String filename) throws IOException {
        super(filename, 512000, 20, true);
        this.setLevel(Level.WARNING);
        this.setFilter(new LogFilter());
        this.setFormatter(new LogFormatter());
    }

    @Override
    public synchronized void publish(LogRecord record) {
        super.publish(record);
        this.flush();
    }
}

