package burp;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

class AES_UI extends JPanel{
    public static final int COMPLETE_BODY = 0;
    public static final int URL_BODY_PARAM = 1;
    private final IExtensionHelpers helpers = BurpExtender.helpers;


    public static final int BASE64 = 0;
    public static final int HEX = 1;
    public static final int BYTES = 2;

    // request option
    public int requestOption = COMPLETE_BODY;
    public boolean requestURLEncode = false;
    public String[] requestParams = null;
    public int requestCipherFormat = BASE64;

    //response option
    public int responseOption = COMPLETE_BODY;
    public boolean responseURLEncode = false;
    public String[] responseParams = null;
    public int responseCipherFormat = BASE64;
    public boolean ignoreResponse = false;

    // global option
    public String alg;
    public byte[] secretKey;
    public byte[] iv;
    public String targetHost;
    public boolean start = false;

    // decode/encode editor
    public int cipherTextFormat = BASE64;
    public boolean urlEncode = false;

    public JPanel topPanel;
    public JTextField ivTextField;
    public JComboBox algComboBox;
    public JTextField secretKeyTextField;
    public JTextField requestParamTextField;
    public JCheckBox requestURLCheckBox;
    public JTextField responseParamTextField;
    public JCheckBox responseURLCheckBox;


    public JPanel respSettingPanel;
    public JPanel reqSettingPanel;
    public JPanel globalSettingPanel;
    public JTextField targetHostTextField;
    public JButton startButton;
    public JButton encryptButton;
    public JButton decryptButton;
    public JButton clearButton;
    public JCheckBox URLEncodeDecodeCheckBox;
    public JTabbedPane tabbedPane1;
    public JTabbedPane tabbedPane2;
    public JTextArea inputTextArea;
    public JTextArea outputTextArea;
    public JComboBox responseCipherFormatComboBox;
    public JComboBox cipherComboBox;
    public JComboBox responseComboBox;
    public JComboBox requestComboBox;
    public JCheckBox ignoreResponseCheckBox;
    public JComboBox requestCipherFormatComboBox;

    public void saveConfig(IBurpExtenderCallbacks callbacks){
        callbacks.saveExtensionSetting("alg", Integer.toString(algComboBox.getSelectedIndex()));
        callbacks.saveExtensionSetting("iv", ivTextField.getText());
        callbacks.saveExtensionSetting("secretKey", secretKeyTextField.getText());
        callbacks.saveExtensionSetting("targetHost", targetHostTextField.getText());
    }

    public void loadConfig(IBurpExtenderCallbacks callbacks){
        String alg = callbacks.loadExtensionSetting("alg");
        if(alg != null){
            algComboBox.setSelectedIndex(Integer.parseInt(alg));
        }
        String iv = callbacks.loadExtensionSetting("iv");
        if(iv != null){
            ivTextField.setText(iv);
        }
        String sk= callbacks.loadExtensionSetting("secretKey");
        if(sk != null){
            secretKeyTextField.setText(sk);
        }
        String host = callbacks.loadExtensionSetting("targetHost");
        if(host != null){
            targetHostTextField.setText(host);
        }
        BurpExtender.stdout.println("AES Configuration loaded.");
    }

