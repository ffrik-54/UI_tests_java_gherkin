package com.utils;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SqsException;

/**
 * Utils for interaction with Amazon SQS
 *
 * @author pierredesporte
 */

public class AwsSQSManager {

    String queueName = "queue" + System.currentTimeMillis();

    private static final SqsClient SQS_CLIENT = SqsClient.builder().region(Region.EU_WEST_1).build();

    private AwsSQSManager() {
    }

    public static SqsClient getSqsClient() {
        return SQS_CLIENT;
    }

    /**
     * Send a message to a queue Amazon SQS with message and attribute.
     *
     * @param sqsClient,       the Amazon SQS.
     * @param queueUrl,        the url of the queue.
     * @param message,         the message to send.
     * @param attributes,      the map of attributes.
     * @param groupId,         the id of the group.
     * @param deduplicationId, the id of the deduplication.
     * @throws IOException
     **/
    public static void sendMessage(SqsClient sqsClient, String queueUrl, String message,
        Map<String, MessageAttributeValue> attributes, int groupId, int deduplicationId) throws IOException {

        Logger.getGlobal().log(Level.INFO, "SqsClient : Send message");
        try {
            sqsClient.sendMessage(
                SendMessageRequest.builder().queueUrl(queueUrl).messageBody(message).messageAttributes(attributes)
                    .messageDeduplicationId(String.valueOf(deduplicationId)).messageGroupId(String.valueOf(groupId))
                    .build());

        } catch (SqsException e) {
            Logger.getGlobal().log(Level.WARNING, "sendMessage error : {0}", e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}