package com.crossroadsinn.signups;

import org.junit.jupiter.api.Test;

import java.util.Map;

public class SignupsParserTest {

    private final SignupsParser sut = new SignupsParser();

    @Test
    public void getColumnIndicesWIthValidHeaderLine() {
        String[] headerLine = {"Timestamp", "Timestamp", "GW2 Account", "Discord Account", "Tier", "Additional comments:"};

        Map<String, Integer> columnIndices = sut.getColumnIndices(headerLine);

        System.out.println(columnIndices);
    }
}
