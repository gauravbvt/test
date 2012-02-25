package com.mindalliance.channels.core.community.notification;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/14/12
 * Time: 9:58 AM
 */
public interface NotificationService {

    void sendUrgentNotifications();

    void sendBatchedFeedbackNotifications();
}