    public AES_UI() {
        this.setLayout(new BorderLayout(0, 0));
        final JSplitPane splitPane1 = new JSplitPane();
        splitPane1.setOrientation(0);
        this.add(splitPane1, BorderLayout.CENTER);
        topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        topPanel.putClientProperty("html.disable", Boolean.FALSE);
        splitPane1.setLeftComponent(topPanel);
        globalSettingPanel = new JPanel();
        globalSettingPanel.setLayout(new GridBagLayout());
        topPanel.add(globalSettingPanel);
        globalSettingPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Global setting", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label1 = new JLabel();
        label1.setText("algorithm");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        globalSettingPanel.add(label1, gbc);
        final JLabel label2 = new JLabel();
        label2.setText("secret key");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        globalSettingPanel.add(label2, gbc);
        algComboBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("AES/CBC/PKCS5Padding");
        defaultComboBoxModel1.addElement("AES/ECB/PKCS5Padding");
        defaultComboBoxModel1.addElement("AES/CBC/NoPadding");
        defaultComboBoxModel1.addElement("AES/ECB/NoPadding");
        algComboBox.setModel(defaultComboBoxModel1);
        algComboBox.setSelectedIndex(0);
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        globalSettingPanel.add(algComboBox, gbc);
        ivTextField = new JTextField();
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        globalSettingPanel.add(ivTextField, gbc);
        final JLabel label3 = new JLabel();
        label3.setText("iv");
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        globalSettingPanel.add(label3, gbc);
        secretKeyTextField = new JTextField();
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        globalSettingPanel.add(secretKeyTextField, gbc);
        startButton = new JButton();
        startButton.setText("Start");
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        globalSettingPanel.add(startButton, gbc);
        targetHostTextField = new JTextField();
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        globalSettingPanel.add(targetHostTextField, gbc);
        final JLabel label4 = new JLabel();
        label4.setText("target host");
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        globalSettingPanel.add(label4, gbc);
        reqSettingPanel = new JPanel();
        reqSettingPanel.setLayout(new GridBagLayout());
        topPanel.add(reqSettingPanel);
        reqSettingPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Request Option to Decrypt/Encrypt", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        requestParamTextField = new JTextField();
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        reqSettingPanel.add(requestParamTextField, gbc);
        requestURLCheckBox = new JCheckBox();
        requestURLCheckBox.setInheritsPopupMenu(false);
        requestURLCheckBox.setSelected(false);
        requestURLCheckBox.setText("URL Decode/Encode");
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        reqSettingPanel.add(requestURLCheckBox, gbc);
        requestComboBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel2 = new DefaultComboBoxModel();
        defaultComboBoxModel2.addElement("complete body");
        defaultComboBoxModel2.addElement("parameters");
        requestComboBox.setModel(defaultComboBoxModel2);
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        reqSettingPanel.add(requestComboBox, gbc);
        final JLabel label5 = new JLabel();
        label5.setText("ciphertext location");
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        reqSettingPanel.add(label5, gbc);
        final JLabel label6 = new JLabel();
        label6.setText("params separated with space");
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        reqSettingPanel.add(label6, gbc);
        final JLabel label7 = new JLabel();
        label7.setText("request ciphertext format");
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        reqSettingPanel.add(label7, gbc);
        requestCipherFormatComboBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel3 = new DefaultComboBoxModel();
        defaultComboBoxModel3.addElement("Base64");
        defaultComboBoxModel3.addElement("Hex");
        defaultComboBoxModel3.addElement("Bytes");
        requestCipherFormatComboBox.setModel(defaultComboBoxModel3);
        requestCipherFormatComboBox.setSelectedIndex(0);
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        reqSettingPanel.add(requestCipherFormatComboBox, gbc);
        respSettingPanel = new JPanel();
        respSettingPanel.setLayout(new GridBagLayout());
        topPanel.add(respSettingPanel);
        respSettingPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Response Option to Decrypt/Encrypt", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        responseParamTextField = new JTextField();
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        respSettingPanel.add(responseParamTextField, gbc);
        responseURLCheckBox = new JCheckBox();
        responseURLCheckBox.setText("URL Decode/Encode");
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        respSettingPanel.add(responseURLCheckBox, gbc);
        responseComboBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel4 = new DefaultComboBoxModel();
        defaultComboBoxModel4.addElement("complete body");
        defaultComboBoxModel4.addElement("parameters");
        responseComboBox.setModel(defaultComboBoxModel4);
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        respSettingPanel.add(responseComboBox, gbc);
        final JLabel label8 = new JLabel();
        label8.setText("ciphertext location");
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        respSettingPanel.add(label8, gbc);
        final JLabel label9 = new JLabel();
        label9.setText("params separated with space");
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        respSettingPanel.add(label9, gbc);
        responseCipherFormatComboBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel5 = new DefaultComboBoxModel();
        defaultComboBoxModel5.addElement("Base64");
        defaultComboBoxModel5.addElement("Hex");
        defaultComboBoxModel5.addElement("Bytes");
        responseCipherFormatComboBox.setModel(defaultComboBoxModel5);
        responseCipherFormatComboBox.setSelectedIndex(0);
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        respSettingPanel.add(responseCipherFormatComboBox, gbc);
        final JLabel label10 = new JLabel();
        label10.setText("response ciphertext format");
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        respSettingPanel.add(label10, gbc);
        ignoreResponseCheckBox = new JCheckBox();
        ignoreResponseCheckBox.setText("Ignore response");
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        respSettingPanel.add(ignoreResponseCheckBox, gbc);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new BorderLayout(0, 0));
        panel1.setMinimumSize(new Dimension(1139, 300));
        splitPane1.setRightComponent(panel1);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        panel2.setDoubleBuffered(false);
        panel1.add(panel2, BorderLayout.NORTH);
        encryptButton = new JButton();
        encryptButton.setText("Encrypt");
        panel2.add(encryptButton);
        decryptButton = new JButton();
        decryptButton.setText("Dcrypt");
        panel2.add(decryptButton);
        clearButton = new JButton();
        clearButton.setText("Clear");
        panel2.add(clearButton);
        cipherComboBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel6 = new DefaultComboBoxModel();
        defaultComboBoxModel6.addElement("Base64");
        defaultComboBoxModel6.addElement("Hex");
        cipherComboBox.setModel(defaultComboBoxModel6);
        cipherComboBox.setOpaque(true);
        cipherComboBox.setSelectedIndex(0);
        panel2.add(cipherComboBox);
        URLEncodeDecodeCheckBox = new JCheckBox();
        URLEncodeDecodeCheckBox.setText("URL Encode/Decode");
        panel2.add(URLEncodeDecodeCheckBox);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new BorderLayout(0, 0));
        panel1.add(panel3, BorderLayout.CENTER);
        final JSplitPane splitPane2 = new JSplitPane();
