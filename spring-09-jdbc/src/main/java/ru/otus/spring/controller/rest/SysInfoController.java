package ru.otus.spring.controller.rest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.spring.dto.SysInfoDto;

@RestController
public class SysInfoController {

    @PostMapping("/sysinfo")
    public SysInfoDto getSysInfo(SysInfoDto sysinfo) {
        return sysinfo;
    }
}
