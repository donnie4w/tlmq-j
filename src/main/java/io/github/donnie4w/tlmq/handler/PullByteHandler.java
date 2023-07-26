/*
 * Copyright 2023 tldb Author. All Rights Reserved.
 * email: donnie4w@gmail.com
 * https://githuc.com/donnie4w/tldb
 * https://githuc.com/donnie4w/tlmq-j
 */
package io.github.donnie4w.tlmq.handler;

import io.github.donnie4w.tlmq.tldb.bean.MqBean;

@FunctionalInterface
public interface PullByteHandler {
    public void run(MqBean mb);
}
