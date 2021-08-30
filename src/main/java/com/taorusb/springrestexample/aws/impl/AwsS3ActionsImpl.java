package com.taorusb.springrestexample.aws.impl;

import com.taorusb.springrestexample.aws.AwsS3Actions;
import software.amazon.awssdk.auth.credentials.InstanceProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


public class AwsS3ActionsImpl implements AwsS3Actions {

    private String bucketName;
    private S3Client s3Client;

    public AwsS3ActionsImpl(String bucketName) {
        this.bucketName = bucketName;
        s3Client = S3Client.builder()
                .credentialsProvider(InstanceProfileCredentialsProvider.builder().build())
                .build();
    }

    @Override
    public String addObject(String key, String path) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("x-amz-meta-myVal", "test");
        PutObjectRequest putOb = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .metadata(metadata)
                .build();
        s3Client.putObject(putOb, RequestBody.fromBytes(getObjectFile(path)));
        return getObjectUrl(key);
    }

    private static byte[] getObjectFile(String filePath) {
        FileInputStream fileInputStream = null;
        byte[] bytesArray = null;
        try {
            File file = new File(filePath);
            bytesArray = new byte[(int) file.length()];
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bytesArray);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("File path is invalid.");
        } catch (IOException e) {
            throw new RuntimeException(e.getCause());
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bytesArray;
    }

    @Override
    public void deleteObject(String key) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            s3Client.deleteObject(deleteObjectRequest);
        } catch (S3Exception e) {
            throw new RuntimeException(e.getCause());
        }
    }

    private String getObjectUrl(String key) {
        try {
            GetUrlRequest request = GetUrlRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            URL url = s3Client.utilities().getUrl(request);
            return url.toString();
        } catch (S3Exception e) {
            throw new RuntimeException(e.getCause());
        }
    }
}