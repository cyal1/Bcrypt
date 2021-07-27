package burp;

import javax.swing.*;
import java.awt.*;

class DefaultTab extends JPanel{
//    private  JPanel panel = new JPanel();
//
    public JPanel globalSettingPanel = new JPanel();
    public JPanel requestSettingPanel = new JPanel();
    public JPanel responseSettingPanel = new JPanel();
    public JPanel bottomTextFieldPanel = new JPanel();

    public DefaultTab() {
        this.setLayout(new GridLayout(2,1));
        JPanel topSettingPanel = new JPanel();
        topSettingPanel.setLayout(new GridLayout(1,3));
        // globalSettingPanel
        globalSettingPanel.setBorder(BorderFactory.createTitledBorder("global setting"));
        // requestSettingPanel
        requestSettingPanel.setBorder(BorderFactory.createTitledBorder("request setting"));
        // responseSettingPanel
        responseSettingPanel.setBorder(BorderFactory.createTitledBorder("response setting"));

        topSettingPanel.add(globalSettingPanel);
        topSettingPanel.add(requestSettingPanel);
        topSettingPanel.add(responseSettingPanel);

        this.add(topSettingPanel);
        // bottomTextFieldPanel
        bottomTextFieldPanel.add(new JTextField("Default TabPane"));

        this.add(bottomTextFieldPanel);
    }
}
