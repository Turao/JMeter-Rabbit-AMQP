package com.zeroclue.jmeter.protocol.pubsub;

import com.google.cloud.pubsub.v1.Publisher;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.api.gax.rpc.ApiException;
import com.google.cloud.pubsub.v1.*;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.ProjectTopicName;

import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.Interruptible;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.property.TestElementProperty;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class PubSubPublisher extends PubSubSampler implements Interruptible {
    private Publisher publisher;
    private ProjectTopicName topicName = ProjectTopicName.of("my-project-id", "my-topic-id");
    

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
        
        try {
            // Create a publisher instance with default settings bound to the topic
            publisher = Publisher.newBuilder(topicName).build();
        } catch (Exception ex) {
            log.error("Failed to initialize channel : ", ex);
            result.setResponseMessage(ex.toString());
            return result;
        }
        
        String data = getMessage(); // Sampler data
        
        result.setSampleLabel(getTitle());
        /*
        * Perform the sampling
        */
        
        // aggregate samples.
        int loop = getIterationsAsInt();
        result.sampleStart(); // Start timing
        try {            
            
            ByteString data = ByteString.copyFromUtf8(data);
            for (int idx = 0; idx < loop; idx++) {
                PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();
                
                // Once published, returns a server-assigned message id (unique within the topic)
                ApiFuture<String> future = publisher.publish(pubsubMessage);
                
                // Add an asynchronous callback to handle success / failure
                ApiFutures.addCallback(
                    future,
                    new ApiFutureCallback<String>() {
                        
                        @Override
                        public void onFailure(Throwable throwable) { throw throwable; }
                        
                        @Override
                        public void onSuccess(String messageId) { }
                    },
                    MoreExecutors.directExecutor()
                );
            }
            
        } catch(Exception e) {
            log.debug(ex.getMessage(), ex);
            result.setResponseCode("000");
            result.setResponseMessage(ex.toString());
        } finally {
            result.sampleEnd();
            if (publisher != null) {
                // When finished with the publisher, shutdown to free up resources.
                publisher.shutdown();
                publisher.awaitTermination(1, TimeUnit.MINUTES);
            }
            
            return result;
        }
    }

    @Override
    public boolean interrupt() {
        cleanup();
        return true;
    }
}