package com.project.casualtalkchat.chat_page;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.dom.Style;

import java.util.List;

public class MessageList extends Scroller {

    public final Div div = new Div();
    private DataProvider dataProvider;
    private int pageNumber = 0;

    public MessageList(DataProvider dataProvider) {
        this.dataProvider = dataProvider;

        this.getStyle()
                .setTransform("rotate(180deg)")
                .set("direction", "rtl");
        div.getStyle()
                .setTransform("rotate(180deg)")
                .setTop("0")
                .setPosition(Style.Position.RELATIVE)
                .set("direction", "ltr");
        setContent(div);

        getElement().executeJs("""
            var self = this;
            this.addEventListener("scroll", function(e) {
                if(self.scrollTop + self.clientHeight >= self.scrollHeight - 1) {
                    self.$server.loadMoreRows();
                }
            });
                """);

        getElement().executeJs("""
                this.addEventListener('wheel', function(e){
                    wheel(e, this)
                })

                function wheel(event, elm) {
                    var delta = event.deltaY*-1;
                    handle(delta, elm);
                    if (event.preventDefault) event.preventDefault();
                    event.returnValue = false;
                }

                function handle(delta, elem) {
                    const time = 10;
                    const distance = delta;
                    elem.scrollBy({
                        top: distance,
                        left: 0,
                        behavior: "instant"
                    });
                }
                """);

        loadMoreRows();
    }

    public void setDataProvider(DataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    public DataProvider getDataProvider() {
        return this.dataProvider;
    }

    public void setItems(List<MessageListItem> items) {
        this.div.removeAll();
        items.forEach(div::add);
    }

    public void addItems(List<MessageListItem> items) {
        items.forEach(div::add);
    }

    @ClientCallable
    public void loadMoreRows() {
        List<Component> messages =  dataProvider.fetchPage(pageNumber ++);
        messages.forEach(div::addComponentAsFirst);
    }

    public void reload() {
        this.pageNumber = 0;
        this.div.removeAll();
        loadMoreRows();
    }
}
