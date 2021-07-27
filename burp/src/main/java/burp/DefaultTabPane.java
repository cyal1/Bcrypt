package burp;


import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;


import javax.crypto.Cipher;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.charset.StandardCharsets;
import java.util.Objects;


public class DefaultTabPane extends Component {
    private JPanel mainPanel;
    private JPanel topPanel;
    private JTextField ivTextField;
    private JComboBox algComboBox;
    private JTextField secretKey;
    private JTextField reqSepParamText;
    private JCheckBox reqURLCheckBox;
    private JTextField respSepParam;
    private JCheckBox respURLCheckBox;


    private JPanel respSettingPanel;
    private JPanel reqSettingPanel;
    private JPanel globalSettingPanel;
    private JRadioButton reqBodyBtn;
    private JRadioButton reqParamBtn;
    private JRadioButton respBodyBtn;
    private JRadioButton respParamBtn;
    private JTextField textField1;
    private JButton startButton;
    private JButton encryptButton;
    private JButton decryptButton;
    private JButton clearButton;
    private JCheckBox URLEncodeDecodeCheckBox;
    private JTabbedPane tabbedPane1;
    private JTabbedPane tabbedPane2;
    private JTextArea inputTextArea;
    private JTextArea outputTextArea;


    public DefaultTabPane() {
        encryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                byte[] secret = new byte[16];
                System.arraycopy(secretKey.getText().getBytes(StandardCharsets.UTF_8), 0, secret, 0, 16);
                String text = inputTextArea.getText();
                String alg = Objects.requireNonNull(algComboBox.getSelectedItem()).toString();
                String mode = alg.split("/")[1];
                String pad = alg.split("/")[2];
                byte[] iv = null;
                if (pad.equals("NoPadding") && text.length() % 16 != 0) {
                    outputTextArea.setText("NoPadding required plain text length must be multiple of 16 bytes");
                    return;
                }
                if (!mode.equals("ECB")) {
                    iv = ivTextField.getText().getBytes(StandardCharsets.UTF_8);
                    if (iv.length != 16) {
                        outputTextArea.setText("IV length must be 16 bytes long");
                        return;
                    }
                }
                String encryptedText = CryptUtils.AESEncrypt(Cipher.ENCRYPT_MODE, algComboBox.getSelectedItem().toString(), secret, text, iv);
                outputTextArea.setText(encryptedText);
            }
        });
        decryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                byte[] secret = new byte[16];
                System.arraycopy(secretKey.getText().getBytes(StandardCharsets.UTF_8), 0, secret, 0, 16);
                String text = inputTextArea.getText();
                String alg = Objects.requireNonNull(algComboBox.getSelectedItem()).toString();
                String mode = alg.split("/")[1];
                byte[] iv = null;
                if (!mode.equals("ECB")) {
                    iv = ivTextField.getText().getBytes(StandardCharsets.UTF_8);
                    if (iv.length != 16) {
                        outputTextArea.setText("IV length must be 16 bytes long");
                        return;
                    }
                }
                String plainText = CryptUtils.AESEncrypt(Cipher.DECRYPT_MODE, alg, secret, text, iv);
                outputTextArea.setText(plainText);
            }
        });
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inputTextArea.setText("");
            }
        });
        algComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String alg = Objects.requireNonNull(algComboBox.getSelectedItem()).toString();
                String mode = alg.split("/")[1];
                if (mode.equals("ECB")) {
                    ivTextField.setBackground(Color.lightGray);
                    ivTextField.setEnabled(false);
                } else {
                    ivTextField.setBackground(Color.white);
                    ivTextField.setEnabled(true);
                }
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
        globalSettingPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "golobal setting", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.BELOW_TOP, null, null));
        final JLabel label1 = new JLabel();
        label1.setText("type");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        globalSettingPanel.add(label1, gbc);
        final JLabel label2 = new JLabel();
        label2.setText("key");
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
        secretKey = new JTextField();
        secretKey.setText("UFlZUEBLRkBISEZa");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        globalSettingPanel.add(secretKey, gbc);
        textField1 = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        globalSettingPanel.add(textField1, gbc);
        final JLabel label4 = new JLabel();
        label4.setText("Traget Host");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        globalSettingPanel.add(label4, gbc);
        startButton = new JButton();
        startButton.setText("Start");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        globalSettingPanel.add(startButton, gbc);
        reqSettingPanel = new JPanel();
        reqSettingPanel.setLayout(new GridBagLayout());
        topPanel.add(reqSettingPanel);
        reqSettingPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "request setting", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.BELOW_TOP, null, null));
        reqURLCheckBox = new JCheckBox();
        reqURLCheckBox.setInheritsPopupMenu(false);
        reqURLCheckBox.setSelected(false);
        reqURLCheckBox.setText("URL Decode/Encode");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        reqSettingPanel.add(reqURLCheckBox, gbc);
        reqSepParamText = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        reqSettingPanel.add(reqSepParamText, gbc);
        reqParamBtn = new JRadioButton();
        reqParamBtn.setName("reqParam");
        reqParamBtn.setSelected(false);
        reqParamBtn.setText("Specific request parameters (separated with space)");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        reqSettingPanel.add(reqParamBtn, gbc);
        reqBodyBtn = new JRadioButton();
        reqBodyBtn.setName("reqParam");
        reqBodyBtn.setText("Complete request body");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        reqSettingPanel.add(reqBodyBtn, gbc);
        respSettingPanel = new JPanel();
        respSettingPanel.setLayout(new GridBagLayout());
        topPanel.add(respSettingPanel);
        respSettingPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "response setting", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.BELOW_TOP, null, null));
        respSepParam = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        respSettingPanel.add(respSepParam, gbc);
        respURLCheckBox = new JCheckBox();
        respURLCheckBox.setText("URL Decode/Encode");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        respSettingPanel.add(respURLCheckBox, gbc);
        respBodyBtn = new JRadioButton();
        respBodyBtn.setText("Complete response body");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        respSettingPanel.add(respBodyBtn, gbc);
        respParamBtn = new JRadioButton();
        respParamBtn.setText("Specific response parameters (separated with space)");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        respSettingPanel.add(respParamBtn, gbc);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new BorderLayout(0, 0));
        splitPane1.setRightComponent(panel1);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
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
        URLEncodeDecodeCheckBox = new JCheckBox();
        URLEncodeDecodeCheckBox.setText("URL Encode/Decode");
        panel2.add(URLEncodeDecodeCheckBox);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new BorderLayout(0, 0));
        panel1.add(panel3, BorderLayout.CENTER);
        final JSplitPane splitPane2 = new JSplitPane();
        splitPane2.setContinuousLayout(false);
        splitPane2.setDividerLocation(498);
        splitPane2.setDividerSize(8);
        splitPane2.setEnabled(true);
        splitPane2.setOneTouchExpandable(false);
        panel3.add(splitPane2, BorderLayout.CENTER);
        tabbedPane1 = new JTabbedPane();
        splitPane2.setLeftComponent(tabbedPane1);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Input", panel4);
        inputTextArea = new JTextArea();
        panel4.add(inputTextArea, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        tabbedPane2 = new JTabbedPane();
        splitPane2.setRightComponent(tabbedPane2);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane2.addTab("Output", panel5);
        outputTextArea = new JTextArea();
        panel5.add(outputTextArea, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}
