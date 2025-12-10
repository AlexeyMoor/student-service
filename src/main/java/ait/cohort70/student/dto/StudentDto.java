package ait.cohort70.student.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StudentDto { // ответ с информацией о студенте (GET)
    private Long id;
    private String name;
    private Map<String, Integer> scores;
}
