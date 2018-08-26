package gb.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller {
    final String IP_ADDRESS = "localhost";
    final int PORT = 8190;
    @FXML
    TextField msgField;
    @FXML
    TextArea chatArea;
    @FXML
    HBox bottomPanel;
    @FXML
    HBox upperPanel;
    @FXML
    TextField loginfield;
    @FXML
    PasswordField passwordField;
    @FXML
    HBox registrPanel;
    @FXML
    TextField regLog;
    @FXML
    TextField regNick;
    @FXML
    PasswordField regPass;
    Socket socket;
    DataInputStream in;
    DataOutputStream out;
    boolean isAuthohorized;

    public void setAuthohorized(boolean isAuthohorized) {
        this.isAuthohorized = isAuthohorized;
        if (!isAuthohorized) {
            upperPanel.setVisible(true);
            upperPanel.setManaged(true);
            bottomPanel.setVisible(false);
            bottomPanel.setManaged(false);
            registrPanel.setVisible(true);
            registrPanel.setManaged(true);
        } else {
            upperPanel.setVisible(false);
            upperPanel.setManaged(false);
            bottomPanel.setVisible(true);
            bottomPanel.setManaged(true);
            registrPanel.setVisible(false);
            registrPanel.setManaged(false);
        }
    }

    public void connect() {
        try {
            socket = new Socket(IP_ADDRESS, PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            setAuthohorized(false);
            new Thread(() -> {
                try {

                    while (true) {
                        String str = in.readUTF();
                        if (str.startsWith("/authok")) {
                            setAuthohorized(true);
                            break;
                        } else {
                            chatArea.appendText(str + "\n");
                        }
                    }
                    while (true) {
                        String str = in.readUTF();
                        if (str.equals("/serverclosed")) {
                            break;
                        }
                        chatArea.appendText(str + "\n");
//                        System.out.println("print message in chat");

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    setAuthohorized(false);
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg() {
        try {
            out.writeUTF(msgField.getText());
            msgField.clear();
            msgField.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void tryToAuth() {
        connect();
        try {
            out.writeUTF("/auth " + loginfield.getText() + " " + passwordField.getText());
            regNick.clear();
            passwordField.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void addNewUser(ActionEvent actionEvent) {
        connect();

        try {
            out.writeUTF("/addUser " + regLog.getText() + " " + regPass.getText() + " " +
                    regNick.getText());
            regLog.clear();
            regPass.clear();
            regNick.clear();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
