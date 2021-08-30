package com.taorusb.springrestexample.aws.impl;

import com.jayway.jsonpath.JsonPath;
import com.taorusb.springrestexample.aws.AwsSqsActions;
import org.jetbrains.annotations.NotNull;
import software.amazon.awssdk.auth.credentials.InstanceProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AwsSqsActionsImpl implements AwsSqsActions, Runnable {

    private final SqsClient sqsClient;
    private final String queueName;
    private String queUrl;
    private final Deque<String> inputProjectNames = new ArrayDeque<>();
    private final List<String> namesBuffer;
    private final Map<String, String> handledResults = new HashMap<>();
    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private final Semaphore semaphore;
    private boolean isWorkerWait = false;

    public AwsSqsActionsImpl(String queueName, int bufferSize) {
        this.queueName = queueName;
        semaphore = new Semaphore(bufferSize, true);
        namesBuffer = new ArrayList<>(bufferSize);
        Executors.newSingleThreadExecutor(new CustomThread()).execute(this);
        sqsClient = SqsClient.builder()
                .credentialsProvider(InstanceProfileCredentialsProvider.builder().build())
                .build();
        setQueUrl();
    }

    private void setQueUrl() {
        GetQueueUrlResponse getQueueUrlResponse =
                sqsClient.getQueueUrl(GetQueueUrlRequest.builder().queueName(queueName).build());
        queUrl = getQueueUrlResponse.queueUrl();
    }

    @Override
    public String getMessage(String key) {
        String buildingStatus = "";
        try {
            semaphore.acquire();
            inputProjectNames.addFirst(key);
            lock.lock();
            namesBuffer.add(inputProjectNames.pop());
            if (isWorkerWait) {
                condition.signal();
            }
            while (!handledResults.containsKey(key)) {
                condition.await();
            }
            buildingStatus = handledResults.remove(key);
            return buildingStatus;
        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + " is interrupted");
        } finally {
            lock.unlock();
            semaphore.release();
        }
        return buildingStatus;
    }

    private void handleRemoteQueue() {
        ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
                .queueUrl(queUrl)
                .waitTimeSeconds(20)
                .maxNumberOfMessages(10)
                .build();
        List<Message> messages = sqsClient.receiveMessage(receiveMessageRequest).messages();
        messages.stream().filter(message -> {
            if (namesBuffer.contains(JsonPath.parse(message.body()).read("$.build-id"))) {
                return true;
            } else {
                if (!inputProjectNames.isEmpty()) {
                    namesBuffer.add(inputProjectNames.pop());
                }
                return false;
            }
        })
                .map(message -> {
                    deleteMessage(message);
                    return JsonPath.parse(message.body());
                })
                .peek(documentContext -> namesBuffer.remove(documentContext.read("$.build-id")))
                .forEach(documentContext -> {
                    String key = documentContext.read("$.build-id");
                    String value = documentContext.read("$.build-status");
                    handledResults.put(key, value);
                });
    }

    private void deleteMessage(Message message) {
        DeleteMessageRequest deleteMessageRequest = DeleteMessageRequest.builder()
                .queueUrl(queUrl)
                .receiptHandle(message.receiptHandle())
                .build();
        sqsClient.deleteMessage(deleteMessageRequest);
    }

    @Override
    public void run() {
        try {
            while (true) {
                lock.lock();
                if (inputProjectNames.isEmpty() && namesBuffer.isEmpty()) {
                    isWorkerWait = true;
                    condition.await();
                }
                handleRemoteQueue();
                condition.signalAll();
            }
        } catch (InterruptedException e) {
            System.out.println("worker thread interrupted");
        } finally {
            lock.unlock();
        }
    }

    private static class CustomThread implements ThreadFactory {
        @Override
        public Thread newThread(@NotNull Runnable r) {
            Thread thread = new Thread(r);
            thread.setDaemon(false);
            return thread;
        }
    }
}