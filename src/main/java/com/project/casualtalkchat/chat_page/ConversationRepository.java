package com.project.casualtalkchat.chat_page;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("chat-conversation-repository")
public interface ConversationRepository extends JpaRepository<ConversationEntity, String> {

    @Query("""
    select sortedConversations.conv from (
        select c AS conv, max(m.sentTime) AS lastMessage from ChatMessageEntity m left join
        m.conversation c
        left join c.admins a
        left join c.members mbr
        where a.id = :adminId OR mbr.id = :memberId GROUP BY m.conversation ORDER BY lastMessage DESC
    ) AS sortedConversations""")
    List<ConversationEntity> getAllByAdminsIdOrMembersIdSortedByLastMessageSentTime(String adminId, String memberId);
}
