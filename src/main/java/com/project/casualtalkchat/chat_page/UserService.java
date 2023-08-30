package com.project.casualtalkchat.chat_page;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void addUserAsInvited(String id, UserEntity currentUser) {
        UserEntity inviter = userRepository.getReferenceById(id);

        inviter.getInvitations()
                .add(currentUser);
        userRepository.save(inviter);
    }

    @Transactional
    public void acceptInvitation(String id, UserEntity inviter) {
        UserEntity user = userRepository.getReferenceById(id);

        addUserAsFriend(user, inviter);
        removeInvitation(user, inviter);
    }

    @Transactional
    public void removeInvitation(String id, UserEntity inviter) {
        UserEntity user = userRepository.getReferenceById(id);

        removeInvitation(user, inviter);
    }

    public List<UserEntity> getAllNonFriendUsers(String userId) {
        return userRepository.findAllNonFriendUsers(userId);
    }

    public List<UserEntity> getInvitationsToUser(String userId) {
        return userRepository.findAllByInvitationsId(userId);
    }

    public List<UserEntity> getAllFriends(String userId) {
        return userRepository.findAllFriends(userId);
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
