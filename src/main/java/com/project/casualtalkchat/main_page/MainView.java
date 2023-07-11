package com.project.casualtalkchat.main_page;

import com.project.casualtalkchat.common.NavigationBar;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("")
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
        Html video = new Html("<video style=\"width: 100%; height: 95%;\" playsinline autoplay muted loop><source src=\"https://mdbootstrap.com/img/video/Lines.mp4\" type=\"video/mp4\"></video>");
        add(navigationBar, video);
    }
}
