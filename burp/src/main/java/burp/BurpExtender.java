package burp;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.math.BigInteger;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class BurpExtender implements IBurpExtender,ITab,IContextMenuFactory,IExtensionStateListener,IHttpListener,IProxyListener{
    private JTabbedPane tabPane;
    private Send2Xray send2xray;
    private IBurpExtenderCallbacks callbacks;
    private AES_UI aesTab;
    private Customized custom;
    public static IExtensionHelpers helpers;
    public static PrintWriter stdout;
    public static PrintWriter stderr;

    @Override
    public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) {
        stdout = new PrintWriter(callbacks.getStdout(), true);
        stderr = new PrintWriter(callbacks.getStderr(), true);
        helpers = callbacks.getHelpers();
        this.callbacks = callbacks;

        this.tabPane = new JTabbedPane();

        this.send2xray = new Send2Xray();
        this.aesTab = new AES_UI();
        this.custom = new Customized();

        this.tabPane.addTab("Customized",custom);
        this.tabPane.addTab("AES", aesTab);
        this.tabPane.addTab("Send2Xray", send2xray);

        this.send2xray.loadConfig(this.callbacks);
        this.aesTab.loadConfig(this.callbacks);
        this.custom.loadConfig(this.callbacks);

        callbacks.addSuiteTab(BurpExtender.this);
        callbacks.registerContextMenuFactory(this);
        callbacks.registerExtensionStateListener(this);
        callbacks.registerHttpListener(this);
        callbacks.registerProxyListener(this);
    }

    @Override
    public String getTabCaption() {
        return "Bcrypt";
    }

    @Override
    public Component getUiComponent() {
        return this.tabPane;
    }

    @Override
    public List<JMenuItem> createMenuItems(IContextMenuInvocation invocation) throws NoSuchAlgorithmException, KeyManagementException {
        List<JMenuItem> menu = new ArrayList<>();
        JMenuItem send2XrayMenu = new JMenuItem("Send to Xray");
//        JMenuItem CustomEncrypt = new JMenuItem("Customized Encrypt");
//        JMenuItem CustomDecrypt = new JMenuItem("Customized Decrypt");
        Send2XrayListener mil = new Send2XrayListener(this.send2xray, invocation.getSelectedMessages());
        send2XrayMenu.addActionListener(mil);
        menu.add(send2XrayMenu);
        return menu;
    }

    @Override
    public void extensionUnloaded() {
        this.aesTab.saveConfig(this.callbacks);
        this.send2xray.saveConfig(this.callbacks);
        this.custom.saveConfig(this.callbacks);
    }

    @Override
    public void processHttpMessage(int toolFlag, boolean messageIsRequest, IHttpRequestResponse messageInfo) {
        if (this.aesTab.start){
            this.aesTab.processHttpMessage_AES(toolFlag, messageIsRequest, messageInfo);
        }
        if (this.custom.start){
            Customized.interpreter.set("messageIsRequest", messageIsRequest);
            Customized.interpreter.set("messageInfo", messageInfo);
            Customized.interpreter.exec("processHttpMessage(messageIsRequest, messageInfo)");
        }
    }

    @Override
    public void processProxyMessage(boolean messageIsRequest, IInterceptedProxyMessage message) {
        if (this.aesTab.start){
            this.aesTab.processProxyMessage_AES(messageIsRequest, message);
        }
        if (this.custom.start){
            Customized.interpreter.set("messageIsRequest", messageIsRequest);
            Customized.interpreter.set("messageInfo", message.getMessageInfo());
            Customized.interpreter.exec("processProxyMessage(messageIsRequest, messageInfo)");
        }
    }

    public static String bytesToHex(byte[] b) {
        return String.format("%x", new BigInteger(1, b));
    }

    public static byte[] hexToBytes(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
}
