package ait.cohort70.student.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;

@Getter
@EqualsAndHashCode(of = "id") // сравнение только по id
@Document(collection = "students") // указываем имя коллекции в MongoDB для этого класса
public class Student {
    // @Id // указываем, что это поле - идентификатор документа в MongoDB
    private long id;
    @Setter
    private String name;
    @Setter
    private String password;
    private Map<String, Integer> scores = new HashMap<>();

    public Student(long id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }

    public void addScore(String examName, Integer score) {
        this.scores.put(examName, score); // put - добавляет или обновляет значение по ключу
    }
}
