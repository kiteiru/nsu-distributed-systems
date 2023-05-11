package ru.kiteiru.types;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.annotation.Id;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.Instant;

@Data
@JsonIgnoreProperties({"uuid", "hash", "maxLength", "partCount", "startTime", "finishedParts"})
@NoArgsConstructor
public class Task {
    @Id
    @NonNull
    String uuid;
    private String status;
    private String hash;
    private int maxLength;
    private List<String> answer;
    private Integer partCount;
    private Instant startTime;
    private List<Integer> finishedParts;

    public Task(String status) {
        this.status = status;
        this.answer = new ArrayList<>();
    }

    public Task(String uuid, String hash, int maxLength, Integer partCount) {
        this.uuid = uuid;
        this.status = "IN_PROGRESS";
        this.hash = hash;
        this.maxLength = maxLength;
        this.answer = new ArrayList<>();
        this.partCount = partCount;
        this.startTime = Instant.now();
        this.finishedParts = new ArrayList<>();
    }
}
