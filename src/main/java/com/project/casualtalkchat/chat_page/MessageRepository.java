package com.project.casualtalkchat.chat_page;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("chat-message-repository")
public interface MessageRepository extends JpaRepository<MessageEntity, String> {

    Page<MessageEntity> getAllByConversationIdOrderBySentTimeDesc(Pageable pageable, String conversationId);

    void deleteAllByConversationId(String id);

    List<MessageEntity> getAllByConversationIdAndContentLikeOrderBySentTimeDesc(String conversationId, String pattern);

    @Query(value = """
            select message_possition from (
                select *, row_number() over() as message_possition from message
                where conversation_id = :conversation_id
                order by sent_time desc
                ) as numbered_messages
            where id = :id""", nativeQuery = true)
    int getMessagePositionInConversationById(@Param("conversation_id") String conversationId, @Param("id") String messageId);
}
