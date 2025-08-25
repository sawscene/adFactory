/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.adfactorysettingtool.scene;

import adtekfuji.adfactorysettingtool.utils.Constants;
import adtekfuji.adfactorysettingtool.utils.PropertyFileUtils;
import adtekfuji.adfactorysettingtool.utils.PropertyInfo;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.SortedSet;
import java.util.TreeSet;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author phamvanthanh
 */
public class EditPropertyFileController implements Initializable {
    private static final Logger logger = LogManager.getLogger();
    
    @FXML
    private TabPane tabPane;
    
    @FXML
    private Button btnSave;
    
    @FXML
    private Button btnReload;
    
    private Tab tab;
    private GridPane gridPane;
    private ScrollPane scrollPane;
    private Label label;
    private TextField textField;
    private CheckBox checkBox;
    private List<Tab> listTabs;
    private HashMap<String, PropertyInfo> listFileContent;
    private HashMap<String, HashMap<Node, Node>> listNodes;
    private String path_config;
  private Stage stage;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        path_config = System.getenv("ADFACTORY_HOME") + File.separator + "conf";

        try {
            listFileContent = PropertyFileUtils.readAllFilesContent(path_config, Constants.SUFFIX_PROPERTIES_FILE);
        } catch (IOException ex) {
            logger.error("[IOException] " + ex.getMessage());
        }
        
        //Load data to UI
        loadDataToUI();
        
