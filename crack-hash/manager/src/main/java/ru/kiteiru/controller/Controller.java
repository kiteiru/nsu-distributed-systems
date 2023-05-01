package ru.kiteiru.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ru.kiteiru.json.HashAndLength;
import ru.kiteiru.json.RequestId;
import ru.kiteiru.json.TaskStatus;
import ru.kiteiru.service.ManagerService;

@RestController
@RequestMapping("/api")
public class Controller {

    private final ManagerService service;

    @Autowired
    public Controller(ManagerService service) {
        this.service = service;
    }

    @PostMapping("/hash/crack")
    public RequestId postMethod(@RequestBody HashAndLength body) {
        return service.getRequestId(body);
    }

    @GetMapping("/hash/status")
    public TaskStatus getMessage(RequestId id) {
        return service.getTaskStatus(id);
    }
}
