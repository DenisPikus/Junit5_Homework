package com.dmdev.dao;

import com.dmdev.entity.Provider;
import com.dmdev.entity.Status;
import com.dmdev.entity.Subscription;
import com.dmdev.integration.IntegrationTestBase;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SubscriptionDaoTestIT extends IntegrationTestBase {

    private SubscriptionDao subscriptionDao = SubscriptionDao.getInstance();

    @Test
    void findAll() {
        Subscription subscription1 = subscriptionDao.insert(getSubscription(1, "Ivan"));
        Subscription subscription2 = subscriptionDao.insert(getSubscription(2, "Vova"));
        Subscription subscription3 = subscriptionDao.insert(getSubscription(3, "Sveta"));

        List<Subscription> actualResult = subscriptionDao.findAll();

        assertThat(actualResult).hasSize(3);
        List<Integer> subscriptionsIds = actualResult.stream()
                .map(Subscription::getId)
                .toList();
        assertThat(subscriptionsIds).contains(subscription1.getId(), subscription2.getId(), subscription3.getId());
    }

    @Test
    void findById() {
        Subscription expectedResult = subscriptionDao.insert(getSubscription(1, "Ivan"));

        Optional<Subscription> actualResult = subscriptionDao.findById(expectedResult.getId());

        assertThat(actualResult).isPresent();
        assertThat(actualResult.get()).isEqualTo(expectedResult);
    }

    @Test
    void delete() {
        Subscription subscription = subscriptionDao.insert(getSubscription(1, "Ivan"));

        boolean actualResult = subscriptionDao.delete(subscription.getId());

        assertTrue(actualResult);
    }

    @Test
    void update() {
        Subscription expectedResult = getSubscription(1, "Ivan");
        subscriptionDao.insert(expectedResult);
        expectedResult.setName("Gena");
        expectedResult.setStatus(Status.CANCELED);

        Subscription actualResult = subscriptionDao.update(expectedResult);

        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void insert() {
        Subscription expectedResult = getSubscription(1, "Ivan");

        Subscription actualResult = subscriptionDao.insert(expectedResult);

        assertNotNull(actualResult.getId());
        assertThat(actualResult).isEqualTo(expectedResult);

    }

    @Test
    void findByUserId() {
        Subscription expectedResult = subscriptionDao.insert(getSubscription(99, "Ivan"));

        List<Subscription> subscriptions = subscriptionDao.findByUserId(expectedResult.getUserId());

        assertThat(subscriptions).hasSize(1);
        Subscription actualResult = subscriptions.get(0);
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    private static Subscription getSubscription(Integer userId, String name) {
        return Subscription.builder()
                .userId(userId)
                .name(name)
                .provider(Provider.GOOGLE)
                .expirationDate(Instant.parse("2024-12-03T10:15:30.00Z"))
                .status(Status.ACTIVE)
                .build();
    }
}
