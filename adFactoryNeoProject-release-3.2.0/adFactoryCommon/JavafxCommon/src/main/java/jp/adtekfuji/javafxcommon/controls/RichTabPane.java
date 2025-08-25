/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.controls;

import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.StringUtils;
import adtekfuji.utility.Tuple;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.*;
import javafx.scene.input.*;
import jp.adtekfuji.javafxcommon.skin.TabPaneSkin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * タブの追加と移動が可能なタブペイン
 *
 * @author s-heya
 */
public class RichTabPane extends TabPane implements ListChangeListener {

    private final Logger logger = LogManager.getLogger();

    private static final SceneContiner sc = SceneContiner.getInstance();
    private static final ResourceBundle rb = LocaleUtils.getBundle("locale");

    private final String DRAG_KEY = "RichTab";
    private String prefixName = "Tab";
    private final static String insertTab = "\u2795";
    private boolean editable = true;
    private final int NOT_SPECIFY_ADD_INDEX = -99;
    private Function<RichTab, Optional<ContextMenu>> createContextMenuCreator;

    private List<Tuple<Integer, RichTab>> dragTabs = new ArrayList<>();

    private final List<RichTab> selectedTabs = new ArrayList<>();
      
    public void selectedTabClear() {
        selectedTabs.forEach(selectedTab -> selectedTab.setStyle(null));
        selectedTabs.clear();
    }

    public List<RichTab> getSelectedTabs() {
        return selectedTabs;
    }
    
    /**
     * コンストラクタ
     */
    public RichTabPane() {
        setSkin(new TabPaneSkin(this));
        // [+]タブを最後尾に表示する
        RichTab tab = new RichTab(insertTab);
        tab.setEditable(false);
        tab.setClosable(false);
        this.getTabs().add(tab);
          
        this.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) -> {
            if (tab.equals(newValue)) {
                addTab();
            }
        });

        this.getTabs().addListener(this);
           
        // キーが押されたときのイベントをハンドルする
        this.addEventFilter(KeyEvent.KEY_PRESSED, this::handleKeyPress);
        
        // ドラッグ中にタブエリアに入った。
        this.setOnDragEntered(this::dragTab);
        
        // ドラッグ中、マウスカーソルがタブエリアから出た。
        this.setOnDragExited((DragEvent event) -> {
            dropTab(event, false);
        });

        // ドラッグ中、マウスカーソルがタブエリア内を移動。
        this.setOnDragOver((DragEvent event) -> {
            Dragboard dragboard = event.getDragboard();
            if (dragboard.hasString() && DRAG_KEY.equals(dragboard.getString())) {
                event.acceptTransferModes(TransferMode.MOVE);
                event.consume();
            }
        });

        // ドラッグ中、タブエリア内にドロップされた。
        this.setOnDragDropped((DragEvent event) -> {
            dropTab(event, true);
        });

        this.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && selectedTabs.stream().noneMatch(RichTab::isDeleting)) {
                getTabs().forEach(selectedTab -> selectedTab.setStyle(null));
                if (!selectedTabs.isEmpty()) {
                    RichTab focusTab = selectedTabs.get(selectedTabs.size() - 1);
                    selectedTabs.clear();
                    selectedTabs.add(focusTab);
                } else {
                    this.selectFirst();
                }
            }
        }); 

    }
    
    /**
     * [+]タブか?
     *
     * @param tab チェックするタブ
     * @return true: [+]タブ / false: [+]タブではない
     */
    public static boolean isInsertTab(RichTab tab) {
        return insertTab.equals(tab.getLabel().getText());
    }

    /**
     * 削除可能なタブか?
     * @param tab
     * @return
     */
    public static boolean isDeleteTab(RichTab tab) {
        return insertTab.equals(tab.getLabel().getText());
    }

    /**
     * 選択されているか?
     * @return
     */
    public boolean isSelected() {
        return !selectedTabs.isEmpty();
    }

