package com.mindalliance.channels.playbook.geo
/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 23, 2008
 * Time: 11:36:53 AM
 */
class ServiceFailureAreaException extends AreaException {

    ServiceFailureAreaException() {
        super()
    }

    ServiceFailureAreaException(String message) {
        super(message)
    }

    ServiceFailureAreaException(Throwable cause) {
        super(cause)
    }

    ServiceFailureAreaException(String message, Throwable cause) {
        super(message, cause)
    }

}