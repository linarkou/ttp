package ru.abzaltdinov.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Pair<T1, T2> {
    private T1 first;
    private T2 second;
}
