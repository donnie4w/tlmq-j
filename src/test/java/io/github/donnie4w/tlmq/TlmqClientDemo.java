/*
 * Copyright 2023 tldb Author. All Rights Reserved.
 * email: donnie4w@gmail.com
 * https://githuc.com/donnie4w/tldb
 * https://githuc.com/donnie4w/tlmq-j
 */
package io.github.donnie4w.tlmq;

import io.github.donnie4w.tlmq.cli.MqClient;
import io.github.donnie4w.tlmq.cli.TlException;
import io.github.donnie4w.tlmq.decode.TSerialize;
import io.github.donnie4w.tlmq.tldb.bean.JMqBean;
import io.github.donnie4w.tlmq.tldb.bean.MqBean;
import io.github.donnie4w.tlmq.cli.SimpleClient;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class TlmqClientDemo {
    public static Logger logger = Logger.getLogger("tlmq");

    public static void main(String[] args) throws Exception {
        logger.info("java mqcli demo run");
        MqClient mc = new SimpleClient("wss://127.0.0.1:5100", "mymq=123");
        mc.pubByteHandler((mb) -> {
            logger.info("bytes>>>" + new String(mb.getMsg(), StandardCharsets.UTF_8));
        });
        mc.pubJsonHandler((mb) -> {
            logger.info("json>>>>" + mb.toString());
        });
        mc.pubMemHandler((mb) -> {
            logger.info("mem json>>>" + mb.toString());
        });
        mc.pullByteHandler((mb) -> {
            logger.info(new String(mb.getMsg(), StandardCharsets.UTF_8));
        });
        mc.pullJsonHandler((mb) -> {
            logger.info(mb.toString());
        });
        mc.errHandler((errCode) -> {
            System.out.println("err code >> " + errCode);
        });
        mc.ackHandler((ackId) -> {
            System.out.println("ack id >> " + ackId);
        });

        mc.connect();
        //sc.recvAck((byte) 60); //60s 设定服务器重发数据的时间，默认60秒
        mc.mergeOn((byte) 10);  //10M  设定服务器压缩原数据大小上限 10M
        //sc.setZlib(true);
        Thread.sleep(1000);
        long v = mc.sub("usertable"); //订阅 topic “usertable”
        //sc.subCancel("usertable"); //订阅 topic “usertable”
        logger.info("sub ackId:" + v);
        mc.subJson("usertable2");
        mc.sub("usertable3");  //订阅 topic “usertable”
        mc.pubMem("usertable", "this is java pubmem"); // 只内存发布，不存数据
        mc.pubJson("usertable2", "this is java pubJson2"); //发布 topic usertable2  及信息
        mc.pubJson("usertable", "this is java pubJson"); //发布 topic usertable2  及信息
        mc.pubByte("usertable2", "this is java pubByte".getBytes(StandardCharsets.UTF_8)); //发布 topic usertable2  及信息
        long id = mc.pullIdSync("usertable");
        logger.info("pullIdSync >>" + id);

        JMqBean jmb = mc.pullJsonSync("usertable", id);
        logger.info(jmb == null ? "null" : "pullJsonSync>>" + jmb.toString());

        MqBean mb = mc.pullByteSync("usertable", id);
        logger.info(mb == null ? "null" : "pullByteSync>>" + mb.toString());
        Thread.sleep(600000);
    }

    @Test
    public void TestLock() throws Exception {
        logger.info("lock");
        MqClient mc = new SimpleClient("ws://127.0.0.1:5001", "mymq=123");
        mc.connect();
        AtomicInteger ai = new AtomicInteger(0);
        for (int i = 0; i < 40; i++) {
            new Thread(() -> {
                try {
                    String token = mc.lock("testlock2", 3);
                    System.out.println("testlock>>" + ai.incrementAndGet());
                    System.out.println("token>>>" + TSerialize.bytes(token).length);
                    if (ai.get() == 1) {
                        Thread.sleep(6000);
                    }
                    mc.unLock(token);
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {

                }
            }).start();
        }
        Thread.sleep(10000);
    }


    @Test
    public void TestTryLock() throws Exception {
        logger.info("trylock");
        MqClient mc = new SimpleClient("ws://127.0.0.1:5001", "mymq=123");
        mc.connect();
        AtomicInteger ai = new AtomicInteger(0);
        for (int i = 0; i < 50; i++) {
            new Thread(() -> {
                try {
                    int id = ai.incrementAndGet();
                    while(true){
                        String token = mc.tryLock("testlock2", 3);
                        if (token != null) {
                            System.out.println("trylock thread-id-" + id + ",success, sleep 2 second");
                            Thread.sleep(2000);
                            mc.unLock(token);
                            System.out.println("trylock thread-id-" + id + ", unlock");
                            break;
                        }else{
                            Thread.sleep(100);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
        Thread.sleep(30000);
    }
}