package com.taorusb.springrestexample.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.taorusb.springrestexample.model.File;
import lombok.Data;
import org.intellij.lang.annotations.Pattern;

import java.util.Objects;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileDto {

    private Long userId;
    private Long id;
    private String name;
    private String URL;
    private String path;

    public File toFile() {
        File file = new File();
        file.setId(id);
        checkFile(path);
        file.setPath(path);
        file.setName(getFileName(path));
        file.setURL(URL);
        file.setUserId(userId);
        return file;
    }

    public static FileDto fromFile(File file) {
        FileDto fileDto = new FileDto();
        fileDto.setId(file.getId());
        fileDto.setName(file.getName());
        fileDto.setURL(file.getURL());
        return fileDto;
    }

    private void checkFile(String path) {
        if (Objects.isNull(path)) {
            throw new IllegalArgumentException("Path can not be null.");
        }
    }

    private String getFileName(String s) {
        String[] arr = s.split("/");
        String fileName = arr[arr.length - 1];
        return fileName;
    }
}
