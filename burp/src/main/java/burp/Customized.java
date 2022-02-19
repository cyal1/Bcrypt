package burp;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.python.util.PythonInterpreter;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class Customized extends JPanel{
    private final JButton button1;
    private final RSyntaxTextArea textEditor;
    private final RSyntaxTextArea docsEditor;
    private String code;
    public static PythonInterpreter interpreter = new PythonInterpreter();
    public boolean start;
    private final String defaultCode= """
            # *_* coding: utf-8 *_*

            #           processProxyMessage                     processHttpMessage
            #
            # client -----------------------> burpSuit proxy ----------------------->  server
            #        <-----------------------                <-----------------------

            from burp import CryptUtils


            def aes_cbc_encode(key, plainText, iv):
                # TextArea on the right panel for more information.
                return helper.base64Encode(CryptUtils.AESEncrypt(1, "AES/CBC/PKCS5Padding", key, plainText, iv))

            def MyFilter(messageIsRequest, messageInfo):
                Host = messageInfo.getHttpService().getHost()
            #    if Host != "www.baidu.com":
            #        return
                if not messageIsRequest:
                     mime_type = helper.analyzeResponse(messageInfo.getResponse()).getInferredMimeType()
            #         print(mime_type)
                     if mime_type != "HTML" or mime_type != "JSON" or mime_type != "XML" or mime_type!= "script":
                        return\s
                    \s
            def processHttpMessage(messageIsRequest, messageInfo):
                MyFilter(messageIsRequest, messageInfo)
               \s
                if messageIsRequest:
                    # processHttpMessage request
                   \s
                    request = messageInfo.getRequest()
                    reqInfo = helper.analyzeRequest(request)
                    headers =  reqInfo.getHeaders()
                    parameters = reqInfo.getParameters()
                    body = request[reqInfo.getBodyOffset():]

                    # headers.add("processHttpMessageRequest: test")
                    # parameters.add(helper.buildParameter("debug","true", IParameter.PARAM_URL))

                    newMessage = helper.buildHttpMessage(headers, body)
                    messageInfo.setRequest(newMessage)

                else:
                    # processHttpMessage response

                    response = messageInfo.getResponse()
                    respInfo = helper.analyzeResponse(response)
                    statusCode = respInfo.getStatusCode()
                    headers = respInfo.getHeaders()
                    body = response[respInfo.getBodyOffset():] # []byte
            #        print body.tostring().decode("utf8")

            #        headers.add("processHttpMessageResponse: test")

                    newMessage = helper.buildHttpMessage(headers, body)
                    messageInfo.setResponse(newMessage)


            def processProxyMessage(messageIsRequest, messageInfo):
                MyFilter(messageIsRequest, messageInfo)
               \s
                if messageIsRequest:
                    # processProxyMessage request

                    request = messageInfo.getRequest()
                    reqInfo = helper.analyzeRequest(request)
                    headers =  reqInfo.getHeaders()
                    parameters = reqInfo.getParameters()
                    body = request[reqInfo.getBodyOffset():]

                    # headers.add("Test: test")
                    # parameters.add(helper.buildParameter("debug","true", IParameter.PARAM_URL))

                    newMessage = helper.buildHttpMessage(headers, body)
                    messageInfo.setRequest(newMessage)

                else:
                    # processProxyMessage response

                    response = messageInfo.getResponse()
                    respInfo = helper.analyzeResponse(response)
                    statusCode = respInfo.getStatusCode()
                    headers = respInfo.getHeaders()
                    body = response[respInfo.getBodyOffset():]

                    # headers.add("Test: test")

                    newMessage = helper.buildHttpMessage(headers, body)
                    messageInfo.setResponse(newMessage)


            #    # third-party pycrypto API for Jython
            #
            #    from base64 import b64decode,b64encode
            #    import sys
            #    # git clone https://github.com/csm/jycrypto.git # Jython 使用 Crypto 需要下载该项目
            #    # implementation of the pycrypto API for Jython
            #    # pycryptoLib = "/tmp/jycrypto/lib"
            #    # if pycryptoLib not in sys.path:
            #        # sys.path.append(pycryptoLib)
            #    # from Crypto.Cipher import AES
            #   \s
            #    def aes_decrypt_ecb(data, key):
            #       aes = AES.new(key, AES.MODE_ECB)
            #       decrypted_text = aes.decrypt(b64decode(data))
            #       decrypted_text = decrypted_text[:-ord(decrypted_text[-1])]
            #       return decrypted_text
            #   \s
            #    def aes_encrypt_ecb(data, key):
            #       while len(data) % 16 != 0:
            #           data += (16 - len(data) % 16) * chr(16 - len(data) % 16)
            #       data = str.encode(data)
            #       aes = AES.new(str.encode(key), AES.MODE_ECB)
            #       return str(base64.b64encode(aes.encrypt(data)))
            #   \s
            #    def aes_encrypt_cbc(data, key, iv):
            #        bs = AES.block_size
            #        pad = lambda s: s + (bs - len(s) % bs) * chr(bs - len(s) % bs)
            #        cipher = AES.new(key, AES.MODE_CBC, iv)
            #        data = cipher.encrypt(pad(data))
            #        return b64encode(data)
            #    \s
            #    def aes_decrypt_cbc(data, key, iv):
            #        decipher = AES.new(key, AES.MODE_CBC, iv)
            #        plaintext = decipher.decrypt(b64decode(data))
            #        return plaintext
            """;
    private final String defaultDocs = """
            # https://github.com/PortSwigger/example-event-listeners/blob/master/python/EventListeners.py

            # Notice

            ## To create a byte-compatible object to pass to the Burp Extender APIs:
            ```
                bytearray("foo") # => new byte[] {'f', 'o', 'o'}
            ```
            ## To convert an existing list to a Java array:
            ```
                from jarray import array
                array([1, 2, 3], 'i') # => new int[] {1, 2, 3}
                # 'i' is integer
            ```

            ## Useful java classes
            ```
            from burp import CryptUtils, BurpExtender

            // ECB Mode iv need be None
            // @MODE\tENCRYPT_MODE = 1; @DECRYPT_MODE = 2;
            // @alg
            //\tAES/CBC/PKCS5Padding
            //\tAES/ECB/PKCS5Padding
            //\tAES/CBC/NoPadding
            //\tAES/ECB/NoPadding

            byte[] CryptUtils.AESEncrypt(int MODE,String alg, byte[] key, byte[] plainText, byte[] iv)

            String BurpExtender.bytesToHex(byte[] b)
            byte[] BurpExtender.hexToBytes(String s)

            eg.
            \tCryptUtils.AESEncrypt(1, "AES/ECB/PKCS5Padding",b"aaaaaaaaaaaaaaaa",b'plainText',None)
            \tprint(BurpExtender.byteToHex(b'abc'))
            ```

            ## Look for more api docs.
             >> Burp Suite -> Extender -> APIs
            \s
            ---
            ** boolean  messageIsRequest **

            ** IHttpRequestResponse  messageInfo **
            *
                 byte[] getRequest();
                 byte[] getResponse();
                 String getComment();
                 void setRequest(byte[] message);
                 void setResponse(byte[] message);
            *
            ** IRequestInfo reqInfo **
            *
                static final byte CONTENT_TYPE_NONE = 0;
                static final byte CONTENT_TYPE_URL_ENCODED = 1;
                static final byte CONTENT_TYPE_MULTIPART = 2;
                static final byte CONTENT_TYPE_XML = 3;
                static final byte CONTENT_TYPE_JSON = 4;
                static final byte CONTENT_TYPE_AMF = 5;
                static final byte CONTENT_TYPE_UNKNOWN = -1;
                String getMethod();
                URL getUrl();
                List<String> getHeaders();
                List<IParameter> getParameters();
                int getBodyOffset();
                byte getContentType();
            *
            ** IResponseInfo respInfo **
            *
                List<String> getHeaders();
                int getBodyOffset();
                short getStatusCode();
                List<ICookie> getCookies();
                String getStatedMimeType();
                String getInferredMimeType();
            *
            ** IExtensionHelpers    helper **
            *
                String urlDecode(String data);
                String urlEncode(String data);
                byte[] urlDecode(byte[] data);
                byte[] urlEncode(byte[] data);
                byte[] base64Decode(String data);
                byte[] base64Decode(byte[] data);
                String base64Encode(String data);
                String base64Encode(byte[] data);
                byte[] stringToBytes(String data);
                String bytesToString(byte[] data);
                   \s
                IRequestInfo analyzeRequest(IHttpRequestResponse request);
                IRequestInfo analyzeRequest(IHttpService httpService, byte[] request);
                IRequestInfo analyzeRequest(byte[] request);
                IResponseInfo analyzeResponse(byte[] response);

                byte[] addParameter(byte[] request, IParameter parameter);
                byte[] removeParameter(byte[] request, IParameter parameter);
                byte[] updateParameter(byte[] request, IParameter parameter);
                IParameter buildParameter(String name, String value, byte type);
                IParameter getRequestParameter(byte[] request, String parameterName);
            *

            ** 处理中文 **
            *
            chinese_string = helper.urlDecode("%E7%99%BE%E5%BA%A6").decode("utf8"))
            *

            ** 笔记处 **
            print dir(OBJECT) # 查看方法

            """;


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
        textEditor.setAutoIndentEnabled(true); //
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
        if (!Objects.equals(myCode, "")){
            textEditor.setText(myCode);
        }else{
            textEditor.setText(this.defaultCode);
        }
        String docs = callbacks.loadExtensionSetting("docs");
        if(!Objects.equals(docs, "")){
            docsEditor.setText(docs);
        }else{

            docsEditor.setText(defaultDocs);
        }
        BurpExtender.stdout.println("Customized Configuration loaded.");
    }
}
