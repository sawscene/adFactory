/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.adfactorysettingtool.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author phamvanthanh
 */
public class PropertyInfo {

    private List<Item> listItem;

    public PropertyInfo() {
        listItem = new ArrayList<>();
    }

    public void add(String key, String value) {
        listItem.add(new Item(key, value));
    }

    public void edit(String key, String value) {
        boolean edited = false;

        //edit item value
        for (Item item : listItem) {
            if (key.toUpperCase().equals(item.key.toUpperCase())) {
                item.setValue(value);
                edited = true;
            }
        }

        //add new when no matching with the input key
        if (!edited) {
            add(key, value);
        }
    }

    public List<Item> getListItem() {
        return listItem;
    }

    public void setListItem(List<Item> listItem) {
        this.listItem = listItem;
    }

    @Override
    public String toString() {
        StringBuilder text = new StringBuilder();

        for (Item item : listItem) {
            text.append(item.key + Constants.EQUAL_SYMBOL + item.value + System.lineSeparator());
        }
        return text.toString();
    }

    public class Item implements Comparable<Item> {

        private String key;
        private String value;

        public Item(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public int compareTo(Item o) {
            if (o.key != null && this.key != null) {
                return this.key.trim().toUpperCase().compareTo(o.key.trim().toUpperCase());
            } else {
                return 0;
            }
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 47 * hash + Objects.hashCode(this.key);
            hash = 47 * hash + Objects.hashCode(this.value);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Item other = (Item) obj;
            if (!Objects.equals(this.key, other.key)) {
                return false;
            }
            if (!Objects.equals(this.value, other.value)) {
                return false;
            }
            return true;
        }

    }
}
