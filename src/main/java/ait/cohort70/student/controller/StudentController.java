package ait.cohort70.student.controller;

import ait.cohort70.student.dto.ScoreDto;
import ait.cohort70.student.dto.StudentCredentialsDto;
import ait.cohort70.student.dto.StudentDto;
import ait.cohort70.student.dto.StudentUpdateDto;
import ait.cohort70.student.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController // Аннотация для обозначения контроллера REST
@RequiredArgsConstructor // Автоматически генерирует конструктор с обязательными полями (final)
public class StudentController {
    private final StudentService studentService;

    @PostMapping("/student")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Устанавливает статус ответа HTTP на 204 No Content
    public void addStudent(@RequestBody StudentCredentialsDto studentCredentialsDto) {
        studentService.addStudent(studentCredentialsDto);
    }

    @GetMapping("/student/{id}")
    public StudentDto findStudent(@PathVariable Long id) {
        return studentService.findStudent(id);
    }

    @DeleteMapping("/student/{id}")
    public StudentDto removeStudent(@PathVariable Long id) {
        return studentService.removeStudent(id);
    }

    @PatchMapping("/student/{id}")
    public StudentCredentialsDto updateStudent(@PathVariable Long id, @RequestBody StudentUpdateDto studentUpdateDto) {
        return studentService.updateStudent(id, studentUpdateDto);
    }

    @PatchMapping("/score/student/{id}")
    public void addScore(@PathVariable Long id, @RequestBody ScoreDto scoreDto) {
        studentService.addScore(id, scoreDto);
    }

    @GetMapping("/students/name/{name}")
    public List<StudentDto> findStudentsByName(@PathVariable String name) {
        return studentService.findStudentsByName(name);
    }

    @GetMapping("/quantity/students")
    public Long countStudentsByNames(@RequestParam Set<String> names) {
        return studentService.countStudentsByNames(names);
    }

    @GetMapping("/students/exam/{examName}/minscore/{minScore}")
    public List<StudentDto> findStudentsByExamNameMinScore(@PathVariable String examName, @PathVariable Integer minScore) {
        return studentService.findStudentsByExamNameMinScore(examName, minScore);
    }
}
