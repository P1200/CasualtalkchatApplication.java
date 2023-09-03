package com.project.casualtalkchat.common;

import com.project.casualtalkchat.chat_page.UserEntity;
import com.project.casualtalkchat.security.CustomUserDetails;
import com.vaadin.flow.server.StreamResource;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserEntityUtils {

    public static StreamResource getAvatarResource(UserEntity userDetails) {
        return getAvatarResource(userDetails.getAvatarName());
    }

    public static StreamResource getAvatarResource(CustomUserDetails userDetails) {
        return getAvatarResource(userDetails.getAvatar());
    }

    private static StreamResource getAvatarResource(String userDetails) {
        StreamResource imageResource;
        if (userDetails == null) {

            imageResource = new StreamResource("default_avatar_image.png",
                    () -> UserEntityUtils.class.getResourceAsStream("/images/default_avatar_image.png"));

        } else {

            imageResource = new StreamResource(userDetails,
                    () -> UserEntityUtils.class.getResourceAsStream("/images/users/avatars/" +
                            userDetails));
        }
        return imageResource;
    }
}
