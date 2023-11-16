package com.project.casualtalkchat.chat_page;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.avatar.AvatarGroup;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
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

    private final UserFilter userFilter = new UserFilter();
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
        Dialog addNewFriendDialog = getAddNewFriendDialog();
        addNewFriend.addClickListener(event -> addNewFriendDialog.open());
        tabSheet.add(friends, getContent(getFriendsDataGrid(), addNewFriend));

        Tab invitations = new Tab(VaadinIcon.ENVELOPES.create(), new Span("Invitations"));
        Button addNewFriendButtonInInvitations = new Button("Add new friend");
        addNewFriendButtonInInvitations.addClickListener(event -> addNewFriendDialog.open());
        tabSheet.add(invitations, getContent(getInvitationsDataGrid(), addNewFriendButtonInInvitations));

        tabSheet.setHeight(100, Unit.PERCENTAGE);

        messageSoundComponent = getMessageSoundComponent();

        add(addNewFriendDialog, tabSheet, messageSoundComponent);
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
        button.getStyle()
                .setPosition(Style.Position.ABSOLUTE)
                .setBottom("0");
        Div content = new Div(tabContent, button);
        content.getStyle()
                .setPosition(Style.Position.RELATIVE)
                .setHeight("100%");
        return content;
    }

    private Dialog getAddNewFriendDialog() {
        Dialog dialog = new ManagementDialog();

        dialog.setHeaderTitle("Add new friend");

        VerticalLayout dialogLayout = getUserDataGrid();
        dialog.add(dialogLayout);

        return dialog;
    }

    private VerticalLayout getUserDataGrid() {

        Grid<UserEntity> grid = new UserGrid();
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
        return new GridFilterableWrapper(grid, userFilterDataProvider, userFilter);
    }

    private VerticalLayout getFriendsToAddDataGrid(String conversationId) {

        Grid<UserEntity> grid = new UserGrid();
        grid.addColumn(UserEntity::getId, "userId");
        grid.addComponentColumn(userEntity -> {
            Button addButton = new Button("Add");
            addButton.setDisableOnClick(true);
            addButton.addClickListener(event -> {
                conversationService.addUserToConversation(conversationId, userEntity.getId());
                addButton.setText("Added");
                conversationDataProvider.refreshAll();
            });
            return  addButton;
        });
        FriendNotParticipatingInChatDataProvider friendNotParticipatingDataProvider =
                new FriendNotParticipatingInChatDataProvider(userService, userId, conversationId);
        ConfigurableFilterDataProvider<UserEntity, Void, UserFilter> friendNotParticipatingFilterDataProvider =
                friendNotParticipatingDataProvider.withConfigurableFilter();
        return new GridFilterableWrapper(grid, friendNotParticipatingFilterDataProvider, userFilter);
    }

    private Component getInvitationsDataGrid() {

        Grid<UserEntity> grid = new UserGrid();
        grid.addComponentColumn(userEntity -> {
            Button acceptInvitationButton = new Button(VaadinIcon.CHECK_CIRCLE.create());
            acceptInvitationButton.addClickListener(event -> {
                userService.acceptInvitation(userId, userEntity);
                grid.getDataProvider().refreshAll();
                friendFilterDataProvider.refreshAll();
            });

            Button removeInvitationButton = new Button(new Icon("lumo", "cross"));
            removeInvitationButton.addClickListener(event -> {
                userService.removeInvitation(userId, userEntity);
                grid.getDataProvider().refreshAll();
            });
            return new Div(acceptInvitationButton, removeInvitationButton);
        });
        VerticalLayout verticalLayout = new GridFilterableWrapper(grid, invitationFilterDataProvider, userFilter);
        verticalLayout.setHeight(85, Unit.PERCENTAGE);
        return verticalLayout;
    }

    private Component getFriendsDataGrid() {

        Grid<UserEntity> grid = new UserGrid();
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
        VerticalLayout verticalLayout = new GridFilterableWrapper(grid, friendFilterDataProvider, userFilter);
        verticalLayout.setHeight(85, Unit.PERCENTAGE);
        return verticalLayout;
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

            Button addMoreFriendsToChatButton = new Button(VaadinIcon.PLUS_CIRCLE.create());
            addMoreFriendsToChatButton.addClickListener(clickEvent -> {
                Dialog dialog = new ManagementDialog();

                dialog.setHeaderTitle(conversation.getName() + " chat management");

                Tab admins = new Tab(VaadinIcon.USER_STAR.create(), new Span("Admins"));
                Tab members = new Tab(VaadinIcon.USERS.create(), new Span("Members"));
                Tab friends = new Tab(VaadinIcon.PLUS.create(), new Span("Add friend"));

                TabSheet chatManagementTabSheet = new TabSheet();

                chatManagementTabSheet.add(admins, getAdminsTabContent(conversation.getId()));
                chatManagementTabSheet.add(members, getMembersTabContent(conversation.getId()));
                chatManagementTabSheet.add(friends, getFriendsToAddDataGrid(conversation.getId()));

                dialog.add(chatManagementTabSheet);
                dialog.open();
            });

            return new Div(addMoreFriendsToChatButton, removeChatButton);
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

        VerticalLayout layout = new GridFilterableWrapper(conversationsGrid, conversationFilterDataProvider, chatsFilter);
        layout.setHeight(85, Unit.PERCENTAGE);
        return layout;
    }

    private Component getAdminsTabContent(String conversationId) {
        Grid<UserEntity> grid = new UserGrid();
        grid.addColumn(UserEntity::getId, "userId");
        AdminsInChatDataProvider friendNotParticipatingDataProvider =
                new AdminsInChatDataProvider(userService, conversationId);
        ConfigurableFilterDataProvider<UserEntity, Void, UserFilter> adminsFilterDataProvider =
                friendNotParticipatingDataProvider.withConfigurableFilter();
        return new GridFilterableWrapper(grid, adminsFilterDataProvider, userFilter);
    }

    private Component getMembersTabContent(String conversationId) {
        Grid<UserEntity> grid = new UserGrid();
        grid.addColumn(UserEntity::getId, "userId");

        if (conversationService.isConversationAdmin(conversationId, userId)) {
            grid.addComponentColumn(member -> {
                Button removeFromChatButton = new Button(VaadinIcon.EXIT.create());
                removeFromChatButton.addClickListener(event -> {
                    conversationService.removeFromConversation(conversationId, member.getId());
                    grid.getDataProvider()
                        .refreshAll();
                    conversationDataProvider.refreshAll();
                });
                return removeFromChatButton;
            });
        }

        MembersInChatDataProvider friendNotParticipatingDataProvider =
                new MembersInChatDataProvider(userService, conversationId);
        ConfigurableFilterDataProvider<UserEntity, Void, UserFilter> adminsFilterDataProvider =
                friendNotParticipatingDataProvider.withConfigurableFilter();
        return new GridFilterableWrapper(grid, adminsFilterDataProvider, userFilter);
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