# Bcrypt
This is an extension for Burp Suite，

**Feature:**

* Customized
* AES
* Send2Xray
* Convert Content-Type

### Customized

Execute custom python script，modify HTTP request between the client and server.

```
           processProxyMessage                     processHttpMessage

 client -----------------------> burpSuit proxy ----------------------->  server
        <-----------------------                <-----------------------
```

![img.png](img.png)

### AES

Reference [AES-Killer](https://github.com/Ebryx/AES-Killer) and  something enhancement

![img_1.png](img_1.png)

### Send2Xray

Context menu for Send request message to the specified proxy

Standalone version: https://github.com/cyal1/SendToXray/tree/requests_mode 

![image.gif](image.gif)

### Convert Content-Type

Context Menu for Convert Content-Type

![convert.gif](convert.gif)
