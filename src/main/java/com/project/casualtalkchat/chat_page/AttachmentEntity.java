package com.project.casualtalkchat.chat_page;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity(name = "ChatAttachmentEntity")
@Table(name = "attachment")
public class AttachmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotNull
    private String type;

    @NotNull
    private String pathToContent;

    @NotNull
    private String userFileName;
}
