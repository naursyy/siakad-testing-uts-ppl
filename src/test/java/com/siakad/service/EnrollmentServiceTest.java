package com.siakad.service;

import com.siakad.exception.*;
import com.siakad.model.*;
import com.siakad.repository.CourseRepository;
import com.siakad.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit Test untuk EnrollmentService
 * Kombinasi STUB dan MOCK:
 * - STUB: mensimulasikan data tetap dari repository
 * - MOCK: memverifikasi pemanggilan method (update, sendEmail)
 */
@ExtendWith(MockitoExtension.class)
public class EnrollmentServiceTest {

    private static final String STUDENT_ID = "S123";
    private static final String COURSE_CODE = "PPL301";

    @Mock private StudentRepository studentRepository;
    @Mock private CourseRepository courseRepository;
    @Mock private NotificationService notificationService;
    @Mock private GradeCalculator gradeCalculator;

    @InjectMocks private EnrollmentService enrollmentService;

    private Student activeStudent;
    private Student suspendedStudent;
    private Course availableCourse;
    private Course fullCourse;

    @BeforeEach
    void setUp() {
        activeStudent = new Student(STUDENT_ID, "Naura", "naura@mail.com", "TI", 4, 3.7, "ACTIVE");
        suspendedStudent = new Student("S456", "Rina", "rina@mail.com", "SI", 5, 1.0, "SUSPENDED");

        availableCourse = new Course(COURSE_CODE, "Pemrograman Java", 3, 30, 25, "Dosen A");
        fullCourse = new Course("DB201", "Basis Data", 3, 25, 25, "Dosen B");

        when(gradeCalculator.calculateMaxCredits(activeStudent.getGpa())).thenReturn(24);
    }

    // ======================================================
    // ==================== STUB SECTION ====================
    // ======================================================

    @Test
    @DisplayName("Test untuk mahasiswa berhasil mendaftar ke mata kuliah")
    void testEnrollSuccess() { // STUB
        when(studentRepository.findById(STUDENT_ID)).thenReturn(activeStudent);
        when(courseRepository.findByCourseCode(COURSE_CODE)).thenReturn(availableCourse);
        when(courseRepository.isPrerequisiteMet(STUDENT_ID, COURSE_CODE)).thenReturn(true);

        Enrollment result = enrollmentService.enrollCourse(STUDENT_ID, COURSE_CODE);

        assertNotNull(result);
        assertEquals(STUDENT_ID, result.getStudentId());
        assertEquals(COURSE_CODE, result.getCourseCode());
        assertEquals("APPROVED", result.getStatus());
    }

    @Test
    @DisplayName("Test mahasiswa tidak ditemukan saat mendaftar")
    void testEnrollStudentNotFound() { // STUB
        when(studentRepository.findById("S999")).thenReturn(null);

        assertThrows(StudentNotFoundException.class,
                () -> enrollmentService.enrollCourse("S999", COURSE_CODE));
    }

    @Test
    @DisplayName("Test course tidak ditemukan saat mendaftar")
    void testEnrollCourseNotFound() { // STUB
        when(studentRepository.findById(STUDENT_ID)).thenReturn(activeStudent);
        when(courseRepository.findByCourseCode("X999")).thenReturn(null);

        assertThrows(CourseNotFoundException.class,
                () -> enrollmentService.enrollCourse(STUDENT_ID, "X999"));
    }

    @Test
    @DisplayName("Test mahasiswa berstatus suspended tidak bisa mendaftar")
    void testEnrollSuspendedStudent() { // STUB
        when(studentRepository.findById("S456")).thenReturn(suspendedStudent);

        assertThrows(EnrollmentException.class,
                () -> enrollmentService.enrollCourse("S456", COURSE_CODE));
    }

    @Test
    @DisplayName("Test course penuh (kuota habis)")
    void testEnrollCourseFull() { // STUB
        when(studentRepository.findById(STUDENT_ID)).thenReturn(activeStudent);
        when(courseRepository.findByCourseCode("DB201")).thenReturn(fullCourse);

        assertThrows(CourseFullException.class,
                () -> enrollmentService.enrollCourse(STUDENT_ID, "DB201"));
    }

    @Test
    @DisplayName("Test prasyarat tidak terpenuhi")
    void testEnrollPrerequisiteNotMet() { // STUB
        when(studentRepository.findById(STUDENT_ID)).thenReturn(activeStudent);
        when(courseRepository.findByCourseCode(COURSE_CODE)).thenReturn(availableCourse);
        when(courseRepository.isPrerequisiteMet(STUDENT_ID, COURSE_CODE)).thenReturn(false);

        assertThrows(PrerequisiteNotMetException.class,
                () -> enrollmentService.enrollCourse(STUDENT_ID, COURSE_CODE));
    }

