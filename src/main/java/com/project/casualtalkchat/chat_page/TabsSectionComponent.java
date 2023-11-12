package com.project.casualtalkchat.chat_page;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.avatar.AvatarGroup;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
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
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.shared.Registration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;

import java.util.Objects;

import static com.project.casualtalkchat.common.UserEntityUtils.getAvatarResource;

@Slf4j
public class TabsSectionComponent extends Div {

    private final UserFilter personFilter = new UserFilter();
    private final ConversationFilter chatsFilter = new ConversationFilter();
    private final ConfigurableFilterDataProvider<UserEntity, Void, UserFilter> userFilterDataProvider;
    private final ConfigurableFilterDataProvider<UserEntity, Void, UserFilter> invitationFilterDataProvider;
    private final ConfigurableFilterDataProvider<UserEntity, Void, UserFilter> friendFilterDataProvider;
    private final ConfigurableFilterDataProvider<ConversationEntity, Void, ConversationFilter> conversationFilterDataProvider;
    private final UserService userService;
    private final ConversationService conversationService;
    private final String userId;
    private final TabSheet tabSheet;
    private final Tab chats;
    private final Audio messageSoundComponent;
    private final ConversationDataProvider conversationDataProvider;
    private Grid<ConversationEntity> conversationsGrid;
    private Registration broadcasterRegistration;

    public TabsSectionComponent(UserService userService, ConversationService conversationService, String userId) {

        this.userId = userId;
        this.userService = userService;
        this.conversationService = conversationService;
        UserDataProvider userDataProvider = new UserDataProvider(userService, userId);
        this.userFilterDataProvider = userDataProvider.withConfigurableFilter();

        InvitationsDataProvider invitationsDataProvider = new InvitationsDataProvider(userService, userId);
        this.invitationFilterDataProvider = invitationsDataProvider.withConfigurableFilter();

        FriendDataProvider friendDataProvider = new FriendDataProvider(userService, userId);
        this.friendFilterDataProvider = friendDataProvider.withConfigurableFilter();

        conversationDataProvider = new ConversationDataProvider(conversationService, userId);
        this.conversationFilterDataProvider = conversationDataProvider.withConfigurableFilter();

        tabSheet = new TabSheet();

        chats = new Tab(VaadinIcon.CHAT.create(), new Span("Chats"));
        Button addNewChatButton = new Button("Add new chat");
        addNewChatButton.addClickListener(event -> {throw new NotImplementedException();});
        tabSheet.add(chats, getContent(getChatsDataGrid(), addNewChatButton));

        Tab friends = new Tab(VaadinIcon.USERS.create(), new Span("Friends"));
        Button addNewFriend = new Button("Add new friend");
        Dialog createAddNewFriendDialog = getAddNewFriendDialog();
        addNewFriend.addClickListener(event -> createAddNewFriendDialog.open());
        tabSheet.add(friends, getContent(getFriendsDataGrid(), addNewFriend));

        Tab invitations = new Tab(VaadinIcon.ENVELOPES.create(), new Span("Invitations"));
        Button addNewFriendButtonInInvitations = new Button("Add new friend");
        tabSheet.add(invitations, getContent(getInvitationsDataGrid(), addNewFriendButtonInInvitations));

        tabSheet.setHeight(100, Unit.PERCENTAGE);

        messageSoundComponent = getMessageSoundComponent();

        add(createAddNewFriendDialog, tabSheet, messageSoundComponent);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        UI ui = attachEvent.getUI();
        broadcasterRegistration = Broadcaster.register((newMessage, conversationId) -> ui.access(() -> {
            if (isDataGridContainingChatWithGivenId(conversationId) && !userId.equals(newMessage.getSenderId())) {
                messageSoundComponent.getElement()
                        .executeJs("this.play();");
                conversationDataProvider.refreshAll();
            }
        }));
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        broadcasterRegistration.remove();
        broadcasterRegistration = null;
    }

