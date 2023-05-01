package ru.kiteiru.json;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class HashAndLength {
    private String hash;
    private Integer maxLength;
}
