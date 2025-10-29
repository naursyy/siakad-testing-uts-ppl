package com.siakad.service;

import com.siakad.model.CourseGrade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Test untuk GradeCalculator
 * Target: 100% code coverage
 */
@DisplayName("Test GradeCalculator Service")
class GradeCalculatorTest {

    private GradeCalculator gradeCalculator;

    @BeforeEach
    void setUp() {
        gradeCalculator = new GradeCalculator();
    }

    // ==================== TEST CALCULATE GPA ====================

    @Test
    @DisplayName("Test menghitung IPK dengan nilai valid")
    void testCalculateGPA_ValidGrades() {
        // IPK = (12.0 + 9.0 + 8.0) / 8 = 3.625
        List<CourseGrade> grades = Arrays.asList(
                new CourseGrade("CS101", 3, 4.0),
                new CourseGrade("CS102", 3, 3.0),
                new CourseGrade("CS103", 2, 4.0)
        );

        double gpa = gradeCalculator.calculateGPA(grades);
        assertEquals(3.625, gpa, 0.001);
    }

    @Test
    @DisplayName("Test menghitung IPK dengan list kosong harus return 0.0")
    void testCalculateGPA_EmptyList() {
        List<CourseGrade> grades = new ArrayList<>();
        double gpa = gradeCalculator.calculateGPA(grades);
        assertEquals(0.0, gpa, 0.001);
    }

    @Test
    @DisplayName("Test menghitung IPK dengan list null harus return 0.0")
    void testCalculateGPA_NullList() {
        double gpa = gradeCalculator.calculateGPA(null);
        assertEquals(0.0, gpa, 0.001);
    }

    @Test
    @DisplayName("Test menghitung IPK dengan semua nilai A harus return 4.0")
    void testCalculateGPA_AllAGrades() {
        List<CourseGrade> grades = Arrays.asList(
                new CourseGrade("CS101", 3, 4.0),
                new CourseGrade("CS102", 3, 4.0),
                new CourseGrade("CS103", 3, 4.0)
        );

        double gpa = gradeCalculator.calculateGPA(grades);
        assertEquals(4.0, gpa, 0.001);
    }

    @Test
    @DisplayName("Test menghitung IPK dengan semua nilai E harus return 0.0")
    void testCalculateGPA_AllEGrades() {
        List<CourseGrade> grades = Arrays.asList(
                new CourseGrade("CS101", 3, 0.0),
                new CourseGrade("CS102", 3, 0.0)
        );

        double gpa = gradeCalculator.calculateGPA(grades);
        assertEquals(0.0, gpa, 0.001);
    }

    @Test
    @DisplayName("Test menghitung IPK dengan kombinasi berbagai nilai")
    void testCalculateGPA_MixedGrades() {
        // Total: 31.0 / 14 = 2.214...
        List<CourseGrade> grades = Arrays.asList(
                new CourseGrade("CS101", 3, 4.0),
                new CourseGrade("CS102", 4, 3.0),
                new CourseGrade("CS103", 2, 2.0),
                new CourseGrade("CS104", 3, 1.0),
                new CourseGrade("CS105", 2, 0.0)
        );

        double gpa = gradeCalculator.calculateGPA(grades);
        assertEquals(2.214, gpa, 0.01);
    }

    @Test
    @DisplayName("Test menghitung IPK dengan SKS berbeda-beda")
    void testCalculateGPA_DifferentCredits() {
        // Total: 16.0 / 6 = 2.667
        List<CourseGrade> grades = Arrays.asList(
                new CourseGrade("CS101", 2, 4.0),
                new CourseGrade("CS102", 4, 2.0)
        );

        double gpa = gradeCalculator.calculateGPA(grades);
        assertEquals(2.667, gpa, 0.01);
    }

    @ParameterizedTest
    @CsvSource({
            "4.0, 4.0",
            "3.0, 3.0",
            "2.0, 2.0",
            "1.0, 1.0",
            "0.0, 0.0"
    })
    @DisplayName("Test konversi grade point untuk setiap nilai")
    void testCalculateGPA_AllGradePoints(double gradePoint, double expectedGPA) {
        List<CourseGrade> grades = Arrays.asList(
                new CourseGrade("CS101", 3, gradePoint)
        );

        double gpa = gradeCalculator.calculateGPA(grades);
        assertEquals(expectedGPA, gpa, 0.001);
    }

