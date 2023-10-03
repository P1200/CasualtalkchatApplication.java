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
                id NOT IN (SELECT friends_id FROM `user_friends` WHERE `chat_user_entity_id` = :user_id)""", nativeQuery = true)
    List<UserEntity> findAllNonFriendUsers(@Param("user_id") String id);

    List<UserEntity> findAllByInvitationsId(String id);

    @Query(value = """
            SELECT * FROM `user` u
            LEFT JOIN user_friends f ON u.id=f.chat_user_entity_id
            WHERE f.friends_id=:user_id OR
             u.id IN (SELECT f.friends_id FROM `user` u
            LEFT JOIN user_friends f ON u.id=f.chat_user_entity_id WHERE u.id=:user_id)""", nativeQuery = true)
    List<UserEntity> findAllFriends(@Param("user_id") String id);

    @Modifying
    @Query(value = """
            DELETE FROM `user_friends` WHERE
            (`chat_user_entity_id`=:user_id AND friends_id=:friend_id) OR
            (`friends_id`=:user_id AND chat_user_entity_id=:friend_id)""", nativeQuery = true)
    void removeFriend(@Param("user_id") String userId, @Param("friend_id") String friendId);
}
