package com.zeroclue.jmeter.protocol.pubsub;

import org.apache.jmeter.samplers.AbstractSampler;
import org.apache.jmeter.testelement.ThreadListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class PubSubSampler extends AbstractSampler implements ThreadListener {
    
    private static final long serialVersionUID = 1L;
    
    public static final int DEFAULT_TIMEOUT = 1000;
    public static final String DEFAULT_TIMEOUT_STRING = Integer.toString(DEFAULT_TIMEOUT);
    
    public static final int DEFAULT_ITERATIONS = 1;
    public static final String DEFAULT_ITERATIONS_STRING = Integer.toString(DEFAULT_ITERATIONS);
    
    private static final String TIMEOUT = "PubSubSampler.Timeout";
    private static final String ITERATIONS = "PubSubSampler.Iterations";
    
    private static final Logger log = LoggerFactory.getLogger(PubSubSampler.class);
    
    @Override
    public void threadFinished() {
        log.info("PubSubSampler.threadFinished called");
        cleanup();
    }
    
    @Override
    public void threadStarted() {}
    
    protected void cleanup() {}
    
    /**
    * @return a string for the sampleResult Title
    */
    protected String getTitle() {
        return this.getName();
    }
    
    
    protected int getTimeoutAsInt() {
        if (getPropertyAsInt(TIMEOUT) < 1) {
            return DEFAULT_TIMEOUT;
        }
        return getPropertyAsInt(TIMEOUT);
    }
    
    public String getTimeout() {
        return getPropertyAsString(TIMEOUT, DEFAULT_TIMEOUT_STRING);
    }
    
    
    public void setTimeout(String s) {
        setProperty(TIMEOUT, s);
    }
    
    
    public String getIterations() {
        return getPropertyAsString(ITERATIONS, DEFAULT_ITERATIONS_STRING);
    }
    
    public void setIterations(String s) {
        setProperty(ITERATIONS, s);
    }
    
    public int getIterationsAsInt() {
        return getPropertyAsInt(ITERATIONS);
    }
}