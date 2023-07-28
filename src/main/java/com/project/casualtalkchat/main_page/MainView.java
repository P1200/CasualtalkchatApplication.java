package com.project.casualtalkchat.main_page;

import com.project.casualtalkchat.common.NavigationBar;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;

@Route("2")
@JavaScript("https://code.jquery.com/jquery-3.5.1.slim.min.js")
@JavaScript("https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js")
@StyleSheet("https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css")
@JavaScript("https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js")
class MainView extends VerticalLayout {
    MainView() {
        this.setPadding(false);
        NavigationBar navigationBar = new NavigationBar();
        navigationBar.setClassName("navbar");
        navigationBar.setWidth(100, Unit.PERCENTAGE);
        navigationBar.setHeight(5, Unit.PERCENTAGE);

        Html video = new Html("""
                <video style="width:100%; height: auto; position: relative;" playsinline autoplay muted loop>
                    <source src="/videos/hd1848.mp4" type="video/mp4" />
                </video>""");

        Div textOnVideoInVaadin = new Div();


        Html textOnVideo = new Html("<div style=\"\n" +
                "position: absolute;  top: 16vw; left: 50%; width: 75%; transform: translate(-50%, 0%);\">\n" +
                "            <div class=\"container d-flex align-items-center justify-content-center text-center h-100 text-white\">\n" +
                "                <div>\n" +
                "                    <h1 class=\"mb-3 text-white\" style=\"font-size: 9vw;\">Chat with every!</h1>\n" +
                "                    <h5 class=\"mb-4\">Załóż konto lub zaloguj się</h5>\n" +
                "                    <a\n" +
                "                            class=\"btn btn-outline-light m-2\"\n" +
                "                            href=\"login.jsp\"\n" +
                "                            role=\"button\"\n" +
                "                            rel=\"nofollow\"\n" +
                "                    >Logowanie</a\n" +
                "                    >\n" +
                "                    <a\n" +
                "                            class=\"btn btn-outline-light m-2\"\n" +
                "                            href=\"register.jsp\"\n" +
                "                            role=\"button\"\n" +
                "                    >Rejestracja</a\n" +
                "                    >\n" +
                "                </div>\n" +
                "            </div>\n" +
                "        </div>");

        Footer footer = new Footer(new Paragraph("Copyrights"));
        footer.setHeight(5, Unit.PERCENTAGE);

        add(navigationBar, video, textOnVideo, footer);
    }
}
