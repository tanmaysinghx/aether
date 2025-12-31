package com.aether.engine.media.internal.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class MediaUploadDto {
    // We cannot use MultipartFile inside a DTO labeled with @RequestBody easily
    // without custom deserializers if it's JSON.
    // However, for @ModelAttribute (what we likely use for multipart/form-data),
    // this works perfectly.
    private MultipartFile file;
    private String title;
    private String appSource;
    private String videoType;
    private String visibility;
    private String description;
    private String language;
    private String metadata; // Receiving as JSON string to be parsed manually typically, or we can use
                             // custom binding.
}
