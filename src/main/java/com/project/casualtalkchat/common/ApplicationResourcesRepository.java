package com.project.casualtalkchat.common;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Repository
public interface ApplicationResourcesRepository {

    default void saveFile(String path, byte[] file) throws FileCouldNotBeSavedException {
        try (FileOutputStream outputStream =
                     FileUtils.openOutputStream(new File(path))) {

            outputStream.write(file);
        } catch (IOException e) {
            throw new FileCouldNotBeSavedException(path);
        }
    }
}
