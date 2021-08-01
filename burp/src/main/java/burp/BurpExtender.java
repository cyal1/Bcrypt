package burp;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class BurpExtender implements IBurpExtender,ITab,IContextMenuFactory,IExtensionStateListener,IHttpListener,IProxyListener{
    private JTabbedPane tabPane;
    private Send2Xray send2xray;
    private IBurpExtenderCallbacks callbacks;
    private AES_UI aesTab;

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
        this.aesTab = new AES_UI();

        this.tabPane.addTab("AES", aesTab);
        this.tabPane.addTab("Send2Xray", send2xray);

        callbacks.addSuiteTab(BurpExtender.this);
        callbacks.registerContextMenuFactory(this);
        callbacks.registerExtensionStateListener(this);
        callbacks.registerHttpListener(this);
        callbacks.registerProxyListener(this);
        this.send2xray.loadConfig(this.callbacks);
        this.aesTab.loadConfig(this.callbacks);

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
        this.aesTab.saveConfig(this.callbacks);
        this.send2xray.saveConfig(this.callbacks);
    }

    @Override
    public void processHttpMessage(int toolFlag, boolean messageIsRequest, IHttpRequestResponse messageInfo) {
        if (!aesTab.start){
            return;
        }
        String reqHost = messageInfo.getHttpService().getHost();
        if (messageIsRequest){
           if (!reqHost.equals(this.aesTab.targetHost)) {return;}

           IRequestInfo reqInfo = helpers.analyzeRequest(messageInfo);
           List<String> headers = reqInfo.getHeaders();
           if(this.aesTab.requestOption == AES_UI.COMPLETE_BODY){
               // encode body
               byte[] tmpreq = messageInfo.getRequest();
               byte[] messageBody;
               try {
                   messageBody = CryptUtils.AESEncrypt(Cipher.ENCRYPT_MODE, aesTab.alg, aesTab.secretKey, Arrays.copyOfRange(tmpreq, reqInfo.getBodyOffset(), tmpreq.length), aesTab.iv);
               } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
                   stderr.println("processHttpMessage encrypt requests body error: " + e);
                   return;
               }
               byte[] updateMessage = helpers.buildHttpMessage(headers, encoder(aesTab.responseCipherFormat, messageBody).getBytes(StandardCharsets.UTF_8));
               messageInfo.setRequest(updateMessage);
           }else{
               // encode param
               byte[] _request = messageInfo.getRequest();
               try {
                   if(reqInfo.getContentType() == IRequestInfo.CONTENT_TYPE_JSON){
                           _request = update_req_params_json(_request, headers, aesTab.requestParams, true);
                   }else{
                       _request = update_req_params(_request, headers, aesTab.requestParams, true);
                   }
               } catch (InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e) {
                   stderr.println("processHttpMessage encrypt requests param error: " + e);
                   return;
               }
               messageInfo.setRequest(_request);
           }
        }else{
            if(aesTab.ignoreResponse){return;}
            //response decode
            IRequestInfo reqInfo = helpers.analyzeRequest(messageInfo);
            IResponseInfo resInfo = helpers.analyzeResponse(messageInfo.getResponse());
//            String URL = reqInfo.getUrl().toString();
            List<String> headers = resInfo.getHeaders();
            if (reqHost.equals(this.aesTab.targetHost)){
                if(aesTab.requestOption == AES_UI.COMPLETE_BODY){
                    // Complete Response Body decryption
                    byte[] tmpreq = messageInfo.getResponse();
//                    String messageBody = tmpreq.substring(resInfo.getBodyOffset()).trim();
                    byte[] messageBody;
                    byte[] cipherText = decoder(aesTab.responseCipherFormat, new String(Arrays.copyOfRange(tmpreq, resInfo.getBodyOffset(), tmpreq.length)));
                    try {
                        messageBody = CryptUtils.AESEncrypt(Cipher.DECRYPT_MODE,aesTab.alg, aesTab.secretKey, cipherText, aesTab.iv);
                    } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
                        stderr.println("processHttpMessage decrypt response body error: " + e);
                        return;
                    }
                    byte[] updateMessage = helpers.buildHttpMessage(headers, messageBody);
                    messageInfo.setResponse(updateMessage);
                }
                else if(this.aesTab.responseOption == AES_UI.URL_BODY_PARAM){ // TODO burp '+' to ' '
                    byte[] _response = messageInfo.getResponse();
//                    byte[] _request = messageInfo.getRequest();
                    // TODO if json
                    try {
                        _response = this.update_req_params_json(_response, headers, aesTab.requestParams, false);
                    } catch (InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e) {
                        stderr.println("processHttpMessage decrypt response params error: " + e);
                        return;
                    }
                    messageInfo.setResponse(_response);
                }

            }
        }
    }

    @Override
    public void processProxyMessage(boolean messageIsRequest, IInterceptedProxyMessage message) {
        if (!aesTab.start){
            return;
        }
        String reqHost = message.getMessageInfo().getHttpService().getHost();
        if (messageIsRequest){
            if (!reqHost.equals(this.aesTab.targetHost)) {return;}
            IRequestInfo reqInfo = helpers.analyzeRequest(message.getMessageInfo());
            IHttpRequestResponse messageInfo =  message.getMessageInfo();
            List<String> headers = reqInfo.getHeaders();
            if(this.aesTab.requestOption == AES_UI.COMPLETE_BODY){
                // decode body
                byte[] tmpreq = message.getMessageInfo().getRequest();
                byte[] messageBody;
                byte[] cipherText = decoder(aesTab.requestCipherFormat,new String(Arrays.copyOfRange(tmpreq, reqInfo.getBodyOffset(),tmpreq.length)));
                try {
                    messageBody = CryptUtils.AESEncrypt(Cipher.DECRYPT_MODE, aesTab.alg, aesTab.secretKey, cipherText, aesTab.iv);
                } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
                    stderr.println("processProxyMessage decrypt request body error: " + e);
                    return;
                }
                byte[] updateMessage = helpers.buildHttpMessage(headers, messageBody);
                message.getMessageInfo().setRequest(updateMessage);
            }else{
                byte[] request = messageInfo.getRequest();
                byte[] _request;
                try {
                    if(reqInfo.getContentType() == IRequestInfo.CONTENT_TYPE_JSON){
                        _request = update_req_params_json(request, headers, aesTab.requestParams ,false);
                    }else{
                        stdout.println("params to decrypt: " + Arrays.toString(aesTab.requestParams));
                        _request = update_req_params(request, headers, aesTab.requestParams, false);
                    }
                } catch (InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e) {
                    stderr.println("processProxyMessage decrypt request params error: " + e);
                    return;
                }
                messageInfo.setRequest(_request);
            }
        }else{
            //response decode
            if(aesTab.ignoreResponse) { return; }
            // PPM Response
            IHttpRequestResponse messageInfo = message.getMessageInfo();
            IRequestInfo reqInfo = helpers.analyzeRequest(messageInfo);
            IResponseInfo resInfo = helpers.analyzeResponse(messageInfo.getResponse());
//            String URL = reqInfo.getUrl().toString();
            List<String> headers = resInfo.getHeaders();
            if (!reqHost.equals(this.aesTab.targetHost)) {return;}
                if(aesTab.responseOption == AES_UI.COMPLETE_BODY){
                    // Complete Response Body decrypt
                    byte[] tmpreq = messageInfo.getResponse();
//                    String messageBody = tmpreq.substring(resInfo.getBodyOffset()).trim();
                    byte[] messageBody;
                    try {
                        messageBody = CryptUtils.AESEncrypt(Cipher.ENCRYPT_MODE, aesTab.alg, aesTab.secretKey, Arrays.copyOfRange(tmpreq, resInfo.getBodyOffset(), tmpreq.length), aesTab.iv);
                    } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
                        stderr.println("processProxyMessage encrypt response body error: " + e);
                        return;
                    }
                    byte[] updateMessage = helpers.buildHttpMessage(headers, encoder(aesTab.responseCipherFormat,messageBody).getBytes(StandardCharsets.UTF_8));
                    messageInfo.setResponse(updateMessage);
                }
                else if(aesTab.responseOption == AES_UI.URL_BODY_PARAM){
                    byte[] _response = messageInfo.getResponse();
                    // TODO if json
                    try {
                        _response = this.update_req_params_json(_response, headers, aesTab.responseParams, true);
                    } catch (InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e) {
                        stderr.println("processProxyMessage encrypt response params  error: " + e);
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
                _str = CryptUtils.AESEncrypt(Cipher.ENCRYPT_MODE, aesTab.alg, aesTab.secretKey, _p.getValue().trim().getBytes(StandardCharsets.UTF_8), aesTab.iv);

                _str = encoder(aesTab.responseCipherFormat, _str).getBytes(StandardCharsets.UTF_8);
                if(aesTab.responseURLEncode){
                    _str = helpers.urlEncode(_str);
                }
            } else {
                byte[] cipherText = decoder(aesTab.requestCipherFormat, _p.getValue().trim());
                if(aesTab.requestURLEncode){
                    cipherText = helpers.urlEncode(cipherText);
                }
                _str = CryptUtils.AESEncrypt(Cipher.DECRYPT_MODE, aesTab.alg, aesTab.secretKey, cipherText, aesTab.iv);
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
                _str = CryptUtils.AESEncrypt(Cipher.ENCRYPT_MODE, aesTab.alg, aesTab.secretKey, _p.getValue().trim().getBytes(StandardCharsets.UTF_8), aesTab.iv);
                _str = encoder(aesTab.requestCipherFormat, _str).getBytes(StandardCharsets.UTF_8);
            } else {
                _str = CryptUtils.AESEncrypt(Cipher.DECRYPT_MODE, aesTab.alg, aesTab.secretKey, decoder(aesTab.requestCipherFormat,_p.getValue().trim()), aesTab.iv);
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

    public static byte[] decoder(int cipherTextFormat, String s) {
        if (cipherTextFormat == AES_UI.BASE64) {
//            return BurpExtender.helpers.base64Decode(s);
            return Base64.getDecoder().decode(s);
        }
        if (cipherTextFormat == AES_UI.HEX) {
            return hexToByte(s);
        }
        // bytes
        return s.getBytes(StandardCharsets.UTF_8);
    }

    public static String encoder(int cipherTextFormat, byte[] b) {
        if (cipherTextFormat == AES_UI.BASE64) {
//            return BurpExtender.helpers.base64Encode(b);
            return Base64.getEncoder().encodeToString(b);
        }
        if (cipherTextFormat == AES_UI.HEX) {
            return byteToHex(b);
        }
        // bytes
        return new String(b);
    }

    public static String byteToHex(byte[] b) {
        return String.format("%x", new BigInteger(1, b));
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
}
