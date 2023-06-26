package com.dmdev.validator;

import com.dmdev.dto.CreateSubscriptionDto;
import com.dmdev.entity.Provider;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

class CreateSubscriptionValidatorTest {

    private final CreateSubscriptionValidator validator = CreateSubscriptionValidator.getInstance();

    @Test
    void shouldPassValidation() {
        CreateSubscriptionDto subscriptionDto = CreateSubscriptionDto.builder()
                .userId(1)
                .name("Ivan")
                .provider(Provider.GOOGLE.name())
                .expirationDate(Instant.parse("2024-12-03T10:15:30.00Z"))
                .build();

        ValidationResult actualResult = validator.validate(subscriptionDto);

        assertFalse(actualResult.hasErrors());
    }

    @Test
    void invalidUserIdValidation() {
        CreateSubscriptionDto subscriptionDto = CreateSubscriptionDto.builder()
                .userId(null)
                .name("Ivan")
                .provider(Provider.GOOGLE.name())
                .expirationDate(Instant.parse("2024-12-03T10:15:30.00Z"))
                .build();

        ValidationResult actualResult = validator.validate(subscriptionDto);

        assertThat(actualResult.getErrors()).hasSize(1);
        assertThat(actualResult.getErrors().get(0).getMessage()).isEqualTo("userId is invalid");
    }

    @Test
    void invalidUserNameValidation() {
        CreateSubscriptionDto subscriptionDto = CreateSubscriptionDto.builder()
                .userId(1)
                .name("")
                .provider(Provider.GOOGLE.name())
                .expirationDate(Instant.parse("2024-12-03T10:15:30.00Z"))
                .build();

        ValidationResult actualResult = validator.validate(subscriptionDto);

        assertThat(actualResult.getErrors()).hasSize(1);
        assertThat(actualResult.getErrors().get(0).getMessage()).isEqualTo("name is invalid");
    }

    @Test
    void invalidProviderValidation() {
        CreateSubscriptionDto subscriptionDto = CreateSubscriptionDto.builder()
                .userId(1)
                .name("Ivan")
                .provider(null)
                .expirationDate(Instant.parse("2024-12-03T10:15:30.00Z"))
                .build();

        ValidationResult actualResult = validator.validate(subscriptionDto);

        assertThat(actualResult.getErrors()).hasSize(1);
        assertThat(actualResult.getErrors().get(0).getMessage()).isEqualTo("provider is invalid");
    }

    @Test
    void expirationDateIsEqualToNullValidation() {
        CreateSubscriptionDto subscriptionDto = CreateSubscriptionDto.builder()
                .userId(1)
                .name("Ivan")
                .provider(Provider.GOOGLE.name())
                .expirationDate(null)
                .build();

        ValidationResult actualResult = validator.validate(subscriptionDto);

        assertThat(actualResult.getErrors()).hasSize(1);
        assertThat(actualResult.getErrors().get(0).getMessage()).isEqualTo("expirationDate is invalid");
    }

    @Test
    void expirationDateIsBeforeNowValidation() {
        CreateSubscriptionDto subscriptionDto = CreateSubscriptionDto.builder()
                .userId(1)
                .name("Ivan")
                .provider(Provider.GOOGLE.name())
                .expirationDate(Instant.parse("2020-12-03T10:15:30.00Z"))
                .build();

        ValidationResult actualResult = validator.validate(subscriptionDto);

        assertThat(actualResult.getErrors()).hasSize(1);
        assertThat(actualResult.getErrors().get(0).getMessage()).isEqualTo("expirationDate is invalid");
    }

    @Test
    void invalidAllArgumentsValidation() {
        CreateSubscriptionDto subscriptionDto = CreateSubscriptionDto.builder()
                .userId(null)
                .name("")
                .provider(null)
                .expirationDate(Instant.parse("2020-12-03T10:15:30.00Z"))
                .build();

        ValidationResult actualResult = validator.validate(subscriptionDto);

        assertThat(actualResult.getErrors()).hasSize(4);
        List<String> errorCodes = actualResult.getErrors().stream()
                .map(Error::getMessage)
                .collect(Collectors.toList());
        assertThat(errorCodes).contains("expirationDate is invalid", "name is invalid", "userId is invalid", "provider is invalid");
    }
}
