package ru.kiteiru.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.kiteiru.json.CrackHashWorkerResponse;
import ru.kiteiru.service.ManagerService;

@RestController
@RequestMapping("/internal")
public class InternalController {
    private final ManagerService service;

    @Autowired
    public InternalController(ManagerService service) {
        this.service = service;
    }

    @PatchMapping("/api/manager/hash/crack/request")
    public void recieveAnswer(@RequestBody CrackHashWorkerResponse response) {
        service.recieveAnswer(response);
    }

}
