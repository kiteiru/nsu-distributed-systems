package ru.kiteiru.service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import jakarta.security.auth.message.callback.PrivateKeyCallback.Request;

import ru.kiteiru.types.CrackHashManagerRequest;
import ru.kiteiru.types.CrackHashWorkerResponse;
import ru.kiteiru.types.HashAndLength;
import ru.kiteiru.types.RequestId;
import ru.kiteiru.types.Task;
import ru.kiteiru.repository.TaskRepository;

@Service
public class ManagerService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final static String LETTERS_AND_DIGITS = "abcdefghijklmnopqrstuvwxyz0123456789";
    private final Duration taskTimeout = Duration.parse("PT5M");

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    AmqpTemplate rabbitTemplate;

    @Autowired
    private Queue requestQueue;

    @Autowired
    MongoTemplate mongoTemplate;

    private CrackHashManagerRequest.Alphabet initAlphabet() {
        CrackHashManagerRequest.Alphabet alphabet = new CrackHashManagerRequest.Alphabet();

        for (String charString : LETTERS_AND_DIGITS.split("")) {
            alphabet.getSymbols().add(charString);
        }
        // System.out.println(alphabet.getSymbols());

        return alphabet;
    }

    public RequestId getRequestId(HashAndLength body) {

        RequestId requestId = new RequestId(UUID.randomUUID().toString());

        CrackHashManagerRequest request = new CrackHashManagerRequest();

        request.setRequestId(requestId.getRequestId());
        request.setHash(body.getHash());
        request.setMaxLength(body.getMaxLength());
        request.setAlphabet(initAlphabet());
        // System.out.println("REQ ALPH" + request.getAlphabet().getSymbols());

        int partCount = 4;
        request.setPartCount(partCount); // на сколько частей будет поделены комбинации

        // request.setPartNumber(0); // какая по счету часть достается воркеру
        // restTemplate.postForObject(workerUrl + "/internal/api/worker/hash/crack/task", request, Void.class);

        taskRepository.save(new Task(requestId.getRequestId(), body.getHash(), body.getMaxLength(), partCount));

        for (int part = 0; part < partCount; part++) {
            request.setPartNumber(part); // какая по счету часть достается воркеру
            rabbitTemplate.convertAndSend(requestQueue.getName(), request);
        }


        return requestId;
    }

    public Task getTaskStatus(RequestId id) {

        Optional<Task> result = taskRepository.findById(id.getRequestId());
        if (result.isEmpty()){
            return new Task("NOT_FOUND");
        }
        Task task = result.get();

        Duration dur = Duration.between(task.getStartTime(), Instant.now());
        if (dur.toMillis() > taskTimeout.toMillis() && task.getAnswer().isEmpty()) {
            task.setStatus("TIMEOUT");
            taskRepository.save(task);
        }

        return task;
    }

    public void recieveAnswer(CrackHashWorkerResponse response) {
        Optional<Task> result = taskRepository.findById(response.getRequestId());
        if (result.isEmpty()) {
            return;
        }
        Task task = result.get();

        List<String> words = response.getAnswers().getWords();

        if (words.get(0).equals("")) {
            task.getFinishedParts().add(response.getPartNumber());
            if (task.getPartCount() == task.getFinishedParts().size()) {
                task.setStatus("READY");
            }    
        } else {
            System.out.println("Received answer from part "+ response.getPartNumber() + ": " + words.toString() +
                                ", took: " + Duration.between(task.getStartTime(), Instant.now()).toMillis() / 1000 + "s");
        }
        
        for (String word : words) {
            if (!task.getAnswer().contains(word) && !word.equals("")) {
                task.getAnswer().add(word);
            }
        }
        
        taskRepository.save(task);
    }
}