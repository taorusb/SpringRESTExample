package com.taorusb.springrestexample.config;

import com.taorusb.springrestexample.aws.AwsCodebuildActions;
import com.taorusb.springrestexample.aws.AwsS3Actions;
import com.taorusb.springrestexample.aws.AwsSqsActions;
import com.taorusb.springrestexample.aws.impl.AwsCodebuildActionsImpl;
import com.taorusb.springrestexample.aws.impl.AwsS3ActionsImpl;
import com.taorusb.springrestexample.aws.impl.AwsSqsActionsImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsConfig {

    @Bean(name = "s3-for-file")
    public AwsS3Actions getS3BeanForFile(@Value("${aws.s3.bucket.name.for.file}")  String bucketName) {
        return new AwsS3ActionsImpl(bucketName);
    }

    @Bean(name = "s3-for-archive")
    public AwsS3Actions getS3BeanForArchive(@Value("${aws.s3.bucket.name.for.archive}")  String bucketName) {
        return new AwsS3ActionsImpl(bucketName);
    }

    @Bean
    public AwsSqsActions getSqsBean(@Value("${aws.sqs.queue.name}") String queueName,
                                    @Value("${aws.sqs.module.buffer.size}") int bufferSize) {
        return new AwsSqsActionsImpl(queueName, bufferSize);
    }

    @Bean
    public AwsCodebuildActions getCodebuildBean(@Value("${aws.codebuild.container.image}") String codebuildImage,
                                               @Value("${aws.codebuild.service.role.arn}") String serviceRoleArn) {
        return new AwsCodebuildActionsImpl(codebuildImage, serviceRoleArn);
    }
}
