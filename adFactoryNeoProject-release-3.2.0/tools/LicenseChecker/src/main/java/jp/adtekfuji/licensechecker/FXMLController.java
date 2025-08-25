package jp.adtekfuji.licensechecker;

import com.sun.tools.javac.util.StringUtils;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

public class FXMLController implements Initializable {
    
    private static final byte[] AES_KEY = {(byte) 0x97, (byte) 0xa2, (byte) 0xd4, 0x26, (byte) 0xe2, (byte) 0xe8, (byte) 0xac, 0x52, (byte) 0xe5, 0x7f, 0x5c, 0x0a, 0x1a, 0x48, 0x5a, 0x67};
    private static final String AES_ALGORITHM = "AES_128/CBC/NOPADDING";

    @FXML
    private TextArea label;
    
    @FXML
    private void onChoice(ActionEvent event) {
        FileChooser.ExtensionFilter filter1 = new FileChooser.ExtensionFilter("ライセンスファイル", "*.license");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("ライセンスファイル選択");
        fileChooser.getExtensionFilters().addAll(filter1);

        File file = fileChooser.showOpenDialog(label.getParent().getScene().getWindow());
        if (Objects.nonNull(file)) {
            displayLicense(file);
        }
    }
    
    /*
    *ライセンスファイルの受け入れ許可
    *2024/12/13 追加　r-honda
    */
    public void handleDragOver(DragEvent event){
        Dragboard db = event.getDragboard();
        if(db.hasFiles()){
            String file = db.getFiles().get(0).toString();
            if(file.substring(file.lastIndexOf(".")).equals(".license")){
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
        }
        event.consume();
    }
    
    /*
    *ドロップされたファイルの処理
    *2024/12/13 追加　r-honda
    */
    public void handleDragDropped(DragEvent event){
        boolean success = false;
        
        Dragboard db = event.getDragboard();
        if(db.hasFiles()){
            File file = db.getFiles().get(0);
            displayLicense(file);
            success = true;
        }
        event.setDropCompleted(success);
        event.consume();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    private void displayLicense(File file) {

        StringBuilder sb = new StringBuilder();
        try (FileInputStream stream = new FileInputStream(file)) {

            sb.append("File : " + file.getAbsolutePath());
            sb.append("\n\n");

            byte[] iv = new byte[16];
            int readBytes = stream.read(iv, 0, 16);

            byte[] buffer = new byte[(int) file.length() - readBytes];
            stream.read(buffer, 0, buffer.length);

            String source = CipherHelper.decrypt(buffer, AES_KEY, iv, AES_ALGORITHM);

            Properties properties = new Properties();
            properties.load(new ByteArrayInputStream(source.getBytes("ISO-8859-1")));

            List list = Collections.list(properties.propertyNames());
            Collections.sort(list);

            for (Object obj : list) {
                String key = (String) obj;
                if (key.trim().length() == 0) {
                    continue;
                }
                sb.append(key + " : " + StringUtils.toUpperCase(properties.getProperty(key)));
                sb.append("\n");
            }

        } catch (Exception ex) {
            sb.append(ex);
        } finally {
            this.label.setText(sb.toString());
        }
    }
}
