package burp;

import javax.crypto.Cipher;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

public class DefaultTabPane extends Component {
    public static final int COMPLETE_BODY = 0;
    public static final int URL_BODY_PARAM = 1;
    public static final int JSON_PARAM = 2;


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


    private JPanel mainPanel;
    private JPanel topPanel;
    private JTextField ivTextField;
    private JComboBox algComboBox;
    private JTextField secretKeyTextField;
    private JTextField requestParamTextField;
    private JCheckBox requestURLCheckBox;
    private JTextField responseParamTextField;
    private JCheckBox responseURLCheckBox;


    private JPanel respSettingPanel;
    private JPanel reqSettingPanel;
    private JPanel globalSettingPanel;
    private JTextField targetHostTextField;
    private JButton startButton;
    private JButton encryptButton;
    private JButton decryptButton;
    private JButton clearButton;
    private JCheckBox URLEncodeDecodeCheckBox;
    private JTabbedPane tabbedPane1;
    private JTabbedPane tabbedPane2;
    private JTextArea inputTextArea;
    private JTextArea outputTextArea;
    private JComboBox responseCipherFormatComboBox;
    private JComboBox cipherComboBox;
    private JComboBox responseComboBox;
    private JComboBox requestComboBox;
    private JCheckBox ignoreResponseCheckBox;
    private JComboBox requestCipherFormatComboBox;


    public static String cipherOutputFormat(int cipherTextFormat, byte[] b) {
        if (cipherTextFormat == DefaultTabPane.BASE64) {
//            return BurpExtender.helpers.base64Encode(b);
            return Base64.getEncoder().encodeToString(b);
        }
        if (cipherTextFormat == DefaultTabPane.HEX) {
            return byteToHex(b);
        }
        // bytes
        return new String(b);
    }

    public static byte[] cipherInputFormat(int cipherTextFormat, String s) {
        if (cipherTextFormat == DefaultTabPane.BASE64) {
//            return BurpExtender.helpers.base64Decode(s);
            return Base64.getDecoder().decode(s);
        }
        if (cipherTextFormat == DefaultTabPane.HEX) {
            return hexToByte(s);
        }
        // bytes
        return s.getBytes(StandardCharsets.UTF_8);
    }

