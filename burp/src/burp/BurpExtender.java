package burp;

import javax.net.ssl.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BurpExtender implements IBurpExtender,ITab,IContextMenuFactory{
    public IExtensionHelpers helpers;
    public JTabbedPane tabPane;
    public static PrintWriter stdout;
    public static PrintWriter stderr;
    public Send2Xray send2xray;

    @Override
    public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) {
        stdout = new PrintWriter(callbacks.getStdout(), true);
        stderr = new PrintWriter(callbacks.getStderr(), true);
        this.tabPane = new JTabbedPane();
        this.tabPane.addTab("AES", new AESTab());
        this.tabPane.addTab("DES", new DefaultTabPane());
        this.tabPane.addTab("3DES", new DefaultTabPane());
        this.send2xray = new Send2Xray();
        this.tabPane.addTab("Send2Xray", this.send2xray);
        callbacks.addSuiteTab(BurpExtender.this);
        callbacks.registerContextMenuFactory(BurpExtender.this);
        this.helpers = callbacks.getHelpers();

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
        JMenuItem item = new JMenuItem("Send to Xray");
        MenuItemListener mil = new MenuItemListener(this, invocation.getSelectedMessages());
        item.addActionListener(mil);
        menu.add(item);
        return menu;
    }
}


