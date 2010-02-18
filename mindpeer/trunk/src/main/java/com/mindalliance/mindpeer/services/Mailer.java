// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.mindpeer.services;

import com.mindalliance.mindpeer.model.User;

/**
 * The service responsible for sending mail to users.
 */
public interface Mailer {

    /**
     * Send an account creation email to the given user.
     * @param user the user
     * @param link the link to include in the message
     */
    void sendConfirmation( User user, CharSequence link );
}
