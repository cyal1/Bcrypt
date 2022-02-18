package burp;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.python.util.PythonInterpreter;

import javax.swing.*;
import java.awt.*;

public class Customized extends JPanel{
    private final JButton button1;
    private final RSyntaxTextArea textEditor;
    private final RSyntaxTextArea docsEditor;
    private String code;
    public static PythonInterpreter interpreter = new PythonInterpreter();
    public boolean start;
    private final String defaultCode="# coding:utf-8\n" +
            "\n" +
            "#           processProxyMessage                     processHttpMessage\n" +
            "#\n" +
            "# client -----------------------> burpSuit proxy ----------------------->  server\n" +
            "#        <-----------------------                <-----------------------\n" +
            "\n" +
            "from burp import CryptUtils\n" +
            "\n" +
            "def aes_cbc_encode(key, plainText, iv):\n" +
            "    # TextArea on the right panel for more information.\n" +
            "    return helper.base64Encode(CryptUtils.AESEncrypt(1, \"AES/CBC/PKCS5Padding\", key, plainText, iv))\n" +
            "\n" +
            "def processHttpMessage(messageIsRequest, messageInfo):\n" +
            "\n" +
            "    # Host = messageInfo.getHttpService().getHost()\n" +
            "\n" +
            "    # if Host != \"www.baidu.com\":\n" +
            "    #   return\n" +
            "\n" +
            "    if messageIsRequest:\n" +
            "        # processHttpMessage request\n" +
            "        return\n" +
            "        request = messageInfo.getRequest()\n" +
            "        reqInfo = helper.analyzeRequest(request)\n" +
            "\n" +
            "        headers =  reqInfo.getHeaders()\n" +
            "        parameters = reqInfo.getParameters()\n" +
            "        body = request[reqInfo.getBodyOffset():]\n" +
            "\n" +
            "        # headers.add(\"processHttpMessageRequest: test\")\n" +
            "        # parameters.add(helper.buildParameter(\"debug\",\"true\", IParameter.PARAM_URL))\n" +
            "\n" +
            "        newMessage = helper.buildHttpMessage(headers, body)\n" +
            "        messageInfo.setRequest(newMessage)\n" +
            "\n" +
            "    else:\n" +
            "        # processHttpMessage response\n" +
            "\n" +
            "        response = messageInfo.getResponse()\n" +
            "        respInfo = helper.analyzeResponse(response)\n" +
            "\n" +
            "        statusCode = respInfo.getStatusCode()\n" +
            "        headers = respInfo.getHeaders()\n" +
            "        body = response[respInfo.getBodyOffset():].tostring()\n" +
            "\n" +
            "        headers.add(\"processHttpMessageResponse: test\")\n" +
            "\n" +
            "        newMessage = helper.buildHttpMessage(headers, body)\n" +
            "        messageInfo.setResponse(newMessage)\n" +
            "\n" +
            "\n" +
            "def processProxyMessage(messageIsRequest, messageInfo):\n" +
            "\n" +
            "    return\n" +
            "\n" +
            "    # Host = messageInfo.getHttpService().getHost()\n" +
            "\n" +
            "    # if Host != \"www.baidu.com\":\n" +
            "    #   return\n" +
            "\n" +
            "    if messageIsRequest:\n" +
            "        # processProxyMessage request\n" +
            "\n" +
            "        request = messageInfo.getRequest()\n" +
            "        reqInfo = helper.analyzeRequest(request)\n" +
            "\n" +
            "        headers =  reqInfo.getHeaders()\n" +
            "        parameters = reqInfo.getParameters()\n" +
            "        body = request[reqInfo.getBodyOffset():]\n" +
            "\n" +
            "        # headers.add(\"Test: test\")\n" +
            "        # parameters.add(helper.buildParameter(\"debug\",\"true\", IParameter.PARAM_URL))\n" +
            "\n" +
            "        newMessage = helper.buildHttpMessage(headers, body)\n" +
            "        messageInfo.setRequest(newMessage)\n" +
            "\n" +
            "    else:\n" +
            "        # processProxyMessage response\n" +
            "\n" +
            "        response = messageInfo.getResponse()\n" +
            "        respInfo = helper.analyzeResponse(response)\n" +
            "\n" +
            "        statusCode = respInfo.getStatusCode()\n" +
            "        headers = respInfo.getHeaders()\n" +
            "        body = response[respInfo.getBodyOffset():].tostring()\n" +
            "\n" +
            "        # headers.add(\"Test: test\")\n" +
            "\n" +
            "        newMessage = helper.buildHttpMessage(headers, body)\n" +
            "        messageInfo.setResponse(newMessage)\n" +
            "\n" +
            "\n" +
            "#    # third-party pycrypto API for Jython\n" +
            "#\n" +
            "#    from base64 import b64decode,b64encode\n" +
            "#    import sys\n" +
            "#    # git clone https://github.com/csm/jycrypto.git\n" +
            "#    # implementation of the pycrypto API for Jython\n" +
            "#    pycryptoLib = \"/tmp/jycrypto/lib\"\n" +
            "#    if pycryptoLib not in sys.path:\n" +
            "#        sys.path.append(pycryptoLib)\n" +
            "#    from Crypto.Cipher import AES\n" +
            "#    \n" +
            "#    def aes_decrypt_ecb(data, key):\n" +
            "#       aes = AES.new(key, AES.MODE_ECB)\n" +
            "#       decrypted_text = aes.decrypt(b64decode(data))\n" +
            "#       decrypted_text = decrypted_text[:-ord(decrypted_text[-1])]\n" +
            "#       return decrypted_text\n" +
            "#    \n" +
            "#    def aes_encrypt_ecb(data, key):\n" +
            "#       while len(data) % 16 != 0:\n" +
            "#           data += (16 - len(data) % 16) * chr(16 - len(data) % 16)\n" +
            "#       data = str.encode(data)\n" +
            "#       aes = AES.new(str.encode(key), AES.MODE_ECB)\n" +
            "#       return str(base64.b64encode(aes.encrypt(data)))\n" +
            "#    \n" +
            "#    def aes_encrypt_cbc(data, key, iv):\n" +
            "#        bs = AES.block_size\n" +
            "#        pad = lambda s: s + (bs - len(s) % bs) * chr(bs - len(s) % bs)\n" +
            "#        cipher = AES.new(key, AES.MODE_CBC, iv)\n" +
            "#        data = cipher.encrypt(pad(data))\n" +
            "#        return b64encode(data)\n" +
            "#     \n" +
            "#    def aes_decrypt_cbc(data, key, iv):\n" +
            "#        decipher = AES.new(key, AES.MODE_CBC, iv)\n" +
            "#        plaintext = decipher.decrypt(b64decode(data))\n" +
            "#        return plaintext\n";


