/*
 * Copyright 2023 tldb Author. All Rights Reserved.
 * email: donnie4w@gmail.com
 * https://githuc.com/donnie4w/tldb
 * https://githuc.com/donnie4w/tlmq-j
 */
package io.github.donnie4w.tlmq.tldb.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JMqBean {
    public int Id;
    public String Topic;
    public String Msg;

    public JMqBean(int id, String topic, String msg) {
        this.Id = id;
        this.Topic = topic;
        this.Msg = msg;
    }

    public JMqBean(int id, String topic) {
        this.Id = id;
        this.Topic = topic;
    }

    public String toString(){
        return  "Id:"+Id+",Topic:"+Topic+",Msg:"+Msg;
    }
}
