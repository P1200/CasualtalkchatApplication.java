package com.project.casualtalkchat.chat_page;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("chat-conversation-repository")
public interface ConversationRepository extends JpaRepository<ConversationEntity, String> {

    @Query("""
    SELECT sortedConversations.conv FROM (
        SELECT c AS conv, max(m.sentTime) AS lastMessage FROM ChatMessageEntity m LEFT JOIN
        m.conversation c
        LEFT JOIN c.admins a
        LEFT JOIN c.members mbr
        WHERE a.id = :userId OR mbr.id = :userId GROUP BY m.conversation ORDER BY lastMessage DESC
    ) AS sortedConversations""")
    List<ConversationEntity> getAllByAdminsIdOrMembersIdSortedByLastMessageSentTime(String userId);

    @Query("""
        SELECT CASE WHEN(COUNT(c) > 0) THEN TRUE ELSE FALSE END FROM ChatConversationEntity c
        LEFT JOIN c.admins a WHERE c.id = :conversationId AND a.id = :userId
    """)
    boolean isConversationAdmin(String conversationId, String userId);
}
