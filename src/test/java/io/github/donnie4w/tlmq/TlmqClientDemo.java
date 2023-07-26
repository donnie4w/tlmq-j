/*
 * Copyright 2023 tldb Author. All Rights Reserved.
 * email: donnie4w@gmail.com
 * https://githuc.com/donnie4w/tldb
 * https://githuc.com/donnie4w/tlmq-j
 */
package io.github.donnie4w.tlmq;

import io.github.donnie4w.tlmq.tldb.bean.JMqBean;
import io.github.donnie4w.tlmq.tldb.bean.MqBean;
import io.github.donnie4w.tlmq.cli.SimpleClient;

import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public class TlmqClientDemo {
    public static Logger logger = Logger.getLogger("tlmq");

    public static void main(String[] args) throws Exception {
        logger.info("java mqcli demo run");
        SimpleClient sc = new SimpleClient("ws://192.168.2.108:5100", "mymq=123");
        sc.pubByteHandler = (mb) -> {
            logger.info(new String(mb.getMsg(), StandardCharsets.UTF_8));
        };
        sc.pullByteHandler = (mb) -> {
            logger.info(new String(mb.getMsg(), StandardCharsets.UTF_8));
        };
        sc.pubJsonHandler = (mb) -> {
            logger.info(mb.toString());
        };
        sc.pubMemHandler = (mb) -> {
            logger.info(mb.toString());
        };
        sc.pullJsonHandler = (mb) -> {
            logger.info(mb.toString());
        };
        sc.errHandler = (errCode) -> {
            System.out.println("err code >> " + errCode);
        };
        sc.ackHandler = (ackId) -> {
            System.out.println("ack id >> " + ackId);
        };

        sc.connect();
        //sc.recvAck((byte) 60); //60s 设定服务器重发数据的时间，默认60秒
        sc.mergeOn((byte) 10);  //10M  设定服务器压缩原数据大小上限 10M
//        sc.setZlib(true);
        Thread.sleep(1000);
        long v = sc.sub("usertable"); //订阅 topic “usertable”
//        cd.subCancel("usertable"); //订阅 topic “usertable”
        logger.info("sub ackId:" + v);
        sc.sub("usertable2");  //订阅 topic “usertable”
        sc.sub("usertable3");  //订阅 topic “usertable”
//        for (int i = 0; i < 10; i++) {
//            new Thread(() -> {
//                for (int k = 0; k < 1000; k++) {
//                    try {
//                        sc.pubMem("usertable", "this is java pubmem" + k); // 只内存发布，不存数据
//                    } catch (TlException e) {
//                    }
//                }
//            }).start();
//        }
        sc.pubJson("usertable", "this is java pubJson"); //发布 topic usertable2  及信息

        long id = sc.pullIdSync("usertable");
        logger.info("pullIdSync >>" + id);

        JMqBean jmb = sc.pullJsonSync("usertable", 1);
        logger.info(jmb == null ? "null" : "pullJsonSync>>" + jmb.toString());

        MqBean mb = sc.pullByteSync("usertable", 1);
        logger.info(mb == null ? "null" : "pullByteSync>>" + mb.toString());
        Thread.sleep(600000);
    }
}