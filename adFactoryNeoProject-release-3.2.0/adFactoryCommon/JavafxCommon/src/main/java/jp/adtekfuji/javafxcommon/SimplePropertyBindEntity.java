/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import javafx.util.converter.LocalTimeStringConverter;
import javafx.scene.Node;

/**
 *
 * @author e.mori
 * @param <T>
 */
public class SimplePropertyBindEntity<T> implements PropertyBindEntityInterface {

    private final StringProperty label = new SimpleStringProperty();
    private final TYPE type;
    private Object property;
    private List selector = null;
    private ListCell buttonCellFactory = null;
    private Callback<ListView, ListCell> comboCellFactory = null;
    private String text = null;
    private String regex = null;
    private ChangeListener<T> actionListner = null;
    private EventHandler<ActionEvent> eventAction = null;
    private Object userData = null;
    private Boolean disable = false;
    private double prefWidth;
    private Integer maxLength;
    Consumer<Node> nodeConsumer = null;

    public static SimplePropertyBindEntity createString(String label, StringProperty property) {
        return new SimplePropertyBindEntity(label, TYPE.STRING).property(property);
    }

    public static SimplePropertyBindEntity createRegerxString(String label, StringProperty property, String regex) {
        return new SimplePropertyBindEntity(label, TYPE.REGEX_STRING).property(property).regex(regex);
    }

    public static SimplePropertyBindEntity createLocalTime(String label, ObjectProperty<LocalTime> property) {
        SimpleStringProperty p = new SimpleStringProperty();
        p.bindBidirectional(property, new LocalTimeStringConverter(DateTimeFormatter.ISO_LOCAL_TIME, DateTimeFormatter.ISO_LOCAL_TIME));
        return new SimplePropertyBindEntity(label, TYPE.TIMESTAMP).property(p);
    }

    public static SimplePropertyBindEntity createInteger(String label, IntegerProperty property) {
        return new SimplePropertyBindEntity(label, TYPE.INTEGER).property(property);
    }

    public static SimplePropertyBindEntity createBoolean(String label, String text, BooleanProperty property) {
        return new SimplePropertyBindEntity(label, TYPE.BOOLEAN).property(property).text(text);
    }

    public static <T> SimplePropertyBindEntity createCombo(String label, List selector, ListCell buttonCellFactory, Callback<ListView<T>, ListCell<T>> comboCellFactory, ObjectProperty<T> property) {
        return new SimplePropertyBindEntity(label, TYPE.COMBO).property(property).selector(selector).buttonCellFactory(buttonCellFactory).comboCellFactory(comboCellFactory);
    }

    public static SimplePropertyBindEntity createPassword(String label, StringProperty property) {
        return new SimplePropertyBindEntity(label, TYPE.PASSWORD).property(property);
    }

    public static SimplePropertyBindEntity createTextArea(String label, StringProperty property) {
        return new SimplePropertyBindEntity(label, TYPE.TEXTAREA).property(property);
    }

    public static SimplePropertyBindEntity createColorPicker(String label, ObjectProperty<Color> property) {
        return new SimplePropertyBindEntity(label, TYPE.COLORPICKER).property(property);
    }

    public static SimplePropertyBindEntity createButton(String label, StringProperty property, EventHandler<ActionEvent> action, Object userData) {
        return new SimplePropertyBindEntity(label, TYPE.BUTTON).property(property).eventAction(action).userData(userData);
    }

    private SimplePropertyBindEntity(String label, TYPE type) {
        this.label.set(label);
        this.type = type;
    }

    public SimplePropertyBindEntity selector(List selector) {
        this.selector = selector;
        return this;
    }

    public SimplePropertyBindEntity buttonCellFactory(ListCell buttonCellFactory) {
        this.buttonCellFactory = buttonCellFactory;
        return this;
    }

    public SimplePropertyBindEntity comboCellFactory(Callback<ListView, ListCell> comboCellFactory) {
        this.comboCellFactory = comboCellFactory;
        return this;
    }

    public SimplePropertyBindEntity property(Object property) {
        this.property = property;
        return this;
    }

    public SimplePropertyBindEntity text(String text) {
        this.text = text;
        return this;
    }

    public SimplePropertyBindEntity regex(String regex) {
        this.regex = regex;
        return this;
    }

    public SimplePropertyBindEntity comboCellFactory(String regex) {
        this.regex = regex;
        return this;
    }

    public SimplePropertyBindEntity actionListner(ChangeListener<T> actionListner) {
        this.actionListner = actionListner;
        return this;
    }

    public SimplePropertyBindEntity eventAction(EventHandler<ActionEvent> eventAction) {
        this.eventAction = eventAction;
        return this;
    }

    public SimplePropertyBindEntity userData(Object userData) {
        this.userData = userData;
        return this;
    }

    public SimplePropertyBindEntity isDisable(Boolean isDisable) {
        this.disable = isDisable;
        return this;
    }

    @Override
    public TYPE getType() {
        return this.type;
    }

    @Override
    public StringProperty getLabel() {
        return this.label;
    }

    @Override
    public Object getProperty() {
        return this.property;
    }

    @Override
    public PropertyBindEntityInterface setProperty(Object property) {
        this.property = property;
        return this;
    }

    @Override
    public List getSelector() {
        return this.selector;
    }

    @Override
    public ListCell getButtonCellFactory() {
        return this.buttonCellFactory;
    }

    @Override
    public Callback<ListView, ListCell> getComboCellFactory() {
        return this.comboCellFactory;
    }

    @Override
    public String getText() {
        return this.text;
    }

    @Override
    public String getRegex() {
        return this.regex;
    }

    @Override
    public ChangeListener<T> getActionListner() {
        return this.actionListner;
    }

    @Override
    public EventHandler<ActionEvent> getEventAction() {
        return this.eventAction;
    }

    @Override
    public Object getUserData() {
        return this.userData;
    }

    @Override
    public boolean getDisable() {
        return this.disable;
    }

    @Override
    public List<Object> getProperties() {
        return null;
    }

    @Override
    public double getPrefWidth() {
        return this.prefWidth;
    }

    @Override
    public PropertyBindEntityInterface setPrefWidth(double prefWidth) {
        this.prefWidth = prefWidth;
        return this;
    }

    @Override
    public PropertyBindEntityInterface setMaxLength(int maxLength) {
        this.maxLength = maxLength;
        return this;
    }

    @Override
    public int getMaxLength() {
        if (Objects.isNull(this.maxLength)) {
            this.maxLength = 1024;
        }
        return this.maxLength;
    }

    public PropertyBindEntityInterface setNodeConsumer(Consumer<Node> nodeConsumer)
    {
        this.nodeConsumer = nodeConsumer;
        return this;
    }

    @Override
    public Consumer<Node> getNodeConsumer()
    {
        return this.nodeConsumer;
    }

}