//    /**
//     * コンテキストメニューを作成
//     *
//     * @param menuItems メニューアイテム
//     * @return コンテキストメニュー
//     */
//    private static Optional<ContextMenu> createContextMenu(List<MenuItem> menuItems) {
//        if (Objects.isNull(menuItems)
//                || menuItems.isEmpty()) {
//            return Optional.empty();
//        }
//        ContextMenu contextMenu = new ContextMenu();
//        contextMenu.setStyle(style);
//        contextMenu.getItems().addAll(menuItems);
//        return Optional.of(contextMenu);
//    }

    /**
     * MenuItem生成用関数設定
     *
     * @param createContextMenuCreator ContextMenuItem生成用関数
     */
    public void setContextMenuCreator(Function<RichTab, Optional<ContextMenu>> createContextMenuCreator) {
        if (Objects.isNull(createContextMenuCreator)) {
            return;
        }
        this.createContextMenuCreator = createContextMenuCreator;
        this.getTabs()
                .stream()
                .filter(tab -> tab instanceof RichTab)
                .map(tab -> (RichTab) tab)
                .forEach(richTab -> {
                    createContextMenuCreator.apply(richTab)
                            .ifPresent(contextMenu -> richTab.getLabel().setContextMenu(contextMenu));
                });
    }
    
    /**
     * タブをナビゲートするためのキー押下イベントを処理します。
     * 
     * @param event キー押下イベント
     */
    private void handleKeyPress(KeyEvent event) {
        KeyCode key = event.getCode();
        if (KeyCode.LEFT.equals(key) || KeyCode.UP.equals(key)) {
            this.selectPrevious();
            event.consume();
        } else if (KeyCode.RIGHT.equals(key) || KeyCode.DOWN.equals(key)) {
            this.selectNext();
            event.consume();
        }
    }  

    /**
     *
     * @param event
     */
    private void dragTab(DragEvent event) {
        try {
            if (selectedTabs.isEmpty() || selectedTabs.size() >= this.getTabs().size()-1) {
                return;
            }
            
            Dragboard dragboard = event.getDragboard();
            if (dragboard.hasString() && DRAG_KEY.equals(dragboard.getString())) {                
                dragTabs = selectedTabs
                        .stream()
                        .map(selectedTab -> new Tuple<>(getTabs().indexOf(selectedTab), selectedTab))
                        .collect(Collectors.toList());

                selectedTabs.forEach(selectedTab -> {
                    selectedTab.setDragging(true);  
                });
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            event.acceptTransferModes(TransferMode.MOVE);
            event.consume();
        }
    }

    /**
     *
     * @param event
     * @param isDrop
     */
    private void dropTab(DragEvent event, boolean isDrop) {
        try {
            if (dragTabs.isEmpty()) {
                return;
            }
            
            Dragboard dragboard = event.getDragboard();
            if (dragboard.hasString() && DRAG_KEY.equals(dragboard.getString())) {
                int index = getTabIndex(new Point2D(event.getScreenX(), event.getScreenY()));
                if (!isDrop || index < 0) {
                    selectedTabs.clear();
                    selectedTabs.addAll(dragTabs.stream().map(Tuple::getRight).collect(Collectors.toList()));
                    selectTabs(selectedTabs);

                    getTabs().forEach(item -> ((RichTab)item).setDragging(false));
                    dragTabs.clear();
                    if (isDrop) {
                        event.setDropCompleted(true);
                    }
                    return;
                }
                
                dragTabs.forEach(tuple -> getTabs().remove(tuple.getRight()));
                
                int tabsRemovedBeforeDrop = (int) dragTabs.stream()
                    .filter(tuple -> tuple.getLeft() < index)
                    .count();

                int adjustedDropIndex = index - tabsRemovedBeforeDrop;
                
                for (int n = dragTabs.size() - 1; n >= 0; --n) {                    
                    getTabs().add(adjustedDropIndex, dragTabs.get(n).getRight());
                }
                selectedTabs.clear();
                selectedTabs.addAll(dragTabs.stream().map(Tuple::getRight).collect(Collectors.toList()));
                selectTabs(selectedTabs);

                dragTabs.clear();
                getTabs().forEach(item -> ((RichTab)item).setDragging(false));

                if (isDrop) {
                    event.setDropCompleted(true);
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            event.consume();
        }
    }
       
    /**
    * 指定されたインデックスのタブを選択状態にします。
    * 
    * @param index 選択するタブのインデックス (0から始まる)
    */
    private void selectTab(int index) {
        Tab tab = getTabs().get(index);
        selectedTabClear();
        selectedTabs.add((RichTab) tab);
        getSelectionModel().select(tab);
    }

    /**
    * 最初のタブを選択状態にします。
    * 
    * このメソッドは、最初のタブ (インデックス0) を選択状態にするために使用されます。 
    */
    public void selectFirst() {
        selectTab(0);
    }
    
    /**
    * 最後のタブを選択状態にします。
    * 
    * このメソッドは、最後のタブ (タブ数 - 2(通常は 「+」 などの追加タブを除いたもの)) を選択状態にするために使用されます。 
    */
    private void selectLast() {
        selectTab(this.getTabs().size() - 2);
    }
    
    /**
    * 一つ前のタブを選択します。
    * 
    * 現在選択されているタブのインデックスを取得し、そのインデックスが0以下の場合は最後のタブを選択します。
    * それ以外の場合は、 現在のインデックスの1つ前のタブを選択します。
    * 
    * このメソッドは、 タブを順番に切り替える操作やナビゲーションの一環として利用されることがあります。 
    */
    private void selectPrevious() {
        int selectedIndex = getSelectionModel().getSelectedIndex();
        if(selectedIndex <= 0) {
            selectLast();
        } else {
            selectTab(selectedIndex - 1);
        }
    }
    
    /**
    * 次のタブを選択します。
    * 
    * 現在選択されているタブのインデックスを取得し、そのインデックスが
    * 最後のタブ (通常は 「+」 などの追加タブを除いたもの) のインデックスを超える場合、
    * 最初のタブを選択します。 そうでない場合は、現在の次のタブ (インデックス + 1) を選択します。
    * 
    * このメソッドは、タブを順番に切り替える操作やナビゲーションの一環として利用されます。 
    */
    private void selectNext() {
        int selectedIndex = getSelectionModel().getSelectedIndex();
        if(selectedIndex >= getTabs().size() - 2) {
            selectFirst();
        } else {
            selectTab(selectedIndex + 1);
        }
    }

    /**
     * タブを閉じる
     * <div>
     * ここでは消去確認メッセージを表示し、OKが押された場合のみタブを閉じる。 それ以外が押された場合、タブは閉じない。
     * </div>
     *
     * @param event
     */
    private void onCloseRequest(Event event) {
        final RichTab target = (RichTab) event.getTarget();
        final ButtonType ret = sc.showOkCanselDialog(Alert.AlertType.CONFIRMATION, LocaleUtils.getString("key.Delete"), LocaleUtils.getString("key.DeleteSingleMessage"), target.getName());
        if (!Objects.equals(ret, ButtonType.OK)) {
            event.consume();
        }
        selectedTabClear();
    }

    /**
     * タブを削除する
     */
    public void removeTab() {
        selectedTabs.forEach(selectedTab -> selectedTab.setDeleting(true));
        final ButtonType ret = sc.showOkCanselDialog(Alert.AlertType.CONFIRMATION, LocaleUtils.getString("key.Delete"), LocaleUtils.getString("key.DeleteSingleMessage"));
        if (Objects.equals(ret, ButtonType.OK)) {
            selectedTabs.forEach(selectedTab -> getTabs().remove(selectedTab));
            selectedTabClear();
        }
        getTabs().forEach(tab -> ((RichTab)tab).setDeleting(false));
    }

    /**
     * タブが変更された。
     *
     * @param change
     */
    @Override
    public void onChanged(ListChangeListener.Change change) {
        try {
            this.getTabs().removeListener(this);
        } finally {
            this.getTabs().addListener(this);
        }
    }

    /**
     * タブの名前を設定する。
     *
     * @param prefixName
     */
    public void setPrefixName(String prefixName) {
        this.prefixName = prefixName;
        //this.renameTab();
    }

    /**
     * タブの名前を編集可能にする。
     *
     * @param editable
     */
    public void setEditable(boolean editable) {
        this.editable = editable;
        for (Tab tab : this.getTabs()) {
            if (tab instanceof RichTab) {
                ((RichTab) tab).setEditable(this.editable);
            }
        }
    }

    /**
     * タブを追加する。（シート名指定）
     *
     * @param tabName
     * @return
     */
    public RichTab addTab(String tabName) {
        return settingRichTab(tabName, NOT_SPECIFY_ADD_INDEX);
    }

    /**
     * タブを追加する。（シート名は"Sheet タブ数"）
     *
     * @return
     */
    public RichTab addTab() {
        return settingRichTab("", NOT_SPECIFY_ADD_INDEX);
    }

    /**
     * タブを追加する。（シート名指定）
     *
     * @param tabName 追加するタブのシート名
     * @param index 追加するタブ位置
     * @return
     */
    public RichTab addTab(String tabName, int index) {
        return settingRichTab(tabName, index);
    }
    
     /**
     * タブを追加する。（シート名は"Sheet タブ数")
     *
     * @param index 追加するタブ位置
     * @return
     */
    public RichTab addTab(int index){
        return settingRichTab("", index);
    }

    /**
     * タブを設定する。
     *
     * @param tabName 追加するタブのシート名
     * @param index 追加するタブ位置(最後に追加する場合はNOT_SPECIFY_ADD_INDEXをセット)
     */
    private RichTab settingRichTab(String tabName, int index) {
        StringBuilder sb = new StringBuilder();
        if (!tabName.isEmpty()) {
            sb.append(tabName);
        } else {
            sb.append(this.prefixName);
            sb.append(" ");
            sb.append(this.getTabs().size());
        }
      
        RichTab tab = new RichTab(sb.toString());
        tab.setEditable(this.editable);
        tab.setOnCloseRequest(this::onCloseRequest);
        // タブのラベルのパディングを調整し、タブの大部分をクリックできるようにしました。
        tab.getLabel().setStyle("-fx-padding: 8px 0px; ");
    
        // ドラッグ開始
        tab.getLabel().setOnDragDetected(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Label label = (Label) event.getSource();
                Dragboard dragboard = label.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent clipboardContent = new ClipboardContent();
                clipboardContent.putString(DRAG_KEY);
                dragboard.setContent(clipboardContent);

                String dragText = label.getText();
                if (selectedTabs
                        .stream()
                        .map(RichTab::getLabel)
                        .map(Label::getText)
                        .noneMatch(txt -> StringUtils.equals(txt, dragText))) {
                    selectedTabs.forEach(selectedTab -> selectedTab.setStyle(null));
                    selectedTabs.clear();
                    selectedTabs.add(tab);
                }
                RichTab lastTab = selectedTabs.get(selectedTabs.size() - 1);
                if(!Objects.equals(lastTab.getName(), dragText)) {
                    lastTab.setStyle("-fx-background-color: #f4f4f4;");
                }

//                draggingTab.set(tab);
                event.consume();
            }
        });



        tab.getLabel().setOnMouseClicked(event -> {
            Platform.runLater(() -> { 
                if (selectedTabs.size() == 1 && !event.isShiftDown() && !event.isControlDown() && tab.isEditable() && event.getClickCount() == 2) {
                    tab.rename();
                    return;
                }

                if (event.getButton() == MouseButton.SECONDARY) {
                    if (selectedTabs.contains(tab)) {
                        return;
                    }

                    selectedTabClear();
                    selectedTabs.add(tab);
                    getSelectionModel().select(tab);
                    return;
                }

                if (event.isControlDown()) {
                    if (selectedTabs.contains(tab)) {
                        if (selectedTabs.size() >= 2) {
                            selectedTabs.remove(tab);
                            tab.setStyle(null);
                            selectTabs(selectedTabs);
                        }
                    } else {
                        selectedTabs.add(tab);
                        selectTabs(selectedTabs);
                    }
                } else if (event.isShiftDown()) {
                    int prevIndex = getTabs().indexOf(selectedTabs.get(selectedTabs.size() - 1));
                    int selectIndex = getTabs().indexOf(tab);
                    int step = selectIndex < prevIndex ? -1 : 1;
                    for (int n = prevIndex; n != selectIndex; n += step) {
                        RichTab selectedTab = (RichTab) getTabs().get(n);
                        if (!selectedTabs.contains(selectedTab)) {
                            selectedTabs.add(selectedTab);
                        }
                    }

                    selectedTabs.remove(tab);
                    selectedTabs.add(tab);
                    selectTabs(selectedTabs);
                } else {
                    selectedTabClear();
                    selectedTabs.add(tab);
                }
            });
        });

        // コンテキストメニューの設定
        if (Objects.nonNull(createContextMenuCreator)) {
            createContextMenuCreator.apply(tab)
                    .ifPresent(contextMenu -> tab.getLabel().setContextMenu(contextMenu));
        }

        int addIndex = index;
        if (addIndex == NOT_SPECIFY_ADD_INDEX) {
            addIndex = getTabs().size() - 1;
        }
        getTabs().add(addIndex, tab);
        getSelectionModel().select(tab);
        selectedTabs.clear();
        selectedTabs.add(tab);
        
        return tab;
    }

    /**
     * 指定されたタブのリストを選択状態にし、最後のタブを選択します。
     *
     * @param selectedTabs 選択するタブのリスト
     */
    void selectTabs(List<RichTab> selectedTabs) {
        for (int n = 0; n < selectedTabs.size() - 1; ++n) {
            selectedTabs.get(n).setStyle("-fx-background-color: #f4f4f4;");
        }

        Tab lastTab = selectedTabs.get(selectedTabs.size() - 1);
        lastTab.setStyle(null);
        getSelectionModel().select(lastTab);
    }


    /**
     * 座標からタブインデックスを取得する。
     *
     * @param point
     * @return
     */
    private int getTabIndex(Point2D point) {
        int tabIndex = -1;
        int lastIndex = this.getTabs().size() - 1;
        
        Rectangle2D tabPaneRect = this.getAbsoluteRect(this);
        if (tabPaneRect.contains(point)) {
            
            if(isNavigationVisible()) {
                if(point.getX() > (tabPaneRect.getMaxX()- getNavigationButtonsWidth() - getControlButtonsWidth())) {
                    return -1;
                }
            }

            if (!this.getTabs().isEmpty()) {
                Rectangle2D firstTabRect = this.getAbsoluteRect(this.getTabs().get(0));
                if (firstTabRect.getMaxY() + 60.0 < point.getY() || firstTabRect.getMinY() > point.getY()) {
                    return tabIndex;
                }

                Rectangle2D lastTabRect = this.getAbsoluteRect(this.getTabs().get(lastIndex));
                if (point.getX() < (firstTabRect.getMinX() + firstTabRect.getWidth() / 2)) {
                    tabIndex = 0;
                } else if (point.getX() > (lastTabRect.getMaxX() - lastTabRect.getWidth() / 2)) {
                    //tabIndex = this.getTabs().size();
                    tabIndex = -1;
                } else {
                    for (int i = 0; i < lastIndex; i++) {
                        Tab leftTab = this.getTabs().get(i);
                        Tab rightTab = this.getTabs().get(i + 1);
                        if (leftTab instanceof RichTab && rightTab instanceof RichTab) {
                            Rectangle2D leftTabRect = this.getAbsoluteRect(leftTab);
                            Rectangle2D rightTabRect = this.getAbsoluteRect(rightTab);
                            if (this.betweenX(leftTabRect, rightTabRect, point.getX())) {
                                tabIndex = i + 1;
                                if (tabIndex >= lastIndex) {
                                    // [+]タブより後ろには追加できない
                                    tabIndex = lastIndex;
                                }
                                break;
                            }
                        }
                    }
                }
            }
            return tabIndex;
        }

        return tabIndex;
    }

    /**
     * 絶対座標を取得する。
     *
     * @param node
     * @return
     */
    private Rectangle2D getAbsoluteRect(Control node) {
        Point2D point = node.localToScreen(node.getLayoutBounds().getMinX(), node.getLayoutBounds().getMinY());
        return new Rectangle2D(
                point.getX(),
                point.getY(),
                node.getWidth(),
                node.getHeight());
    }

    /**
     * 絶対座標を取得する。
     *
     * @param tab
     * @return
     */
    private Rectangle2D getAbsoluteRect(Tab tab) {
        Control node = ((RichTab) tab).getLabel();
        return this.getAbsoluteRect(node);
    }

    /**
     * 指定した座標が、2点の間に位置するかどうか。
     *
     * @param rect1
     * @param rect2
     * @param xPoint
     * @return
     */
    private boolean betweenX(Rectangle2D rect1, Rectangle2D rect2, double xPoint) {
        double lowerBound = rect1.getMinX() + rect1.getWidth() / 2;
        double upperBound = rect2.getMaxX() - rect2.getWidth() / 2;
        return xPoint >= lowerBound && xPoint <= upperBound;
    }

    /**
     * シートの名前を生成する。
     * 
     * @param src 元シート名
     * @return 
     */
    public String generateSheetName(String src) {
        logger.info("generateSheetName: " + src);
        final String[] escapes = {"+", ".", "?", "{", "}", "(", ")", "[", "]", "^", "$", "-", "|", "/"};
        final String numberRegex = "\\s\\(\\d+\\)$";

        String prefix = src.replaceFirst(numberRegex, "");
        String _prefix = prefix;

        for (String escape : escapes) {
            _prefix = _prefix.replace(escape, "\\" + escape);
        }
        
        // 正規表現 "^prefix($|($|\s\(\d+\)$)"
        StringBuilder nameRegex = new StringBuilder();
        nameRegex.append("^");
        nameRegex.append(_prefix);
        nameRegex.append("($|");
        nameRegex.append(numberRegex);
        nameRegex.append(")");
        
        Pattern p = Pattern.compile(nameRegex.toString());
         
        List<Integer> serials = this.getTabs().stream()
                .map(o -> {
                    Matcher m = p.matcher(((RichTab)o).getName());
                    if (m.find()) {
                        if (StringUtils.isEmpty(m.group(1))) {
                            return 1;
                        }
                        String value = m.group(1).replaceAll("\\s|\\(|\\)", "");
                        return Integer.parseInt(value);
                    }
                    return 0;
                })
                .filter(o -> o != 0)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        
        if (serials.isEmpty()) {
            return src;
        }
        
        Integer id = 2;
        Matcher matcher = p.matcher(src);
        if (matcher.find() && !StringUtils.isEmpty(matcher.group(1))) {
            String value = matcher.group(1).replaceAll("\\s|\\(|\\)", "");
            id = Math.max(2, Integer.parseInt(value));
        }

        while (serials.contains(id)) {
            id++;
        }

        StringBuilder sb = new StringBuilder(prefix);
        sb.append(" (");
        sb.append(id);
        sb.append(")");
        return sb.toString();
    }
        
    /**
     * 自動でラベルを付ける
     */
    public void autoLabelingTabs() {
        if (!Boolean.parseBoolean(AdProperty.getProperties().getProperty("autoLabelingTabs", "false"))) {
            return;
        }

        logger.info("autoLabelingTabs");
        int ii = 1;
        for (Tab tab : this.getTabs()) {
            if (tab instanceof RichTab) {
                RichTab richTab = (RichTab) tab;
                if (!insertTab.equals(richTab.getName())) {
                    richTab.setName(String.valueOf(ii++));
                }
            }
        }
    }
    
    /**
    * TabPane のスキンにおいて、ナビゲーションコントロール（スクロール矢印など）が
    * 表示されているかどうかを確認します。
    *
    * @return ナビゲーションコントロールが表示されていれば {@code true}、そうでなければ {@code false} を返します。
    */
    public boolean isNavigationVisible() {
        TabPaneSkin skin = (TabPaneSkin) getSkin();
        return skin != null && skin.isNavigationVisible();
    }
    
    /**
    * TabPane のスキンにおいて、ナビゲーションボタン（スクロールボタンなど）の
    * 合計幅を取得します。
    *
    * @return ナビゲーションボタンの幅（ピクセル単位）
    */
    public double getNavigationButtonsWidth() {
        TabPaneSkin skin = (TabPaneSkin) getSkin();
        return skin.getNavigationButtonsWidth();
    }
    
    /**
    * TabPane のスキンにおいて、コントロールボタン（タブを閉じるボタンなど）の
    * 合計幅を取得します。
    *
    * @return コントロールボタンの幅（ピクセル単位）
    */
    public double getControlButtonsWidth() {
        TabPaneSkin skin = (TabPaneSkin) getSkin();
        return skin.getControlButtonsWidth();
    }
}
