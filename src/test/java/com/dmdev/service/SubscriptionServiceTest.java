package com.dmdev.service;

import com.dmdev.dao.SubscriptionDao;
import com.dmdev.dto.CreateSubscriptionDto;
import com.dmdev.entity.Provider;
import com.dmdev.entity.Status;
import com.dmdev.entity.Subscription;
import com.dmdev.exception.SubscriptionException;
import com.dmdev.exception.ValidationException;
import com.dmdev.mapper.CreateSubscriptionMapper;
import com.dmdev.validator.CreateSubscriptionValidator;
import com.dmdev.validator.Error;
import com.dmdev.validator.ValidationResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    private SubscriptionDao subscriptionDao;
    @Mock
    private CreateSubscriptionMapper createSubscriptionMapper;
    @Mock
    private CreateSubscriptionValidator createSubscriptionValidator;
    @Mock
    private Clock clock;
    @InjectMocks
    private SubscriptionService subscriptionService;

    @Test
    void upsertSuccess() {
        CreateSubscriptionDto subscriptionDto = getSubscriptionDto();
        Subscription expectedResult = getSubscription(Status.ACTIVE);
        doReturn(new ValidationResult()).when(createSubscriptionValidator).validate(subscriptionDto);
        doReturn(List.of(expectedResult)).when(subscriptionDao).findByUserId(subscriptionDto.getUserId());
        doReturn(expectedResult).when(subscriptionDao).upsert(expectedResult);

        Subscription actualResult = subscriptionService.upsert(subscriptionDto);

        assertThat(actualResult).isEqualTo(expectedResult);
        verify(createSubscriptionValidator).validate(subscriptionDto);
        verify(subscriptionDao).findByUserId(subscriptionDto.getUserId());
        verify(subscriptionDao).upsert(expectedResult);
    }

    @Test
    void upsertThrowException() {
        CreateSubscriptionDto subscriptionDto = getSubscriptionDto();
        ValidationResult validationResult = new ValidationResult();
        validationResult.add(Error.of(100, "userId is invalid"));
        doReturn(validationResult).when(createSubscriptionValidator).validate(any());

        assertThrows(ValidationException.class, () -> subscriptionService.upsert(any()));
        verifyNoInteractions(subscriptionDao);
    }

    @Test
    void cancel() {
        Subscription expectedResult = getSubscription(Status.ACTIVE);
        doReturn(Optional.of(expectedResult)).when(subscriptionDao).findById(expectedResult.getId());

        subscriptionService.cancel(expectedResult.getId());

        verify(subscriptionDao).findById(expectedResult.getId());
        verify(subscriptionDao).update(expectedResult);
    }

    @Test
    void cancelThrowExceptionWhenSubscriptionIdIsAbsent() {
        doThrow(IllegalArgumentException.class).when(subscriptionDao).findById(any());

        assertThrows(IllegalArgumentException.class, () -> subscriptionService.cancel(any()));

        verify(subscriptionDao, times(0)).update(any());
    }

    @ParameterizedTest
    @EnumSource(
            value = Status.class,
            names = "ACTIVE",
            mode = EnumSource.Mode.EXCLUDE)
    void cancelThrowExceptionWhenSubscriptionStatusIsExpired(Status status) {
        Subscription expectedResult = getSubscription(status);
        doThrow(SubscriptionException.class).when(subscriptionDao).findById(expectedResult.getId());

        assertThrows(SubscriptionException.class, () -> subscriptionService.cancel(expectedResult.getId()));

        verify(subscriptionDao, times(0)).update(any());
    }

    @ParameterizedTest
    @EnumSource(
            value = Status.class,
            names = "EXPIRED",
            mode = EnumSource.Mode.EXCLUDE)
    void expire(Status status) {
        Subscription expectedResult = getSubscription(status);
        doReturn(Optional.of(expectedResult)).when(subscriptionDao).findById(expectedResult.getId());

        subscriptionService.expire(expectedResult.getId());

        verify(subscriptionDao).findById(expectedResult.getId());
        verify(subscriptionDao).update(expectedResult);
    }

    @Test
    void expireThrowExceptionWhenSubscriptionIdIsAbsent() {
        doThrow(IllegalArgumentException.class).when(subscriptionDao).findById(any());

        assertThrows(IllegalArgumentException.class, () -> subscriptionService.expire(any()));

        verify(subscriptionDao, times(0)).update(any());
    }

    @Test
    void expireThrowExceptionWhenSubscriptionStatusIsExpired() {
        Subscription expectedResult = getSubscription(Status.EXPIRED);
        doThrow(SubscriptionException.class).when(subscriptionDao).findById(expectedResult.getId());

        assertThrows(SubscriptionException.class, () -> subscriptionService.expire(expectedResult.getId()));

        verify(subscriptionDao, times(0)).update(any());
    }

    private static Subscription getSubscription(Status status) {
        return Subscription.builder()
                .id(1)
                .userId(1)
                .name("Ivan")
                .provider(Provider.GOOGLE)
                .expirationDate(Instant.parse("2024-12-03T10:15:30.00Z"))
                .status(status)
                .build();
    }

    private static CreateSubscriptionDto getSubscriptionDto() {
        return CreateSubscriptionDto.builder()
                .userId(1)
                .name("Ivan")
                .provider(Provider.GOOGLE.name())
                .expirationDate(Instant.parse("2024-12-03T10:15:30.00Z"))
                .build();
    }
}
