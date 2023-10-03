package com.project.casualtalkchat.chat_page;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("chat-conversation-repository")
public interface ConversationRepository extends JpaRepository<ConversationEntity, String> {

    List<ConversationEntity> getAllByAdminsIdOrMembersId(String id, String id2);
}
