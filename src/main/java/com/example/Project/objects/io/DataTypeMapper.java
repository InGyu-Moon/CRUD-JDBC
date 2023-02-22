package com.example.Project.objects.io;

import org.apache.poi.ss.usermodel.CellType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataTypeMapper {

    private Map<String, List<CellType>> dataTypeMapping;

    public DataTypeMapper() {
        dataTypeMapping = new HashMap<>();
        dataTypeMapping.put("VARCHAR2", Arrays.asList(CellType.STRING));
        dataTypeMapping.put("NUMBER", Arrays.asList(CellType.NUMERIC, CellType.FORMULA));
        dataTypeMapping.put("DATE", Arrays.asList(CellType.NUMERIC, CellType.FORMULA));
    }

    public boolean areTypesEquivalent(String dbType, CellType cellType) {
        return dataTypeMapping.containsKey(dbType) && dataTypeMapping.get(dbType).contains(cellType);
    }
}

