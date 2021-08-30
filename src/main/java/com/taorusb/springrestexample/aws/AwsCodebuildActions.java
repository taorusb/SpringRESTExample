package com.taorusb.springrestexample.aws;

public interface AwsCodebuildActions {

    String createProject(String source, String name);

    void deleteProject(String name);

    String startBuild(String name);
}
