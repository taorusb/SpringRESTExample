package com.taorusb.springrestexample.config;

import com.taorusb.springrestexample.aws.AwsS3Actions;
import com.taorusb.springrestexample.aws.impl.AwsS3ActionsImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsS3Config {

    @Bean(name = "s3-for-file")
    public AwsS3Actions getS3ConfigForFile(@Value("${aws.s3.credentials.profile.for.file}") String credentialsProfile,
                                          @Value("${aws.s3.bucket.name.for.file}")  String bucketName) {
        return new AwsS3ActionsImpl(credentialsProfile, bucketName);
    }

    @Bean(name = "s3-for-archive")
    public AwsS3Actions getS3ConfigForArchive(@Value("${aws.s3.credentials.profile.for.archive}") String credentialsProfile,
                                           @Value("${aws.s3.bucket.name.for.archive}")  String bucketName) {
        return new AwsS3ActionsImpl(credentialsProfile, bucketName);
    }
}
