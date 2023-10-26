package com.project.casualtalkchat.chat_page;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("chat-attachment-repository")
public interface AttachmentRepository extends JpaRepository<AttachmentEntity, String> {

}
