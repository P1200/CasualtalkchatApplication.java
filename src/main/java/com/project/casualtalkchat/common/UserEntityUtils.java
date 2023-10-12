package com.project.casualtalkchat.common;

import com.project.casualtalkchat.security.CustomUserDetails;
import com.vaadin.flow.server.StreamResource;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserEntityUtils {

    public static final String USER_AVATARS_PATH = "/images/users/avatars/";
    public static final String DEFAULT_AVATAR_NAME = "default_avatar_image.png";
    public static final String DEFAULT_AVATAR_PATH = "/images/";

    public static StreamResource getAvatarResource(CustomUserDetails userDetails) {
        return getAvatarResource(userDetails.getAvatar());
    }

    public static StreamResource getAvatarResource(String avatarName) {
        StreamResource imageResource;
        if (avatarName == null) {

            imageResource = new StreamResource(DEFAULT_AVATAR_NAME,
                    () -> UserEntityUtils.class.getResourceAsStream(DEFAULT_AVATAR_PATH + DEFAULT_AVATAR_NAME));

        } else {

            imageResource = new StreamResource(avatarName,
                    () -> UserEntityUtils.class.getResourceAsStream(USER_AVATARS_PATH +
                            avatarName));
        }
        return imageResource;
    }
}
