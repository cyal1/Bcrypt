package burp;

import javax.net.ssl.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;

class MenuItemListener implements ActionListener {

//    private String tunnelHost = "127.0.0.1";
//    private String tunnelPort = "9999";
    private final IHttpRequestResponse[] arr;
    private final BurpExtender burpExtender;
    private final SSLSocketFactory factory;
    private String proxyHost;
    private int proxyPort;

    public MenuItemListener(BurpExtender burpExtender, IHttpRequestResponse[] arr) throws NoSuchAlgorithmException, KeyManagementException {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            // set up a TrustManager that trusts everything
            sslContext.init(null, new TrustManager[] {new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() { return null;}
            public void checkClientTrusted(X509Certificate[] certs, String authType) {}
            public void checkServerTrusted(X509Certificate[] certs, String authType) {}
        } }, new SecureRandom());
        this.factory = sslContext.getSocketFactory();
        this.burpExtender = burpExtender;
        this.arr = arr;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.proxyHost = this.burpExtender.send2xray.getHost();
        this.proxyPort = this.burpExtender.send2xray.getPort();
        for (IHttpRequestResponse message : this.arr) {
            IRequestInfo ir = this.burpExtender.helpers.analyzeRequest(message);
            List<String> newHeader = ir.getHeaders();
            newHeader.set(0, ir.getMethod() + " " + ir.getUrl() + " HTTP/1.1");
            String protocol = message.getHttpService().getProtocol(); // https
            String host = message.getHttpService().getHost();
            int port = message.getHttpService().getPort();
            byte[] body = Arrays.copyOfRange(message.getRequest(),ir.getBodyOffset(),message.getRequest().length);
            byte[] proxy_request = this.burpExtender.helpers.buildHttpMessage(newHeader,body);
            Socket socket;
            SSLSocket sslSocket = null;
            try {
                socket = new Socket();
                socket.connect(new InetSocketAddress(proxyHost, proxyPort),5000);
                socket.setSoTimeout(5000);
                if(protocol.equals("https")){
//                    SSLSocketFactory factory = sslContext.getSocketFactory();
                    doTunnelHandshake(socket, host, port);
                    sslSocket = (SSLSocket)this.factory.createSocket(socket, host, port, true);
                    sslSocket.setSoTimeout(5000);
                    sslSocket.addHandshakeCompletedListener(
                            event -> {
//                                this.burpExtender.stdout.println("Handshake finished!");
//                                this.burpExtender.stdout.println("\t CipherSuite:" + event.getCipherSuite());
//                                this.burpExtender.stdout.println("\t SessionId " + event.getSession());
//                                this.burpExtender.stdout.println("\t PeerHost " + event.getSession().getPeerHost());
                            }
                    );
                }
                try {
                    DataOutputStream out;
                    if(protocol.equals("https")){
                        out = new DataOutputStream(sslSocket.getOutputStream());
                    }else{
                        out = new DataOutputStream(socket.getOutputStream());
                    }
                    try {
                        if(protocol.equals("https")){
                            out.write(message.getRequest());
                        }else{
                            out.write(proxy_request);
                        }
                    }finally {
                        out.close();
                    }
                }finally {
                    socket.close();
                    if(protocol.equals("https")){
                        sslSocket.close();
                    }
                }
            } catch (IOException ioException) {
                this.burpExtender.send2xray.setLabelStatus("fail");
                BurpExtender.stdout.println(ioException);
            }
        }
    }

    private void doTunnelHandshake(Socket tunnel, String https_host, int https_port) throws IOException {
        OutputStream out = tunnel.getOutputStream();
        String msg = "CONNECT " + https_host + ":" + https_port + " HTTP/1.0\r\n"
                + "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.164 Safari/537.36"
                + "\r\n\r\n";
        byte[] b;
        try {
            /*
             * We really do want ASCII7 -- the http protocol doesn't change
             * with locale.
             */
            b = msg.getBytes("ASCII7");
        } catch (UnsupportedEncodingException ignored) {
            /*
             * If ASCII7 isn't there, something serious is wrong, but
             * Paranoia Is Good (tm)
             */
            b = msg.getBytes();
        }
        out.write(b);
        out.flush();

        /*
         * We need to store the reply so we can create a detailed
         * error message to the user.
         */
        byte[] reply = new byte[200];
        int             replyLen = 0;
        int             newlinesSeen = 0;
        boolean         headerDone = false;     /* Done on first newline */

        InputStream in = tunnel.getInputStream();
        boolean         error = false;

        while (newlinesSeen < 2) {
            int i = in.read();
            if (i < 0) {
                throw new IOException("Unexpected EOF from proxy");
            }
            if (i == '\n') {
                headerDone = true;
                ++newlinesSeen;
            } else if (i != '\r') {
                newlinesSeen = 0;
                if (!headerDone && replyLen < reply.length) {
                    reply[replyLen++] = (byte) i;
                }
            }
        }

        /*
         * Converting the byte array to a string is slightly wasteful
         * in the case where the connection was successful, but it's
         * insignificant compared to the network overhead.
         */
        String replyStr;
        try {
            replyStr = new String(reply, 0, replyLen, "ASCII7");
        } catch (UnsupportedEncodingException ignored) {
            replyStr = new String(reply, 0, replyLen);
        }
//        this.burpExtender.stdout.println(replyStr);
        /* We asked for HTTP/1.0, so we should get that back */
        if (!replyStr.startsWith("HTTP/1.0 200")) {
            throw new IOException("Unable to tunnel through "
                    + this.proxyHost + ":" + this.proxyPort
                    + ".  Proxy returns \"" + replyStr + "\"");
        }
        /* tunneling Handshake was successful! */
    }
}