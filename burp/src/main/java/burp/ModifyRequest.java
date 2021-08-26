package burp;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.w3c.dom.*;

import com.google.gson.Gson;
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
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ModifyRequest implements IContextMenuFactory {
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

    public static byte[] convertToXML(IExtensionHelpers helpers, byte[] request) throws Exception {
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
            Map<String, String> params = splitQuery(body);
            Gson gson = new Gson();
            body = gson.toJson(params);
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

    public static byte[] convertToJSON(IExtensionHelpers helpers, byte[] request) {

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
                json = xmlJSONObject.toString();
                JsonElement je = new JsonParser().parse(json);
                json = je.getAsJsonObject().get("root").toString();
            } else if (content_type == IRequestInfo.CONTENT_TYPE_URL_ENCODED) {
                Map<String, String> params = splitQuery(body);
                Gson gson = new Gson();
                json = gson.toJson(params);
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

    public static byte[] convertToQueryString(IExtensionHelpers helpers, byte[] request) throws UnsupportedEncodingException {
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
                JsonElement json = new JsonParser().parse(xmlJSONObject.toString());
                dstBody = json.getAsJsonObject().get("root").getAsJsonObject().entrySet().stream()
                        .map(entry -> entry.getKey() + "=" + entry.getValue().getAsString())
                        .reduce((e1, e2) -> e1 + "&" + e2).orElse("");
            } else if (content_type == IRequestInfo.CONTENT_TYPE_JSON) {
                JsonElement json = new JsonParser().parse(srcBody);
                dstBody = json.getAsJsonObject().entrySet().stream()
                        .map(entry -> entry.getKey() + "=" + entry.getValue().getAsString())
                        .reduce((e1, e2) -> e1 + "&" + e2).orElse("");
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


    private static Map<String, String> splitQuery(String body) throws UnsupportedEncodingException {
        Map<String, String> query_pairs = new LinkedHashMap<>();
        String[] pairs = body.split("&");

        for (String pair : pairs) {
            final int idx = pair.indexOf("=");
            final String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), "UTF-8") : pair;
            if (!query_pairs.containsKey(key)) {
                query_pairs.put(key, "");
            }
            final String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), "UTF-8") : "";
            query_pairs.put(key, value.trim());
        }
        return query_pairs;
    }

    public static String prettyPrint(Document xml) throws Exception {
        Transformer tf = TransformerFactory.newInstance().newTransformer();
        tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        tf.setOutputProperty(OutputKeys.INDENT, "yes");
        Writer out = new StringWriter();
        tf.transform(new DOMSource(xml), new StreamResult(out));
        return (out.toString());
    }
}
