package io.github.donnie4w.tlmq.cli;

import io.github.donnie4w.tlmq.handler.*;
import io.github.donnie4w.tlmq.tldb.bean.JMqBean;
import io.github.donnie4w.tlmq.tldb.bean.MqBean;

public interface MqClient {

    public void pullByteHandler(PullByteHandler pullByteHandler);

    public void pullJsonHandler(PullJsonHandler pullJsonHandler);

    public void pubByteHandler(PubByteHandler pubByteHandler);

    public void pubJsonHandler(PubJsonHandler pubJsonHandler);

    public void pubMemHandler(PubMemHandler pubMemHandler);

    public void ackHandler(AckHandler ackHandler);

    public void errHandler(ErrHandler errHandler);

    //method after the connection successful
    public void before(Before before);

    //connect to server
    public void connect();

    // Subscribe to a topic
    public long sub(String topic) throws TlException;

    // Unsubscribed topic
    public long subCancel(String topic) throws TlException;

    // Publishing topic and PubByteHandler will receive it
    public long pubByte(String topic, byte[] bs) throws TlException;
    // Publishing topic and PubJsonHandler will receive it
    public long pubJson(String topic, String msg) throws TlException;

    // the topic body is not stored when use PubMem
    public long pubMem(String topic, String msg) throws TlException;

    // pull the topic body by topic id used asynchronization mode
    public long pullByte(String topic, long id) throws TlException;

    // pull the topic body by topic id used asynchronization mode
    public long pullJson(String topic, int id) throws TlException;

    // pull the topic body by topic id used synchronization mode
    public JMqBean pullJsonSync(String topic, long id) throws TlException;

    // pull the topic body by topic id used synchronization mode
    public MqBean pullByteSync(String topic, long id) throws TlException;

    // pull the maximum id number of the topic
    public long pullIdSync(String topic) throws TlException;

    // setup requires a client return receipt
    public long recvAck(byte sec) throws TlException;

    // set the limit of the size of protocol data sent by the server before compression(Unit:MB)
    public long mergeOn(byte size) throws TlException;

    public long setZlib(boolean on) throws TlException;
}
