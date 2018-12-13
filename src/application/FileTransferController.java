package application;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class FileTransferController {

    @FXML
    private AnchorPane topbar, downloadView, settingsView, connectionView;

    @FXML
    private ImageView connection;

    @FXML
    private ImageView shutdown;

    @FXML
    private TextField textfield_ip;

    @FXML
    private ImageView connection_indic;

    @FXML
    private ImageView download_indic;

    @FXML
    private ImageView settings_indic;


    @FXML
    private ImageView button_download;

    @FXML
    private ImageView button_explorer;

    @FXML
    private ImageView button_refresh;


    @FXML
    private TextField textfield_dpath;

    @FXML
    private ImageView button_explorer2;

}
