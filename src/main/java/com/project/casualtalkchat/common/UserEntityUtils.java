package com.project.casualtalkchat.common;

import com.project.casualtalkchat.security.CustomUserDetails;
import com.vaadin.flow.server.StreamResource;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserEntityUtils { //TODO merge this class with UserImagesRepository

    public static final String USER_AVATARS_PATH = "avatars/";
    public static final String DEFAULT_AVATAR_NAME = "default_avatar_image.png";
    public static final String DEFAULT_AVATAR_PATH = "/images/";
    static String PATH_TO_USER_IMAGES = "user/images/";
    static Path ABSOLUTE_PATH_TO_USER_AVATARS = Paths.get(PATH_TO_USER_IMAGES + USER_AVATARS_PATH)
            .toAbsolutePath()
            .normalize();

    public static StreamResource getAvatarResource(CustomUserDetails userDetails) {
        return getAvatarResource(userDetails.getAvatar());
    }

    public static StreamResource getAvatarResource(String avatarName) {
        StreamResource imageResource;
        if (avatarName == null) {
            imageResource = getDefaultAvatarResource();
        } else {
            try {
                //Don`t close the stream
                FileInputStream fileInputStream =
                        new FileInputStream(ABSOLUTE_PATH_TO_USER_AVATARS + "/" + avatarName);
                imageResource = new StreamResource(avatarName, () -> fileInputStream);
            } catch (FileNotFoundException e) {
                log.debug("User avatar resource not found.");
                imageResource = getDefaultAvatarResource();
            }
        }
        return imageResource;
    }

    private static StreamResource getDefaultAvatarResource() {
        if (UserEntityUtils.class.getResource(DEFAULT_AVATAR_PATH + DEFAULT_AVATAR_NAME) != null) {
            return new StreamResource(DEFAULT_AVATAR_NAME,
                    () -> UserEntityUtils.class.getResourceAsStream(DEFAULT_AVATAR_PATH + DEFAULT_AVATAR_NAME));
        } else {
            log.error("Default avatar resource doesn't exist.");
            return null;
        }
    }
}
