// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.mindpeer.services;

import com.mindalliance.mindpeer.WicketApplication;
import com.mindalliance.mindpeer.dao.UserDao;
import com.mindalliance.mindpeer.model.User;
import org.springframework.transaction.annotation.Transactional;

/**
 * ...
 */
public class MindPeerImpl implements MindPeer {

    private UserDao userDao;

    private Mailer mailer;

    private WicketApplication wicketApplication;

    /**
     * Create a new MindPeerImpl instance.
     */
    public MindPeerImpl() {
    }

    /**
     * Register a new user and send verification email.
     * @param user the new user. The confirmation number will be reset for security
     * @param path the URL used by the user to get to MindPeer, eg. (http://localhost:8080/ or
     * https://mindpeer.mind-alliance.com/...)  This makes sure the link sent in the confirmation
     * email is actually accessible by the new user. Notice the trailing slash...
     */
    @Transactional
    public void register( User user, String path ) {
        user.setConfirmation( User.TICKET_MIN + Math.round( Math.random() * User.TICKET_RANGE ) );
        User saved = userDao.save( user );
        String s = path + wicketApplication.getConfirmationURL( saved );
        mailer.sendConfirmation( saved, s );
    }

    /**
     * Return the MindPeerImpl's wicketApplication.
     * @return the value of wicketApplication
     */
    public WicketApplication getWicketApplication() {
        return wicketApplication;
    }

    /**
     * Sets the wicketApplication of this MindPeerImpl.
     * @param wicketApplication the new wicketApplication value.
     *
     */
    public void setWicketApplication( WicketApplication wicketApplication ) {
        this.wicketApplication = wicketApplication;
    }

    /**
     * Return the MindPeerImpl's mailer.
     * @return the value of mailer
     */
    public Mailer getMailer() {
        return mailer;
    }

    /**
     * Sets the mailer of this MindPeerImpl.
     * @param mailer the new mailer value.
     *
     */
    public void setMailer( Mailer mailer ) {
        this.mailer = mailer;
    }

    /**
     * Return the MindPeerImpl's userDao.
     * @return the value of userDao
     */
    public UserDao getUserDao() {
        return userDao;
    }

    /**
     * Sets the userDao of this MindPeerImpl.
     * @param userDao the new userDao value.
     *
     */
    public void setUserDao( UserDao userDao ) {
        this.userDao = userDao;
    }
}
