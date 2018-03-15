package com.besafx.app.auditing;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

public class MyEntityListener {

    private static final Logger log = LoggerFactory.getLogger(MyEntityListener.class);

    @PrePersist
    public void prePersist(Object object) {
        perform(object, Action.INSERTED);
    }

    @PreUpdate
    public void preUpdate(Object object) {
        perform(object, Action.UPDATED);
    }

    @PreRemove
    public void preRemove(Object object) {
        perform(object, Action.DELETED);
    }

    @javax.transaction.Transactional(javax.transaction.Transactional.TxType.MANDATORY)
    public void perform(Object object, Action action) {

    }
}
