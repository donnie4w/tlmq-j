/*
 * Copyright 2023 tldb Author. All Rights Reserved.
 * email: donnie4w@gmail.com
 * https://githuc.com/donnie4w/tldb
 * https://githuc.com/donnie4w/tlmq-j
 */
package io.github.donnie4w.tlmq.cli;

import io.github.donnie4w.tlmq.decode.TSerialize;
import io.github.donnie4w.tlmq.tldb.bean.JMqBean;
import io.github.donnie4w.tlmq.tldb.bean.MqBean;
import jakarta.websocket.*;

import javax.net.ssl.SSLContext;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

import static io.github.donnie4w.tlmq.cli.Const.*;

public class Cli extends Endpoint implements MessageHandler.Whole<byte[]> {
    private Session session;
    private ICli cli;

    private AtomicLong al = new AtomicLong(0);
    public static Logger logger = Logger.getLogger("cli");

    public void close() {
        try {
            session.close();
        } catch (IOException e) {
        }
    }

    public Cli(ICli cli, String uri, String origin) throws TlException {
        try {
            this.cli = cli;
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            ClientEndpointConfig cec = ClientEndpointConfig.Builder
                    .create().configurator(new ClientEndpointConfig.Configurator() {
                        public void beforeRequest(Map<String, List<String>> headers) {
                            headers.put("Origin", Arrays.asList(origin));
                        }
                    }).build();
            URI path = URI.create(uri);
            if ("wss".equalsIgnoreCase(path.getScheme())) {
                SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
                        .loadTrustMaterial(null, (X509Certificate[] chain, String authType) -> true)
                        .build();
                cec.getUserProperties().put("org.apache.tomcat.websocket.SSL_CONTEXT", sslContext);
            }
            session = container.connectToServer(this, cec, path);
            session.addMessageHandler(this);
        } catch (Exception e) {
            throw new TlException(e);
        }
    }

    public long ping() throws TlException {
        return send(MQ_PING, new byte[]{});
    }

    @Override
    public void onMessage(byte[] message) {
        cli.onMessage(message);
    }

    @Override
    public void onError(Session session, Throwable throwable) {
        this.cli.onError(throwable);
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        this.cli.onOpen();
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        this.cli.onClose();
    }

    public synchronized void send(ByteBuffer bb) throws TlException {
        try {
            session.getBasicRemote().sendObject(bb.array());
        } catch (Exception e) {
            throw new TlException(e);
        }
    }

    private long send(byte type, byte[] bs) throws TlException {
        long ackid = getAckId();
        send(getSendMsg(type, bs, ackid));
        return ackid;
    }

    private long send(byte type) throws TlException {
        long ackid = getAckId();
        send(getSendMsg(type,null, ackid));
        return ackid;
    }

    private ByteBuffer getSendMsg(byte type, byte[] bs, long ackId) throws TlException {
        int size = 1 + Long.BYTES;
        if (null != bs) {
            size = size + bs.length;
        }
        ByteBuffer bb = ByteBuffer.allocate(size);
        bb.put(type);
        if (ackId == 0) {
            ackId = getAckId();
        }
        bb.put(TSerialize.Long2Byte(ackId));
        if (null != bs) {
            bb.put(bs);
        }
        return bb;
    }

    private long send(byte type, String msg) throws TlException {
        byte[] ab = msg.getBytes(StandardCharsets.UTF_8);
        return send(type, ab);
    }

    public long auth(String auth) throws TlException {
        return send(MQ_AUTH, auth);
    }

    public long recvAck(byte sec) throws TlException {
        return send(MQ_RECVACK,new byte[]{sec});
    }

    public long mergeOn(byte size) throws TlException {
        return send(MQ_MERGE,new byte[]{size});
    }

    public long setZlib(boolean on) throws TlException {
        long id;
        if (on){
            id= send(MQ_ZLIB,new byte[]{1});
        }else{
            id= send(MQ_ZLIB,new byte[]{0});
        }
        this.cli.isZlibOn = on;
        return id;
    }

    public void ackMsg(byte[] bs) {
        ByteBuffer bb = ByteBuffer.allocate(1 + Long.BYTES);
        bb.put(MQ_ACK);
        bb.put(TSerialize.Long2Byte(TSerialize.CRC32(bs)));
        try {
            send(bb);
        } catch (TlException e) {
            logger.info(e.getMessage());
        }
    }