    public Customized() {
        interpreter.setOut(BurpExtender.stdout);
        interpreter.setErr(BurpExtender.stderr);
        interpreter.set("helper",BurpExtender.helpers);
        this.setLayout(new BorderLayout(0, 2));
        // https://github.com/bobbylight/RSyntaxTextArea/issues/269
        javax.swing.text.JTextComponent.removeKeymap("RTextAreaKeymap");
        javax.swing.UIManager.put("RTextAreaUI.inputMap", null);
        javax.swing.UIManager.put("RTextAreaUI.actionMap", null);
        javax.swing.UIManager.put("RSyntaxTextAreaUI.inputMap", null);
        javax.swing.UIManager.put("RSyntaxTextAreaUI.actionMap", null);
        textEditor = new RSyntaxTextArea();
        textEditor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PYTHON);
        textEditor.setAntiAliasingEnabled(true);
        textEditor.isAutoIndentEnabled();
        textEditor.setPaintTabLines(true);
        textEditor.setTabSize(4);
        textEditor.setTabsEmulated(true);
        textEditor.setHighlightCurrentLine(true);
        RTextScrollPane ts = new RTextScrollPane( textEditor );
        this.add(ts, BorderLayout.CENTER);
        docsEditor = new RSyntaxTextArea(0,60);
        docsEditor.setLineWrap(true);
        docsEditor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_MARKDOWN);
        RTextScrollPane rsp = new RTextScrollPane(docsEditor);
        this.add(rsp, BorderLayout.EAST);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        this.add(panel2, BorderLayout.SOUTH);
        button1 = new JButton();
        button1.setText("Load Default Code");
        panel2.add(button1);
        JButton button2 = new JButton();
        button2.setText("Start");
        panel2.add(button2);
        final JLabel label1 = new JLabel();
        label1.setText("");
        label1.setFont(new Font("Serif",Font.BOLD,14));
        panel2.add(label1);

        button1.addActionListener(e -> {
            textEditor.setText(this.defaultCode);
        });

        button2.addActionListener(e -> {
            if (button2.getText().equals("Start")){

                this.code = textEditor.getText().replace("\r\n", "\n").replace("\n", "\r\n");
                try{
                    interpreter.exec(this.code);
                }catch (Exception ex){
                    JOptionPane.showMessageDialog(new JPanel(),ex.getMessage(),"Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                textEditor.setEditable(false);
                textEditor.setBackground(Color.lightGray);
                button2.setText("Stop");
                label1.setText("Running");
                label1.setForeground(SystemColor.CYAN);
                button1.setEnabled(false);
                this.start = true;
            }else{
                button2.setText("Start");
                label1.setText("Stopped");
                label1.setForeground(Color.red);
                textEditor.setBackground(Color.white);
                textEditor.setEditable(true);
                button1.setEnabled(true);
                this.start = false;
            }
        });
    }
    public void saveConfig(IBurpExtenderCallbacks callbacks){
        callbacks.saveExtensionSetting("code", textEditor.getText());
        callbacks.saveExtensionSetting("docs", docsEditor.getText());
    }

    public void loadConfig(IBurpExtenderCallbacks callbacks){
        String myCode = callbacks.loadExtensionSetting("code");
        if(myCode != null){
            textEditor.setText(myCode);
        }else{
            textEditor.setText(defaultCode);
        }
        String docs = callbacks.loadExtensionSetting("docs");
        if(docs != null){
            docsEditor.setText(docs);
        }else{
            String docs1 = "# https://github.com/PortSwigger/example-event-listeners/blob/master/python/EventListeners.py\n" +
                    "\n" +
                    "# Notice\n" +
                    "\n" +
                    "## To create a byte-compatible object to pass to the Burp Extender APIs:\n" +
                    "```\n" +
                    "    bytearray(\"foo\") # => new byte[] {'f', 'o', 'o'}\n" +
                    "```\n" +
                    "## To convert an existing list to a Java array:\n" +
                    "```\n" +
                    "    from jarray import array\n" +
                    "    array([1, 2, 3], 'i') # => new int[] {1, 2, 3}\n" +
                    "    # 'i' is integer\n" +
                    "```\n" +
                    "\n" +
                    "## Useful java classes\n" +
                    "```\n" +
                    "from burp import CryptUtils, BurpExtender\n" +
                    "\n" +
                    "// ECB Mode iv need be None\n" +
                    "// @MODE\tENCRYPT_MODE = 1; @DECRYPT_MODE = 2;\n" +
                    "// @alg\n" +
                    "//\tAES/CBC/PKCS5Padding\n" +
                    "//\tAES/ECB/PKCS5Padding\n" +
                    "//\tAES/CBC/NoPadding\n" +
                    "//\tAES/ECB/NoPadding\n" +
                    "\n" +
                    "byte[] CryptUtils.AESEncrypt(int MODE,String alg, byte[] key, byte[] plainText, byte[] iv)\n" +
                    "\n" +
                    "String BurpExtender.bytesToHex(byte[] b)\n" +
                    "byte[] BurpExtender.hexToBytes(String s)\n" +
                    "\n" +
                    "eg.\n" +
                    "\tCryptUtils.AESEncrypt(1, \"AES/ECB/PKCS5Padding\",b\"aaaaaaaaaaaaaaaa\",b'plainText',None)\n" +
                    "\tprint(BurpExtender.byteToHex(b'abc'))\n" +
                    "```\n" +
                    "\n" +
                    "## Look for more api docs.\n" +
                    " >> Burp Suite -> Extender -> APIs\n" +
                    " \n" +
                    "---\n" +
                    "** boolean  messageIsRequest **\n" +
                    "\n" +
                    "** IHttpRequestResponse  messageInfo **\n" +
                    "*\n" +
                    "     byte[] getRequest();\n" +
                    "     byte[] getResponse();\n" +
                    "     String getComment();\n" +
                    "     void setRequest(byte[] message);\n" +
                    "     void setResponse(byte[] message);\n" +
                    "*\n" +
                    "** IRequestInfo reqInfo **\n" +
                    "*\n" +
                    "    static final byte CONTENT_TYPE_NONE = 0;\n" +
                    "    static final byte CONTENT_TYPE_URL_ENCODED = 1;\n" +
                    "    static final byte CONTENT_TYPE_MULTIPART = 2;\n" +
                    "    static final byte CONTENT_TYPE_XML = 3;\n" +
                    "    static final byte CONTENT_TYPE_JSON = 4;\n" +
                    "    static final byte CONTENT_TYPE_AMF = 5;\n" +
                    "    static final byte CONTENT_TYPE_UNKNOWN = -1;\n" +
                    "    String getMethod();\n" +
                    "    URL getUrl();\n" +
                    "    List<String> getHeaders();\n" +
                    "    List<IParameter> getParameters();\n" +
                    "    int getBodyOffset();\n" +
                    "    byte getContentType();\n" +
                    "*\n" +
                    "** IResponseInfo respInfo **\n" +
                    "*\n" +
                    "    List<String> getHeaders();\n" +
                    "    int getBodyOffset();\n" +
                    "    short getStatusCode();\n" +
                    "    List<ICookie> getCookies();\n" +
                    "    String getStatedMimeType();\n" +
                    "    String getInferredMimeType();\n" +
                    "*\n" +
                    "** IExtensionHelpers    helper **\n" +
                    "*\n" +
                    "    String urlDecode(String data);\n" +
                    "    String urlEncode(String data);\n" +
                    "    byte[] urlDecode(byte[] data);\n" +
                    "    byte[] urlEncode(byte[] data);\n" +
                    "    byte[] base64Decode(String data);\n" +
                    "    byte[] base64Decode(byte[] data);\n" +
                    "    String base64Encode(String data);\n" +
                    "    String base64Encode(byte[] data);\n" +
                    "    byte[] stringToBytes(String data);\n" +
                    "    String bytesToString(byte[] data);\n" +
                    "        \n" +
                    "    IRequestInfo analyzeRequest(IHttpRequestResponse request);\n" +
                    "    IRequestInfo analyzeRequest(IHttpService httpService, byte[] request);\n" +
                    "    IRequestInfo analyzeRequest(byte[] request);\n" +
                    "    IResponseInfo analyzeResponse(byte[] response);\n" +
                    "\n" +
                    "    byte[] addParameter(byte[] request, IParameter parameter);\n" +
                    "    byte[] removeParameter(byte[] request, IParameter parameter);\n" +
                    "    byte[] updateParameter(byte[] request, IParameter parameter);\n" +
                    "    IParameter buildParameter(String name, String value, byte type);\n" +
                    "    IParameter getRequestParameter(byte[] request, String parameterName);\n" +
                    "*   \n" +
                    "---\n";
            docsEditor.setText(docs1);
        }
    }
}
