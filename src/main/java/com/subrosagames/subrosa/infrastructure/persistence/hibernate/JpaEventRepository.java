package com.subrosagames.subrosa.infrastructure.persistence.hibernate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContexts;

import com.subrosagames.subrosa.domain.game.event.TriggerType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.subrosagames.subrosa.domain.game.event.EventRepository;
import com.subrosagames.subrosa.domain.game.persistence.EventEntity;
import com.subrosagames.subrosa.domain.game.persistence.TriggeredEventEntity;
import com.subrosagames.subrosa.event.Event;
import com.subrosagames.subrosa.event.message.EventMessage;

/**
 *
 */
@Repository
@Transactional
public class JpaEventRepository implements EventRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public TriggeredEventEntity createTriggeredEvent(EventMessage eventType, Event trigger) {
        TriggeredEventEntity triggeredEventEntity = new TriggeredEventEntity();
        triggeredEventEntity.setEventClass(eventType.getEventClass());
        triggeredEventEntity.setTriggerType(TriggerType.PREREQUISITE);
//        triggeredEventEntity.setTriggerEvent(findEvent(trigger));
        entityManager.merge(triggeredEventEntity);
        return triggeredEventEntity;
    }

}
