package com.project.casualtalkchat.common;

import com.vaadin.flow.server.InputStreamFactory;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

@Repository
public interface UserImagesRepository {

    String PATH_TO_USER_IMAGES = "user/images/";
    Path ABSOLUTE_PATH_TO_USER_IMAGES = Paths.get(PATH_TO_USER_IMAGES)
                                                .toAbsolutePath()
                                                .normalize();

    default void saveFile(String path, byte[] file) throws FileCouldNotBeSavedException {
        try (FileOutputStream outputStream =
                     FileUtils.openOutputStream(new File(ABSOLUTE_PATH_TO_USER_IMAGES + "/" + path))) {

            outputStream.write(file);
        } catch (IOException e) {
            throw new FileCouldNotBeSavedException(path);
        }
    }

    default InputStreamFactory getFile(String path) throws FileCouldNotBeGetException {
        try {
            //Don`t close the stream
            FileInputStream fileInputStream = new FileInputStream(ABSOLUTE_PATH_TO_USER_IMAGES + "/" + path);
            return () -> fileInputStream;
        } catch (FileNotFoundException e) {
            throw new FileCouldNotBeGetException(path);
        }
    }
}