    @Test
    @DisplayName("Test validasi SKS melebihi batas maksimum")
    void testValidateCreditLimit_ExceedsLimit() { // STUB
        when(studentRepository.findById(STUDENT_ID)).thenReturn(activeStudent);

        boolean result = enrollmentService.validateCreditLimit(STUDENT_ID, 25);

        assertFalse(result);
    }

    @Test
    @DisplayName("Test validasi SKS mahasiswa tidak ditemukan")
    void testValidateCreditLimit_StudentNotFound() { // STUB
        when(studentRepository.findById("S999")).thenReturn(null);

        assertThrows(StudentNotFoundException.class,
                () -> enrollmentService.validateCreditLimit("S999", 10));
    }

    @Test
    @DisplayName("Test drop course berhasil")
    void testDropCourseSuccess() { // STUB
        when(studentRepository.findById(STUDENT_ID)).thenReturn(activeStudent);
        when(courseRepository.findByCourseCode(COURSE_CODE)).thenReturn(availableCourse);

        assertDoesNotThrow(() -> enrollmentService.dropCourse(STUDENT_ID, COURSE_CODE));
    }

    @Test
    @DisplayName("Test drop course gagal karena student tidak ditemukan")
    void testDropCourse_StudentNotFound() { // NEW STUB
        when(studentRepository.findById("S000")).thenReturn(null);

        assertThrows(StudentNotFoundException.class,
                () -> enrollmentService.dropCourse("S000", COURSE_CODE));
    }

    @Test
    @DisplayName("Test drop course gagal karena course tidak ditemukan")
    void testDropCourse_CourseNotFound() { // NEW STUB
        when(studentRepository.findById(STUDENT_ID)).thenReturn(activeStudent);
        when(courseRepository.findByCourseCode("NONE")).thenReturn(null);

        assertThrows(CourseNotFoundException.class,
                () -> enrollmentService.dropCourse(STUDENT_ID, "NONE"));
    }


    // ======================================================
    // ==================== MOCK SECTION ====================
    // ======================================================

    @Test
    @DisplayName("Test verifikasi update() dan email terkirim setelah daftar berhasil")
    void testEnrollSuccess_VerifyActions() { // MOCK
        when(studentRepository.findById(STUDENT_ID)).thenReturn(activeStudent);
        when(courseRepository.findByCourseCode(COURSE_CODE)).thenReturn(availableCourse);
        when(courseRepository.isPrerequisiteMet(STUDENT_ID, COURSE_CODE)).thenReturn(true);

        enrollmentService.enrollCourse(STUDENT_ID, COURSE_CODE);

        verify(courseRepository).update(availableCourse);
        verify(notificationService).sendEmail(eq(activeStudent.getEmail()), anyString(), anyString());
    }

    @Test
    @DisplayName("Test memastikan update() tidak dipanggil jika mahasiswa tidak ditemukan")
    void testEnrollFailure_NoUpdateCalled() { // MOCK
        when(studentRepository.findById("S999")).thenReturn(null);

        assertThrows(StudentNotFoundException.class,
                () -> enrollmentService.enrollCourse("S999", COURSE_CODE));

        verify(courseRepository, never()).update(any());
    }

    @Test
    @DisplayName("Test verifikasi update() dan email saat drop course")
    void testDropCourse_VerifyActions() { // MOCK
        when(studentRepository.findById(STUDENT_ID)).thenReturn(activeStudent);
        when(courseRepository.findByCourseCode(COURSE_CODE)).thenReturn(availableCourse);

        enrollmentService.dropCourse(STUDENT_ID, COURSE_CODE);

        verify(courseRepository).update(availableCourse);
        verify(notificationService).sendEmail(eq(activeStudent.getEmail()), anyString(), anyString());
    }

    @Test
    @DisplayName("Test memastikan email tidak dikirim jika course penuh")
    void testEnrollCourseFull_NoEmailSent() { // NEW MOCK
        when(studentRepository.findById(STUDENT_ID)).thenReturn(activeStudent);
        when(courseRepository.findByCourseCode("DB201")).thenReturn(fullCourse);

        assertThrows(CourseFullException.class,
                () -> enrollmentService.enrollCourse(STUDENT_ID, "DB201"));

        verify(notificationService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Test verifikasi tidak ada update saat prasyarat tidak terpenuhi")
    void testEnrollPrerequisiteNotMet_NoUpdate() { // NEW MOCK
        when(studentRepository.findById(STUDENT_ID)).thenReturn(activeStudent);
        when(courseRepository.findByCourseCode(COURSE_CODE)).thenReturn(availableCourse);
        when(courseRepository.isPrerequisiteMet(STUDENT_ID, COURSE_CODE)).thenReturn(false);

        assertThrows(PrerequisiteNotMetException.class,
                () -> enrollmentService.enrollCourse(STUDENT_ID, COURSE_CODE));

        verify(courseRepository, never()).update(any());
    }
}
