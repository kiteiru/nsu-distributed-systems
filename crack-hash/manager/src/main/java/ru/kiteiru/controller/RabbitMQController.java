package ru.kiteiru.controller;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.kiteiru.types.CrackHashWorkerResponse;
import ru.kiteiru.service.ManagerService;

@Component
@RabbitListener(queues = "${rabbitmq.response.queue}", id = "manager")
public class RabbitMQController {
    @Autowired
    ManagerService managerService;

    @RabbitHandler
    public void receiver(CrackHashWorkerResponse response) {
        managerService.recieveAnswer(response);
    }
    
}
