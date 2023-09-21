/*
 * Copyright 2023 tldb Author. All Rights Reserved.
 * email: donnie4w@gmail.com
 * https://githuc.com/donnie4w/tldb
 * https://githuc.com/donnie4w/tlmq-j
 */
package io.github.donnie4w.tlmq.decode;

import com.google.gson.Gson;
import io.github.donnie4w.tlmq.tldb.bean.JMqBean;
import io.github.donnie4w.tlmq.tldb.bean.MqBean;
import io.github.donnie4w.tlmq.cli.TlException;
import org.apache.thrift.TSerializable;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.transport.TMemoryBuffer;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.zip.CRC32;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class TSerialize {

    public static byte[] tEncode(@NotNull TSerializable ts) throws TlException {
        try {
            TMemoryBuffer tmb = new TMemoryBuffer(1024);
            ts.write(new TCompactProtocol(tmb));
            return tmb.getArray();
        } catch (Exception e) {
            throw new TlException(e);
        }
    }

    public static <T extends TSerializable> T tDecode(byte[] bs, T ts) throws TlException {
        try {
            TMemoryBuffer tmb = new TMemoryBuffer(1024);
            tmb.write(bs);
            ts.read(new TCompactProtocol(tmb));
            return ts;
        } catch (Exception e) {
            throw new TlException(e);
        }
    }

    public static String jEncode(JMqBean mb) {
        Gson gs = new Gson();
        return gs.toJson(mb);
    }

    public static JMqBean jDecode(String loadstr) {
        Gson gs = new Gson();
        return gs.fromJson(loadstr, JMqBean.class);
    }

    public static long CRC32(byte[] bs) {
        CRC32 crc32 = new CRC32();
        crc32.update(bs);
        return crc32.getValue();
    }

    public static String string(byte[] bs){
        return new String(bs,StandardCharsets.ISO_8859_1);
    }

    public static byte[] bytes(String s){
        return s.getBytes(StandardCharsets.ISO_8859_1);
    }

    public static long byte2Long(byte[] bs) {
        ByteBuffer buf = ByteBuffer.allocate(Long.BYTES).order(ByteOrder.BIG_ENDIAN);
        buf.put(bs, 0, bs.length);
        buf.flip();
        return buf.getLong();
    }

    public static byte[] long2Bytes(long value) {
        return ByteBuffer.allocate(Long.BYTES).order(ByteOrder.BIG_ENDIAN).putLong(0, value).array();
    }

    public static byte[] int2Bytes(int value) {
        return ByteBuffer.allocate(Integer.BYTES).order(ByteOrder.BIG_ENDIAN).putInt(0, value).array();
    }

    public static byte[] zlibUncz(byte[] data) throws TlException {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream baos=new ByteArrayOutputStream(data.length);
        try {
            byte[] bs = new byte[data.length];
            while (inflater.getRemaining()>0){
                int c = inflater.inflate(bs);
                baos.write(bs,0,c);
            }
            return baos.toByteArray();
        } catch (DataFormatException e) {
            throw new TlException(e);
        } finally {
            inflater.end();
            try {
                baos.close();
            } catch (IOException e) {
                throw new TlException(e);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        MqBean bp = new MqBean();
        bp.setId(1111111);
        bp.setTopic("22222222");
        bp.setMsg("33333".getBytes(StandardCharsets.UTF_8));

        byte[] bb3 = tEncode(bp);
        MqBean bp3 = (MqBean) tDecode(bb3, new MqBean());
        for (byte b : bb3) {
            System.out.print(b);
            System.out.print(' ');
        }
        System.out.println();
        System.out.println(bp3.getId());
        System.out.println(bp3.getTopic());

        JMqBean jbp = new JMqBean(1111, "wuwu", "nn");
        String loadstr = jEncode(jbp);
        System.out.println(loadstr);
        JMqBean jbp4 = jDecode(loadstr);
        System.out.println(jbp4.Id);
        System.out.println(jbp4.Topic);
        System.out.println(jbp4.toString());

        long value = 12345678;
        byte[] bb = long2Bytes(value);
        System.out.println(byte2Long(bb));

        byte[] bs = new byte[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,0};
        String s  = new String(bs,StandardCharsets.ISO_8859_1);
        System.out.println(s.getBytes(StandardCharsets.ISO_8859_1).length);
    }
}
