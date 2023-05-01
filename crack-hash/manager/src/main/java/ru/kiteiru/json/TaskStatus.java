package ru.kiteiru.json;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.scheduling.config.Task;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@JsonIgnoreProperties("startTime")
public class TaskStatus {
    private String status;
    private List<String> answer;
    private Instant startTime;

    public TaskStatus() {
        this.status = "IN_PROGRESS";
        this.answer = new ArrayList<String>();
        this.startTime = Instant.now();
    }
}
