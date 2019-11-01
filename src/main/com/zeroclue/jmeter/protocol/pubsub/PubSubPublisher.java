package com.zeroclue.jmeter.protocol.pubsub;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.api.gax.rpc.ApiException;
import com.google.cloud.pubsub.v1.*;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.ProjectTopicName;
import com.google.pubsub.v1.PubsubMessage;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.management.RuntimeErrorException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.Interruptible;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.property.TestElementProperty;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

public class PubSubPublisher extends PubSubSampler implements Interruptible {
    private Publisher publisher;
    private ProjectTopicName topic;
    
    //++ These are JMX names, and must not be changed
    private final static String MESSAGE = "PubSubPublisher.Message";
    private final static String PROJECT_ID = "PubSubPublisher.ProjectId";
    private final static String TOPIC_NAME = "PubSubPublisher.TopicName";

    private static final Logger log = LoggingManager.getLoggerForClass();


    public PubSubPublisher() {
        super();
    }
    
    @Override
    public SampleResult sample(Entry e) {
        SampleResult result = new SampleResult();
        result.setSampleLabel(getName());
        result.setSuccessful(false);
        result.setResponseCode("500");

        topic = ProjectTopicName.of(getProjectId(), getTopicName());
        
        try {
            // Create a publisher instance with default settings bound to the topic
            publisher = Publisher.newBuilder(topic).build();
        } catch (Exception ex) {
            log.error("Failed to initialize channel : ", ex);
            result.setResponseMessage(ex.toString());
            return result;
        }
        
        ByteString data = ByteString.copyFromUtf8(getMessage()); // Sampler data
        
        result.setSampleLabel(getTitle());
        /*
        * Perform the sampling
        */
        
        // aggregate samples.
        int loop = getIterationsAsInt();
        result.sampleStart(); // Start timing
        try {            
            
            
            for (int idx = 0; idx < loop; idx++) {
                PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();
                
                // Once published, returns a server-assigned message id (unique within the topic)
                ApiFuture<String> future = publisher.publish(pubsubMessage);
                
                // Add an asynchronous callback to handle success / failure
                ApiFutures.addCallback(
                    future,
                    new ApiFutureCallback<String>() {
                        
                        @Override
                        public void onFailure(Throwable throwable) { throw new Error("Failure to Publish message", throwable); }
                        
                        @Override
                        public void onSuccess(String messageId) { }
                    },
                    MoreExecutors.directExecutor()
                );
            }
            
        } catch(Exception ex) {
            log.debug(ex.getMessage(), ex);
            result.setResponseCode("000");
            result.setResponseMessage(ex.toString());
        } finally {
            result.sampleEnd();
            if (publisher != null) {
                // When finished with the publisher, shutdown to free up resources.
                publisher.shutdown();
                // publisher.awaitTermination(1, TimeUnit.MINUTES);
            }
            
            return result;
        }
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