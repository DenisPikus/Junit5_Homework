package com.dmdev.mapper;

import com.dmdev.dto.CreateSubscriptionDto;
import com.dmdev.entity.Provider;
import com.dmdev.entity.Status;
import com.dmdev.entity.Subscription;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class CreateSubscriptionMapperTest {

    private final CreateSubscriptionMapper mapper = CreateSubscriptionMapper.getInstance();

    @Test
    void map() {
        CreateSubscriptionDto subscriptionDto = CreateSubscriptionDto.builder()
                .userId(1)
                .name("Ivan")
                .provider(Provider.GOOGLE.name())
                .expirationDate(Instant.parse("2024-12-03T10:15:30.00Z"))
                .build();
        Subscription expectedResult = Subscription.builder()
                .userId(1)
                .name("Ivan")
                .provider(Provider.GOOGLE)
                .expirationDate(Instant.parse("2024-12-03T10:15:30.00Z"))
                .status(Status.ACTIVE)
                .build();

        Subscription actualResult = mapper.map(subscriptionDto);

        assertThat(actualResult).isEqualTo(expectedResult);
    }
}
