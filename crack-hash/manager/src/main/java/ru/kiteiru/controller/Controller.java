package ru.kiteiru.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ru.kiteiru.service.ManagerService;
import ru.kiteiru.types.HashAndLength;
import ru.kiteiru.types.RequestId;
import ru.kiteiru.types.Task;

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
    public Task getMessage(RequestId id) {
        return service.getTaskStatus(id);
    }
}
