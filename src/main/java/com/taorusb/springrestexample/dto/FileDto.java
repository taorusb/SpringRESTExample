package com.taorusb.springrestexample.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.taorusb.springrestexample.model.File;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileDto {

    public interface PostReq {

    }

    public interface PutReq {

    }


    @NotNull(groups = PutReq.class)
    private Long id;
    @NotNull(groups = PostReq.class)
    private Long userId;
    private String name;
    private String link;
    @NotNull(groups = {PostReq.class, PutReq.class})
    private String path;

    public File toFile() {
        File file = new File();
        file.setId(id);
        file.setUserId(userId);
        file.setPath(path);
        file.setName(getFileName(path));
        file.setLink(link);
        return file;
    }

    public static FileDto fromFile(File file) {
        FileDto fileDto = new FileDto();
        fileDto.setId(file.getId());
        fileDto.setName(file.getName());
        fileDto.setLink(file.getLink());
        return fileDto;
    }

    private String getFileName(String s) {
        String[] arr = s.split("/");
        String fileName = arr[arr.length - 1];
        return fileName;
    }
}
