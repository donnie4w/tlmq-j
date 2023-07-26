/*
 * Copyright 2023 tldb Author. All Rights Reserved.
 * email: donnie4w@gmail.com
 * https://githuc.com/donnie4w/tldb
 * https://githuc.com/donnie4w/tlmq-j
 */
package io.github.donnie4w.tlmq.cli;

import lombok.Data;

@Data
public abstract class ICli {
    public String url;
    public String origin;
    public String auth;

    public String httpUrl;
    public boolean recvAckOn;

    public boolean isZlibOn;

    public ICli(String url, String auth) {
        this.url = url+"/mq";
        this.origin = "http://tldb-mq";
        this.auth = auth;
        this.httpUrl = parseHttpUrl();
    }

    public ICli(String url, String origin, String auth) {
        this.url = url+"/mq";
        this.origin = origin;
        this.auth = auth;
        this.httpUrl = parseHttpUrl();
    }

    public abstract void onMessage(byte[] msg);

    public abstract void onOpen();

    public abstract void onError(Throwable throwable);

    public abstract void onClose();

    public String parseHttpUrl() {
        try {
            String[] ss = this.url.split("//");
            String[] s = ss[1].split("/");
            String u1 = "http";
            if (ss[0].startsWith("wss:")) {
                u1 = "https";
            }
            u1 = u1 + "://" + s[0] + "/mq2";
            return u1;
        } catch (Exception e) {
            throw new TlRunTimeException("url parse error" + e.getMessage());
        }
    }
}
