package com.taorusb.springrestexample.aws.impl;

import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.codebuild.AWSCodeBuild;
import com.amazonaws.services.codebuild.AWSCodeBuildClient;
import com.amazonaws.services.codebuild.model.*;
import com.taorusb.springrestexample.aws.AwsCodebuildActions;

public class AwsCodebuildActionsImpl implements AwsCodebuildActions {

    private final AWSCodeBuild awsCodeBuild;
    private final String codebuildImage;
    private final String serviceRoleArn;

    public AwsCodebuildActionsImpl(String codebuildImage, String serviceRoleArn) {
        this.codebuildImage = codebuildImage;
        this.serviceRoleArn = serviceRoleArn;
        awsCodeBuild = AWSCodeBuildClient.builder()
                .withCredentials(InstanceProfileCredentialsProvider.getInstance())
                .build();
    }

    @Override
    public String createProject(String source, String name) {
        ProjectSource projectSource = new ProjectSource();
        projectSource.setType(SourceType.S3);
        projectSource.setLocation(source);
        ProjectArtifacts projectArtifacts = new ProjectArtifacts();
        projectArtifacts.setType(ArtifactsType.NO_ARTIFACTS);
        ProjectEnvironment projectEnvironment = new ProjectEnvironment();
        projectEnvironment.setType(EnvironmentType.LINUX_CONTAINER);
        projectEnvironment.setImage(codebuildImage);
        projectEnvironment.setPrivilegedMode(true);
        CreateProjectRequest createProjectRequest = new CreateProjectRequest();
        createProjectRequest.setName(name);
        createProjectRequest.setSource(projectSource);
        createProjectRequest.setEnvironment(projectEnvironment);
        createProjectRequest.setServiceRole(serviceRoleArn);
        createProjectRequest.setArtifacts(projectArtifacts);
        try {
            CreateProjectResult result = awsCodeBuild.createProject(createProjectRequest);
            return result.getProject().getName();
        } catch (ResourceAlreadyExistsException | InvalidInputException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void deleteProject(String name) {
        DeleteProjectRequest deleteProjectRequest = new DeleteProjectRequest();
        deleteProjectRequest.setName(name);
        awsCodeBuild.deleteProject(deleteProjectRequest);
    }

    @Override
    public String startBuild(String name) {
        StartBuildRequest startBuildRequest = new StartBuildRequest();
        startBuildRequest.setProjectName(name);
        return awsCodeBuild.startBuild(startBuildRequest).getBuild().getArn();
    }
}