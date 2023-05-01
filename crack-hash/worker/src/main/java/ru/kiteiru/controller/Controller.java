package ru.kiteiru.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.kiteiru.json.CrackHashManagerRequest;
import ru.kiteiru.service.WorkerService;

@RestController
@RequestMapping("/internal")
public class Controller {

    private final WorkerService service;

    @Autowired
    public Controller(WorkerService service) {
        this.service = service;
    }

    @PostMapping("/api/worker/hash/crack/task")
    public void postMethod(@RequestBody CrackHashManagerRequest body) {
        service.crackHashTask(body);
    }


}
