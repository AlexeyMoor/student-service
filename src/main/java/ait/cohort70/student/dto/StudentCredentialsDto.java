package ait.cohort70.student.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StudentCredentialsDto { // учетные данные студента (POST)
    private Long id;
    private String name;
    private String password;
}
