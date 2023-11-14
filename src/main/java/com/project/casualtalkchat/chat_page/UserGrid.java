package com.project.casualtalkchat.chat_page;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;

import static com.project.casualtalkchat.common.UserEntityUtils.getAvatarResource;

class UserGrid extends Grid<UserEntity> {

    public UserGrid() {
        this.addComponentColumn(userEntity -> {
            Image avatar = new Image(getAvatarResource(userEntity.getAvatarName()), "avatar");
            avatar.setWidth(50, Unit.PERCENTAGE);
            return avatar;
        });
        this.addColumn(UserEntity::getUsername, "username");
    }
}
