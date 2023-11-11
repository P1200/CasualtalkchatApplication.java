package com.project.casualtalkchat.chat_page;

import com.vaadin.flow.shared.Registration;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Broadcaster {
    static Executor executor = Executors.newSingleThreadExecutor();

    static LinkedList<BiConsumer<MessageDTO, String>> listeners = new LinkedList<>();

    public static synchronized Registration register(BiConsumer<MessageDTO, String> listener) {
        listeners.add(listener);

        return () -> {
            synchronized (Broadcaster.class) {
                listeners.remove(listener);
            }
        };
    }

    public static synchronized void broadcast(MessageDTO item, String conversationId) {
        for (BiConsumer<MessageDTO, String> listener : listeners) {
            executor.execute(() -> listener.accept(item, conversationId));
        }
    }
}
