package com.mindalliance.sb;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Simple service that keeps track of when the application was started.
 * This is used to figure out last modification time for http query. 
 * Modification time should always be greater or equals to the startup time...
 */
@Service
public class TimeKeeper implements InitializingBean {

    private final AtomicLong startupTime = new AtomicLong(); 
    
    @Override
    public void afterPropertiesSet() throws Exception {
        startupTime.set( System.currentTimeMillis() );
    }
    
    public long getStartupTime() {
        return startupTime.get();    
    }
}