//        splitPane2.setContinuousLayout(false);
//        splitPane2.setDividerLocation(502);
        splitPane2.setResizeWeight(0.5);
        splitPane2.setBackground(new Color(251,251,251));
//        splitPane2.setDividerSize(9);
//        splitPane2.setEnabled(true);
//        splitPane2.setOneTouchExpandable(false);
        panel3.add(splitPane2, BorderLayout.CENTER);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new BorderLayout(0, 0));
        splitPane2.setLeftComponent(panel4);
        tabbedPane1 = new JTabbedPane();
        tabbedPane1.setTabPlacement(1);
        panel4.add(tabbedPane1, BorderLayout.CENTER);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridBagLayout());
        tabbedPane1.addTab("Input", panel5);
        inputTextArea = new JTextArea();
        inputTextArea.setLineWrap(true);
        inputTextArea.setWrapStyleWord(true);
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel5.add(inputTextArea, gbc);
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new BorderLayout(0, 0));
        splitPane2.setRightComponent(panel6);
        tabbedPane2 = new JTabbedPane();
        panel6.add(tabbedPane2, BorderLayout.CENTER);
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridBagLayout());
        tabbedPane2.addTab("Output", panel7);
        outputTextArea = new JTextArea();
        outputTextArea.setLineWrap(true);
        outputTextArea.setWrapStyleWord(true);
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel7.add(outputTextArea, gbc);



        requestParamTextField.setEnabled(false);
        requestParamTextField.setBackground(new Color(240,240,240));
        responseParamTextField.setEnabled(false);
        responseParamTextField.setBackground(new Color(240,240,240));

        startButton.addActionListener(e -> {
            if (!start) {
                // global setting
                // secretKey = new byte[16];
                // System.arraycopy(secretKeyTextField.getText().getBytes(StandardCharsets.UTF_8), 0, secretKey, 0, 16);

                secretKey = secretKeyTextField.getText().getBytes(StandardCharsets.UTF_8);
                if(secretKey.length != 16){
                    JOptionPane.showMessageDialog(new JPanel(),"secret key must be 16 bytes long","Warning", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                iv = ivTextField.getText().getBytes(StandardCharsets.UTF_8);
                if((ivTextField.isEnabled() && iv.length != 16)){
                    JOptionPane.showMessageDialog(new JPanel(),"iv must be 16 bytes long","Warning", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if(!ivTextField.isEnabled()){
                    iv = null;
                }
                targetHost = targetHostTextField.getText();
                if(targetHost.equals("")){
                    JOptionPane.showMessageDialog(new JPanel(),"pls input a valid host","Warning", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                alg = Objects.requireNonNull(algComboBox.getSelectedItem()).toString();

                requestCipherFormat = cipherComboBox.getSelectedIndex();
                responseCipherFormat = responseCipherFormatComboBox.getSelectedIndex();
                requestCipherFormat = requestCipherFormatComboBox.getSelectedIndex();


                // request setting
                requestOption = requestComboBox.getSelectedIndex();
                requestParams = requestParamTextField.getText().split(" ");
                requestURLEncode = requestURLCheckBox.isSelected();

                // response setting
                responseOption = responseComboBox.getSelectedIndex();
                responseParams = responseParamTextField.getText().split(" ");
                responseURLEncode = responseURLCheckBox.isSelected();
                ignoreResponse = ignoreResponseCheckBox.isSelected();

                startButton.setText("Stop");
                algComboBox.setEnabled(false);
                secretKeyTextField.setEnabled(false);
                ivTextField.setEnabled(false);
                ivTextField.setBackground(new Color(251,251,251));
                requestCipherFormatComboBox.setEnabled(false);
                responseCipherFormatComboBox.setEnabled(false);
                targetHostTextField.setEnabled(false);
                requestComboBox.setEnabled(false);
                requestParamTextField.setEnabled(false);
                requestURLCheckBox.setEnabled(false);
                responseComboBox.setEnabled(false);
                responseParamTextField.setEnabled(false);
                responseURLCheckBox.setEnabled(false);
                ignoreResponseCheckBox.setEnabled(false);
                start = true;
            } else {
                startButton.setText("Start");
                algComboBox.setEnabled(true);
                secretKeyTextField.setEnabled(true);
                if (!(alg.equals("AES/ECB/PKCS5Padding") || alg.equals("AES/ECB/NoPadding"))) {
                    ivTextField.setEnabled(true);
                    ivTextField.setBackground(Color.white);
                }
                targetHostTextField.setEnabled(true);
                requestURLCheckBox.setEnabled(true);
                requestComboBox.setEnabled(true);
                if (requestOption != COMPLETE_BODY) {
                    requestParamTextField.setEnabled(true);
                }
                requestCipherFormatComboBox.setEnabled(true);
                if (!ignoreResponseCheckBox.isSelected()) {
                    responseCipherFormatComboBox.setEnabled(true);
                    responseComboBox.setEnabled(true);
                    responseURLCheckBox.setEnabled(true);
                    if (responseOption != COMPLETE_BODY) {
                        responseParamTextField.setEnabled(true);
                    }
                }
                ignoreResponseCheckBox.setEnabled(true);
                start = false;
            }
        });

        responseComboBox.addActionListener(e -> {
                if (responseComboBox.getSelectedIndex() == COMPLETE_BODY) {
                    responseParamTextField.setEnabled(false);
                    responseParamTextField.setBackground(new Color(240,240,240));
                } else {
                    responseParamTextField.setEnabled(true);
                    responseParamTextField.setBackground(Color.WHITE);
                }
        });

        requestComboBox.addActionListener(e -> {
            int index = requestComboBox.getSelectedIndex();
            if (index == COMPLETE_BODY) {
                requestParamTextField.setEnabled(false);
                requestParamTextField.setBackground(new Color(240,240,240));
            } else {
                requestParamTextField.setEnabled(true);
                requestParamTextField.setBackground(Color.white);
            }
        });

        algComboBox.addActionListener(e -> {
            int index = algComboBox.getSelectedIndex();
            if (index == 1 || index == 3) {
                ivTextField.setEnabled(false);
                ivTextField.setBackground(new Color(240,240,240));
            } else {
                ivTextField.setEnabled(true);
                ivTextField.setBackground(Color.white);
            }
        });

        clearButton.addActionListener(e -> {
            inputTextArea.setText("");
            outputTextArea.setText("");
        });

        decryptButton.addActionListener(e -> {
            cipherTextFormat = cipherComboBox.getSelectedIndex();
            urlEncode = URLEncodeDecodeCheckBox.isSelected();
//            secretKey = new byte[16];
//            System.arraycopy(secretKeyTextField.getText().getBytes(StandardCharsets.UTF_8), 0, secretKey, 0, 16);
            secretKey = secretKeyTextField.getText().getBytes(StandardCharsets.UTF_8);
            String text = inputTextArea.getText();
            alg = Objects.requireNonNull(algComboBox.getSelectedItem()).toString();
            String mode = alg.split("/")[1];
            iv = ivTextField.getText().getBytes(StandardCharsets.UTF_8);
            if (!ivTextField.isEnabled()){
                iv = null;
            }
//            if (mode.equals("CBC")) {
//                iv = ivTextField.getText().getBytes(StandardCharsets.UTF_8);
//                if (iv.length != 16) {
//                    outputTextArea.setText("IV length must be 16 bytes long");
//                    return;
//                }
//            }
            byte[] plainText;
            if (urlEncode) {
                text = BurpExtender.helpers.urlDecode(text);
            }
            try {
                plainText = CryptUtils.AESEncrypt(Cipher.DECRYPT_MODE, alg, secretKey, decoder(cipherTextFormat, text), iv);
            } catch (Exception ex) {
                outputTextArea.setText(ex.toString());
                return;
            }
            if (urlEncode) {
                outputTextArea.setText(BurpExtender.helpers.urlEncode(new String(plainText)));
            } else {
                outputTextArea.setText(new String(plainText));
            }
        });

        encryptButton.addActionListener(e -> {

//            secretKey = new byte[16];
//            System.arraycopy(secretKeyTextField.getText().getBytes(StandardCharsets.UTF_8), 0, secretKey, 0, 16);
            secretKey = secretKeyTextField.getText().getBytes(StandardCharsets.UTF_8);
            String text = inputTextArea.getText();
            alg = Objects.requireNonNull(algComboBox.getSelectedItem()).toString();
            cipherTextFormat = cipherComboBox.getSelectedIndex();
            urlEncode = URLEncodeDecodeCheckBox.isSelected();
            String mode = alg.split("/")[1];
//            String pad = alg.split("/")[2];
            iv = ivTextField.getText().getBytes(StandardCharsets.UTF_8);
            if (!ivTextField.isEnabled()){
                iv = null;
            }
//            if (pad.equals("NoPadding") && text.length() % 16 != 0) {
//                outputTextArea.setText("NoPadding required plain text length must be multiple of 16 bytes");
//                return;
//            }
//            if (mode.equals("CBC")) {
//                iv = ivTextField.getText().getBytes(StandardCharsets.UTF_8);
//                if (iv.length != 16) {
//                    outputTextArea.setText("IV length must be 16 bytes long");
//                    return;
//                }
//            }
//            if (mode.equals("CBC")) {
//                iv = ivTextField.getText().getBytes(StandardCharsets.UTF_8);
//                if (iv.length != 16) {
//                    outputTextArea.setText("IV length must be 16 bytes long");
//                    return;
//                }
//            }
            byte[] encryptedText;
            if(urlEncode){
                text = BurpExtender.helpers.urlDecode(text);
            }
            try {
                encryptedText = CryptUtils.AESEncrypt(Cipher.ENCRYPT_MODE, alg, secretKey, text.getBytes(StandardCharsets.UTF_8), iv);
            } catch (Exception ex) {
                outputTextArea.setText(ex.toString());
                return;
            }
            if (urlEncode) {
                outputTextArea.setText(BurpExtender.helpers.urlEncode(encoder(cipherTextFormat, encryptedText)));
            } else {
                outputTextArea.setText(encoder(cipherTextFormat, encryptedText));
            }
        });

        ignoreResponseCheckBox.addActionListener(e -> {
            if(ignoreResponseCheckBox.isSelected()){
                responseComboBox.setEnabled(false);
                responseCipherFormatComboBox.setEnabled(false);
                responseURLCheckBox.setEnabled(false);
                if(responseParamTextField.isEnabled()){
                    responseParamTextField.setEnabled(false);
                }
            }else{
                responseComboBox.setEnabled(true);
                responseCipherFormatComboBox.setEnabled(true);
                responseURLCheckBox.setEnabled(true);
                if(responseComboBox.getSelectedIndex() != COMPLETE_BODY){
                    responseParamTextField.setEnabled(true);
                }
            }
        });
    }

    public void processHttpMessage_AES(int toolFlag, boolean messageIsRequest, IHttpRequestResponse messageInfo){
        String reqHost = messageInfo.getHttpService().getHost();
        if (messageIsRequest){
            if (!reqHost.equals(this.targetHost)) {return;}

            IRequestInfo reqInfo = helpers.analyzeRequest(messageInfo);
            java.util.List<String> headers = reqInfo.getHeaders();
            if(this.requestOption == AES_UI.COMPLETE_BODY){
                // encode body
                byte[] tmpreq = messageInfo.getRequest();
                byte[] messageBody;
                try {
                    messageBody = CryptUtils.AESEncrypt(Cipher.ENCRYPT_MODE, this.alg, this.secretKey, Arrays.copyOfRange(tmpreq, reqInfo.getBodyOffset(), tmpreq.length), this.iv);
                } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
                    BurpExtender.stderr.println("processHttpMessage encrypt requests body error: " + e);
                    return;
                }
                byte[] updateMessage = helpers.buildHttpMessage(headers, encoder(this.responseCipherFormat, messageBody).getBytes(StandardCharsets.UTF_8));
                messageInfo.setRequest(updateMessage);
            }else{
                // encode param
                byte[] _request = messageInfo.getRequest();
                try {
                    if(reqInfo.getContentType() == IRequestInfo.CONTENT_TYPE_JSON){
                        _request = update_req_params_json(_request, headers, this.requestParams, true);
                    }else{
                        _request = update_req_params(_request, headers, this.requestParams, true);
                    }
                } catch (InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e) {
                    BurpExtender.stderr.println("processHttpMessage encrypt requests param error: " + e);
                    return;
                }
                messageInfo.setRequest(_request);
            }
        }else{
            if(this.ignoreResponse){return;}
            //response decode
            IRequestInfo reqInfo = helpers.analyzeRequest(messageInfo);
            IResponseInfo resInfo = helpers.analyzeResponse(messageInfo.getResponse());
//            String URL = reqInfo.getUrl().toString();
            java.util.List<String> headers = resInfo.getHeaders();
            if (reqHost.equals(this.targetHost)){
                if(this.requestOption == AES_UI.COMPLETE_BODY){
                    // Complete Response Body decryption
                    byte[] tmpreq = messageInfo.getResponse();
//                    String messageBody = tmpreq.substring(resInfo.getBodyOffset()).trim();
                    byte[] messageBody;
                    byte[] cipherText = decoder(this.responseCipherFormat, new String(Arrays.copyOfRange(tmpreq, resInfo.getBodyOffset(), tmpreq.length)));
                    try {
                        messageBody = CryptUtils.AESEncrypt(Cipher.DECRYPT_MODE,this.alg, this.secretKey, cipherText, this.iv);
                    } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
                        BurpExtender.stderr.println("processHttpMessage decrypt response body error: " + e);
                        return;
                    }
                    byte[] updateMessage = helpers.buildHttpMessage(headers, messageBody);
                    messageInfo.setResponse(updateMessage);
                }
                else if(this.responseOption == AES_UI.URL_BODY_PARAM){ // TODO burp '+' to ' '
                    byte[] _response = messageInfo.getResponse();
//                    byte[] _request = messageInfo.getRequest();
                    // TODO if json
                    try {
                        _response = this.update_req_params_json(_response, headers, this.requestParams, false);
                    } catch (InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e) {
                        BurpExtender.stderr.println("processHttpMessage decrypt response params error: " + e);
                        return;
                    }
                    messageInfo.setResponse(_response);
                }

            }
        }
    }

    public void processProxyMessage_AES(boolean messageIsRequest, IInterceptedProxyMessage message){
        String reqHost = message.getMessageInfo().getHttpService().getHost();
        if (messageIsRequest){
            if (!reqHost.equals(this.targetHost)) {return;}
            IRequestInfo reqInfo = helpers.analyzeRequest(message.getMessageInfo());
            IHttpRequestResponse messageInfo =  message.getMessageInfo();
            java.util.List<String> headers = reqInfo.getHeaders();
            if(this.requestOption == AES_UI.COMPLETE_BODY){
                // decode body
                byte[] tmpreq = message.getMessageInfo().getRequest();
                byte[] messageBody;
                byte[] cipherText = decoder(this.requestCipherFormat,new String(Arrays.copyOfRange(tmpreq, reqInfo.getBodyOffset(),tmpreq.length)));
                try {
                    messageBody = CryptUtils.AESEncrypt(Cipher.DECRYPT_MODE, this.alg, this.secretKey, cipherText, this.iv);
                } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
                    BurpExtender.stderr.println("processProxyMessage decrypt request body error: " + e);
                    return;
                }
                byte[] updateMessage = helpers.buildHttpMessage(headers, messageBody);
                message.getMessageInfo().setRequest(updateMessage);
            }else{
                byte[] request = messageInfo.getRequest();
                byte[] _request;
                try {
                    if(reqInfo.getContentType() == IRequestInfo.CONTENT_TYPE_JSON){
                        _request = update_req_params_json(request, headers, this.requestParams ,false);
                    }else{
//                        BurpExtender.stdout.println("params to decrypt: " + Arrays.toString(this.requestParams));
                        _request = update_req_params(request, headers, this.requestParams, false);
                    }
                } catch (InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e) {
                    BurpExtender.stderr.println("processProxyMessage decrypt request params error: " + e);
                    return;
                }
                messageInfo.setRequest(_request);
            }
        }else{
            //response decode
            if(this.ignoreResponse) { return; }
            // PPM Response
            IHttpRequestResponse messageInfo = message.getMessageInfo();
            IRequestInfo reqInfo = helpers.analyzeRequest(messageInfo);
            IResponseInfo resInfo = helpers.analyzeResponse(messageInfo.getResponse());
//            String URL = reqInfo.getUrl().toString();
            List<String> headers = resInfo.getHeaders();
            if (!reqHost.equals(this.targetHost)) {return;}
            if(this.responseOption == AES_UI.COMPLETE_BODY){
                // Complete Response Body decrypt
                byte[] tmpreq = messageInfo.getResponse();
//                    String messageBody = tmpreq.substring(resInfo.getBodyOffset()).trim();
                byte[] messageBody;
                try {
                    messageBody = CryptUtils.AESEncrypt(Cipher.ENCRYPT_MODE, this.alg, this.secretKey, Arrays.copyOfRange(tmpreq, resInfo.getBodyOffset(), tmpreq.length), this.iv);
                } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
                    BurpExtender.stderr.println("processProxyMessage encrypt response body error: " + e);
                    return;
                }
                byte[] updateMessage = helpers.buildHttpMessage(headers, encoder(this.responseCipherFormat,messageBody).getBytes(StandardCharsets.UTF_8));
                messageInfo.setResponse(updateMessage);
            }
            else if(this.responseOption == AES_UI.URL_BODY_PARAM){
                byte[] _response = messageInfo.getResponse();
                // TODO if json
                try {
                    _response = this.update_req_params_json(_response, headers, this.responseParams, true);
                } catch (InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e) {
                    BurpExtender.stderr.println("processProxyMessage encrypt response params  error: " + e);
                    return;
                }
                messageInfo.setResponse(_response);
            }
        }
    }
    public byte[] update_req_params (byte[] _request, List<String> headers, String[] _params, Boolean _do_enc) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        for (String param : _params) {
            IParameter _p = helpers.getRequestParameter(_request, param);
            if (_p == null || _p.getName().length() == 0) {
                continue;
            }
            byte[] _str;
            if (_do_enc) {
                _str = CryptUtils.AESEncrypt(Cipher.ENCRYPT_MODE, this.alg, this.secretKey, _p.getValue().trim().getBytes(StandardCharsets.UTF_8), this.iv);

                _str = encoder(this.responseCipherFormat, _str).getBytes(StandardCharsets.UTF_8);
//                if(this.responseURLEncode){
//                    _str = helpers.urlEncode(_str);
//                }
            } else {
                String p = _p.getValue().trim();
                if(this.requestURLEncode){
                    p = helpers.urlDecode(_p.getValue().trim());
                }
                byte[] cipherText = decoder(this.requestCipherFormat, p);
                _str = CryptUtils.AESEncrypt(Cipher.DECRYPT_MODE, this.alg, this.secretKey, cipherText, this.iv);
            }
            IParameter _newP = helpers.buildParameter(param, new String(_str), _p.getType());
            _request = helpers.removeParameter(_request, _p);
            _request = helpers.addParameter(_request, _newP);
            IRequestInfo reqInfo2 = helpers.analyzeRequest(_request);
            String tmpreq = new String(_request);
            String messageBody = tmpreq.substring(reqInfo2.getBodyOffset()).trim();
            _request = helpers.buildHttpMessage(headers, messageBody.getBytes());
        }
        return _request;
    }

    public byte[] update_req_params_json(byte[] _request, List<String> headers, String[] _params, Boolean _do_enc) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        for (String param : _params) {
            IParameter _p = helpers.getRequestParameter(_request, param);
            if (_p == null || _p.getName().length() == 0) {
                continue;
            }
            byte[] _str;
            if (_do_enc) {
                _str = CryptUtils.AESEncrypt(Cipher.ENCRYPT_MODE, this.alg, this.secretKey, _p.getValue().trim().getBytes(StandardCharsets.UTF_8), this.iv);
                _str = encoder(this.requestCipherFormat, _str).getBytes(StandardCharsets.UTF_8);
            } else {
                _str = CryptUtils.AESEncrypt(Cipher.DECRYPT_MODE, this.alg, this.secretKey, decoder(this.requestCipherFormat,_p.getValue().trim()), this.iv);
            }

            IRequestInfo reqInfo = helpers.analyzeRequest(_request);
            String tmpreq = new String(_request);
            String messageBody = tmpreq.substring(reqInfo.getBodyOffset()).trim();

            int _fi = messageBody.indexOf(param);
            if (_fi < 0) {
                continue;
            }
            _fi = _fi + param.length() + 3;
            int _si = messageBody.indexOf("\"", _fi);
            messageBody = messageBody.substring(0, _fi) + new String(_str) + messageBody.substring(_si);
            _request = helpers.buildHttpMessage(headers, messageBody.getBytes());
        }
        return _request;
    }

    public  byte[] decoder(int cipherTextFormat, String s) {
        if (cipherTextFormat == AES_UI.BASE64) {
            return BurpExtender.helpers.base64Decode(s);
        }
        if (cipherTextFormat == AES_UI.HEX) {
            return BurpExtender.hexToBytes(s);
        }
        // bytes
        return s.getBytes(StandardCharsets.UTF_8);
    }

    public  String encoder(int cipherTextFormat, byte[] b) {
        if (cipherTextFormat == AES_UI.BASE64) {
            return BurpExtender.helpers.base64Encode(b);
        }
        if (cipherTextFormat == AES_UI.HEX) {
            return BurpExtender.bytesToHex(b);
        }
        // bytes
        return new String(b);
    }
}
