/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import javafx.scene.Node;

/**
 *
 * @author e.mori
 * @param <T>
 */
public class PropertyBindEntity<T> implements PropertyBindEntityInterface {

    private final StringProperty label = new SimpleStringProperty();
    private final TYPE type;
    private Object property;
    private List selector;
    private ListCell buttonCellFactory;
    private Callback<ListView, ListCell> comboCellFactory;
    private String text;
    private String regex;
    private ChangeListener<T> actionListner = null;
    private EventHandler<ActionEvent> eventAction = null;
    private Object userData = null;
    private boolean disable = false;
    private Consumer<Node> nodeConsumer = null;

    private List<Object> properties;
    private double prefWidth;
    private Integer maxLength;

    public static PropertyBindEntity createString(String label, String property) {
        if (Objects.isNull(property)) {
            property = "";
        }
        return new PropertyBindEntity(label, TYPE.STRING, null, null, null, property, null, null);
    }

    public static PropertyBindEntity createRegerxString(String label, String property, String regex) {
        return new PropertyBindEntity(label, TYPE.REGEX_STRING, null, null, null, property, null, regex);
    }

    public static PropertyBindEntity createTimeStamp(String label, String property) {
        return new PropertyBindEntity(label, TYPE.TIMESTAMP, null, null, null, property, null, null);
    }

    public static PropertyBindEntity createTimeHMStamp(String label, String property) {
        return new PropertyBindEntity(label, TYPE.TIMEHMSTAMP, null, null, null, property, null, null);
    }

    public static PropertyBindEntity createInteger(String label, Integer property) {
        return new PropertyBindEntity(label, TYPE.INTEGER, null, null, null, property, null, null);
    }

    public static PropertyBindEntity createBoolean(String label, String text, Boolean property) {
        return new PropertyBindEntity(label, TYPE.BOOLEAN, null, null, null, property, text, null);
    }

    public static <T> PropertyBindEntity createCombo(String label, List selector, ListCell buttonCellFactory, Callback<ListView<T>, ListCell<T>> comboCellFactory, Object property) {
        return new PropertyBindEntity(label, TYPE.COMBO, selector, buttonCellFactory, comboCellFactory, property, null, null);
    }

    public static PropertyBindEntity createPassword(String label, String property) {
        return new PropertyBindEntity(label, TYPE.PASSWORD, null, null, null, property, null, null);
    }

    public static PropertyBindEntity createTextArea(String label, String property) {
        return new PropertyBindEntity(label, TYPE.TEXTAREA, null, null, null, property, null, null);
    }

    public static PropertyBindEntity createColorPicker(String label, Color property) {
        return new PropertyBindEntity(label, TYPE.COLORPICKER, null, null, null, property, null, null);
    }

    public static PropertyBindEntity createButton(String label, StringProperty property, EventHandler<ActionEvent> action, Object userData) {
        PropertyBindEntity propBind = new PropertyBindEntity(label, TYPE.BUTTON, null, null, null, property, null, null);
        propBind.eventAction = action;
        propBind.userData = userData;
        return propBind;
    }
    
    /**
     * ボタン作成
     *
     * @param label ラベル
     * @param property ボタンに表示する文字列
     * @param action ボタンクリック時のアクション
     * @param userData データ
     * @return PropertyBindEntity
     */
    public static PropertyBindEntity createButton(String label, String property, EventHandler<ActionEvent> action, Object userData) {
        PropertyBindEntity propBind = new PropertyBindEntity(label, TYPE.BUTTON, null, null, null, property, null, null);
        propBind.eventAction = action;
        propBind.userData = userData;
        return propBind;
    }

    /**
     * 時間帯入力
     *
     * @param label
     * @param from
     * @param to
     * @return
     */
    public static PropertyBindEntity createTimePeriods(String label, String from, String to) {
        return new PropertyBindEntity(label, TYPE.TIMEPERIODS, new SimpleStringProperty(from), new SimpleStringProperty(to));
    }

    private PropertyBindEntity(String label, TYPE type, List selector, ListCell buttonCellFactory, Callback<ListView, ListCell> comboCellFactory, Object property, String text, String regex) {
        this.label.set(label);
        this.type = type;
        this.selector = selector;
        this.buttonCellFactory = buttonCellFactory;
        this.comboCellFactory = comboCellFactory;
        if (this.type == TYPE.STRING || this.type == TYPE.REGEX_STRING
                || this.type == TYPE.TIMESTAMP || this.type == TYPE.TIMEHMSTAMP || this.type == TYPE.TEXTAREA
                || this.type == TYPE.PASSWORD || this.type == TYPE.BUTTON) {
            this.property = new SimpleStringProperty((String) property);
        } else if (this.type == TYPE.INTEGER) {
            this.property = new SimpleIntegerProperty((Integer) property);
        } else if (this.type == TYPE.BOOLEAN) {
            this.property = new SimpleBooleanProperty((Boolean) property);
        } else if (this.type == TYPE.COMBO || this.type == TYPE.COLORPICKER) {
            this.property = new SimpleObjectProperty(property);
        } else {
            this.property = null;
        }
        this.text = text;
        this.regex = regex;
    }

    private PropertyBindEntity(String label, TYPE type, Object... args) {
        this.label.set(label);
        this.type = type;
        this.properties = new ArrayList<>(Arrays.asList(args));
    }

    public PropertyBindEntity actionListner(ChangeListener<T> actionListner) {
        this.actionListner = actionListner;
        return this;
    }

    public PropertyBindEntity isDisable(Boolean isDisable) {
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
        return this.properties;
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
