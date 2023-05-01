package ru.kiteiru.service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.security.auth.message.callback.PrivateKeyCallback.Request;
import ru.kiteiru.json.CrackHashManagerRequest;
import ru.kiteiru.json.CrackHashWorkerResponse;
import ru.kiteiru.json.HashAndLength;
import ru.kiteiru.json.RequestId;
import ru.kiteiru.json.TaskStatus;

@Service
public class ManagerService {

    private final RestTemplate restTemplate = new RestTemplate();
    private ConcurrentHashMap<RequestId, TaskStatus> idAndStatus;
    private final static String LETTERS_AND_DIGITS = "abcdefghijklmnopqrstuvwxyz0123456789";
    private final Duration taskTimeout = Duration.parse("PT5M");

    public ManagerService() {
        this.idAndStatus = new ConcurrentHashMap<RequestId, TaskStatus>();
    }

    private CrackHashManagerRequest.Alphabet initAlphabet() {
        CrackHashManagerRequest.Alphabet alphabet = new CrackHashManagerRequest.Alphabet();

        for (String charString : LETTERS_AND_DIGITS.split("")) {
            alphabet.getSymbols().add(charString);
        }
        // System.out.println(alphabet.getSymbols());

        return alphabet;
    }

    public RequestId getRequestId(HashAndLength body) {

        List<String> addresses = getWorkers();

        RequestId requestId = new RequestId(UUID.randomUUID().toString());
        idAndStatus.put(requestId, new TaskStatus());


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

        for (int part = 0; part < partCount; part++) {
            int addrIdx = part % addresses.size();
            String addr = addresses.get(addrIdx);
            String workerUrl = "http://" + addr + ":8081";
            request.setPartNumber(part); // какая по счету часть достается воркеру
            restTemplate.postForObject(workerUrl + "/internal/api/worker/hash/crack/task", request, Void.class);
        }


        return requestId;
    }

    public TaskStatus getTaskStatus(RequestId id) {
        TaskStatus status = idAndStatus.get(id);
        System.out.println(status.toString());

        Duration dur = Duration.between(status.getStartTime(), Instant.now());
        if (dur.toMillis() > taskTimeout.toMillis() && status.getAnswer().isEmpty()) {
            status.setStatus("TIMEOUT");
        }

        return status;
    }

    public void recieveAnswer(CrackHashWorkerResponse response) {
        if (response.getAnswers().getWords().isEmpty()) {
            return;
        }

        TaskStatus status = idAndStatus.get(new RequestId(response.getRequestId()));
        if (status == null) {
            return;
        }
        System.out.println(status);

        status.getAnswer().addAll(response.getAnswers().getWords());
        status.setStatus("READY");
    }

    public List<String> getWorkers() {
        InetAddress[] machines = null;
        try {
            machines = InetAddress.getAllByName("worker");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        List<String> addresses = new ArrayList<>();
        for(InetAddress address : machines){
            addresses.add(address.getHostAddress());
            System.out.println(address.getHostAddress());
        }
        return addresses;
    }
}