// package com.zeroclue.jmeter.protocol.pubsub;

// import com.google.cloud.pubsub.v1.Subscriber;

// import org.apache.jmeter.samplers.Entry;
// import org.apache.jmeter.samplers.Interruptible;
// import org.apache.jmeter.samplers.SampleResult;
// import org.apache.jmeter.testelement.TestStateListener;
// import org.apache.jorphan.logging.LoggingManager;
// import org.apache.log.Logger;

// public class PubSubConsumer extends PubSubSampler implements Interruptible, TestStateListener {

//     private Subscriber subscriber;

//     public PubSubConsumer() {
//         super();
//     }

//     @Override
//     public SampleResult sample(Entry entry) {
//         SampleResult result = new SampleResult();
//         result.setSampleLabel(getName());
//         result.setSuccessful(false);
//         result.setResponseCode("500");

//         trace("PubSubConsumer.sample()");

//         try {
//             subscriber = Subscriber.newBuilder(subscriptionName, new MessageReceiverImpl()).build();
            

//             // only do this once per thread. Otherwise it slows down the consumption by appx 50%
//             if (consumer == null) {
//                 log.info("Creating consumer");
//                 consumer = new QueueingConsumer(channel);
//             }
//             if (consumerTag == null) {
//                 log.info("Starting basic consumer");
//                 consumerTag = channel.basicConsume(getQueue(), autoAck(), consumer);
//             }
//         } catch (Exception ex) {
//             log.error("Failed to initialize channel", ex);
//             result.setResponseMessage(ex.toString());
//             return result;
//         }

//         // aggregate samples.
//         int loop = getIterationsAsInt();
//         result.sampleStart(); // Start timing
//         try {
//             for (int idx = 0; idx < loop; idx++) {
//                 delivery = consumer.nextDelivery(getReceiveTimeoutAsInt());

//                 if(delivery == null){
//                     result.setResponseMessage("timed out");
//                     return result;
//                 }

//                 /*
//                  * Set up the sample result details
//                  */
//                 if (getReadResponseAsBoolean()) {
//                     String response = new String(delivery.getBody());
//                     result.setSamplerData(response);
//                     result.setResponseMessage(response);
//                 }
//                 else {
//                     result.setSamplerData("Read response is false.");
//                 }

//                 if(!autoAck())
//                     channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
//             }

//             result.setResponseData("OK", null);
//             result.setDataType(SampleResult.TEXT);

//             result.setResponseCodeOK();
//             result.setSuccessful(true);
//         } catch (ShutdownSignalException e) {
//             consumer = null;
//             log.warn("PubSub consumer failed to consume", e);
//             result.setResponseCode("400");
//             result.setResponseMessage(e.getMessage());
//             interrupt();
//         } catch (ConsumerCancelledException e) {
//             consumer = null;
//             log.warn("PubSub consumer failed to consume", e);
//             result.setResponseCode("300");
//             result.setResponseMessage(e.getMessage());
//             interrupt();
//         } catch (InterruptedException e) {
//             consumer = null;
//             log.info("interuppted while attempting to consume");
//             result.setResponseCode("200");
//             result.setResponseMessage(e.getMessage());
//         } catch (IOException e) {
//             consumer = null;
//             log.warn("PubSub consumer failed to consume", e);
//             result.setResponseCode("100");
//             result.setResponseMessage(e.getMessage());
//         } finally {
//             result.sampleEnd(); // End timimg
//         }

//         trace("PubSubConsumer.sample ended");

//         return result;
//     }

// }