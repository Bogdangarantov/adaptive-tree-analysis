package com.adaptivetreeanalysis.domain.benchmark.converter;

import com.adaptivetreeanalysis.domain.benchmark.enums.ExperimentStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class ExperimentStatusConverter implements AttributeConverter<ExperimentStatus, String> {

    @Override
    public String convertToDatabaseColumn(ExperimentStatus attribute) {
        return attribute == null ? null : attribute.value();
    }

    @Override
    public ExperimentStatus convertToEntityAttribute(String dbData) {
        return dbData == null ? null : ExperimentStatus.fromValue(dbData);
    }
}
