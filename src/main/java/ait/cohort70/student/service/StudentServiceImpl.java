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
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Service // Аннотация для обозначения сервисного слоя
@RequiredArgsConstructor // Автоматически генерирует конструктор с обязательными полями (final)
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
    private final ModelMapper modelMapper;

    // Добавляет нового студента
    @Override
    public void addStudent(StudentCredentialsDto studentCredentialsDto) {
        if (!studentRepository.existsById(studentCredentialsDto.getId())) {
            Student student = modelMapper.map(studentCredentialsDto, Student.class);
            studentRepository.save(student);
        } else {
            throw new EntityExistsException("Student with ID " + studentCredentialsDto.getId() + " already exists!");
        }
    }

    // Найти студента по ID
    @Override
    public StudentDto findStudent(Long id) {
        Student student = studentRepository.findById(id).orElseThrow(NotFoundException::new);
        return modelMapper.map(student, StudentDto.class);
    }

    // Удалить студента по ID
    @Override
    public StudentDto removeStudent(Long id) {
        Student student = studentRepository.findById(id).orElseThrow(NotFoundException::new);
        studentRepository.deleteById(id);
        return modelMapper.map(student, StudentDto.class);
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
        return modelMapper.map(student, StudentCredentialsDto.class);
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
        return studentRepository.findByNameIgnoreCase(name)
                .map(s -> modelMapper.map(s, StudentDto.class))
                .toList();
    }

    // Подсчитать количество студентов по набору имен
    @Override
    public Long countStudentsByNames(Set<String> names) {
        if (names == null || names.isEmpty()) {
            return 0L;
        }
        Set<String> filteredNames = names.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .collect(toSet());
        if (filteredNames.isEmpty()) {
            return 0L;
        }
        return studentRepository.countByNameInIgnoreCase(filteredNames);
    }

    // Найти студентов, сдавших определенный экзамен на минимальный балл
    @Override
    public List<StudentDto> findStudentsByExamNameMinScore(String examName, Integer minScore) {
        return studentRepository.findByExamAndScoreGreaterThan(examName, minScore)
                .map(s -> modelMapper.map(s, StudentDto.class))
                .toList();
    }
}