    @Test
    @DisplayName("Test calculateGPA harus throw exception jika grade point invalid (< 0.0)")
    void testCalculateGPA_InvalidGradePoint_LessThanZero() {
        List<CourseGrade> grades = Arrays.asList(
                new CourseGrade("CS101", 3, 4.0),
                new CourseGrade("CS102", 3, -0.5) // Nilai invalid
        );

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            gradeCalculator.calculateGPA(grades);
        });

        assertTrue(exception.getMessage().contains("Invalid grade point: -0.5"));
    }

    @Test
    @DisplayName("Test calculateGPA harus throw exception jika grade point invalid (> 4.0)")
    void testCalculateGPA_InvalidGradePoint_GreaterThanFour() {
        List<CourseGrade> grades = Arrays.asList(
                new CourseGrade("CS101", 3, 4.1) // Nilai invalid
        );

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            gradeCalculator.calculateGPA(grades);
        });

        assertTrue(exception.getMessage().contains("Invalid grade point: 4.1"));
    }

    @Test
    @DisplayName("Test calculateGPA dengan grade point 0.0 (valid boundary)")
    void testCalculateGPA_GradePoint_Zero_Valid() {
        List<CourseGrade> grades = Arrays.asList(
                new CourseGrade("CS101", 3, 0.0)
        );
        double gpa = gradeCalculator.calculateGPA(grades);
        assertEquals(0.0, gpa, 0.001);
    }

    @Test
    @DisplayName("Test calculateGPA dengan grade point 4.0 (valid boundary)")
    void testCalculateGPA_GradePoint_Four_Valid() {
        List<CourseGrade> grades = Arrays.asList(
                new CourseGrade("CS101", 3, 4.0)
        );
        double gpa = gradeCalculator.calculateGPA(grades);
        assertEquals(4.0, gpa, 0.001);
    }

    @Test
    @DisplayName("Test calculateGPA dengan grade point antara 0 dan 4 (valid mid-range)")
    void testCalculateGPA_ValidMidRangeGradePoints() {
        List<CourseGrade> grades = Arrays.asList(
                new CourseGrade("CS101", 3, 2.5),
                new CourseGrade("CS102", 3, 1.5),
                new CourseGrade("CS103", 2, 3.5)
        );
        // (7.5 + 4.5 + 7.0) / 8 = 2.375
        double gpa = gradeCalculator.calculateGPA(grades);
        assertEquals(2.375, gpa, 0.001);
    }

    @Test
    @DisplayName("Test calculateGPA dengan total SKS besar")
    void testCalculateGPA_LargeTotalCredits() {
        List<CourseGrade> grades = Arrays.asList(
                new CourseGrade("CS101", 6, 4.0),
                new CourseGrade("CS102", 6, 3.5),
                new CourseGrade("CS103", 6, 3.0)
        );
        // (24.0 + 21.0 + 18.0) / 18 = 3.5
        double gpa = gradeCalculator.calculateGPA(grades);
        assertEquals(3.5, gpa, 0.001);
    }

    @Test
    @DisplayName("Test calculateGPA dengan totalCredits = 0 (edge case)")
    void testCalculateGPA_TotalCreditsZero() {
        // Skenario: List tidak kosong tapi totalCredits = 0
        // Ini bisa terjadi jika ada CourseGrade dengan credits = 0
        List<CourseGrade> grades = Arrays.asList(
                new CourseGrade("CS101", 0, 4.0)
        );
        double gpa = gradeCalculator.calculateGPA(grades);
        assertEquals(0.0, gpa, 0.001);
    }

    // ==================== TEST DETERMINE ACADEMIC STATUS ====================

    // --- Semester 1-2 (IPK >= 2.0 -> ACTIVE, < 2.0 -> PROBATION) ---

    @Test
    @DisplayName("Test status ACTIVE untuk semester 1 dengan IPK >= 2.0 (Boundary)")
    void testDetermineAcademicStatus_Semester1_Active_Boundary() {
        assertEquals("ACTIVE", gradeCalculator.determineAcademicStatus(2.0, 1));
        assertEquals("ACTIVE", gradeCalculator.determineAcademicStatus(2.5, 2));
    }

    @Test
    @DisplayName("Test status PROBATION untuk semester 2 dengan IPK < 2.0 (Boundary)")
    void testDetermineAcademicStatus_Semester2_Probation_Boundary() {
        assertEquals("PROBATION", gradeCalculator.determineAcademicStatus(1.99, 1));
        assertEquals("PROBATION", gradeCalculator.determineAcademicStatus(1.5, 2));
    }

    @Test
    @DisplayName("Test determineAcademicStatus semester 2 dengan IPK boundary 2.0")
    void testDetermineAcademicStatus_Semester2_Boundary_2_0() {
        assertEquals("ACTIVE", gradeCalculator.determineAcademicStatus(2.0, 2));
    }

    // --- Semester 3-4 (IPK >= 2.25 -> ACTIVE, 2.0-2.24 -> PROBATION, < 2.0 -> SUSPENDED) ---

    @Test
    @DisplayName("Test status ACTIVE untuk semester 3 dengan IPK >= 2.25 (Boundary)")
    void testDetermineAcademicStatus_Semester3_Active_Boundary() {
        assertEquals("ACTIVE", gradeCalculator.determineAcademicStatus(2.25, 3));
        assertEquals("ACTIVE", gradeCalculator.determineAcademicStatus(3.0, 4));
    }

    @Test
    @DisplayName("Test status PROBATION untuk semester 4 dengan IPK 2.0 <= GPA < 2.25 (Range Coverage)")
    void testDetermineAcademicStatus_Semester4_Probation_Range() {
        assertEquals("PROBATION", gradeCalculator.determineAcademicStatus(2.0, 3));
        assertEquals("PROBATION", gradeCalculator.determineAcademicStatus(2.24, 4));
    }

    @Test
    @DisplayName("Test status PROBATION untuk semester 3 dengan IPK 2.10 (mid-range)")
    void testDetermineAcademicStatus_Semester3_Probation_MidRange() {
        assertEquals("PROBATION", gradeCalculator.determineAcademicStatus(2.10, 3));
    }

    @Test
    @DisplayName("Test status SUSPENDED untuk semester 3 dengan IPK < 2.0 (Boundary)")
    void testDetermineAcademicStatus_Semester3_Suspended_Boundary() {
        assertEquals("SUSPENDED", gradeCalculator.determineAcademicStatus(1.99, 3));
        assertEquals("SUSPENDED", gradeCalculator.determineAcademicStatus(1.5, 4));
    }

    @Test
    @DisplayName("Test determineAcademicStatus semester 4 dengan IPK boundary 2.25")
    void testDetermineAcademicStatus_Semester4_Boundary_2_25() {
        assertEquals("ACTIVE", gradeCalculator.determineAcademicStatus(2.25, 4));
    }

    // --- Semester 5+ (IPK >= 2.5 -> ACTIVE, 2.0-2.49 -> PROBATION, < 2.0 -> SUSPENDED) ---

    @Test
    @DisplayName("Test status ACTIVE untuk semester 5+ dengan IPK >= 2.5 (Boundary)")
    void testDetermineAcademicStatus_Semester5Plus_Active_Boundary() {
        assertEquals("ACTIVE", gradeCalculator.determineAcademicStatus(2.5, 5));
        assertEquals("ACTIVE", gradeCalculator.determineAcademicStatus(3.5, 8));
    }

    @Test
    @DisplayName("Test status PROBATION untuk semester 5+ dengan IPK 2.0 <= GPA < 2.5 (Range Coverage)")
    void testDetermineAcademicStatus_Semester5Plus_Probation_Range() {
        assertEquals("PROBATION", gradeCalculator.determineAcademicStatus(2.0, 6));
        assertEquals("PROBATION", gradeCalculator.determineAcademicStatus(2.49, 7));
    }

    @Test
    @DisplayName("Test status PROBATION untuk semester 6 dengan IPK 2.25 (mid-range)")
    void testDetermineAcademicStatus_Semester6_Probation_MidRange() {
        assertEquals("PROBATION", gradeCalculator.determineAcademicStatus(2.25, 6));
    }

    @Test
    @DisplayName("Test status SUSPENDED untuk semester 5+ dengan IPK < 2.0 (Boundary)")
    void testDetermineAcademicStatus_Semester5Plus_Suspended_Boundary() {
        assertEquals("SUSPENDED", gradeCalculator.determineAcademicStatus(1.99, 5));
        assertEquals("SUSPENDED", gradeCalculator.determineAcademicStatus(1.0, 8));
    }

    @Test
    @DisplayName("Test determineAcademicStatus semester 5 dengan IPK boundary 2.5")
    void testDetermineAcademicStatus_Semester5_Boundary_2_5() {
        assertEquals("ACTIVE", gradeCalculator.determineAcademicStatus(2.5, 5));
    }

    @Test
    @DisplayName("Test determineAcademicStatus semester 8 dengan berbagai IPK")
    void testDetermineAcademicStatus_Semester8_Various() {
        assertEquals("SUSPENDED", gradeCalculator.determineAcademicStatus(1.9, 8));
        assertEquals("PROBATION", gradeCalculator.determineAcademicStatus(2.3, 8));
        assertEquals("ACTIVE", gradeCalculator.determineAcademicStatus(3.0, 8));
    }

    @Test
    @DisplayName("Test determineAcademicStatus semester tinggi (12)")
    void testDetermineAcademicStatus_HighSemester() {
        assertEquals("ACTIVE", gradeCalculator.determineAcademicStatus(3.5, 12));
        assertEquals("PROBATION", gradeCalculator.determineAcademicStatus(2.3, 12));
        assertEquals("SUSPENDED", gradeCalculator.determineAcademicStatus(1.5, 12));
    }

    // --- Validasi GPA dan Semester ---

    @Test
    @DisplayName("Test determineAcademicStatus dengan GPA 0.0 (valid boundary)")
    void testDetermineAcademicStatus_GPA_Zero_Valid() {
        String status = gradeCalculator.determineAcademicStatus(0.0, 1);
        assertEquals("PROBATION", status);
    }

    @Test
    @DisplayName("Test determineAcademicStatus dengan GPA 4.0 (valid boundary)")
    void testDetermineAcademicStatus_GPA_Four_Valid() {
        String status = gradeCalculator.determineAcademicStatus(4.0, 1);
        assertEquals("ACTIVE", status);
    }

    @Test
    @DisplayName("Test determineAcademicStatus harus throw exception jika GPA invalid")
    void testDetermineAcademicStatus_InvalidGPA() {
        assertThrows(IllegalArgumentException.class, () -> {
            gradeCalculator.determineAcademicStatus(-0.1, 1);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            gradeCalculator.determineAcademicStatus(4.1, 1);
        });
    }

    @Test
    @DisplayName("Test determineAcademicStatus harus throw exception jika semester invalid (< 1)")
    void testDetermineAcademicStatus_InvalidSemester() {
        assertThrows(IllegalArgumentException.class, () -> {
            gradeCalculator.determineAcademicStatus(3.0, 0);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            gradeCalculator.determineAcademicStatus(3.0, -5);
        });
    }

    // ==================== TEST CALCULATE MAX CREDITS ====================

    @Test
    @DisplayName("Test batas SKS 24 untuk IPK >= 3.0 (Boundary)")
    void testCalculateMaxCredits_GPA_3_0_Boundary() {
        assertEquals(24, gradeCalculator.calculateMaxCredits(3.0));
        assertEquals(24, gradeCalculator.calculateMaxCredits(4.0));
    }

    @Test
    @DisplayName("Test batas SKS 21 untuk IPK 2.5-2.99 (Lower Boundary 2.5)")
    void testCalculateMaxCredits_GPA_2_5_Boundary() {
        assertEquals(21, gradeCalculator.calculateMaxCredits(2.5));
    }

    @Test
    @DisplayName("Test batas SKS 21 untuk IPK 2.5-2.99 (Upper Boundary 2.99)")
    void testCalculateMaxCredits_GPA_2_99_Boundary() {
        assertEquals(21, gradeCalculator.calculateMaxCredits(2.99));
    }

    @Test
    @DisplayName("Test calculateMaxCredits dengan GPA 2.75 (mid-range 2.5-2.99)")
    void testCalculateMaxCredits_GPA_MidRange_2_5_to_3() {
        int credits = gradeCalculator.calculateMaxCredits(2.75);
        assertEquals(21, credits);
    }

    @Test
    @DisplayName("Test batas SKS 18 untuk IPK 2.0-2.49 (Lower Boundary 2.0)")
    void testCalculateMaxCredits_GPA_2_0_Boundary() {
        assertEquals(18, gradeCalculator.calculateMaxCredits(2.0));
    }

    @Test
    @DisplayName("Test batas SKS 18 untuk IPK 2.0-2.49 (Upper Boundary 2.49)")
    void testCalculateMaxCredits_GPA_2_49_Boundary() {
        assertEquals(18, gradeCalculator.calculateMaxCredits(2.49));
    }

    @Test
    @DisplayName("Test calculateMaxCredits dengan GPA 2.25 (mid-range 2.0-2.49)")
    void testCalculateMaxCredits_GPA_MidRange_2_0_to_2_5() {
        int credits = gradeCalculator.calculateMaxCredits(2.25);
        assertEquals(18, credits);
    }

    @Test
    @DisplayName("Test batas SKS 15 untuk IPK < 2.0 (Upper Boundary 1.99)")
    void testCalculateMaxCredits_GPA_1_99_Boundary() {
        assertEquals(15, gradeCalculator.calculateMaxCredits(1.99));
    }

    @Test
    @DisplayName("Test batas SKS 15 untuk IPK 0.0 (Worst Case)")
    void testCalculateMaxCredits_GPA_Zero() {
        assertEquals(15, gradeCalculator.calculateMaxCredits(0.0));
    }

    @Test
    @DisplayName("Test calculateMaxCredits dengan GPA 0.0 (valid boundary)")
    void testCalculateMaxCredits_GPA_Zero_Valid() {
        int credits = gradeCalculator.calculateMaxCredits(0.0);
        assertEquals(15, credits);
    }

    @Test
    @DisplayName("Test calculateMaxCredits dengan GPA 4.0 (valid boundary)")
    void testCalculateMaxCredits_GPA_Four_Valid() {
        int credits = gradeCalculator.calculateMaxCredits(4.0);
        assertEquals(24, credits);
    }

    @Test
    @DisplayName("Test calculateMaxCredits dengan GPA di setiap boundary")
    void testCalculateMaxCredits_AllBoundaries() {
        assertEquals(15, gradeCalculator.calculateMaxCredits(1.99));
        assertEquals(18, gradeCalculator.calculateMaxCredits(2.0));
        assertEquals(18, gradeCalculator.calculateMaxCredits(2.49));
        assertEquals(21, gradeCalculator.calculateMaxCredits(2.5));
        assertEquals(21, gradeCalculator.calculateMaxCredits(2.99));
        assertEquals(24, gradeCalculator.calculateMaxCredits(3.0));
    }

    @ParameterizedTest
    @CsvSource({
            "4.0, 24",
            "3.5, 24",
            "3.0, 24",
            "2.99, 21",
            "2.75, 21",
            "2.5, 21",
            "2.49, 18",
            "2.25, 18",
            "2.0, 18",
            "1.99, 15",
            "1.5, 15",
            "0.5, 15",
            "0.0, 15"
    })
    @DisplayName("Test batas SKS untuk berbagai nilai IPK (Comprehensive)")
    void testGetMaxCredits_VariousGPAs(double gpa, int expectedCredits) {
        int maxCredits = gradeCalculator.calculateMaxCredits(gpa);
        assertEquals(expectedCredits, maxCredits);
    }

    @Test
    @DisplayName("Test calculateMaxCredits harus throw exception jika GPA invalid")
    void testCalculateMaxCredits_InvalidGPA() {
        assertThrows(IllegalArgumentException.class, () -> {
            gradeCalculator.calculateMaxCredits(-0.1);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            gradeCalculator.calculateMaxCredits(4.1);
        });
    }

    // ==================== INTEGRATION TESTS ====================

    @Test
    @DisplayName("Test integrasi: menghitung IPK, status, dan batas SKS untuk mahasiswa berprestasi")
    void testIntegration_GPAAndStatus() {
        List<CourseGrade> grades = Arrays.asList(
                new CourseGrade("CS101", 3, 4.0),
                new CourseGrade("CS102", 3, 3.0)
        );

        double gpa = gradeCalculator.calculateGPA(grades);
        String status = gradeCalculator.determineAcademicStatus(gpa, 5);
        int maxCredits = gradeCalculator.calculateMaxCredits(gpa);

        assertEquals(3.5, gpa, 0.001);
        assertEquals("ACTIVE", status);
        assertEquals(24, maxCredits);
    }

    @Test
    @DisplayName("Test integrasi: mahasiswa dengan IPK rendah di semester awal (PROBATION)")
    void testIntegration_LowGPA_EarlySemester() {
        List<CourseGrade> grades = Arrays.asList(
                new CourseGrade("CS101", 3, 1.0),
                new CourseGrade("CS102", 3, 0.0)
        );

        double gpa = gradeCalculator.calculateGPA(grades);
        String status = gradeCalculator.determineAcademicStatus(gpa, 1);
        int maxCredits = gradeCalculator.calculateMaxCredits(gpa);

        assertEquals(0.5, gpa, 0.001);
        assertEquals("PROBATION", status);
        assertEquals(15, maxCredits);
    }

    @Test
    @DisplayName("Test integrasi: mahasiswa semester 3 dengan IPK di batas SUSPENDED")
    void testIntegration_Suspended_Semester3() {
        List<CourseGrade> grades = Arrays.asList(
                new CourseGrade("CS101", 3, 1.0),
                new CourseGrade("CS102", 3, 1.0),
                new CourseGrade("CS103", 3, 0.0)
        );

        double gpa = gradeCalculator.calculateGPA(grades);
        String status = gradeCalculator.determineAcademicStatus(gpa, 3);
        int maxCredits = gradeCalculator.calculateMaxCredits(gpa);

        assertTrue(gpa < 2.0);
        assertEquals("SUSPENDED", status);
        assertEquals(15, maxCredits);
    }

    @Test
    @DisplayName("Test integrasi lengkap: mahasiswa semester 7 dengan IPK 2.45")
    void testIntegration_Complex_Semester7_GPA_2_45() {
        List<CourseGrade> grades = Arrays.asList(
                new CourseGrade("CS101", 3, 3.0),
                new CourseGrade("CS102", 3, 2.5),
                new CourseGrade("CS103", 3, 2.0),
                new CourseGrade("CS104", 3, 2.3)
        );

        double gpa = gradeCalculator.calculateGPA(grades);
        String status = gradeCalculator.determineAcademicStatus(gpa, 7);
        int maxCredits = gradeCalculator.calculateMaxCredits(gpa);

        assertEquals(2.45, gpa, 0.01);
        assertEquals("PROBATION", status);
        assertEquals(18, maxCredits);
    }

    @Test
    @DisplayName("Test integrasi: transisi dari semester 2 ke 3 dengan IPK 2.20")
    void testIntegration_Transition_Semester2To3_GPA_2_20() {
        double gpa = 2.20;

        assertEquals("ACTIVE", gradeCalculator.determineAcademicStatus(gpa, 2));
        assertEquals("PROBATION", gradeCalculator.determineAcademicStatus(gpa, 3));
        assertEquals(18, gradeCalculator.calculateMaxCredits(gpa));
    }

    @Test
    @DisplayName("Test integrasi: transisi dari semester 4 ke 5 dengan IPK 2.40")
    void testIntegration_Transition_Semester4To5_GPA_2_40() {
        double gpa = 2.40;

        assertEquals("ACTIVE", gradeCalculator.determineAcademicStatus(gpa, 4));
        assertEquals("PROBATION", gradeCalculator.determineAcademicStatus(gpa, 5));
        assertEquals(18, gradeCalculator.calculateMaxCredits(gpa));
    }
}