package com.adaptivetreeanalysis.domain.benchmark.converter;

import com.adaptivetreeanalysis.domain.benchmark.enums.DatasetDistributionType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class DatasetDistributionTypeConverter implements AttributeConverter<DatasetDistributionType, String> {

    @Override
    public String convertToDatabaseColumn(DatasetDistributionType attribute) {
        return attribute == null ? null : attribute.value();
    }

    @Override
    public DatasetDistributionType convertToEntityAttribute(String dbData) {
        return dbData == null ? null : DatasetDistributionType.fromValue(dbData);
    }
}
