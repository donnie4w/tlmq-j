/*
 * Copyright 2023 tldb Author. All Rights Reserved.
 * email: donnie4w@gmail.com
 * https://githuc.com/donnie4w/tldb
 * https://githuc.com/donnie4w/tlmq-j
 */
package io.github.donnie4w.tlmq.handler;

import io.github.donnie4w.tlmq.tldb.bean.JMqBean;

@FunctionalInterface
public interface PullJsonHandler {
    public void run(JMqBean mb);
}
