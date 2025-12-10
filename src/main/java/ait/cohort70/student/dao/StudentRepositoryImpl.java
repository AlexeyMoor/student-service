package ait.cohort70.student.dao;

import ait.cohort70.student.model.Student;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class StudentRepositoryImpl implements StudentRepository {
    private final Map<Long, Student> students = new ConcurrentHashMap<>(); // потокобезопасное хранилище студентов

    // сохраняет студента в хранилище
    @Override
    public Student save(Student student) {
        students.put(student.getId(), student);
        return student;
    }

    // ищет студента по id
    @Override
    public Optional<Student> findById(Long id) {
        return Optional.ofNullable(students.get(id));
    }

    // удаляет студента по id
    @Override
    public void deleteById(Long id) {
        students.remove(id);
    }

    // возвращает всех студентов в виде списка
    @Override
    public List<Student> findAll() {
        return new ArrayList<>(students.values());
    }
}
