/*
 * Copyright 2023 tldb Author. All Rights Reserved.
 * email: donnie4w@gmail.com
 * https://githuc.com/donnie4w/tldb
 * https://githuc.com/donnie4w/tlmq-j
 */
package io.github.donnie4w.tlmq.cli;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

import org.apache.http.Header;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

public class HttpClient {
    public static Logger logger = Logger.getLogger("HttpClient");


    public static CloseableHttpClient getHttpClient() throws NoSuchAlgorithmException, KeyManagementException {
        RequestConfig config = RequestConfig.custom().setConnectionRequestTimeout(5000).build();
        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(SSLUtil.newSSLConnectionSocketFactory())
                .setDefaultRequestConfig(config)
                .build();
        return httpClient;
    }

    public static byte[] post(String uri, byte[] bs, String origin, String auth) throws TlException {
        HttpPost httpPost = new HttpPost(uri);
        CloseableHttpResponse response = null;
        try {
            httpPost.setEntity(new ByteArrayEntity(bs));
            httpPost.setHeaders(new Header[]{new BasicHeader("Origin", origin), new BasicHeader("Cookie", "auth=" + auth)});
            response = getHttpClient().execute(httpPost);
            return EntityUtils.toByteArray(response.getEntity());
        } catch (Exception e) {
            throw new TlException(e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                logger.info(e.getMessage());
            }
        }
    }
}