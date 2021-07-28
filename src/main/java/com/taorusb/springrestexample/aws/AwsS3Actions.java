package com.taorusb.springrestexample.aws;

public interface AwsS3Actions {

    String addObject(String key, String path);

    void deleteObject(String objectName);
}
