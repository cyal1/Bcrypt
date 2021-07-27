package burp;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class BurpExtender implements IBurpExtender,ITab,IContextMenuFactory,IExtensionStateListener{
    private JTabbedPane tabPane;
    private Send2Xray send2xray;
    private IBurpExtenderCallbacks callbacks;

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
        send2xray = new Send2Xray();
        this.tabPane.addTab("Send2Xray", send2xray);
        this.tabPane.addTab("AES", new AESTab());
        this.tabPane.addTab("DES", new TestTab());
        this.tabPane.addTab("3DES", new TestTab());

        callbacks.addSuiteTab(BurpExtender.this);
        callbacks.registerContextMenuFactory(this);
        callbacks.registerExtensionStateListener(this);
        this.send2xray.loadConfig(this.callbacks);

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
        Send2XrayListener mil = new Send2XrayListener(this.send2xray, invocation.getSelectedMessages());
        send2XrayMenu.addActionListener(mil);
        menu.add(send2XrayMenu);
        return menu;
    }

    @Override
    public void extensionUnloaded() {
        this.send2xray.saveConfig(this.callbacks);
    }
}


