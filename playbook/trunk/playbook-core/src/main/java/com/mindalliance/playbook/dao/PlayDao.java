package com.mindalliance.playbook.dao;

import com.mindalliance.playbook.model.Play;
import org.springframework.security.access.annotation.Secured;

/**
 * Play accessor.
 */
@Secured( "ROLE_USER" )
public interface PlayDao extends IndexedDao<Play,Long> {

}
