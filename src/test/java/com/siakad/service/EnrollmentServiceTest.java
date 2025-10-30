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
        // Hanya inisialisasi object
        activeStudent = new Student(STUDENT_ID, "Naura", "naura@mail.com", "TI", 4, 3.7, "ACTIVE");
        suspendedStudent = new Student("S456", "Rina", "rina@mail.com", "SI", 5, 1.0, "SUSPENDED");

        availableCourse = new Course(COURSE_CODE, "Pemrograman Java", 3, 30, 25, "Dosen A");
        fullCourse = new Course("DB201", "Basis Data", 3, 25, 25, "Dosen B");
    }

    // ======================================================
    // ==================== STUB SECTION ====================
    // ======================================================

    // ==================== ENROLLMENT TESTS ====================
    @Test
    @DisplayName("Test untuk mahasiswa berhasil mendaftar ke mata kuliah")
    void testEnrollSuccess() { // STUB - ENROLLMENT SUCCESS
        when(studentRepository.findById(STUDENT_ID)).thenReturn(activeStudent);
        when(courseRepository.findByCourseCode(COURSE_CODE)).thenReturn(availableCourse);
        when(courseRepository.isPrerequisiteMet(STUDENT_ID, COURSE_CODE)).thenReturn(true);

        Enrollment result = enrollmentService.enrollCourse(STUDENT_ID, COURSE_CODE);

        assertNotNull(result);
        assertEquals(STUDENT_ID, result.getStudentId());
        assertEquals(COURSE_CODE, result.getCourseCode());
        assertEquals("APPROVED", result.getStatus());
        assertNotNull(result.getEnrollmentDate());
    }

    @Test
    @DisplayName("Test mahasiswa tidak ditemukan saat mendaftar")
    void testEnrollStudentNotFound() { // STUB - ENROLLMENT FAILURE: STUDENT NOT FOUND
        when(studentRepository.findById("S999")).thenReturn(null);

        assertThrows(StudentNotFoundException.class,
                () -> enrollmentService.enrollCourse("S999", COURSE_CODE));
    }

    @Test
    @DisplayName("Test course tidak ditemukan saat mendaftar")
    void testEnrollCourseNotFound() { // STUB - ENROLLMENT FAILURE: COURSE NOT FOUND
        when(studentRepository.findById(STUDENT_ID)).thenReturn(activeStudent);
        when(courseRepository.findByCourseCode("X999")).thenReturn(null);

        assertThrows(CourseNotFoundException.class,
                () -> enrollmentService.enrollCourse(STUDENT_ID, "X999"));
    }

    @Test
    @DisplayName("Test mahasiswa berstatus suspended tidak bisa mendaftar")
    void testEnrollSuspendedStudent() { // STUB - ENROLLMENT FAILURE: SUSPENDED STUDENT
        when(studentRepository.findById("S456")).thenReturn(suspendedStudent);

        assertThrows(EnrollmentException.class,
                () -> enrollmentService.enrollCourse("S456", COURSE_CODE));
    }

    @Test
    @DisplayName("Test course penuh (kuota habis)")
    void testEnrollCourseFull() { // STUB - ENROLLMENT FAILURE: COURSE FULL
        when(studentRepository.findById(STUDENT_ID)).thenReturn(activeStudent);
        when(courseRepository.findByCourseCode("DB201")).thenReturn(fullCourse);

        assertThrows(CourseFullException.class,
                () -> enrollmentService.enrollCourse(STUDENT_ID, "DB201"));
    }

    @Test
    @DisplayName("Test prasyarat tidak terpenuhi")
    void testEnrollPrerequisiteNotMet() { // STUB - ENROLLMENT FAILURE: PREREQUISITE NOT MET
        when(studentRepository.findById(STUDENT_ID)).thenReturn(activeStudent);
        when(courseRepository.findByCourseCode(COURSE_CODE)).thenReturn(availableCourse);
        when(courseRepository.isPrerequisiteMet(STUDENT_ID, COURSE_CODE)).thenReturn(false);

        assertThrows(PrerequisiteNotMetException.class,
                () -> enrollmentService.enrollCourse(STUDENT_ID, COURSE_CODE));
    }

    // ==================== CREDIT VALIDATION TESTS ====================
    @Test
    @DisplayName("Test validasi SKS melebihi batas maksimum - return false")
    void testValidateCreditLimit_ExceedsLimit() { // STUB - CREDIT VALIDATION: EXCEEDS LIMIT
        when(studentRepository.findById(STUDENT_ID)).thenReturn(activeStudent);
        when(gradeCalculator.calculateMaxCredits(activeStudent.getGpa())).thenReturn(24);

        boolean result = enrollmentService.validateCreditLimit(STUDENT_ID, 25);

        assertFalse(result, "Should return false when requested credits exceed max credits");
    }

    @Test
    @DisplayName("Test validasi SKS dalam batas - return true")
    void testValidateCreditLimit_WithinLimit() { // STUB - CREDIT VALIDATION: WITHIN LIMIT
        when(studentRepository.findById(STUDENT_ID)).thenReturn(activeStudent);
        when(gradeCalculator.calculateMaxCredits(activeStudent.getGpa())).thenReturn(24);

        boolean result = enrollmentService.validateCreditLimit(STUDENT_ID, 20);

        assertTrue(result, "Should return true when requested credits are within limit");
    }

    @Test
    @DisplayName("Test validasi SKS sama dengan batas - return true")
    void testValidateCreditLimit_EqualLimit() { // STUB - CREDIT VALIDATION: EQUAL LIMIT
        when(studentRepository.findById(STUDENT_ID)).thenReturn(activeStudent);
        when(gradeCalculator.calculateMaxCredits(activeStudent.getGpa())).thenReturn(24);

        boolean result = enrollmentService.validateCreditLimit(STUDENT_ID, 24);

        assertTrue(result, "Should return true when requested credits equal max credits");
    }

    @Test
    @DisplayName("Test validasi SKS mahasiswa tidak ditemukan")
    void testValidateCreditLimit_StudentNotFound() { // STUB - CREDIT VALIDATION: STUDENT NOT FOUND
        when(studentRepository.findById("S999")).thenReturn(null);

        assertThrows(StudentNotFoundException.class,
                () -> enrollmentService.validateCreditLimit("S999", 10));
    }

    // ==================== DROP COURSE TESTS ====================
    @Test
    @DisplayName("Test drop course berhasil")
    void testDropCourseSuccess() { // STUB - DROP COURSE: SUCCESS
        when(studentRepository.findById(STUDENT_ID)).thenReturn(activeStudent);
        when(courseRepository.findByCourseCode(COURSE_CODE)).thenReturn(availableCourse);

        assertDoesNotThrow(() -> enrollmentService.dropCourse(STUDENT_ID, COURSE_CODE));
    }

    @Test
    @DisplayName("Test drop course gagal karena student tidak ditemukan")
    void testDropCourse_StudentNotFound() { // STUB - DROP COURSE: STUDENT NOT FOUND
        when(studentRepository.findById("S000")).thenReturn(null);

        assertThrows(StudentNotFoundException.class,
                () -> enrollmentService.dropCourse("S000", COURSE_CODE));
    }

    @Test
    @DisplayName("Test drop course gagal karena course tidak ditemukan")
    void testDropCourse_CourseNotFound() { // STUB - DROP COURSE: COURSE NOT FOUND
        when(studentRepository.findById(STUDENT_ID)).thenReturn(activeStudent);
        when(courseRepository.findByCourseCode("NONE")).thenReturn(null);

        assertThrows(CourseNotFoundException.class,
                () -> enrollmentService.dropCourse(STUDENT_ID, "NONE"));
    }

    // ======================================================
    // ==================== MOCK SECTION ====================
    // ======================================================

    // ==================== ENROLLMENT VERIFICATION TESTS ====================
    @Test
    @DisplayName("Test verifikasi update() dan email terkirim setelah daftar berhasil")
    void testEnrollSuccess_VerifyActions() { // MOCK - ENROLLMENT VERIFICATION: SUCCESS ACTIONS
        when(studentRepository.findById(STUDENT_ID)).thenReturn(activeStudent);
        when(courseRepository.findByCourseCode(COURSE_CODE)).thenReturn(availableCourse);
        when(courseRepository.isPrerequisiteMet(STUDENT_ID, COURSE_CODE)).thenReturn(true);

        Enrollment result = enrollmentService.enrollCourse(STUDENT_ID, COURSE_CODE);

        // Verify repository update was called with updated course
        verify(courseRepository).update(availableCourse);
        assertEquals(26, availableCourse.getEnrolledCount(), "Enrolled count should be incremented");

        // Verify email was sent
        verify(notificationService).sendEmail(
                eq(activeStudent.getEmail()),
                eq("Enrollment Confirmation"),
                contains("You have been enrolled in: " + availableCourse.getCourseName())
        );

        // Verify enrollment properties
        assertNotNull(result.getEnrollmentId());
        assertTrue(result.getEnrollmentId().startsWith("ENR-"));
        assertNotNull(result.getEnrollmentDate());
    }

    @Test
    @DisplayName("Test memastikan update() tidak dipanggil jika mahasiswa tidak ditemukan")
    void testEnrollFailure_NoUpdateCalled() { // MOCK - ENROLLMENT VERIFICATION: NO UPDATE ON FAILURE
        when(studentRepository.findById("S999")).thenReturn(null);

        assertThrows(StudentNotFoundException.class,
                () -> enrollmentService.enrollCourse("S999", COURSE_CODE));

        verify(courseRepository, never()).update(any());
        verify(notificationService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Test memastikan email tidak dikirim jika course penuh")
    void testEnrollCourseFull_NoEmailSent() { // MOCK - ENROLLMENT VERIFICATION: NO EMAIL ON COURSE FULL
        when(studentRepository.findById(STUDENT_ID)).thenReturn(activeStudent);
        when(courseRepository.findByCourseCode("DB201")).thenReturn(fullCourse);

        assertThrows(CourseFullException.class,
                () -> enrollmentService.enrollCourse(STUDENT_ID, "DB201"));

        verify(notificationService, never()).sendEmail(anyString(), anyString(), anyString());
        verify(courseRepository, never()).update(any());
    }

    @Test
    @DisplayName("Test verifikasi tidak ada update saat prasyarat tidak terpenuhi")
    void testEnrollPrerequisiteNotMet_NoUpdate() { // MOCK - ENROLLMENT VERIFICATION: NO UPDATE ON PREREQUISITE FAILURE
        when(studentRepository.findById(STUDENT_ID)).thenReturn(activeStudent);
        when(courseRepository.findByCourseCode(COURSE_CODE)).thenReturn(availableCourse);
        when(courseRepository.isPrerequisiteMet(STUDENT_ID, COURSE_CODE)).thenReturn(false);

        assertThrows(PrerequisiteNotMetException.class,
                () -> enrollmentService.enrollCourse(STUDENT_ID, COURSE_CODE));

        verify(courseRepository, never()).update(any());
        verify(notificationService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Test verifikasi tidak ada update saat mahasiswa suspended")
    void testEnrollSuspendedStudent_NoUpdate() { // MOCK - ENROLLMENT VERIFICATION: NO UPDATE ON SUSPENDED STUDENT
        when(studentRepository.findById("S456")).thenReturn(suspendedStudent);

        assertThrows(EnrollmentException.class,
                () -> enrollmentService.enrollCourse("S456", COURSE_CODE));

        verify(courseRepository, never()).update(any());
        verify(notificationService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    // ==================== DROP COURSE VERIFICATION TESTS ====================
    @Test
    @DisplayName("Test verifikasi update() dan email saat drop course")
    void testDropCourse_VerifyActions() { // MOCK - DROP COURSE VERIFICATION: SUCCESS ACTIONS
        when(studentRepository.findById(STUDENT_ID)).thenReturn(activeStudent);
        when(courseRepository.findByCourseCode(COURSE_CODE)).thenReturn(availableCourse);

        enrollmentService.dropCourse(STUDENT_ID, COURSE_CODE);

        // Verify repository update was called with decremented count
        verify(courseRepository).update(availableCourse);
        assertEquals(24, availableCourse.getEnrolledCount(), "Enrolled count should be decremented");

        // Verify email was sent
        verify(notificationService).sendEmail(
                eq(activeStudent.getEmail()),
                eq("Course Drop Confirmation"),
                contains("You have dropped: " + availableCourse.getCourseName())
        );
    }

    @Test
    @DisplayName("Test verifikasi tidak ada aksi saat drop course student tidak ditemukan")
    void testDropCourse_StudentNotFound_NoActions() { // MOCK - DROP COURSE VERIFICATION: NO ACTIONS ON STUDENT NOT FOUND
        when(studentRepository.findById("S000")).thenReturn(null);

        assertThrows(StudentNotFoundException.class,
                () -> enrollmentService.dropCourse("S000", COURSE_CODE));

        verify(courseRepository, never()).update(any());
        verify(notificationService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Test verifikasi tidak ada aksi saat drop course tidak ditemukan")
    void testDropCourse_CourseNotFound_NoActions() { // MOCK - DROP COURSE VERIFICATION: NO ACTIONS ON COURSE NOT FOUND
        when(studentRepository.findById(STUDENT_ID)).thenReturn(activeStudent);
        when(courseRepository.findByCourseCode("NONE")).thenReturn(null);

        assertThrows(CourseNotFoundException.class,
                () -> enrollmentService.dropCourse(STUDENT_ID, "NONE"));

        verify(courseRepository, never()).update(any());
        verify(notificationService, never()).sendEmail(anyString(), anyString(), anyString());
    }
}