    private boolean isDataGridContainingChatWithGivenId(String conversationId) {
        return conversationDataProvider.getConversations()
                .stream()
                .anyMatch(conversationEntity -> conversationEntity.getId().equals(conversationId));
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
                userService.addUserAsInvited(userId, userEntity);
                inviteButton.setText("Invited");
            });
            return  inviteButton;
        });
        return getLayout(grid, userFilterDataProvider);
    }

    private Component getInvitationsDataGrid() {

        Grid<UserEntity> grid = getUserEntityGrid();
        grid.addComponentColumn(userEntity -> {
            Button acceptInvitationButton = new Button(VaadinIcon.CHECK_CIRCLE.create());
            acceptInvitationButton.addClickListener(event -> {
                userService.acceptInvitation(userId, userEntity);
                grid.getDataProvider().refreshAll();
            });

            Button removeInvitationButton = new Button(new Icon("lumo", "cross"));
            removeInvitationButton.addClickListener(event -> {
                userService.removeInvitation(userId, userEntity);
                grid.getDataProvider().refreshAll();
            });
            return new Div(acceptInvitationButton, removeInvitationButton);
        });
        return getLayout(grid, invitationFilterDataProvider);
    }

    private Component getFriendsDataGrid() {

        Grid<UserEntity> grid = getUserEntityGrid();
        grid.addComponentColumn(userEntity -> {
            Button createNewChatButton = new Button(VaadinIcon.PLUS_CIRCLE.create());
            createNewChatButton.addClickListener(event -> {
                conversationService.createNewConversation(userId, userEntity);
                tabSheet.setSelectedTab(chats);
                conversationsGrid.getDataProvider().refreshAll();
            });

            Button removeFriendButton = new Button(new Icon("lumo", "cross"));
            removeFriendButton.addClickListener(event -> {

                ConfirmDialog confirmation = new ConfirmDialog();
                confirmation.setHeader("Delete " + userEntity.getUsername() + " from your friend list?");
                confirmation.setText(
                        "Are you sure you want to remove this user from your friend list?");

                confirmation.setCancelable(true);

                confirmation.setConfirmText("Delete");
                confirmation.setConfirmButtonTheme("error primary");
                confirmation.addConfirmListener(e -> {
                    userService.removeFriend(userId, userEntity);
                    grid.getDataProvider().refreshAll();
                });
                confirmation.open();
            });
            return new Div(createNewChatButton, removeFriendButton);
        });
        return getLayout(grid, friendFilterDataProvider);
    }

    private Component getChatsDataGrid() {

        conversationsGrid = getConversationEntityGrid();

        conversationsGrid.addComponentColumn(conversation -> {
            Button removeChatButton = new Button(VaadinIcon.TRASH.create());
            removeChatButton.addClickListener(event -> {

                ConfirmDialog confirmation = new ConfirmDialog();
                confirmation.setHeader("Delete " + conversation.getName() + " conversation?");
                confirmation.setText(
                        "Are you sure you want to remove this conversation?");

                confirmation.setCancelable(true);

                confirmation.setConfirmText("Delete");
                confirmation.setConfirmButtonTheme("error primary");
                confirmation.addConfirmListener(e -> {
                    conversationService.removeConversation(conversation);
                    conversationsGrid.getDataProvider().refreshAll();
                });
                confirmation.open();
            });
            return new Div(removeChatButton);
        });

        conversationsGrid.addItemClickListener(conversation -> {
            log.debug("Chat has been changed.");
            String currentConversationId = conversation.getItem()
                                                        .getId();
            ComponentUtil.getData(UI.getCurrent(), ChatView.class)
                        .changeChat(currentConversationId);
            conversationService.markConversationAsViewedBy(currentConversationId, userId);
            conversationDataProvider.refreshAll();
            conversationsGrid.select(conversation.getItem());
        });

        return getLayoutForChats(conversationsGrid, conversationFilterDataProvider);
    }

    private VerticalLayout getLayout(Grid grid, ConfigurableFilterDataProvider<UserEntity, Void, UserFilter> filterDataProvider) {
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

    private VerticalLayout getLayoutForChats(Grid grid,
                                     ConfigurableFilterDataProvider<ConversationEntity, Void, ConversationFilter> filterDataProvider) {
        grid.setItems(filterDataProvider);

        TextField searchField = new TextField();
        searchField.setWidth("50%");
        searchField.setPlaceholder("Search");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(e -> {
            chatsFilter.setSearchTerm(e.getValue());
            filterDataProvider.setFilter(chatsFilter);
        });

        VerticalLayout layout = new VerticalLayout(searchField, grid);
        layout.setPadding(false);
        return layout;
    }

    private Grid<ConversationEntity> getConversationEntityGrid() {
        Grid<ConversationEntity> grid = new Grid<>();
        grid.addComponentColumn(getConversationMiniature());

        grid.addColumn(ConversationEntity::getName, "name");

        grid.setPartNameGenerator(conversation -> {
            if (isConversationNotViewed(conversation)) {
                return "has-new-message";
            } else {
                return null;
            }
        });
        return grid;
    }

    private boolean isConversationNotViewed(ConversationEntity conversation) {
        return conversation.getMembersWhoNotViewed()
                            .stream()
                            .anyMatch(member -> userId.equals(member.getId()));
    }

    private ValueProvider<ConversationEntity, AvatarGroup> getConversationMiniature() {
        return conversationEntity -> {
            AvatarGroup avatars = new AvatarGroup();
            avatars.setMaxItemsVisible(3);

            for (UserEntity person : conversationEntity.getMembers()) {
                if (idsNotEqual(person.getId(), userId)) {
                    AvatarGroup.AvatarGroupItem avatar = new AvatarGroup.AvatarGroupItem(
                            person.getUsername());
                    avatar.setImageResource(getAvatarResource(person.getAvatarName()));
                    avatars.add(avatar);
                }
            }
            return avatars;
        };
    }

    private boolean idsNotEqual(String personId, String userId) {
        return !Objects.equals(personId, userId);
    }

    private Grid<UserEntity> getUserEntityGrid() {
        Grid<UserEntity> grid = new Grid<>();
        grid.addComponentColumn(userEntity -> {
            Image avatar = new Image(getAvatarResource(userEntity.getAvatarName()), "avatar");
            avatar.setWidth(50, Unit.PERCENTAGE);
            return avatar;
        });
        grid.addColumn(UserEntity::getUsername, "username");
        return grid;
    }

    private Audio getMessageSoundComponent() {
        InputStreamFactory resource =
                () -> getClass().getResourceAsStream("/sounds/incoming_message.mp3");
        Audio messageSound = new Audio(new StreamResource("message_sound", resource));
        messageSound.setSizeUndefined();
        messageSound.getStyle().setDisplay(Style.Display.NONE);
        add(messageSound);
        return messageSound;
    }
}