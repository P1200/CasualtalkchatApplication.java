package com.project.casualtalkchat.chat_page;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("chat-user-repository")
public interface UserRepository extends JpaRepository<UserEntity, String> {

    @Query(value = """
            SELECT * FROM `user` WHERE
                id NOT LIKE :user_id AND
                is_account_confirmed = true AND
                id NOT IN (SELECT friends_id FROM `user_friends` WHERE `chat_user_entity_id` = :user_id
                            UNION 
                            SELECT chat_user_entity_id FROM user_friends WHERE friends_id = :user_id)""",
            nativeQuery = true)
    List<UserEntity> findAllNonFriendUsers(@Param("user_id") String userId);

    List<UserEntity> findAllByInvitationsId(String id);

    @Query("""
        SELECT u FROM ChatUserEntity u LEFT JOIN u.friends f WHERE f.id = :userId OR u IN (
            SELECT f FROM ChatUserEntity u LEFT JOIN u.friends f WHERE u.id = :userId
        )
    """)
    List<UserEntity> findAllFriends(String userId);

    @Query("""
        SELECT u FROM ChatUserEntity u LEFT JOIN u.friends f WHERE (f.id = :userId OR u IN (
            SELECT f FROM ChatUserEntity u LEFT JOIN u.friends f WHERE u.id = :userId
        )) AND u NOT IN (
            SELECT c.admins FROM ChatConversationEntity c WHERE c.id = :conversationId
        ) AND u NOT IN (
            SELECT c.members FROM ChatConversationEntity c WHERE c.id = :conversationId
        )
    """)
    List<UserEntity> findAllFriendsNotParticipatingInChat(String userId, String conversationId);

    @Modifying
    @Query(value = """
            DELETE FROM `user_friends` WHERE
            (`chat_user_entity_id`=:user_id AND friends_id=:friend_id) OR
            (`friends_id`=:user_id AND chat_user_entity_id=:friend_id)""", nativeQuery = true)
    void removeFriend(@Param("user_id") String userId, @Param("friend_id") String friendId);

    @Query(value = "SELECT a FROM ChatConversationEntity c LEFT JOIN c.admins a WHERE c.id = :conversationId")
    List<UserEntity> findAllConversationAdmins(String conversationId);

    @Query(value = "SELECT m FROM ChatConversationEntity c LEFT JOIN c.members m WHERE c.id = :conversationId")
    List<UserEntity> findAllConversationMembers(String conversationId);
}
