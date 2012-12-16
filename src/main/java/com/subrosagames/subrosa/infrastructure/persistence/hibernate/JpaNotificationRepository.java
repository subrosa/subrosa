package com.subrosagames.subrosa.infrastructure.persistence.hibernate;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.subrosagames.subrosa.domain.notification.DeviceType;
import com.subrosagames.subrosa.domain.notification.NotificationRepository;
import com.subrosagames.subrosa.domain.notification.persistence.DeviceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 *
 */
@Repository
@Transactional
public class JpaNotificationRepository implements NotificationRepository {

    private static final Logger LOG = LoggerFactory.getLogger(JpaNotificationRepository.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<String> getDevicesForGame(int gameId) {
//        List<DeviceRegistration> resultList = entityManager
//                .createQuery("SELECT dr "
//                        + "     FROM DeviceRegistration dr "
//                        + "         INNER JOIN GameEntity g "
//                        + "         INNER JOIN TeamEntity t "
//                        + "         INNER JOIN PlayerEntity p "
//                        + "         INNER JOIN Account a "
//                        + "     WHERE g.id = :gameId AND a.id = dr.accountId", DeviceRegistration.class)
//                .setParameter("gameId", gameId)
//                .getResultList();
        List<DeviceRegistration> resultList = entityManager.createQuery("SELECT dr FROM DeviceRegistration dr", DeviceRegistration.class)
                .getResultList();
        return Lists.transform(resultList, new Function<DeviceRegistration, String>() {
            @Override
            public String apply(DeviceRegistration deviceRegistration) {
                return deviceRegistration.getRegistrationId();
            }
        });
    }

    @Override
    public void registerDevice(String registrationId, DeviceType deviceType) {
        LOG.debug("Persisting device registration: {}/{}", registrationId, deviceType);
        DeviceRegistration deviceRegistration = new DeviceRegistration();
        deviceRegistration.setAccountId(5);
        deviceRegistration.setRegistrationId(registrationId);
        deviceRegistration.setDeviceType(deviceType);
        entityManager.persist(deviceRegistration);
    }

    @Override
    public void unregisterDevice(String registrationId, DeviceType deviceType) {
        LOG.debug("Removing device registration: {}/{}", registrationId, deviceType);
        DeviceRegistration deviceRegistration = new DeviceRegistration();
        deviceRegistration.setRegistrationId(registrationId);
        deviceRegistration.setDeviceType(deviceType);
        entityManager.remove(deviceRegistration);
    }
}