    public long sub(String topic) throws TlException {
        return send(MQ_SUB, topic);
    }

    public long subCancel(String topic) throws TlException {
        return send(MQ_SUBCANCEL, topic);
    }

    public long pubByte(String topic, byte[] bs) throws TlException {
        MqBean mb = new MqBean();
        mb.setMsg(bs);
        mb.setTopic(topic);
        mb.setId(0);
        return send(MQ_PUBBYTE, TSerialize.TEncode(mb));
    }

    public long pullByte(String topic, long id) throws TlException {
        MqBean mb = new MqBean();
        mb.setMsg(new byte[]{});
        mb.setTopic(topic);
        mb.setId(id);
        return send(MQ_PULLBYTE, TSerialize.TEncode(mb));
    }

    public long pubJson(String topic, String msg) throws TlException {
        JMqBean jmb = new JMqBean(0, topic, msg);
        return send(MQ_PUBJSON, TSerialize.JEncode(jmb));
    }

    public long pubMem(String topic, String msg) throws TlException {
        JMqBean jmb = new JMqBean(0, topic, msg);
        return send(MQ_PUBMEM, TSerialize.JEncode(jmb));
    }

    public long pullJson(String topic, int id) throws TlException {
        JMqBean jmb = new JMqBean(id, topic, topic);
        return send(MQ_PULLJSON, TSerialize.JEncode(jmb));
    }

    public long getAckId() throws TlException {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos.write(TSerialize.Long2Byte(System.nanoTime()));
            baos.write(TSerialize.Long2Byte(al.addAndGet(1)));
            return TSerialize.CRC32(baos.toByteArray());
        } catch (IOException e) {
            throw new TlException(e);
        }
    }

    public JMqBean pullJsonSync(String topic, long id) throws TlException {
        String m = TSerialize.JEncode(new JMqBean((int) id, topic));
        ByteBuffer bb = this.getSendMsg(MQ_PULLJSON, m.getBytes(StandardCharsets.UTF_8), 0);
        byte[] bs = HttpClient.post(this.cli.httpUrl, bb.array(), this.cli.origin, this.cli.auth);
        switch (bs[0]) {
            case MQ_ERROR:
                logger.info("pullJsonSync error >> " + topic + " >> " + id + " >> " + TSerialize.Byte2Long(Arrays.copyOfRange(bs, 1, 9)));
                break;
            case MQ_PULLJSON:
                return TSerialize.JDecode(new String(Arrays.copyOfRange(bs, 1, bs.length), StandardCharsets.UTF_8));
            default:
        }
        return null;
    }

    public MqBean pullByteSync(String topic, long id) throws TlException {
        byte[] mbs = TSerialize.TEncode(new MqBean(topic, id));
        ByteBuffer bb = this.getSendMsg(MQ_PULLBYTE, mbs, 0);
        byte[] bs = HttpClient.post(this.cli.httpUrl, bb.array(), this.cli.origin, this.cli.auth);
        switch (bs[0]) {
            case MQ_ERROR:
                logger.info("pullByteSync error >> " + topic + " >> " + id + " >> " + TSerialize.Byte2Long(Arrays.copyOfRange(bs, 1, 9)));
                break;
            case MQ_PULLBYTE:
                return TSerialize.TDecode(Arrays.copyOfRange(bs, 1, bs.length), new MqBean());
        }
        return null;
    }

    public long pullIdSync(String topic) throws TlException {
        byte[] mbs = topic.getBytes(StandardCharsets.UTF_8);
        ByteBuffer bb = this.getSendMsg(MQ_CURRENTID, mbs, 0);
        byte[] bs = HttpClient.post(this.cli.httpUrl, bb.array(), this.cli.origin, this.cli.auth);
        switch (bs[0]) {
            case MQ_ERROR:
                logger.info("pullIdSync error >> " + topic + " >> " + TSerialize.Byte2Long(Arrays.copyOfRange(bs, 1, 9)));
                break;
            case MQ_CURRENTID:
                return TSerialize.Byte2Long(Arrays.copyOfRange(bs, 1, 9));
        }
        return 0;
    }
}
