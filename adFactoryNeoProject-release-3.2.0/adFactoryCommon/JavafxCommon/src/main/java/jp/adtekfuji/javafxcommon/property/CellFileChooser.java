/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.property;

import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.utility.StringUtils;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

/**
 * ファイル選択セル
 *
 * @author s-heya
 */
public class CellFileChooser extends AbstractCell {
    private static File defaultDir = new File(System.getProperty("user.home"), "Desktop");
    private final HBox hbox = new HBox();
    private final Button button = new Button();
    private final Button deleteButton = new Button("x");
    private boolean isFileNameOnly = false;
    private List<FileChooser.ExtensionFilter> filter = Arrays.asList(new FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.mp3"), new FileChooser.ExtensionFilter("All Files", "*.*"));

    /**
     * ファイル選択
     */
    private final EventHandler onSelected = (EventHandler) (Event event) -> {
        try {
            StringProperty filePath = (StringProperty) button.getUserData();

            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(defaultDir);
            fileChooser.setTitle(LocaleUtils.getString("key.FileChoice"));
            fileChooser.getExtensionFilters().addAll(filter);

            File file = fileChooser.showOpenDialog(SceneContiner.getInstance().getWindow());
            if (Objects.nonNull(file)) {

                filePath.set(file.getPath());
                if (!StringUtils.isEmpty(filePath.get())) {
                    if (this.isFileNameOnly) {
                        this.button.setText(CellFileChooser.getFileName(filePath.get()));
                    } else {
                        this.button.setText(filePath.get());
                    }
                } else {
                    this.button.setText(LocaleUtils.getString("key.Unselected"));
                }

                defaultDir = file.getParentFile();
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
    };

    /**
     * コンストラクター
     *
     * @param record
     * @param filePath
     */
    public CellFileChooser(Record record, StringProperty filePath) {
        super(record);

        if (!StringUtils.isEmpty(filePath.get())) {
            if (this.isFileNameOnly) {
                this.button.setText(CellFileChooser.getFileName(filePath.get()));
            } else {
                this.button.setText(filePath.get());
            }
        } else {
            this.button.setText(LocaleUtils.getString("key.Unselected"));
        }
        this.button.setOnAction(onSelected);
        this.button.setUserData(filePath);
        this.button.setMinWidth(80.0);
        this.button.setAlignment(Pos.BASELINE_LEFT);
        this.button.getStyleClass().add("ContentTextBox");

        this.deleteButton.getStyleClass().add("DeleteButton");
        this.deleteButton.setOnAction((ActionEvent event) -> {
            filePath.set("");
            this.button.setText(LocaleUtils.getString("key.Unselected"));
        });

        this.hbox.setAlignment(Pos.CENTER_LEFT);
        this.hbox.setSpacing(2.0);
        this.hbox.getChildren().addAll(this.button, this.deleteButton);
    }

    /**
     * コンストラクター(スタイル指定)
     *
     * @param record
     * @param filePath
     * @param buttonClass
     * @param deleteButtonClass
     * @param hBoxClass
     */
    public CellFileChooser(Record record, StringProperty filePath, String buttonClass, String deleteButtonClass, String hBoxClass) {
        super(record);

        if (!StringUtils.isEmpty(filePath.get())) {
            if (this.isFileNameOnly) {
                this.button.setText(CellFileChooser.getFileName(filePath.get()));
            } else {
                this.button.setText(filePath.get());
            }
        } else {
            this.button.setText(LocaleUtils.getString("key.Unselected"));
        }
        this.button.setOnAction(onSelected);
        this.button.setUserData(filePath);
        this.button.setAlignment(Pos.BASELINE_LEFT);
        this.button.getStyleClass().add(buttonClass);

        this.deleteButton.getStyleClass().add(deleteButtonClass);
        this.deleteButton.setOnAction((ActionEvent event) -> {
            filePath.set("");
            this.button.setText(LocaleUtils.getString("key.Unselected"));
        });

        this.hbox.setAlignment(Pos.CENTER_LEFT);
        this.hbox.setSpacing(2.0);
        this.button.getStyleClass().add(hBoxClass);
        this.hbox.getChildren().addAll(this.button, this.deleteButton);
    }

    /**
     * コンストラクター(スタイル・ファイルフィルター指定)
     *
     * @param record
     * @param filePath
     * @param buttonClass
     * @param deleteButtonClass
     * @param hBoxClass
     * @param filter
     * @param isFileNameOnly
     */
    public CellFileChooser(Record record, StringProperty filePath, String buttonClass, String deleteButtonClass, String hBoxClass, List<FileChooser.ExtensionFilter> filter, boolean isFileNameOnly) {
        super(record);

        this.isFileNameOnly = isFileNameOnly;
        if (!StringUtils.isEmpty(filePath.get())) {
            if (this.isFileNameOnly) {
                this.button.setText(CellFileChooser.getFileName(filePath.get()));
            } else {
                this.button.setText(filePath.get());
            }
        } else {
            this.button.setText(LocaleUtils.getString("key.Unselected"));
        }

        this.filter = filter;
        this.button.setOnAction(onSelected);
        this.button.setUserData(filePath);
        this.button.setAlignment(Pos.BASELINE_LEFT);
        this.button.getStyleClass().add(buttonClass);

        this.deleteButton.getStyleClass().add(deleteButtonClass);
        this.deleteButton.setOnAction((ActionEvent event) -> {
            filePath.set("");
            this.button.setText(LocaleUtils.getString("key.Unselected"));
        });

        this.hbox.setAlignment(Pos.CENTER_LEFT);
        this.hbox.setSpacing(2.0);
        this.button.getStyleClass().add(hBoxClass);
        this.hbox.getChildren().addAll(this.button, this.deleteButton);
    }

    /**
     * ノードを生成する。
     */
    @Override
    public void createNode() {
        super.setNode(this.hbox);
    }

    /**
     * ファイルパスからファイル名を取得する
     *
     * @param filePath ファイルパス
     * @return ファイル名
     */
    private static String getFileName(String filePath){
        String fileName = filePath;
        try {
            fileName = new File(filePath).getName();
            if (fileName.indexOf(".") > 0) {
               fileName = fileName.substring(0, fileName.lastIndexOf("."));
            }
        } catch(Exception ex) {
            System.out.println(ex);
        }
        return fileName;
    }
}
