package com.zeroclue.jmeter.protocol.pubsub;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.ProjectTopicName;
import com.google.pubsub.v1.PubsubMessage;


import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.Interruptible;
import org.apache.jmeter.samplers.SampleResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PubSubPublisher extends PubSubSampler implements Interruptible {
    
    private static final long serialVersionUID = 1L;
    
    private Publisher publisher;
    private ProjectTopicName topic;
    
    //++ These are JMX names, and must not be changed
    private final static String MESSAGE = "PubSubPublisher.Message";
    private final static String PROJECT_ID = "PubSubPublisher.ProjectId";
    private final static String TOPIC_NAME = "PubSubPublisher.TopicName";
    
    private static final Logger logger = LoggerFactory.getLogger(PubSubPublisher.class);
    
    
    public PubSubPublisher() {
        super();
    }
    
    @Override
    public SampleResult sample(Entry e) {
        SampleResult result = new SampleResult();
        result.setSampleLabel(getName());
        result.setSuccessful(false);
        result.setResponseCode("500"); // internal error status
        
        topic = ProjectTopicName.of(getProjectId(), getTopicName());
        
        try {
            // Create a publisher instance with default settings bound to the topic
            logger.info("Creating PubSub producer");
            publisher = Publisher.newBuilder(topic).build();
        } catch (Exception ex) {
            logger.error("Failed to create a publisher instance: ", ex);
            result.setResponseMessage(ex.toString());
            return result;
        }
        
        ByteString data = ByteString.copyFromUtf8(getMessage()); // Sampler data
        PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();
        
        result.setSampleLabel(getTitle());
        /*
        * Perform the sampling
        */
        
        // aggregate samples.
        int loop = getIterationsAsInt();
        logger.info("Start sampling ("+ loop +" samples)");
        List<ApiFuture<String>> futures = new ArrayList<ApiFuture<String>>();
        result.sampleStart(); // Start timing
        try {
            for (int idx = 0; idx < loop; idx++) {
                // Once published, returns a server-assigned message id (unique within the topic)
                logger.info("Publishing message " + pubsubMessage.toString() + " to topic: " + getTopicName());
                ApiFuture<String> future = publisher.publish(pubsubMessage);
                futures.add(future);
            }
            ApiFutures.allAsList(futures).get(); // wait for all messages to be published
        } catch(Exception ex) {
            logger.debug(ex.getMessage(), ex);
            result.setResponseCode("500"); // internal error status
            result.setResponseMessage(ex.toString());
        } finally {
            result.sampleEnd(); // do we need to measure the time needed to shutdown the publisher?
            logger.info("Sample ended! @ PubSubPublisher");
            
            try {
                logger.info("Shutting down Publisher");
                publisher.shutdown();
                publisher.awaitTermination(1, TimeUnit.MINUTES);
            } catch (Exception ex) {
                logger.error("Failed to shut Publisher down: ", ex);
            }
            
        }
        return result;
    }
    
    @Override
    public boolean interrupt() {
        cleanup();
        return true;
    }
    
    /**
    * @return the message for the sample
    */
    public String getMessage() {
        return getPropertyAsString(MESSAGE);
    }
    
    public void setMessage(String content) {
        setProperty(MESSAGE, content);
    }
    
    
    /**
    * @return the name of the topic for the sample
    */
    public String getTopicName() {
        return getPropertyAsString(TOPIC_NAME);
    }
    
    public void setTopicName(String name) {
        setProperty(TOPIC_NAME, name);
    }
    
    /**
    * @return the project id for the sample
    */
    public String getProjectId() {
        return getPropertyAsString(PROJECT_ID);
    }
    
    public void setProjectId(String id) {
        setProperty(PROJECT_ID, id);
    }
}