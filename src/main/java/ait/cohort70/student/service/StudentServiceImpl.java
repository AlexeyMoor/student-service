package ait.cohort70.student.service;

import ait.cohort70.student.dao.StudentRepository;
import ait.cohort70.student.dto.ScoreDto;
import ait.cohort70.student.dto.StudentCredentialsDto;
import ait.cohort70.student.dto.StudentDto;
import ait.cohort70.student.dto.StudentUpdateDto;
import ait.cohort70.student.dto.exceptions.EntityExistsException;
import ait.cohort70.student.dto.exceptions.NotFoundException;
import ait.cohort70.student.model.Student;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Service // Аннотация для обозначения сервисного слоя
@RequiredArgsConstructor // Автоматически генерирует конструктор с обязательными полями (final)
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;

    // Добавляет нового студента
    @Override
    public void addStudent(StudentCredentialsDto studentCredentialsDto) {
        if (studentRepository.findById(studentCredentialsDto.getId()).isEmpty()) {
            Student student = new Student(
                    studentCredentialsDto.getId(),
                    studentCredentialsDto.getName(),
                    studentCredentialsDto.getPassword());
            studentRepository.save(student);
        } else {
            throw new EntityExistsException("Student with ID " + studentCredentialsDto.getId() + " already exists!");
        }
    }

    // Найти студента по ID
    @Override
    public StudentDto findStudent(Long id) {
        Student student = studentRepository.findById(id).orElseThrow(NotFoundException::new);
        return new StudentDto(
                student.getId(),
                student.getName(),
                student.getScores()
        );
    }

    // Удалить студента по ID
    @Override
    public StudentDto removeStudent(Long id) {
        Student student = studentRepository.findById(id).orElseThrow(NotFoundException::new);
        studentRepository.deleteById(id);
        return new StudentDto(
                student.getId(),
                student.getName(),
                student.getScores()
        );
    }

    // Обновить информацию о студенте по ID (имя и/или пароль)
    @Override
    public StudentCredentialsDto updateStudent(Long id, StudentUpdateDto studentUpdateDto) {
        Student student = studentRepository.findById(id).orElseThrow(NotFoundException::new);
        if (studentUpdateDto.getName() != null && !studentUpdateDto.getName().isBlank()) {
            student.setName(studentUpdateDto.getName());
        }
        if (studentUpdateDto.getPassword() != null && !studentUpdateDto.getPassword().isBlank()) {
            student.setPassword(studentUpdateDto.getPassword());
        }
        studentRepository.save(student);
        return new StudentCredentialsDto(
                student.getId(),
                student.getName(),
                student.getPassword()
        );
    }

    // Добавить оценку студенту по ID
    @Override
    public void addScore(Long id, ScoreDto scoreDto) {
        Student student = studentRepository.findById(id).orElseThrow(NotFoundException::new);
        if (scoreDto.getExamName() == null || scoreDto.getExamName().isBlank() || scoreDto.getScore() == null) {
            throw new IllegalArgumentException("Exam name and score must be provided");
        }
        student.addScore(scoreDto.getExamName(), scoreDto.getScore());
        studentRepository.save(student);
    }

    // Найти студентов по имени
    @Override
    public List<StudentDto> findStudentsByName(String name) {
        return studentRepository.findAll().stream()
                .filter(s -> s.getName() != null && s.getName().equalsIgnoreCase(name))
                .map(s -> new StudentDto(s.getId(), s.getName(), s.getScores()))
                .toList();
    }

    // Подсчитать количество студентов по набору имен
    @Override
    public Long countStudentsByNames(Set<String> names) {
        if (names == null || names.isEmpty()) {
            return 0L;
        }
        Set<String> lowerCaseNames = names.stream()
                .filter(Objects::nonNull)
                .map(String::toLowerCase)
                .collect(toSet());
        return studentRepository.findAll().stream()
                .filter(student -> student.getName() != null)
                .map(student -> student.getName().toLowerCase())
                .filter(lowerCaseNames::contains)
                .count();
    }

    // Найти студентов, сдавших определенный экзамен на минимальный балл
    @Override
    public List<StudentDto> findStudentsByExamNameMinScore(String examName, Integer minScore) {
        return studentRepository.findAll().stream()
                .filter(s -> s.getScores() != null && s.getScores().getOrDefault(examName, Integer.MIN_VALUE) >= minScore)
                .map(s -> new StudentDto(s.getId(), s.getName(), s.getScores()))
                .toList();
    }
}
