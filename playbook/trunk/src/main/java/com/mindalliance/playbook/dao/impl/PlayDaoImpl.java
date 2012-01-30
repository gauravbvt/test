package com.mindalliance.playbook.dao.impl;

import com.mindalliance.playbook.dao.PlayDao;
import com.mindalliance.playbook.model.Play;
import org.springframework.stereotype.Repository;

/**
 * Hibernate implementation.
 */
@Repository
public class PlayDaoImpl extends IndexedHibernateDao<Play,Long> implements PlayDao {

}
