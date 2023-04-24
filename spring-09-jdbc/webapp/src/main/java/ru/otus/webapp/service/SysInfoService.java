package ru.otus.webapp.service;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import ru.otus.webapp.dto.SysInfoDto;

@Service
public class SysInfoService {

    @Bean
    public SysInfoDto getSysInfo() {

        String osName = System.getProperty("os.name");
        String timeZone = System.getProperty("user.timezone");
        String osArch = System.getProperty("os.arch");
        String cpuCount = Integer.toString(Runtime.getRuntime().availableProcessors());

        return new SysInfoDto(osName, timeZone, osArch, cpuCount);
    }
}
