package com.mindalliance.channels.core.util;

import org.apache.wicket.request.Response;

import java.io.IOException;
import java.io.OutputStream;

/**
 * An HTTP Response as OutputStream.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 1/24/12
 * Time: 1:56 PM
 */
public class ResponseOutputStream  extends OutputStream {

    private final Response response;
    private final byte[] singleByteBuffer = new byte[1];

    public ResponseOutputStream(Response response) {
        this.response = response;
    }
    @Override
    public void write(int b) throws IOException {
        singleByteBuffer[0] = (byte) b;
        write(singleByteBuffer);
    }
    @Override
    public void write(byte[] b) throws IOException {
        response.write(b);
    }
    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        if (off == 0 && len == b.length) {
            this.write(b);
        } else {
            super.write(b, off, len);
        }
    }
}