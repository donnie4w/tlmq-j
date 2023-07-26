/*
 * Copyright 2023 tldb Author. All Rights Reserved.
 * email: donnie4w@gmail.com
 * https://githuc.com/donnie4w/tldb
 * https://githuc.com/donnie4w/tlcli-j
 */
package io.github.donnie4w.tlmq.cli;

public class TlException extends Exception{
    private static final long serialVersionUID = 1L;

    public TlException() {
        super();
    }

    public TlException(String message) {
        super(message);
    }

    public TlException(Throwable cause) {
        super(cause);
    }

    public TlException(String message, Throwable cause) {
        super(message, cause);
    }
}
