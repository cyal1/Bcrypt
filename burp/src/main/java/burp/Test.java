package burp;

import javax.swing.*;
import java.awt.*;
import org.python.util.PythonInterpreter;
import org.fife.ui.rsyntaxtextarea.*;
import org.fife.ui.rtextarea.RTextScrollPane;


public class Test {
     public static String defaultCode = "from burp import IBurpExtender\n" +
            "from burp import IHttpListener\n" +
            "from burp import IProxyListener\n" +
            "from java.io import PrintWriter\n" +
            "\n" +
            "class BurpExtender(IBurpExtender, IHttpListener, IProxyListener):\n" +
            "    \n" +
            "    #\n" +
            "    # implement IBurpExtender\n" +
            "    #\n" +
            "    \n" +
            "    def\tregisterExtenderCallbacks(self, callbacks):\n" +
            "        # keep a reference to our callbacks object\n" +
            "        self._callbacks = callbacks\n" +
            "        \n" +
            "        # set our extension name\n" +
            "        # callbacks.setExtensionName(\"Event listeners\")\n" +
            "        \n" +
            "        # obtain our output stream\n" +
            "        self._stdout = PrintWriter(callbacks.getStdout(), True)\n" +
            "\n" +
            "        # register ourselves as an HTTP listener\n" +
            "        callbacks.registerHttpListener(self)\n" +
            "        \n" +
            "        # register ourselves as a Proxy listener\n" +
            "        callbacks.registerProxyListener(self)\n" +
            "    #\n" +
            "    #\n" +
            "    # implement IHttpListener\n" +
            "    #\n" +
            "\n" +
            "    def processHttpMessage(self, toolFlag, messageIsRequest, messageInfo):\n" +
            "        self._stdout.println(\n" +
            "                (\"HTTP request to \" if messageIsRequest else \"HTTP response from \") +\n" +
            "                messageInfo.getHttpService().toString() +\n" +
            "                \" [\" + self._callbacks.getToolName(toolFlag) + \"]\")\n" +
            "\n" +
            "    #\n" +
            "    # implement IProxyListener\n" +
            "    #\n" +
            "\n" +
            "    def processProxyMessage(self, messageIsRequest, message):\n" +
            "        self._stdout.println(\n" +
            "                (\"Proxy request to \" if messageIsRequest else \"Proxy response from \") +\n" +
            "                message.getMessageInfo().getHttpService().toString())\n";
    public static void main(String[] args) {
//        CustomPy cp = new CustomPy();
        PythonInterpreter pyInterp = new PythonInterpreter();
//        pyInterp.set("wordlists", Wordlist(Bruteforce(), Utils.witnessedWords.savedWords, Utils.getClipboard()))
//        pyInterp.set("handler", handler)
//        pyInterp.set("outputHandler", outputHandler)
//        pyInterp.set("table", outputHandler)
//
//        pyInterp.exec("queueRequests(target, wordlists)");
        EventQueue.invokeLater(() ->{
            JFrame jf = new JFrame();
            RSyntaxTextArea textEditor = new RSyntaxTextArea();
            textEditor.setEditable(true);
            textEditor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PYTHON);
            textEditor.setAntiAliasingEnabled(true);
            textEditor.isAutoIndentEnabled();
            textEditor.setPaintTabLines(true);
            textEditor.setTabSize(4);
            textEditor.setTabsEmulated(true);
            textEditor.setHighlightCurrentLine(true);
            textEditor.setText(defaultCode);
            final JPanel panel1 = new JPanel();
            panel1.setLayout(new BorderLayout(0, 0));
            panel1.add(new RTextScrollPane( textEditor ), BorderLayout.CENTER);
            final JPanel panel2 = new JPanel();
            panel2.setLayout(new GridBagLayout());
            panel1.add(panel2, BorderLayout.SOUTH);
            JButton button1 = new JButton();
            button1.setText("Button");
            GridBagConstraints gbc;
            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            panel2.add(button1, gbc);
            final JPanel spacer1 = new JPanel();
            gbc = new GridBagConstraints();
            gbc.gridx = 1;
            gbc.gridy = 0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            panel2.add(spacer1, gbc);
            final JPanel spacer2 = new JPanel();
            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.fill = GridBagConstraints.VERTICAL;
            panel2.add(spacer2, gbc);
            jf.add(panel1);
            jf.setMinimumSize(new Dimension(1000,1000));
            jf.setVisible(true);

            button1.addActionListener(e -> {
                String code = textEditor.getText();
                code = code.replace("\r\n", "\n");
                code = code.replace("\n", "\r\n");
                pyInterp.exec(code);
            });
        });
    }
}