    public static byte[] hexToByte(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static String byteToHex(byte[] b) {
        return String.format("%x", new BigInteger(1, b));
    }

    public DefaultTabPane() {
        requestParamTextField.setEnabled(false);
        requestParamTextField.setBackground(Color.lightGray);
        responseParamTextField.setEnabled(false);
        responseParamTextField.setBackground(Color.lightGray);
        encryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                secretKey = new byte[16];
                System.arraycopy(secretKeyTextField.getText().getBytes(StandardCharsets.UTF_8), 0, secretKey, 0, 16);
                String text = inputTextArea.getText();
                alg = Objects.requireNonNull(algComboBox.getSelectedItem()).toString();
                cipherTextFormat = cipherComboBox.getSelectedIndex();
                String mode = alg.split("/")[1];
                String pad = alg.split("/")[2];
                iv = null;
                if (pad.equals("NoPadding") && text.length() % 16 != 0) {
                    outputTextArea.setText("NoPadding required plain text length must be multiple of 16 bytes");
                    return;
                }
                if (mode.equals("CBC")) {
                    iv = ivTextField.getText().getBytes(StandardCharsets.UTF_8);
                    if (iv == null) {
                        outputTextArea.setText("IV length must be 16 bytes long");
                        return;
                    }
                    if (iv.length != 16) {
                        outputTextArea.setText("IV length must be 16 bytes long");
                        return;
                    }
                }
                byte[] encryptedText = null;
                try {
                    encryptedText = CryptUtils.AESEncrypt(Cipher.ENCRYPT_MODE, alg, secretKey, text.getBytes(StandardCharsets.UTF_8), iv);
                    assert encryptedText != null;
                } catch (Exception ex) {
                    outputTextArea.setText(ex.toString());
                }
                outputTextArea.setText(cipherOutputFormat(cipherTextFormat, encryptedText));
                if (urlEncode) {
                    outputTextArea.setText(cipherOutputFormat(cipherTextFormat, encryptedText));
                } else {
                    outputTextArea.setText(cipherOutputFormat(cipherTextFormat, encryptedText));
                }
            }
        });
        decryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cipherTextFormat = cipherComboBox.getSelectedIndex();
                secretKey = new byte[16];
                System.arraycopy(secretKeyTextField.getText().getBytes(StandardCharsets.UTF_8), 0, secretKey, 0, 16);
                String text = inputTextArea.getText();
                alg = Objects.requireNonNull(algComboBox.getSelectedItem()).toString();
                String mode = alg.split("/")[1];
                iv = null;
                if (!mode.equals("ECB")) {
                    iv = ivTextField.getText().getBytes(StandardCharsets.UTF_8);
                    if (iv.length != 16) {
                        outputTextArea.setText("IV length must be 16 bytes long");
                        return;
                    }
                }
                byte[] plainText = null;
                try {
                    plainText = CryptUtils.AESEncrypt(Cipher.DECRYPT_MODE, alg, secretKey, cipherInputFormat(cipherTextFormat, text), iv);
                } catch (Exception ex) {
//                    noSuchPaddingException.printStackTrace();
                    outputTextArea.setText(ex.toString());
                }
                if (urlEncode) {
                    outputTextArea.setText(new String(plainText));
                } else {
                    outputTextArea.setText(new String(plainText));
                }
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inputTextArea.setText("");
                outputTextArea.setText("");
            }
        });

        algComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = algComboBox.getSelectedIndex();
                if (index == 1 || index == 3) {
                    ivTextField.setBackground(Color.lightGray);
                    ivTextField.setEnabled(false);
                } else {
                    ivTextField.setBackground(Color.white);
                    ivTextField.setEnabled(true);
                }
            }
        });

        requestComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = requestComboBox.getSelectedIndex();
                if (index == COMPLETE_BODY) {
                    requestParamTextField.setEnabled(false);
                    requestParamTextField.setBackground(Color.lightGray);
                } else {
                    requestParamTextField.setEnabled(true);
                    requestParamTextField.setBackground(Color.white);
                }
            }
        });

        responseComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (responseComboBox.getSelectedIndex() == COMPLETE_BODY) {
                    responseParamTextField.setEnabled(false);
                    responseParamTextField.setBackground(Color.lightGray);
                } else {
                    responseParamTextField.setEnabled(true);
                    responseParamTextField.setBackground(Color.white);
                }
            }
        });

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!start) {
                    // global setting
                    alg = Objects.requireNonNull(algComboBox.getSelectedItem()).toString();
                    secretKey = new byte[16];
                    System.arraycopy(secretKeyTextField.getText().getBytes(StandardCharsets.UTF_8), 0, secretKey, 0, 16);
                    iv = ivTextField.getText().getBytes(StandardCharsets.UTF_8);
                    requestCipherFormat = cipherComboBox.getSelectedIndex();
                    responseCipherFormat = responseCipherFormatComboBox.getSelectedIndex();
                    requestCipherFormat = requestCipherFormatComboBox.getSelectedIndex();
                    targetHost = targetHostTextField.getText();

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
                    cipherComboBox.setEnabled(false);
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
                    }
                    cipherComboBox.setEnabled(true);
                    responseCipherFormatComboBox.setEnabled(true);
                    targetHostTextField.setEnabled(true);
                    requestComboBox.setEnabled(true);
                    if (requestOption != COMPLETE_BODY) {
                        requestParamTextField.setEnabled(true);
                    }
                    requestURLCheckBox.setEnabled(true);
                    responseComboBox.setEnabled(true);
                    if (responseOption != COMPLETE_BODY) {
                        responseParamTextField.setEnabled(true);
                    }
                    responseURLCheckBox.setEnabled(true);
                    requestCipherFormatComboBox.setEnabled(true);
                    ignoreResponseCheckBox.setEnabled(true);
                    start = false;
                }
            }
        });
        ignoreResponseCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("DefaultTabPane");
        frame.setContentPane(new DefaultTabPane().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(0, 0));
        final JSplitPane splitPane1 = new JSplitPane();
        splitPane1.setOrientation(0);
        mainPanel.add(splitPane1, BorderLayout.CENTER);
        topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        topPanel.putClientProperty("html.disable", Boolean.FALSE);
        splitPane1.setLeftComponent(topPanel);
        globalSettingPanel = new JPanel();
        globalSettingPanel.setLayout(new GridBagLayout());
        topPanel.add(globalSettingPanel);
        globalSettingPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Golobal setting", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
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
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        globalSettingPanel.add(algComboBox, gbc);
        ivTextField = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        globalSettingPanel.add(ivTextField, gbc);
        final JLabel label3 = new JLabel();
        label3.setText("iv");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        globalSettingPanel.add(label3, gbc);
        secretKeyTextField = new JTextField();
        secretKeyTextField.setText("UFlZUEBLRkBISEZa");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        globalSettingPanel.add(secretKeyTextField, gbc);
        startButton = new JButton();
        startButton.setText("Start");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        globalSettingPanel.add(startButton, gbc);
        targetHostTextField = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        globalSettingPanel.add(targetHostTextField, gbc);
        final JLabel label4 = new JLabel();
        label4.setText("target host");
        gbc = new GridBagConstraints();
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
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        reqSettingPanel.add(requestURLCheckBox, gbc);
        requestComboBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel2 = new DefaultComboBoxModel();
        defaultComboBoxModel2.addElement("complete body");
        defaultComboBoxModel2.addElement("url/body param");
        defaultComboBoxModel2.addElement("json param");
        requestComboBox.setModel(defaultComboBoxModel2);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        reqSettingPanel.add(requestComboBox, gbc);
        final JLabel label5 = new JLabel();
        label5.setText("ciphertext location");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        reqSettingPanel.add(label5, gbc);
        final JLabel label6 = new JLabel();
        label6.setText("params separated with space");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        reqSettingPanel.add(label6, gbc);
        final JLabel label7 = new JLabel();
        label7.setText("request ciphertext format");
        gbc = new GridBagConstraints();
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
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        respSettingPanel.add(responseParamTextField, gbc);
        responseURLCheckBox = new JCheckBox();
        responseURLCheckBox.setText("URL Decode/Encode");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        respSettingPanel.add(responseURLCheckBox, gbc);
        responseComboBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel4 = new DefaultComboBoxModel();
        defaultComboBoxModel4.addElement("complete body");
        defaultComboBoxModel4.addElement("url/body param");
        defaultComboBoxModel4.addElement("json param");
        responseComboBox.setModel(defaultComboBoxModel4);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        respSettingPanel.add(responseComboBox, gbc);
        final JLabel label8 = new JLabel();
        label8.setText("ciphertext location");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        respSettingPanel.add(label8, gbc);
        final JLabel label9 = new JLabel();
        label9.setText("params separated with space");
        gbc = new GridBagConstraints();
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
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        respSettingPanel.add(responseCipherFormatComboBox, gbc);
        final JLabel label10 = new JLabel();
        label10.setText("response ciphertext format");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        respSettingPanel.add(label10, gbc);
        ignoreResponseCheckBox = new JCheckBox();
        ignoreResponseCheckBox.setText("Ignore response");
        gbc = new GridBagConstraints();
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
        panel2.setMinimumSize(new Dimension(506, 300));
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
        splitPane2.setBackground(new Color(-12828863));
        splitPane2.setContinuousLayout(false);
        splitPane2.setDividerLocation(502);
        splitPane2.setDividerSize(9);
        splitPane2.setEnabled(true);
        splitPane2.setMinimumSize(new Dimension(1139, 300));
        splitPane2.setOneTouchExpandable(false);
        panel3.add(splitPane2, BorderLayout.CENTER);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new BorderLayout(0, 0));
        panel4.setMinimumSize(new Dimension(502, 300));
        splitPane2.setLeftComponent(panel4);
        tabbedPane1 = new JTabbedPane();
        tabbedPane1.setTabPlacement(1);
        panel4.add(tabbedPane1, BorderLayout.CENTER);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridBagLayout());
        tabbedPane1.addTab("Input", panel5);
        inputTextArea = new JTextArea();
        inputTextArea.setLineWrap(true);
        inputTextArea.setMinimumSize(new Dimension(502, 300));
        inputTextArea.setWrapStyleWord(true);
        gbc = new GridBagConstraints();
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
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel7.add(outputTextArea, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }


}