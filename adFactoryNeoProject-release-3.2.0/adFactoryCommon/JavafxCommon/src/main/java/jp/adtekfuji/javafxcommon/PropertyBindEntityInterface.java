/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon;

import java.util.List;
import java.util.function.Consumer;

import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

/**
 *
 * @param <T>
 * @author ke.yokoi
 */
public interface PropertyBindEntityInterface<T> {

    public enum TYPE {
        INTEGER,
        STRING,
        REGEX_STRING,
        BOOLEAN,
        TIMESTAMP,
        COMBO,
        PASSWORD,
        TEXTAREA,
        COLORPICKER,
        BUTTON,
        TIMEPERIODS,
        TIMEHMSTAMP,
    }

    TYPE getType();
    StringProperty getLabel();
    Object getProperty();
    PropertyBindEntityInterface setProperty(Object property);
    List getSelector();
    ListCell getButtonCellFactory();
    Callback<ListView, ListCell> getComboCellFactory();
    String getText();
    String getRegex();
    ChangeListener<T> getActionListner();
    EventHandler<ActionEvent> getEventAction();
    Object getUserData();
    boolean getDisable();
    List<Object> getProperties();
    PropertyBindEntityInterface setPrefWidth(double width);
    double getPrefWidth();
    PropertyBindEntityInterface setMaxLength(int maxLength);
    int getMaxLength();
    Consumer<Node> getNodeConsumer();
}
