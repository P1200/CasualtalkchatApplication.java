package com.project.casualtalkchat.common;

import com.project.casualtalkchat.security.CustomUserDetails;
import com.vaadin.flow.server.StreamResource;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserEntityUtils {

    public static final String USER_AVATARS_PATH = "avatars/";
    public static final String DEFAULT_AVATAR_NAME = "default_avatar_image.png";
    public static final String DEFAULT_AVATAR_PATH = "/images/";

    public static StreamResource getAvatarResource(CustomUserDetails userDetails) {
        return getAvatarResource(userDetails.getAvatar());
    }

    public static StreamResource getAvatarResource(String avatarName) {
        StreamResource imageResource;
        if (avatarName == null) {
            imageResource = getDefaultAvatarResource();
        } else {
            if (UserEntityUtils.class.getResource(USER_AVATARS_PATH + avatarName) != null) {
                imageResource = new StreamResource(avatarName,
                        () -> UserEntityUtils.class.getResourceAsStream(USER_AVATARS_PATH + avatarName));
            } else {
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
