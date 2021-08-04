package burp;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Send2Xray extends JPanel {
    private final JLabel statusLabel;
    private final JTextField hostInput;
    private final JTextField portInput;

    public Send2Xray() {
        this.hostInput = new JTextField("127.0.0.1", 16);
        this.portInput = new JTextField("9999", 5);

        portInput.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                int keyChar = e.getKeyChar();
                if (!(keyChar >= KeyEvent.VK_0 && keyChar <= KeyEvent.VK_9)) {
                    e.consume();
                }
            }
        });

        JButton checkBtn = new JButton("check");
        checkBtn.addActionListener(e -> checkStatusLabel());
        this.statusLabel = new JLabel("");
        this.add(hostInput);
        this.add(portInput);
        this.add(checkBtn);
        this.add(statusLabel);
    }

    public void checkStatusLabel(){
        Socket socket;
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(this.getHost(), this.getPort()), 3000);
            socket.setSoTimeout(2000);
            socket.close();
            this.setLabelStatus("success");
        }catch (IOException ioException) {
            BurpExtender.stderr.println(ioException);
            this.setLabelStatus("fail");
        }
    }

    public String getHost() {
        return this.hostInput.getText();
    }
    public void setHost(String s) {
        this.hostInput.setText(s);
    }

    public int getPort() {
        return Integer.parseInt(this.portInput.getText());
    }
    public void setPort(String port){
        this.portInput.setText(port);
    }

    public void setLabelStatus(String labelStatus) {
        this.statusLabel.setText(labelStatus);
    }


    public void saveConfig(IBurpExtenderCallbacks callbacks){
        callbacks.saveExtensionSetting("xrayProxyHost",this.getHost());
        callbacks.saveExtensionSetting("xrayProxyPort", String.valueOf(this.getPort()));
    }

    public void loadConfig(IBurpExtenderCallbacks callbacks){
        String host = callbacks.loadExtensionSetting("xrayProxyHost");
        String port = callbacks.loadExtensionSetting("xrayProxyPort");
        if ( host != null)
        {
            this.setHost(host);
        }
        if (port != null){
            this.setPort(port);
        }
    }
}