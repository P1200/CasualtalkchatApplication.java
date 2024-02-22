package com.project.casualtalkchat.chat_page;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    void addUserAsInvited(String senderId, UserEntity receiverUserEntity) {
        UserEntity inviter = userRepository.getReferenceById(senderId);
        inviter.getInvitations()
                .add(receiverUserEntity);

        userRepository.save(inviter);
    }

    @Transactional
    void acceptInvitation(String receiverId, UserEntity inviter) {
        UserEntity user = userRepository.getReferenceById(receiverId);

        addUserAsFriend(user, inviter);
        removeInvitation(user, inviter);
    }

    @Transactional
    void removeInvitation(String id, UserEntity inviter) {
        UserEntity user = userRepository.getReferenceById(id);

        removeInvitation(user, inviter);
    }

    List<UserEntity> getAllNonFriendUsers(String userId) {
        return userRepository.findAllNonFriendUsers(userId);
    }

    List<UserEntity> getInvitationsToUser(String userId) {
        return userRepository.findAllByInvitationsId(userId);
    }

    List<UserEntity> getAllFriends(String userId) {
        return userRepository.findAllFriends(userId);
    }

    List<UserEntity> getAllFriendsNotParticipatingInChat(String userId, String conversationId) {
        return userRepository.findAllFriendsNotParticipatingInChat(userId, conversationId);
    }

    @Transactional
    void removeFriend(String userId, UserEntity friendEntity) {

        userRepository.removeFriend(userId, friendEntity.getId());
    }

    List<UserEntity> getAllAdminsInChat(String conversationId) {
        return userRepository.findAllConversationAdmins(conversationId);
    }

    List<UserEntity> getAllMembersInChat(String conversationId) {
        return userRepository.findAllConversationMembers(conversationId);
    }

    private void addUserAsFriend(UserEntity user, UserEntity friend) {

        user.getFriends()
            .add(friend);
        userRepository.save(user);
    }

    private void removeInvitation(UserEntity currentUser, UserEntity inviter) {

        //TODO mbe some optimization?
        UserEntity inviterInSession = userRepository.getReferenceById(inviter.getId());

        inviterInSession.getInvitations()
                .forEach(invitation -> {
                    if (invitation.getId()
                            .equals(currentUser.getId())) {
                        inviterInSession.getInvitations()
                                .remove(invitation);
                    }
                });

        userRepository.save(inviterInSession);
    }
}
