package org.project.utils;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * 用于检测用户提交的审核图片是否正常
 * 通过读取图片文件元数据，分析创建时间和当前时间减去一天的关系，判断用户图片是否正常
 */
@Slf4j
public final class ImageExamine {

    public static Boolean getImageExamine(File savePath) {
        boolean flag = false;
        Metadata metadata;
        try {
            metadata = ImageMetadataReader.readMetadata(savePath);
            for (Directory directory : metadata.getDirectories()) {
                for (Tag tag : directory.getTags()) {
                    String tagName = tag.getTagName();
                    String des = tag.getDescription();
                    if ("Date/Time".equals(tagName) || "Date/Time Original".equals(tagName)) {
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss");
                        LocalDateTime localDateTime = LocalDateTime.parse(des, dtf);
                        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
                        long epochMilli = instant.toEpochMilli();
                        long epochSecond = Instant.now().toEpochMilli();
                        flag = epochSecond - 86400000L < epochMilli;
                        break;
                    }
                }
            }
        } catch (ImageProcessingException | IOException e) {
            log.error("图片上传出错" + e.getMessage());
        }
        return flag;
    }
}
