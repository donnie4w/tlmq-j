/*
 * Copyright 2023 tldb Author. All Rights Reserved.
 * email: donnie4w@gmail.com
 * https://githuc.com/donnie4w/tldb
 * https://githuc.com/donnie4w/tlcli-j
 */
package io.github.donnie4w.tlmq.cli;

public class TlRunTimeException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public TlRunTimeException() {
        super();
    }

    public TlRunTimeException(String message) {
        super(message);
    }

    public TlRunTimeException(Throwable cause) {
        super(cause);
    }

    public TlRunTimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
