package burp;

import javax.script.ScriptException;
import org.openjdk.nashorn.api.scripting.JSObject;
import org.w3c.dom.*;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.XML;
import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ConvertContentType implements IContextMenuFactory {

    @Override
    public List<JMenuItem> createMenuItems(IContextMenuInvocation invocation) {
        ArrayList<JMenuItem> menuList = new ArrayList<>();
        if (invocation != null && invocation.getSelectedMessages() != null && invocation.getSelectedMessages()[0] != null && invocation.getSelectedMessages()[0].getHttpService() != null) {
            IHttpRequestResponse httpInfo = invocation.getSelectedMessages()[0];
            byte[] reqBin = httpInfo.getRequest();

            JMenuItem convert2xml = new JMenuItem("Convert to XML");
            convert2xml.addActionListener(e -> {
                try {
                    invocation.getSelectedMessages()[0].setRequest(convertToXML(BurpExtender.helpers, reqBin));
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            });
            JMenuItem convert2json = new JMenuItem("Convert to JSON");
            convert2json.addActionListener(e -> invocation.getSelectedMessages()[0].setRequest(convertToJSON(BurpExtender.helpers, reqBin)));

            JMenuItem convert2queryString = new JMenuItem("Convert to Query String");
            convert2queryString.addActionListener(e -> {
                try {
                    invocation.getSelectedMessages()[0].setRequest(convertToQueryString(BurpExtender.helpers, reqBin));
                } catch (UnsupportedEncodingException unsupportedEncodingException) {
                    unsupportedEncodingException.printStackTrace();
                }
            });
            menuList.add(convert2xml);
            menuList.add(convert2json);
            menuList.add(convert2queryString);
        }
        return menuList;
    }

    public byte[] convertToXML(IExtensionHelpers helpers, byte[] request) throws Exception {
        IRequestInfo requestInfo = helpers.analyzeRequest(request);
        byte content_type = requestInfo.getContentType();
        if (content_type == IRequestInfo.CONTENT_TYPE_XML || content_type == IRequestInfo.CONTENT_TYPE_NONE) {
            return request;
        }
        if (Objects.equals(requestInfo.getMethod(), "GET")) {
            request = helpers.toggleRequestMethod(request);
        }
        int bodyOffset = requestInfo.getBodyOffset();
        String body = new String(request, bodyOffset, request.length - bodyOffset, StandardCharsets.UTF_8);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document doc = new Document() {
            public String getNodeName() {
                return null;
            }

            public String getNodeValue() throws DOMException {
                return null;
            }

            public void setNodeValue(String nodeValue) throws DOMException {

            }

            public short getNodeType() {
                return 0;
            }

            public Node getParentNode() {
                return null;
            }

            public NodeList getChildNodes() {
                return null;
            }

            public Node getFirstChild() {
                return null;
            }

            public Node getLastChild() {
                return null;
            }

            public Node getPreviousSibling() {
                return null;
            }

            public Node getNextSibling() {
                return null;
            }

            public NamedNodeMap getAttributes() {
                return null;
            }

            public Document getOwnerDocument() {
                return null;
            }

            public Node insertBefore(Node newChild, Node refChild) throws DOMException {
                return null;
            }

            public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
                return null;
            }

            public Node removeChild(Node oldChild) throws DOMException {
                return null;
            }

            public Node appendChild(Node newChild) throws DOMException {
                return null;
            }

            public boolean hasChildNodes() {
                return false;
            }

            public Node cloneNode(boolean deep) {
                return null;
            }

            public void normalize() {

            }

            public boolean isSupported(String feature, String version) {
                return false;
            }

            public String getNamespaceURI() {
                return null;
            }

            public String getPrefix() {
                return null;
            }

            public void setPrefix(String prefix) throws DOMException {

            }

            public String getLocalName() {
                return null;
            }

            public boolean hasAttributes() {
                return false;
            }

            public String getBaseURI() {
                return null;
            }

            public short compareDocumentPosition(Node other) throws DOMException {
                return 0;
            }

            public String getTextContent() throws DOMException {
                return null;
            }

            public void setTextContent(String textContent) throws DOMException {

            }

            public boolean isSameNode(Node other) {
                return false;
            }

            public String lookupPrefix(String namespaceURI) {
                return null;
            }

            public boolean isDefaultNamespace(String namespaceURI) {
                return false;
            }

            public String lookupNamespaceURI(String prefix) {
                return null;
            }

            public boolean isEqualNode(Node arg) {
                return false;
            }

            public Object getFeature(String feature, String version) {
                return null;
            }

            public Object setUserData(String key, Object data, UserDataHandler handler) {
                return null;
            }

            public Object getUserData(String key) {
                return null;
            }

            public DocumentType getDoctype() {
                return null;
            }

            public DOMImplementation getImplementation() {
                return null;
            }

            public Element getDocumentElement() {
                return null;
            }

            public Element createElement(String tagName) throws DOMException {
                return null;
            }

            public DocumentFragment createDocumentFragment() {
                return null;
            }

            public Text createTextNode(String data) {
                return null;
            }

            public Comment createComment(String data) {
                return null;
            }

            public CDATASection createCDATASection(String data) throws DOMException {
                return null;
            }

            public ProcessingInstruction createProcessingInstruction(String target, String data) throws DOMException {
                return null;
            }

            public Attr createAttribute(String name) throws DOMException {
                return null;
            }

            public EntityReference createEntityReference(String name) throws DOMException {
                return null;
            }

            public NodeList getElementsByTagName(String tagname) {
                return null;
            }

            public Node importNode(Node importedNode, boolean deep) throws DOMException {
                return null;
            }

            public Element createElementNS(String namespaceURI, String qualifiedName) throws DOMException {
                return null;
            }

            public Attr createAttributeNS(String namespaceURI, String qualifiedName) throws DOMException {
                return null;
            }

            public NodeList getElementsByTagNameNS(String namespaceURI, String localName) {
                return null;
            }

            public Element getElementById(String elementId) {
                return null;
            }

            public String getInputEncoding() {
                return null;
            }

            public String getXmlEncoding() {
                return null;
            }

            public boolean getXmlStandalone() {
                return false;
            }

            public void setXmlStandalone(boolean xmlStandalone) throws DOMException {

            }

            public String getXmlVersion() {
                return null;
            }

            public void setXmlVersion(String xmlVersion) throws DOMException {

            }

            public boolean getStrictErrorChecking() {
                return false;
            }

            public void setStrictErrorChecking(boolean strictErrorChecking) {

            }

            public String getDocumentURI() {
                return null;
            }

            public void setDocumentURI(String documentURI) {

            }

            public Node adoptNode(Node source) throws DOMException {
                return null;
            }

            public DOMConfiguration getDomConfig() {
                return null;
            }

            public void normalizeDocument() {

            }

            public Node renameNode(Node n, String namespaceURI, String qualifiedName) throws DOMException {
                return null;
            }
        };

        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
        xml.append("<root>");

        if (content_type == IRequestInfo.CONTENT_TYPE_URL_ENCODED) {
            body = qs2JSON(body);
        }
        boolean success = true;

        try {
            Object item = new JSONTokener(body).nextValue();
            xml.append(XML.toString(item));
            xml.append("</root>");

            DocumentBuilder builder = factory.newDocumentBuilder();

            ByteArrayInputStream input = new ByteArrayInputStream(xml.toString().getBytes(StandardCharsets.UTF_8));
            doc = builder.parse(input);

        } catch (Exception e) {
            BurpExtender.stderr.println(e);
            success = false;
        }

        if (!success) {
            return request;
        } else {

            List<String> headers;

            headers = requestInfo.getHeaders();

            headers.removeIf(s -> s.contains("Content-Type"));

            headers.add("Content-Type: application/xml;charset=UTF-8");

            return helpers.buildHttpMessage(headers, prettyPrint(doc).getBytes());

        }

    }

    public byte[] convertToJSON(IExtensionHelpers helpers, byte[] request) {

        IRequestInfo requestInfo = helpers.analyzeRequest(request);
        byte content_type = requestInfo.getContentType();
        if (content_type == IRequestInfo.CONTENT_TYPE_JSON || content_type == IRequestInfo.CONTENT_TYPE_NONE) {
            return request;
        }

        if (Objects.equals(requestInfo.getMethod(), "GET")) {
            request = helpers.toggleRequestMethod(request);
        }

        int bodyOffset = requestInfo.getBodyOffset();

        String body = new String(request, bodyOffset, request.length - bodyOffset);

        String json = "";

        boolean success = true;

        try {
            if (content_type == IRequestInfo.CONTENT_TYPE_XML) {
                JSONObject xmlJSONObject = XML.toJSONObject(body);
                JSONObject jo = new JSONObject( xmlJSONObject.toString() );
                json = jo.get("root").toString();
            } else if (content_type == IRequestInfo.CONTENT_TYPE_URL_ENCODED) {
                json = qs2JSON(body);
            } else {
                json = body;
            }
        } catch (Exception e) {
            BurpExtender.stderr.println(e);
            success = false;

        }

        if (!success) {
            return request;
        } else {

            List<String> headers;

            headers = requestInfo.getHeaders();

            headers.removeIf(s -> s.contains("Content-Type"));

            headers.add("Content-Type: application/json;charset=UTF-8");

            return helpers.buildHttpMessage(headers, json.getBytes());
        }
    }

    public byte[] convertToQueryString(IExtensionHelpers helpers, byte[] request) throws UnsupportedEncodingException {
        IRequestInfo requestInfo = helpers.analyzeRequest(request);
        byte content_type = requestInfo.getContentType();
        if (content_type == IRequestInfo.CONTENT_TYPE_URL_ENCODED || content_type == IRequestInfo.CONTENT_TYPE_NONE) {
            return request;
        }

        if (Objects.equals(requestInfo.getMethod(), "GET")) {
            request = helpers.toggleRequestMethod(request);
        }

        int bodyOffset = requestInfo.getBodyOffset();

        String srcBody = new String(request, bodyOffset, request.length - bodyOffset);
        String dstBody = srcBody;
        boolean success = true;
        try {
            if (content_type == IRequestInfo.CONTENT_TYPE_XML) {
                JSONObject xmlJSONObject = XML.toJSONObject(srcBody);
                JSONObject jo = new JSONObject( xmlJSONObject.toString() );
                String json = jo.get("root").toString();
                dstBody = JSON2qs(json);
            } else if (content_type == IRequestInfo.CONTENT_TYPE_JSON) {
                dstBody = JSON2qs(srcBody);
            } else {
                dstBody = srcBody;
            }
        } catch (Exception e) {
            BurpExtender.stderr.println(e);
            success = false;
        }

        if (!success) {
            return request;
        } else {

            List<String> headers;

            headers = requestInfo.getHeaders();

            headers.removeIf(s -> s.contains("Content-Type"));

            headers.add("Content-Type: application/x-www-form-urlencoded;charset=UTF-8");
            return helpers.buildHttpMessage(headers, dstBody.getBytes());
        }
    }

    public String prettyPrint(Document xml) throws Exception {
        Transformer tf = TransformerFactory.newInstance().newTransformer();
        tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        tf.setOutputProperty(OutputKeys.INDENT, "yes");
        Writer out = new StringWriter();
        tf.transform(new DOMSource(xml), new StreamResult(out));
        return (out.toString());
    }

    public String qs2JSON(String qs) throws ScriptException, NoSuchMethodException {
        return (String) JsEngine.invocable.invokeFunction("convert2JSON", qs);
    }
    public String JSON2qs(String json_string) throws ScriptException, NoSuchMethodException {
        JsEngine.engine.put("json_string", json_string);
        JSObject obj = (JSObject)JsEngine.engine.eval("JSON.parse(json_string)");
        return (String) JsEngine.invocable.invokeFunction("convert2qs", obj);
    }

//    public static void main(String[] args) throws ScriptException, NoSuchMethodException {
//        System.out.println(new ConvertContentType().qs2JSON("a=1&b&c=2&d=%E4%B8%AD%E6%96%87"));
//        System.out.println(new ConvertContentType().JSON2qs("{\"a\":\"1\",\"b\":null,\"c\":\"2\",\"d\":\"中文\"}"));
//    }
}
