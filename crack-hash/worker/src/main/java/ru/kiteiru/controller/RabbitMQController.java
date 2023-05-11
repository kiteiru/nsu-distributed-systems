package ru.kiteiru.controller;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ru.kiteiru.types.CrackHashManagerRequest;
import ru.kiteiru.service.WorkerService;

@Component
@RabbitListener(queues = "${rabbitmq.request.queue}", id = "worker")
public class RabbitMQController {
    @Autowired
    WorkerService workerService;

    @RabbitHandler
    public void ReceiveTask(CrackHashManagerRequest task) {
        workerService.crackHashTask(task);
    }
}
