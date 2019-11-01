package com.zeroclue.jmeter.protocol.pubsub;


import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.samplers.AbstractSampler;
import org.apache.jmeter.testelement.ThreadListener;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class PubSubSampler extends AbstractSampler implements ThreadListener {
    private static final Logger log = LoggingManager.getLoggerForClass();

    @Override
    public void threadFinished() {
        log.info("PubSubSampler.threadFinished called");
        cleanup();
    }

    @Override
    public void threadStarted() {}

    protected void cleanup() {}
} 