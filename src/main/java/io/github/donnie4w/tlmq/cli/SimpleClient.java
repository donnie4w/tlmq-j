/*
 * Copyright 2023 tldb Author. All Rights Reserved.
 * email: donnie4w@gmail.com
 * https://githuc.com/donnie4w/tldb
 * https://githuc.com/donnie4w/tlmq-j
 */
package io.github.donnie4w.tlmq.cli;

import io.github.donnie4w.tlmq.decode.TSerialize;
import io.github.donnie4w.tlmq.tldb.bean.JMqBean;
import io.github.donnie4w.tlmq.tldb.bean.MergeBean;
import io.github.donnie4w.tlmq.tldb.bean.MqBean;
import io.github.donnie4w.tlmq.handler.*;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class SimpleClient extends ICli implements MqClient {

     PubByteHandler pubByteHandler;
     PubJsonHandler pubJsonHandler;
     PubMemHandler pubMemHandler;
     PullByteHandler pullByteHandler;
     PullJsonHandler pullJsonHandler;
     AckHandler ackHandler;
     ErrHandler errHandler;
     Before before;
    public final static Logger logger = Logger.getLogger("tlmq");
    private int pingCount;
    public Cli cli;

    private Map<String, Boolean> subMap = new HashMap();

    public SimpleClient(String url, String auth) {
        super(url, auth);
    }

    @Override
    public void pullByteHandler(PullByteHandler pullByteHandler) {
        this.pullByteHandler = pullByteHandler;
    }

    @Override
    public void pullJsonHandler(PullJsonHandler pullJsonHandler) {
        this.pullJsonHandler = pullJsonHandler;
    }

    @Override
    public void pubByteHandler(PubByteHandler pubByteHandler) {
        this.pubByteHandler = pubByteHandler;
    }

    @Override
    public void pubJsonHandler(PubJsonHandler pubJsonHandler) {
        this.pubJsonHandler = pubJsonHandler;
    }

    @Override
    public void pubMemHandler(PubMemHandler pubMemHandler) {
        this.pubMemHandler = pubMemHandler;
    }

    @Override
    public void ackHandler(AckHandler ackHandler) {
        this.ackHandler = ackHandler;
    }

    @Override
    public void errHandler(ErrHandler errHandler) {
        this.errHandler = errHandler;
    }

    @Override
    public void before(Before before) {
        this.before = before;
    }

    public void connect() {
        this.pingCount = 0;
        try {
            cli = new Cli(this, this.url, this.origin);
            cli.auth(this.auth);
            Thread.sleep(1000);
            if (this.subMap.size() > 0) {
                for (String key : this.subMap.keySet()) {
                    if (this.subMap.get(key)){
                        this.subJson(key);
                    }else{
                        this.sub(key);
                    }
                }
            }
            if (this.before != null) {
                this.before.run();
            }
        } catch (InterruptedException | TlException e) {
            try {
                Thread.sleep(1000);
                this.connect();
            } catch (InterruptedException ex) {
            }
        }
    }

    @Override
    public void onMessage(byte[] msg) {
        if (null != msg) {
            byte type = msg[0];
            if (this.recvAckOn && (type == Const.MQ_PUBBYTE || type == Const.MQ_PUBJSON || type == Const.MQ_PULLBYTE || type == Const.MQ_PULLJSON || type == Const.MQ_MERGE)) {
                this.cli.ackMsg(msg);
            }
            parse(msg);
        }
    }

    private void parse(byte[] msg) {
        if (null != msg) {
            byte[] m = Arrays.copyOfRange(msg, 1, msg.length);
            byte type = msg[0];
            switch (type) {
                case Const.MQ_PING:
                    this.pingCount--;
                    break;
                case Const.MQ_PUBBYTE:
                    try {
                        if (this.pubByteHandler != null) {
                            this.pubByteHandler.run(TSerialize.tDecode(m, new MqBean()));
                        }
                    } catch (Exception e) {
                        logger.info(e.getMessage());
                    }
                    break;
                case Const.MQ_PUBJSON:
                    try {
                        if (this.pubJsonHandler != null) {
                            this.pubJsonHandler.run(TSerialize.jDecode(new String(m, StandardCharsets.UTF_8)));
                        }
                    } catch (Exception e) {
                        logger.info(e.getMessage());
                    }
                    break;
                case Const.MQ_PUBMEM:
                    try {
                        if (this.pubMemHandler != null) {
                            this.pubMemHandler.run(TSerialize.jDecode(new String(m, StandardCharsets.UTF_8)));
                        }
                    } catch (Exception e) {
                        logger.info(e.getMessage());
                    }
                    break;
                case Const.MQ_PULLBYTE:
                    try {
                        if (this.pullByteHandler != null) {
                            this.pullByteHandler.run(TSerialize.tDecode(m, new MqBean()));
                        }
                    } catch (Exception e) {
                        logger.info(e.getMessage());
                    }
                    break;
                case Const.MQ_PULLJSON:
                    try {
                        if (this.pullJsonHandler != null) {
                            this.pullJsonHandler.run(TSerialize.jDecode(new String(m, StandardCharsets.UTF_8)));
                        }
                    } catch (Exception e) {
                        logger.info(e.getMessage());
                    }
                    break;
                case Const.MQ_ACK:
                    try {
                        if (this.ackHandler != null) {
                            this.ackHandler.run(TSerialize.byte2Long(Arrays.copyOfRange(msg, 1, 9)));
                        }
                    } catch (Exception e) {
                        logger.info(e.getMessage());
                    }
                    break;
                case Const.MQ_MERGE:
                    try {
                        MergeBean mb = null;
                        if (this.isZlibOn) {
                            mb = TSerialize.tDecode(TSerialize.zlibUncz(m), new MergeBean());
                        } else {
                            mb = TSerialize.tDecode(m, new MergeBean());
                        }
                        if (mb != null && mb.beanList != null) {
                            for (ByteBuffer bb : mb.beanList) {
                                parse(bb.array());
                            }
                        }
                    } catch (Exception e) {
                        logger.info(e.getMessage());
                    }
                    break;
                case Const.MQ_ERROR:
                    try {
                        if (this.errHandler != null) {
                            this.errHandler.run(TSerialize.byte2Long(Arrays.copyOfRange(msg, 1, 9)));
                        }
                    } catch (Exception e) {
                        logger.info(e.getMessage());
                    }
                    break;
                default:
            }
        }
    }

    @Override
    public void onOpen() {
        logger.info("tldb mq connect");
        try {
            new Thread(() -> {
                while (true) {
                    if (this.pingCount++ > 3) {
                        break;
                    }
                    try {
                        Thread.sleep(3000);
                        this.ping();
                    } catch (InterruptedException | TlException e) {
                        break;
                    }
                }
                this.cli.close();
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void ping() throws TlException {
        this.cli.ping();
    }

    @Override
    public void onError(Throwable throwable) {
        logger.info("onError:" + throwable.getMessage());
    }

    @Override
    public void onClose() {
        logger.info("onClose");
        this.connect();
    }

    public long sub(String topic) throws TlException {
        this.subMap.put(topic, false);
        return this.cli.sub(topic);
    }

    public long subJson(String topic) throws TlException {
        this.subMap.put(topic, true);
        return this.cli.subJson(topic);
    }

    public long subCancel(String topic) throws TlException {
        this.subMap.remove(topic);
        return this.cli.subCancel(topic);
    }

    public long pubByte(String topic, byte[] bs) throws TlException {
        return this.cli.pubByte(topic, bs);
    }

    public long pullByte(String topic, long id) throws TlException {
        return this.cli.pullByte(topic, id);
    }

    public long pubJson(String topic, String msg) throws TlException {
        return this.cli.pubJson(topic, msg);
    }

    public long pubMem(String topic, String msg) throws TlException {
        return this.cli.pubMem(topic, msg);
    }

    public long pullJson(String topic, int id) throws TlException {
        return this.cli.pullJson(topic, id);
    }

    public JMqBean pullJsonSync(String topic, long id) throws TlException {
        return this.cli.pullJsonSync(topic, id);
    }

    public MqBean pullByteSync(String topic, long id) throws TlException {
        return this.cli.pullByteSync(topic, id);
    }

    public long pullIdSync(String topic) throws TlException {
        return this.cli.pullIdSync(topic);
    }

    @Override
    public String lock(String str, int overtime) throws TlException {
        return this.cli.lock(str,overtime);
    }

    public String tryLock(String str, int overtime) throws TlException {
        return this.cli.tryLock(str,overtime);
    }

    @Override
    public void unLock(String token) throws TlException {
         this.cli.unLock(token);
    }

    public long recvAck(byte sec) throws TlException {
        this.recvAckOn = true;
        return this.cli.recvAck(sec);
    }

    public long mergeOn(byte size) throws TlException {
        return this.cli.mergeOn(size);
    }

    public long setZlib(boolean on) throws TlException {
        this.isZlibOn = on;
        return this.cli.setZlib(on);
    }

}
