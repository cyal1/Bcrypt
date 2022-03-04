package burp;

import javax.swing.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
//import java.security.KeyManagementException;
//import java.security.NoSuchAlgorithmException;
//import java.security.SecureRandom;
//import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public record Send2XrayListener(Send2Xray send2Xray) implements IContextMenuFactory {
    //    private SSLSocketFactory factory = null;
    //        try{
    //            SSLContext sslContext = SSLContext.getInstance("SSL");
    //            // set up a TrustManager that trusts everything
    //            sslContext.init(null, new TrustManager[] {new X509TrustManager() {
    //                public X509Certificate[] getAcceptedIssuers() { return null;}
    //                public void checkClientTrusted(X509Certificate[] certs, String authType) {}
    //                public void checkServerTrusted(X509Certificate[] certs, String authType) {}
    //            } }, new SecureRandom());
    //
    //            this.factory = sslContext.getSocketFactory();
    //        } catch (NoSuchAlgorithmException | KeyManagementException e) {
    //            BurpExtender.stderr.println("Send2Xray: " + e.getMessage());
    //        }

    @Override
    public List<JMenuItem> createMenuItems(IContextMenuInvocation invocation) {
        List<JMenuItem> menu = new ArrayList<>();
        if (invocation != null && invocation.getSelectedMessages() != null && invocation.getSelectedMessages()[0] != null && invocation.getSelectedMessages()[0].getHttpService() != null) {
            JMenuItem send2XrayMenu = new JMenuItem("Send to Xray");
//        JMenuItem CustomEncrypt = new JMenuItem("Customized Encrypt");
//        JMenuItem CustomDecrypt = new JMenuItem("Customized Decrypt");
//            Send2XrayListener mil = new Send2XrayListener(invocation.getSelectedMessages());
            IHttpRequestResponse[] arr = invocation.getSelectedMessages();
            send2XrayMenu.addActionListener(e -> {
                for (IHttpRequestResponse message : arr) {
                    IRequestInfo ir = BurpExtender.helpers.analyzeRequest(message);
                    List<String> newHeader = ir.getHeaders();
//                    String protocol = message.getHttpService().getProtocol(); // https
//                    String host = message.getHttpService().getHost();
//                    int port = message.getHttpService().getPort();
                    BurpExtender.stdout.println(ir.getMethod() + " " + ir.getUrl() + " HTTP/1.1");
                    newHeader.set(0, ir.getMethod() + " " + ir.getUrl() + " HTTP/1.1");

                    byte[] body = Arrays.copyOfRange(message.getRequest(), ir.getBodyOffset(), message.getRequest().length);
                    byte[] proxy_request = BurpExtender.helpers.buildHttpMessage(newHeader, body);
                    try {
                        Socket socket = new Socket();
                        socket.connect(new InetSocketAddress(send2Xray.getHost(), send2Xray.getPort()), 5000);
                        socket.setSoTimeout(5000);
                        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                        out.write(proxy_request);
                        out.close();
                        socket.close();
//                        DataOutputStream out;
//                        if(protocol.equals("https")){
////                    SSLSocketFactory factory = sslContext.getSocketFactory();
//                            doTunnelHandshake(socket, host, port);
//                            sslSocket = (SSLSocket)factory.createSocket(socket, host, port, true);
//                            sslSocket.setSoTimeout(5000);
//                            sslSocket.addHandshakeCompletedListener(
//                                    event -> {
////                                this.burpExtender.stdout.println("Handshake finished!");
////                                this.burpExtender.stdout.println("\t CipherSuite:" + event.getCipherSuite());
////                                this.burpExtender.stdout.println("\t SessionId " + event.getSession());
////                                this.burpExtender.stdout.println("\t PeerHost " + event.getSession().getPeerHost());
//                                    }
//                            );
//                            out = new DataOutputStream(sslSocket.getOutputStream());
//                        }else{
//                            out = new DataOutputStream(socket.getOutputStream());
//                        }
//                        out.write(proxy_request);
//                        out.close();
                    } catch (IOException ioException) {
                        send2Xray.setLabelStatus("fail");
                        BurpExtender.stderr.println(ir.getMethod() + " " + ir.getUrl() + " HTTP/1.1" + ioException);
                    }
                }

            });
            menu.add(send2XrayMenu);
        }
        return menu;
    }

//    private void doTunnelHandshake(Socket tunnel, String https_host, int https_port) throws IOException {
//        OutputStream out = tunnel.getOutputStream();
//        String msg = "CONNECT " + https_host + ":" + https_port + " HTTP/1.0\r\n"
//                + "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.164 Safari/537.36"
//                + "\r\n\r\n";
//        byte[] b;
//        try {
//            /*
//             * We really do want ASCII7 -- the http protocol doesn't change
//             * with locale.
//             */
//            b = msg.getBytes("ASCII7");
//        } catch (UnsupportedEncodingException ignored) {
//            /*
//             * If ASCII7 isn't there, something serious is wrong, but
//             * Paranoia Is Good (tm)
//             */
//            b = msg.getBytes();
//        }
//        out.write(b);
//        out.flush();
//
//        /*
//         * We need to store the reply so we can create a detailed
//         * error message to the user.
//         */
//        byte[] reply = new byte[200];
//        int             replyLen = 0;
//        int             newlinesSeen = 0;
//        boolean         headerDone = false;     /* Done on first newline */
//
//        InputStream in = tunnel.getInputStream();
//        boolean         error = false;
//
//        while (newlinesSeen < 2) {
//            int i = in.read();
//            if (i < 0) {
//                throw new IOException("Unexpected EOF from proxy");
//            }
//            if (i == '\n') {
//                headerDone = true;
//                ++newlinesSeen;
//            } else if (i != '\r') {
//                newlinesSeen = 0;
//                if (!headerDone && replyLen < reply.length) {
//                    reply[replyLen++] = (byte) i;
//                }
//            }
//        }
//
//        /*
//         * Converting the byte array to a string is slightly wasteful
//         * in the case where the connection was successful, but it's
//         * insignificant compared to the network overhead.
//         */
//        String replyStr;
//        try {
//            replyStr = new String(reply, 0, replyLen, "ASCII7");
//        } catch (UnsupportedEncodingException ignored) {
//            replyStr = new String(reply, 0, replyLen);
//        }
////        this.burpExtender.stdout.println(replyStr);
//        /* We asked for HTTP/1.0, so we should get that back */
//        if (!replyStr.startsWith("HTTP/1.0 200")) {
//            throw new IOException("Unable to tunnel through "
//                    + send2Xray.getHost() + ":" + send2Xray.getPort()
//                    + ".  Proxy returns \"" + replyStr + "\"");
//        }
//        /* tunneling Handshake was successful! */
//    }
}
