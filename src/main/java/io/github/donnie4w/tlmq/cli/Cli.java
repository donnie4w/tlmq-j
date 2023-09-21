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

import javax.net.ssl.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import java.util.concurrent.ConcurrentHashMap;
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
                cec.getUserProperties().put("org.apache.tomcat.websocket.SSL_CONTEXT", SSLUtil.newSSLContext());
            }
            session = container.connectToServer(this, cec, path);
            session.addMessageHandler(this);
        } catch (Exception e) {
            e.printStackTrace();
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
        send(getSendMsg(type, null, ackid));
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
        bb.put(TSerialize.long2Bytes(ackId));
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
        return send(MQ_RECVACK, new byte[]{sec});
    }

    public long mergeOn(byte size) throws TlException {
        return send(MQ_MERGE, new byte[]{size});
    }

    public long setZlib(boolean on) throws TlException {
        long id;
        if (on) {
            id = send(MQ_ZLIB, new byte[]{1});
        } else {
            id = send(MQ_ZLIB, new byte[]{0});
        }
        this.cli.isZlibOn = on;
        return id;
    }

    public void ackMsg(byte[] bs) {
        ByteBuffer bb = ByteBuffer.allocate(1 + Long.BYTES);
        bb.put(MQ_ACK);
        bb.put(TSerialize.long2Bytes(TSerialize.CRC32(bs)));
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
        return send(MQ_PUBBYTE, TSerialize.tEncode(mb));
    }

    public long pullByte(String topic, long id) throws TlException {
        MqBean mb = new MqBean();
        mb.setMsg(new byte[]{});
        mb.setTopic(topic);
        mb.setId(id);
        return send(MQ_PULLBYTE, TSerialize.tEncode(mb));
    }

    public long pubJson(String topic, String msg) throws TlException {
        JMqBean jmb = new JMqBean(0, topic, msg);
        return send(MQ_PUBJSON, TSerialize.jEncode(jmb));
    }

    public long pubMem(String topic, String msg) throws TlException {
        JMqBean jmb = new JMqBean(0, topic, msg);
        return send(MQ_PUBMEM, TSerialize.jEncode(jmb));
    }

    public long pullJson(String topic, int id) throws TlException {
        JMqBean jmb = new JMqBean(id, topic, topic);
        return send(MQ_PULLJSON, TSerialize.jEncode(jmb));
    }

    public long getAckId() {
        byte[] bs1 = TSerialize.long2Bytes(System.nanoTime());
        byte[] bs2 = TSerialize.long2Bytes(al.incrementAndGet());
        byte[] bs = new byte[16];
        System.arraycopy(bs1, 0, bs, 0, 8);
        System.arraycopy(bs2, 0, bs, 8, 8);
        return (System.nanoTime() << 32) | TSerialize.CRC32(bs);
    }

    public JMqBean pullJsonSync(String topic, long id) throws TlException {
        String m = TSerialize.jEncode(new JMqBean((int) id, topic));
        byte[] bs = HttpClient.post(this.cli.httpUrl, httpbody(m.getBytes(StandardCharsets.UTF_8),MQ_PULLJSON), this.cli.origin, this.cli.auth);
        switch (bs[0]) {
            case MQ_ERROR:
                logger.info("pullJsonSync error >> " + topic + " >> " + id + " >> " + TSerialize.byte2Long(Arrays.copyOfRange(bs, 1, 9)));
                break;
            case MQ_PULLJSON:
                return TSerialize.jDecode(new String(Arrays.copyOfRange(bs, 1, bs.length), StandardCharsets.UTF_8));
            default:
        }
        return null;
    }

    public MqBean pullByteSync(String topic, long id) throws TlException {
        byte[] mbs = TSerialize.tEncode(new MqBean(topic, id));
        byte[] bs = HttpClient.post(this.cli.httpUrl, httpbody(mbs,MQ_PULLBYTE), this.cli.origin, this.cli.auth);
        switch (bs[0]) {
            case MQ_ERROR:
                logger.info("pullByteSync error >> " + topic + " >> " + id + " >> " + TSerialize.byte2Long(Arrays.copyOfRange(bs, 1, 9)));
                break;
            case MQ_PULLBYTE:
                return TSerialize.tDecode(Arrays.copyOfRange(bs, 1, bs.length), new MqBean());
        }
        return null;
    }

    public long pullIdSync(String topic) throws TlException {
        byte[] mbs = topic.getBytes(StandardCharsets.UTF_8);
        byte[] bs = HttpClient.post(this.cli.httpUrl, httpbody(mbs,MQ_CURRENTID), this.cli.origin, this.cli.auth);
        switch (bs[0]) {
            case MQ_ERROR:
                logger.info("pullIdSync error >> " + topic + " >> " + TSerialize.byte2Long(Arrays.copyOfRange(bs, 1, 9)));
                break;
            case MQ_CURRENTID:
                return TSerialize.byte2Long(Arrays.copyOfRange(bs, 1, 9));
        }
        return 0;
    }

    private static byte[] httpbody(byte[] bs, byte Type) {
        byte[] body = new byte[bs.length + 1];
        body[0] = Type;
        System.arraycopy(bs, 0, body, 1, bs.length);
        return body;
    }

    private static ConcurrentHashMap<String, Long> lockmap = new ConcurrentHashMap<>();

    public String lock(String str, int overtime) throws TlException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String key = null;
        try {
            baos.write(new byte[]{MQ_LOCK, 1});
            long strId = getAckId();
            baos.write(TSerialize.long2Bytes(strId));
            baos.write(TSerialize.int2Bytes(overtime));
            baos.write(str.getBytes(StandardCharsets.UTF_8));
            int count = 0;
            while (true) {
                byte[] bs = HttpClient.post(this.cli.httpUrl, baos.toByteArray(), this.cli.origin, this.cli.auth);
                System.out.println("bs.length>>>" + bs.length);
                if (bs != null && bs.length == 16) {
                    key = TSerialize.string(bs);
                    System.out.println(key + ">>>>>>" + TSerialize.bytes(key).length);
                    if (lockmap.contains(key)) {
                        System.out.println("======================>" + key);
                    }
                    lockmap.put(key, strId);
                    break;
                } else if (bs != null && bs[0] == 0) {
                    return key;
                } else if (count == 0) {
                    baos.reset();
                    baos.write(new byte[]{MQ_LOCK, 1});
                    strId = getAckId();
                    baos.write(TSerialize.long2Bytes(strId));
                    baos.write(TSerialize.int2Bytes(overtime));
                    baos.write(str.getBytes(StandardCharsets.UTF_8));
                }
                count++;
                if (count > 3) {
                    break;
                }
            }
        } catch (Exception e) {
            throw new TlException(e);
        } finally {
            try {
                baos.close();
            } catch (IOException e) {
            }
        }
        return key;
    }

    public void unLock(String token) throws TlException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            baos.write(new byte[]{MQ_LOCK, 3});
            Long strId = lockmap.get(token);
            System.out.println("strId>>>>" + strId);
            if (strId != null) {
                baos.write(TSerialize.long2Bytes(strId));
                System.out.println("key len>>>>>" + TSerialize.bytes(token).length);
                baos.write(TSerialize.bytes(token));
                while (true) {
                    byte[] bs = HttpClient.post(this.cli.httpUrl, baos.toByteArray(), this.cli.origin, this.cli.auth);
                    System.out.println("---------------------------------->");
                    if (bs[0] == 1) {
                        lockmap.remove(token);
                        break;
                    } else {
                        Thread.sleep(1000);
                    }
                }
            }
        } catch (Exception e) {
            throw new TlException(e);
        } finally {
            try {
                baos.close();
            } catch (IOException e) {
            }
        }
    }

}
