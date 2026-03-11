package com.adaptivetreeanalysis.domain.benchmark.converter;

import com.adaptivetreeanalysis.domain.benchmark.enums.TreeType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class TreeTypeConverter implements AttributeConverter<TreeType, String> {

    @Override
    public String convertToDatabaseColumn(TreeType attribute) {
        return attribute == null ? null : attribute.value();
    }

    @Override
    public TreeType convertToEntityAttribute(String dbData) {
        return dbData == null ? null : TreeType.fromValue(dbData);
    }
}
