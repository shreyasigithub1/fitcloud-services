package com.fitcloud.gymservice.validation;

import com.fitcloud.gymservice.dto.request.GymRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TimeRangeValidator implements ConstraintValidator<ValidTimeRange, GymRequest> {

    @Override
    public boolean isValid(GymRequest gymRequest, ConstraintValidatorContext constraintValidatorContext) {
        return gymRequest.getClosingTime().isAfter(gymRequest.getOpeningTime());
    }
}