        //Handle change tab event
        tabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> ov, Tab t, Tab t1) {                
                confirmSaveChangeDataAction(t);
            }
        });

        //Handle click [Save] button
        btnSave.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int tabIndex = tabPane.getSelectionModel().getSelectedIndex();
                Tab selectedTab = tabPane.getTabs().get(tabIndex);
                saveProperties(selectedTab);
            }
        });

        //Handle click [Reload] button
        btnReload.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int tabIndex = tabPane.getSelectionModel().getSelectedIndex();
                Tab selectedTab = tabPane.getTabs().get(tabIndex);                
                reloadCurrentTab(selectedTab);
            }
	});
        
        //Wait for UI is displayed to get stage
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Scene scene = null;
                int waitSecond = 120;
                
                while (scene==null) {
                    scene = tabPane.getScene();
                    if (waitSecond-- < 0) break;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {                        
                    }
                }
                stage = (Stage) scene.getWindow();
                
                //Set close window event
                stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent event) {
                        int tabIndex = tabPane.getSelectionModel().getSelectedIndex();
                        if (tabIndex != -1) {
                            Tab currentTab = tabPane.getTabs().get(tabIndex);                        
                            confirmSaveChangeDataAction(currentTab);
                        }
                    }
                });
            }
        });
        t.start();
    }
    
    /**
     * Create UI by data of property files
     */
    public void loadDataToUI() {
        String key;
        String value;        
        HashMap<Node, Node> propertiesObject;
        listTabs = new ArrayList<>();
        listNodes = new HashMap<>();
        boolean isTextField;
        
        //Sort tabs by alphabet
        SortedSet<String> tabNames = new TreeSet<>(listFileContent.keySet());
        
        for (String propertyKey : tabNames) {
            int size = listFileContent.get(propertyKey).getListItem().size();
            int position = 1;
            
            propertiesObject = new HashMap<>();
            tab = new Tab(propertyKey);
            gridPane = new GridPane();            
            gridPane.setHgap(10);
            gridPane.setVgap(10);
            gridPane.setPadding(new Insets(0.0, 10.0, 0.0, 10.0));
            
            //Sort keys of property file by alphabet
            PropertyInfo propertyInfo = new PropertyInfo();
            for (int i = 0 ; i < size; i++) {
                propertyInfo.add(listFileContent.get(propertyKey).getListItem().get(i).getKey(), 
                        listFileContent.get(propertyKey).getListItem().get(i).getValue());
            }
            Collections.sort(propertyInfo.getListItem());
            
            //Show key and it's value to the tab
            for (int i = 0 ; i < size; i++) {
                isTextField = false;
                label = new Label(propertyInfo.getListItem().get(i).getKey());
                
                if (isProperty(propertyInfo.getListItem().get(i).getKey())) {
                    gridPane.add(label, 1, position);
                    value = propertyInfo.getListItem().get(i).getValue();
                    
                    if ("true".equalsIgnoreCase(value)) {
                       checkBox = new CheckBox();
                       checkBox.setSelected(true);
                       gridPane.add(checkBox, 2 , (position++));
                    } else if ("false".equalsIgnoreCase(value)) {
                       checkBox = new CheckBox();
                       checkBox.setSelected(false);
                       gridPane.add(checkBox, 2 , (position++));
                    } else {
                        textField = new TextField(propertyInfo.getListItem().get(i).getValue());
                        textField.setPrefWidth(Constants.TEXT_FIELD_PREF_WIDTH);
                        gridPane.add(textField, 2 , (position++));
                        isTextField = true;
                    }
                    propertiesObject.put(label, isTextField ? textField : checkBox);
                }
            }
            
            listNodes.put(propertyKey, propertiesObject);
            
            scrollPane = new ScrollPane();
            scrollPane.setContent(gridPane);
            tab.setContent(scrollPane);
            
            listTabs.add(tab);
            tabPane.getTabs().add(tab);
        }        
    }
    
    /**
     * Check key is enable to display
     * Example: If key starts with '-' or '#' or 'javafx' -> The key isn't display in UI
     * @param key
     * @return 
     */
    public boolean isProperty(String key) {
        key = key.trim();
        
        if (key != null && !Constants.EMPTY_SYMBOL.equals(key)) {
            for (String checkKey : Constants.CHECK_PROPERTY_PREFIX_KEY.split(Constants.CHECK_PREFIX_KEY_SEPARATOR)) {
                if (key.startsWith(checkKey.trim())) {
                    return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }
    
    public void saveProperties(Tab selectedTab) {
        HashMap<Node, Node> currentTabContent = listNodes.get(selectedTab.getText());
        HashMap<String, String> updateData = new HashMap<>();
        String updateKey, updateValue;
        
        for (Node nodeKey : currentTabContent.keySet()) {
            Label nodeLabel = (Label) nodeKey;
            CheckBox cBox;
            TextField tField;
            
            updateKey = nodeLabel.getText();
            
            if (currentTabContent.get(nodeKey) instanceof CheckBox) {
                cBox = (CheckBox) currentTabContent.get(nodeKey);
                updateValue = cBox.isSelected() ? "true" : "false";     
            } else {
                tField = (TextField) currentTabContent.get(nodeKey);
                updateValue = tField.getText();
            }
            
            updateData.put(updateKey, updateValue);
        }
        
        try {
            PropertyFileUtils.updatePropertiyInfo(updateData, path_config, selectedTab.getText(), Constants.SUFFIX_PROPERTIES_FILE);
            logger.info("Save file: " + selectedTab.getText() + Constants.SUFFIX_PROPERTIES_FILE);
        } catch (IOException ex) {
            logger.error("[IOException] " + ex.getMessage());
        }
    }
    
    public void reloadCurrentTab(Tab currentTab) {     
        Label nodeLabel;
        String nodeValue = null;
        CheckBox cBox;
        TextField tField;
        
        try {
            //Reload file
            PropertyFileUtils.readFileContent(path_config, currentTab.getText(), Constants.SUFFIX_PROPERTIES_FILE);
            
            PropertyInfo propertyInfo = listFileContent.get(currentTab.getText());
            HashMap<Node, Node> currentTabContent = listNodes.get(currentTab.getText());
            int size = propertyInfo.getListItem().size();

            for (Node nodeKey : currentTabContent.keySet()) {
                nodeLabel = (Label) nodeKey;

                for (int i = 0 ; i < size ; i++) {
                    if (propertyInfo.getListItem().get(i).getKey().equals(nodeLabel.getText())) {
                        nodeValue = propertyInfo.getListItem().get(i).getValue();
                        break;
                    }
                }

                if (currentTabContent.get(nodeKey) instanceof CheckBox) {
                    cBox = (CheckBox) currentTabContent.get(nodeKey);

                    if ("true".equalsIgnoreCase(nodeValue)) {
                        cBox.setSelected(true);
                    } else {
                        cBox.setSelected(false);
                    }
                } else {
                    tField = (TextField) currentTabContent.get(nodeKey);
                    tField.setText(nodeValue);
                }
            }
        } catch (IOException ex) {
            logger.error("[IOException] " + ex.getMessage());
        }
    }
    
    /**
     * Confirm saving data when change tab
     * @param currentTab 
     */
    public void confirmSaveChangeDataAction(Tab currentTab) {
        if (isContentChange(currentTab)) {
            ButtonType buttonTypeYes = ButtonType.YES;
            ButtonType buttonTypeNo = ButtonType.NO;
            Scene scene = tabPane.getScene();

            //Create alert
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);                    
            alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText(Constants.CONFIRM_SAVE_DIALOG_MESSAGE.replace("{currentTabName}", currentTab.getText()));
            alert.setX(Math.round(scene.getWindow().getX() + (scene.getWidth()/4)));
            alert.setY(Math.round(scene.getWindow().getY() + (scene.getHeight()/4)));
            
            //Show alert
            Optional<ButtonType> result = alert.showAndWait();

            //Handle event
            if (result.isPresent() && result.get() == ButtonType.YES) {
                //Save data to file before open other tab
                saveProperties(currentTab);
            } else {
                //Reload data before open other tab
                reloadCurrentTab(currentTab);
            }
        }
    }
    
    /**
     * Check content change when change tab
     * @param currentTab
     * @return 
     */
    public boolean isContentChange(Tab currentTab) {
        PropertyInfo propertyInfo = listFileContent.get(currentTab.getText());
        HashMap<Node, Node> currentTabContent = listNodes.get(currentTab.getText());
        int size = propertyInfo.getListItem().size();
        
        for (Node nodeKey : currentTabContent.keySet()) {
            Label nodeLabel = (Label) nodeKey;
            String nodeValue;
            CheckBox cBox;
            TextField tField;

            for (int i = 0 ; i < size ; i++) {
                if (propertyInfo.getListItem().get(i).getKey().equals(nodeLabel.getText())) {
                    nodeValue = propertyInfo.getListItem().get(i).getValue();
                    
                    if (currentTabContent.get(nodeKey) instanceof CheckBox) {
                        cBox = (CheckBox) currentTabContent.get(nodeKey);
                        String checked = cBox.isSelected() ? "true" : "false";
                        if (!nodeValue.equalsIgnoreCase(checked)) {
                            return true;
                        }                        
                    } else {
                        tField = (TextField) currentTabContent.get(nodeKey);
                        if (!nodeValue.equalsIgnoreCase(tField.getText())) {
                            return true;
                        }
                    }                    
                    break;
                }
            }
        }
        return false;
    }    
}
