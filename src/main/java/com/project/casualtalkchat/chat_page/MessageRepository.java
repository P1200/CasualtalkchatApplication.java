package com.project.casualtalkchat.chat_page;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("chat-message-repository")
public interface MessageRepository extends JpaRepository<MessageEntity, String> {

}
