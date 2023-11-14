package com.project.casualtalkchat.chat_page;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;

class GridFilterableWrapper extends VerticalLayout {

    public GridFilterableWrapper(Grid grid, ConfigurableFilterDataProvider filterDataProvider, Filter filter) {
        grid.setItems(filterDataProvider);

        TextField searchField = new TextField();
        searchField.setWidth(100, Unit.PERCENTAGE);
        searchField.setPlaceholder("Search");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(e -> {
            filter.setSearchTerm(e.getValue());
            filterDataProvider.setFilter(filter);
        });

        this.add(searchField, grid);
        this.setPadding(false);
    }
}
