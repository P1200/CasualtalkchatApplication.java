package com.project.casualtalkchat.chat_page;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.dom.Style;

import static com.project.casualtalkchat.common.UserEntityUtils.getAvatarResource;

public class TabsSectionComponent extends Div {

    private final UserFilter personFilter = new UserFilter();
    private final ConfigurableFilterDataProvider<UserEntity, Void, UserFilter> userFilterDataProvider;
    private final ConfigurableFilterDataProvider<UserEntity, Void, UserFilter> invitationFilterDataProvider;
    private final ConfigurableFilterDataProvider<UserEntity, Void, UserFilter> friendFilterDataProvider;
    private final UserService service;
    private final String userId;

    public TabsSectionComponent(UserService userService, String userId) {

        this.userId = userId;
        this.service = userService;
        UserDataProvider userDataProvider = new UserDataProvider(userService, userId);
        this.userFilterDataProvider = userDataProvider.withConfigurableFilter();

        InvitationsDataProvider invitationsDataProvider = new InvitationsDataProvider(userService, userId);
        this.invitationFilterDataProvider = invitationsDataProvider.withConfigurableFilter();

        FriendsDataProvider friendsDataProvider = new FriendsDataProvider(userService, userId);
        this.friendFilterDataProvider = friendsDataProvider.withConfigurableFilter();

        TabSheet tabSheet = new TabSheet();

        Tab chats = new Tab(VaadinIcon.CHAT.create(), new Span("Chats"));
        tabSheet.add(chats, getContent(new Text("Chats content"), new Button("Add new chat")));

        Tab friends = new Tab(VaadinIcon.USERS.create(), new Span("Friends"));
        Button addNewFriend = new Button("Add new friend");
        Dialog createAddNewFriendDialog = getAddNewFriendDialog();
        addNewFriend.addClickListener(event -> createAddNewFriendDialog.open());
        tabSheet.add(friends, getContent(getFriendsDataGrid(), addNewFriend));

        Tab invitations = new Tab(VaadinIcon.ENVELOPES.create(), new Span("Invitations"));
        tabSheet.add(invitations, getContent(getInvitationsDataGrid(), new Button("Add new friend")));

        tabSheet.setHeight(100, Unit.PERCENTAGE);

        add(createAddNewFriendDialog, tabSheet);
    }

    private Div getContent(Component tabContent, Button button) {
        Scroller chatsScroller = new Scroller(tabContent);

        button.getStyle()
                .setPosition(Style.Position.ABSOLUTE)
                .setBottom("0");
        Div content = new Div(chatsScroller, button);
        content.getStyle()
                .setPosition(Style.Position.RELATIVE)
                .setHeight("100%");
        return content;
    }

    private Dialog getAddNewFriendDialog() {
        Dialog dialog = new Dialog();

        dialog.setHeaderTitle("Add new friend");

        VerticalLayout dialogLayout = getUserDataGrid();
        dialog.add(dialogLayout);

        Button closeButton = new Button("Close", e -> dialog.close());
        dialog.getFooter().add(closeButton);

        Button closeCrossButton = new Button(new Icon("lumo", "cross"),
                e -> dialog.close());
        closeCrossButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        dialog.getHeader().add(closeCrossButton);

        dialog.setModal(false);
        dialog.setDraggable(true);
        dialog.setWidth(50, Unit.VW);

        return dialog;
    }

    private VerticalLayout getUserDataGrid() {

        Grid<UserEntity> grid = getUserEntityGrid();
        grid.addColumn(UserEntity::getId, "userId");
        grid.addComponentColumn(userEntity -> {
            Button inviteButton = new Button("Invite");
            inviteButton.setDisableOnClick(true);
            inviteButton.addClickListener(event -> {
                service.addUserAsInvited(userId, userEntity);
                inviteButton.setText("Invited");
            });
            return  inviteButton;
        });
        return getLayout(grid, userFilterDataProvider);
    }

    private Component getFriendsDataGrid() {

        Grid<UserEntity> grid = getUserEntityGrid();
        return getLayout(grid, friendFilterDataProvider);
    }

    private VerticalLayout getLayout(Grid<UserEntity> grid, ConfigurableFilterDataProvider<UserEntity, Void, UserFilter> filterDataProvider) {
        grid.setItems(filterDataProvider);

        TextField searchField = new TextField();
        searchField.setWidth("50%");
        searchField.setPlaceholder("Search");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(e -> {
            personFilter.setSearchTerm(e.getValue());
            filterDataProvider.setFilter(personFilter);
        });

        VerticalLayout layout = new VerticalLayout(searchField, grid);
        layout.setPadding(false);
        return layout;
    }

    private Component getInvitationsDataGrid() {

        Grid<UserEntity> grid = getUserEntityGrid();
        grid.addComponentColumn(userEntity -> {
            Button acceptInvitationButton = new Button(VaadinIcon.CHECK_CIRCLE.create());
            acceptInvitationButton.addClickListener(event -> {
                service.acceptInvitation(userId, userEntity);
                grid.getDataProvider().refreshAll();
            });

            Button removeInvitationButton = new Button(new Icon("lumo", "cross"));
            removeInvitationButton.addClickListener(event -> {
                service.removeInvitation(userId, userEntity);
                grid.getDataProvider().refreshAll();
            });
            return new Div(acceptInvitationButton, removeInvitationButton);
        });
        return getLayout(grid, invitationFilterDataProvider);
    }

    private Grid<UserEntity> getUserEntityGrid() {
        Grid<UserEntity> grid = new Grid<>();
        grid.addComponentColumn(userEntity -> {
            Image avatar = new Image(getAvatarResource(userEntity), "avatar");
            avatar.setWidth(50, Unit.PERCENTAGE);
            return avatar;
        });
        grid.addColumn(UserEntity::getUsername, "username");
        return grid;
    }
}
