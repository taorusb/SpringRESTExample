package com.taorusb.springrestexample.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.taorusb.springrestexample.model.ZipArchive;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ZipArchiveDto {

    public interface PostReq {
    }

    public interface PutReq {
    }

    @NotNull(groups = PostReq.class)
    private Long userId;
    @NotNull(groups = PutReq.class)
    private Long id;
    private String name;
    private String link;
    @NotNull(groups = {PostReq.class, PutReq.class})
    private String path;
    private String buildStatus;
    @NotNull(groups = {PostReq.class, PutReq.class})
    private String projectName;

    public ZipArchive toArchive() {
        ZipArchive zipArchive = new ZipArchive();
        zipArchive.setId(id);
        zipArchive.setUserId(userId);
        zipArchive.setName(getName(path));
        zipArchive.setLink(link);
        zipArchive.setPath(path);
        zipArchive.setProjectName(projectName);
        return zipArchive;
    }

    public static ZipArchiveDto fromArchive(ZipArchive zipArchive) {
        ZipArchiveDto zipArchiveDto = new ZipArchiveDto();
        zipArchiveDto.setId(zipArchive.getId());
        zipArchiveDto.setLink(zipArchive.getLink());
        zipArchiveDto.setName(zipArchive.getName());
        zipArchiveDto.setBuildStatus(zipArchive.getBuildingStatus().getName());
        return zipArchiveDto;
    }

    private String getName(String s) {
        String[] arr = s.split("/");
        String name = arr[arr.length - 1];
        return name;
    }
}
