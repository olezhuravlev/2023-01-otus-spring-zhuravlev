package ru.otus.webapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SysInfoDto {
    private final String osName;
    private final String timeZone;
    private final String osArch;
    private final String cpuCount;
}
