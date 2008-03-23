package com.mindalliance.channels.playbook.geo
/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 23, 2008
 * Time: 11:36:31 AM
 */
class UnknownAreaException extends AreaException {
    UnknownAreaException() {
        super()
    }

    UnknownAreaException(String message) {
        super(message)
    }

    UnknownAreaException(Throwable cause) {
        super(cause)
    }

    UnknownAreaException(String message, Throwable cause) {
        super(message, cause)
    }

}