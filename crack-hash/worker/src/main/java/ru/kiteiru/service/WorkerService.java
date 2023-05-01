package ru.kiteiru.service;

import jakarta.xml.bind.DatatypeConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.support.NullValue;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.ws.wsdl.wsdl11.provider.SoapProvider;
import org.paukov.combinatorics.Generator;

import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

import org.springframework.web.client.RestTemplate;
import org.paukov.combinatorics.CombinatoricsFactory;
import org.paukov.combinatorics.ICombinatoricsVector;
import org.paukov.combinatorics.permutations.PermutationGenerator;
import org.paukov.combinatorics.util.ComplexCombinationGenerator;

import ru.kiteiru.json.CrackHashManagerRequest;
import ru.kiteiru.json.CrackHashWorkerResponse;

@Service
public class WorkerService {

    private final RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
    private final Duration taskTimeout = Duration.parse("PT5M");

    @Async
    public void crackHashTask(CrackHashManagerRequest body) {
        String ALPHABET = String.join("", body.getAlphabet().getSymbols());
        int ALPHABET_SIZE = ALPHABET.length();
        int POSITIONS_NUM = body.getMaxLength();
        int ALL_PARTS_NUM = body.getPartCount();
        int MY_PART_IDX = body.getPartNumber();
        byte[] HASH = DatatypeConverter.parseHexBinary(body.getHash());
        
        // double ALL_COMBINATIONS = Math.pow(POSITIONS_NUM, ALPHABET_SIZE);

        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch(Exception e) {
            e.printStackTrace();
        }

        ICombinatoricsVector<String> vector = CombinatoricsFactory.createVector(ALPHABET.split(""));

        Generator<String> gen = CombinatoricsFactory.createPermutationWithRepetitionGenerator(vector, POSITIONS_NUM);

        int idx = 0;
        System.out.println("Start count permutations...");
        Instant startTime = Instant.now();
        for (ICombinatoricsVector<String> perm : gen) {
            if (idx % ALL_PARTS_NUM == MY_PART_IDX) {
                // get hash of string check if equals our hash

                String str = String.join("", perm.getVector());
                // System.out.println("Combination: " + str);
                byte[] combHash = md5.digest(str.toString().getBytes());
                if (Arrays.equals(combHash, HASH)) {
                    System.out.println("I found hash: " + str.toString());
                    
                    sendAnswer(body.getRequestId(), str);
                }
            }

            Duration dur = Duration.between(startTime, Instant.now());
                if (dur.toMillis() > taskTimeout.toMillis()) {
                    System.out.println("Exceeded time limit: exiting");
                    return;
                }

            idx++;

        }
        System.out.println("End count permutations...");

    }

    private void sendAnswer(String id, String answer) {
        String managerUrl = "http://manager:8080";

        CrackHashWorkerResponse response = new CrackHashWorkerResponse();
        response.setRequestId(id);
        response.setAnswers(new CrackHashWorkerResponse.Answers());
        response.getAnswers().getWords().add(answer);

        restTemplate.patchForObject(managerUrl + "/internal/api/manager/hash/crack/request", response, Void.class);
    }

}
