package com.project.casualtalkchat.chat_page;

import com.project.casualtalkchat.security.CustomUserDetails;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

@Route("chat")
@PermitAll
public class ChatView extends VerticalLayout {

    public ChatView() {
        String username;

        Object principal = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        if (principal instanceof CustomUserDetails userDetails) {
            username = userDetails.getUsername();
        } else if (principal instanceof UserDetails userDetails) {
            username = userDetails.getUsername();
        } else {
            username = principal.toString();
        }

        add(new H1("You are here " + username));
    }
}
