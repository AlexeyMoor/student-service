package ait.cohort70.student.controller;

import ait.cohort70.student.dto.ScoreDto;
import ait.cohort70.student.dto.StudentCredentialsDto;
import ait.cohort70.student.dto.StudentDto;
import ait.cohort70.student.dto.StudentUpdateDto;
import ait.cohort70.student.dto.exceptions.EntityExistsException;
import ait.cohort70.student.dto.exceptions.NotFoundException;
import ait.cohort70.student.service.StudentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
public class StudentControllerTest {
    private final long studentId = 1000L;
    private final String name = "John";
    private final String password = "1234";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private StudentService studentService;

    private StudentCredentialsDto studentCredentialsDto;
    private StudentDto studentDto;

    @BeforeEach
    void setUp() {
        studentCredentialsDto = new StudentCredentialsDto(studentId, name, password);
        studentDto = new StudentDto(studentId, name, null);
    }

    @Test
    void testAddStudentWhenStudentDoesNotExist() throws Exception {
        doNothing().when(studentService).addStudent(any(StudentCredentialsDto.class));

        mockMvc.perform(post("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentCredentialsDto)))
                .andExpect(status().isNoContent());

        verify(studentService, times(1)).addStudent(any(StudentCredentialsDto.class));
    }

    @Test
    void testAddStudentWhenStudentExists() throws Exception {
        doThrow(new EntityExistsException("Student already exists"))
                .when(studentService).addStudent(any(StudentCredentialsDto.class));

        mockMvc.perform(post("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentCredentialsDto)))
                .andExpect(status().isConflict());
    }

    @Test
    void testFindStudentWhenStudentExists() throws Exception {
        when(studentService.findStudent(studentId)).thenReturn(studentDto);

        mockMvc.perform(get("/student/{id}", studentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(studentId))
                .andExpect(jsonPath("$.name").value(name));

        verify(studentService, times(1)).findStudent(studentId);
    }

    @Test
    void testFindStudentWhenStudentNotExists() throws Exception {
        when(studentService.findStudent(studentId)).thenThrow(new NotFoundException());

        mockMvc.perform(get("/student/{id}", studentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testRemoveStudent() throws Exception {
        when(studentService.removeStudent(studentId)).thenReturn(studentDto);

        mockMvc.perform(delete("/student/{id}", studentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(studentId))
                .andExpect(jsonPath("$.name").value(name));

        verify(studentService, times(1)).removeStudent(studentId);
    }

    @Test
    void testUpdateStudent() throws Exception {
        String newName = "Jane";
        StudentUpdateDto updateDto = new StudentUpdateDto(newName, null);
        StudentCredentialsDto updatedDto = new StudentCredentialsDto(studentId, newName, password);
        when(studentService.updateStudent(eq(studentId), any(StudentUpdateDto.class)))
                .thenReturn(updatedDto);

        mockMvc.perform(patch("/student/{id}", studentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(studentId))
                .andExpect(jsonPath("$.name").value(newName))
                .andExpect(jsonPath("$.password").value(password));

        verify(studentService, times(1)).updateStudent(eq(studentId), any(StudentUpdateDto.class));
    }

    @Test
    void testAddScore() throws Exception {
        String examName = "Java Exam";
        int score = 100;
        ScoreDto scoreDto = new ScoreDto(examName, score);
        doNothing().when(studentService).addScore(eq(studentId), any(ScoreDto.class));

        mockMvc.perform(patch("/score/student/{id}", studentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(scoreDto)))
                .andExpect(status().isOk());

        verify(studentService, times(1)).addScore(eq(studentId), any(ScoreDto.class));
    }

    @Test
    void testFindStudentsByName() throws Exception {
        List<StudentDto> students = List.of(studentDto);
        when(studentService.findStudentsByName(name)).thenReturn(students);

        mockMvc.perform(get("/students/name/{name}", name))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(studentId))
                .andExpect(jsonPath("$[0].name").value(name));

        verify(studentService, times(1)).findStudentsByName(name);
    }

    @Test
    void testCountStudentsByNames() throws Exception {
        Set<String> names = Set.of(name, "Jane", "Peter", "Mary");
        when(studentService.countStudentsByNames(names)).thenReturn(2L);

        mockMvc.perform(get("/quantity/students")
                        .param("names", name)
                        .param("names", "Jane")
                        .param("names", "Peter")
                        .param("names", "Mary"))
                .andExpect(status().isOk())
                .andExpect(content().string("2"));

        verify(studentService, times(1)).countStudentsByNames(names);
    }

    @Test
    void testFindStudentsByExamNameMinScore() throws Exception {
        String examName = "Java Exam";
        int minScore = 100;
        List<StudentDto> students = List.of(studentDto);
        when(studentService.findStudentsByExamNameMinScore(examName, minScore)).thenReturn(students);

        mockMvc.perform(get("/students/exam/{examName}/minscore/{minScore}", examName, minScore))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(studentId))
                .andExpect(jsonPath("$[0].name").value(name));

        verify(studentService, times(1)).findStudentsByExamNameMinScore(examName, minScore);
    }
}
