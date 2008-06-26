package com.mindalliance.channels.playbook.mem
/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 26, 2008
 * Time: 11:42:30 AM
 */
class RefLockException extends RuntimeException {

    RefLockException() {}

    RefLockException(String message) {
        super(message)
    }

    RefLockException(String message, Throwable cause) {
        super(message, cause)
    }

}