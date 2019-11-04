package com.zeroclue.jmeter.protocol.pubsub;

import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;

import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.Interruptible;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.TestStateListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public class PubSubConsumer extends PubSubSampler implements Interruptible, TestStateListener {
    
    private static final long serialVersionUID = 1L;
    
    private Subscriber consumer;
    
    //++ These are JMX names, and must not be changed
    private final static String PROJECT_ID = "PubSubConsumer.ProjectId";
    private final static String SUBSCRIPTION_NAME = "PubSubConsumer.SubscriptionName";
    
    private static final Logger logger = LoggerFactory.getLogger(PubSubConsumer.class);
    public static final BlockingQueue<PubsubMessage> receivedMessages = new LinkedBlockingDeque<PubsubMessage>();
    
    public PubSubConsumer() {
        super();
    }
    
    @Override
    public SampleResult sample(Entry entry) {
        SampleResult result = new SampleResult();
        result.setSampleLabel(getName());
        result.setSuccessful(false);
        result.setResponseCode("500"); // internal error status
        
        MessageReceiver receiver = new MessageReceiver() {
            
            @Override
            public void receiveMessage(PubsubMessage message, AckReplyConsumer consumer) {
                logger.info("Message received: " + message.toString());
                consumer.ack();
                try {
                    receivedMessages.put(message);
                } catch(Exception ex) {
                    logger.error("Failure when receiving message. ", ex.toString());
                }
            }
        };
        
        ProjectSubscriptionName subscriptionName = ProjectSubscriptionName.of(getProjectId(), getSubscriptionName());
        // create the consumer
        try {
            if (consumer == null) {
                logger.info("Creating PubSub consumer");
                consumer = Subscriber.newBuilder(subscriptionName, receiver).build();
            }
        } catch (Exception ex) {
            logger.error("Failed to initialize PubSub consumer", ex);
            result.setResponseMessage(ex.toString());
            return result;
        }
        
        // start receiving messages / aggregate samples.
        int loop = getIterationsAsInt();
        logger.info("Start sampling ("+ loop +" samples)");
        result.sampleStart(); // Start timing
        try {
            consumer.startAsync().awaitRunning();
            int count = 0;
            while(count < loop) {
                PubsubMessage message = receivedMessages.take();
                result.setResponseData(message.getMessageId(), null);
                result.setDataType(SampleResult.TEXT);
                result.setResponseCodeOK();
                result.setSuccessful(true);
                
                count++;
            }
        } catch (Exception ex) {
            consumer = null;
            logger.error("PubSub consumer failed to consume", ex);
            result.setResponseCode("500"); // internal error status
            result.setResponseMessage(ex.getMessage());
        } finally {
            result.sampleEnd(); // End timimg
            logger.info("Sample ended! @ PubSubConsumer");
            
            consumer.stopAsync().awaitTerminated();
        }
        
        // trace("PubSubConsumer.sample ended");
        
        return result;
    }
    
    
    
    
    /**
    * @return the name of the topic for the sample
    */
    public String getSubscriptionName() {
        return getPropertyAsString(SUBSCRIPTION_NAME);
    }
    
    public void setSubscriptionName(String name) {
        setProperty(SUBSCRIPTION_NAME, name);
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
    
    
    @Override
    public boolean interrupt() {
        testEnded();
        return true;
    }
    
    /**
    * {@inheritDoc}
    */
    @Override
    public void testEnded() {}
    
    @Override
    public void testEnded(String arg0) {}
    
    @Override
    public void testStarted() {}
    
    @Override
    public void testStarted(String arg0) {}
    
}