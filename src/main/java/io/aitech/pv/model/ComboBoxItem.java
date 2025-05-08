package io.aitech.pv.model;


public class ComboBoxItem {

    private final Long id;
    private final String name;


    public ComboBoxItem(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

}
