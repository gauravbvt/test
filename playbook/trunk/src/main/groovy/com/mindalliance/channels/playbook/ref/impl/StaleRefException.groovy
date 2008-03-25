package com.mindalliance.channels.playbook.ref.impl
/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 25, 2008
 * Time: 7:33:20 PM
 */
class StaleRefException extends RuntimeException {

   StaleRefException() {
       super()
   }

   StaleRefException(String message) {
       super(message)
   }

   StaleRefException(String message, Throwable cause) {
       super(message, cause)
   }

    StaleRefException(Throwable cause) {
        super(cause)
    }

}