# SIAKAD TESTING UTS PPL 2025

Proyek ini merupakan implementasi **Sistem Informasi Akademik (SIAKAD)** untuk kebutuhan **UTS PPL 2025**, fokus pada **unit testing** dan **code coverage**.

## Fitur Utama

* **Perhitungan IPK mahasiswa**
* **Penentuan Status Akademik** (`ACTIVE`/`PROBATION`/`SUSPENDED`)
* **Pendaftaran mata kuliah (KRS)**
* **Drop Mata Kuliah** 

## Struktur Project

```text
siakad-testing/
├── src/
│   ├── main/java/com/siakad/
│   │   ├── exception/
│   │   │   ├── CourseFullException.java
│   │   │   ├── CourseNotFoundException.java
│   │   │   ├── EnrollmentException.java
│   │   │   ├── PrerequisiteNotMetException.java
│   │   │   └── StudentNotFoundException.java
│   │   ├── model/
│   │   │   ├── Course.java
│   │   │   ├── CourseGrade.java
│   │   │   ├── Enrollment.java
│   │   │   └── Student.java
│   │   ├── repository/
│   │   │   ├── CourseRepository.java
│   │   │   └── StudentRepository.java
│   │   └── service/
│   │       ├── EnrollmentService.java
│   │       ├── GradeCalculator.java
│   │       └── NotificationService.java
│   └── test/java/com/siakad/service
│       ├── GradeCalculatorTest.java         # Unit test biasa
│       └── EnrollmentServiceTest.java      # Unit test dengan Stub & Mock

```

## Tools dan Teknologi

* **Java 17**
* **Maven** (manajemen dependensi & build)
* **JUnit 5** (unit testing)
* **Mockito** (mocking)
* **JaCoCo** (code coverage)

## Cara Menjalankan Unit Test

1. Pastikan **Java 17** dan **Maven** sudah terinstal.
2. Jalankan unit test:

```bash
mvn clean test
```

3. Laporan code coverage tersedia di:

```
target/site/jacoco/index.html
```

## Code Coverage

* Target minimum **80%** untuk setiap class.
* Laporan code coverage:

    * `overall_coverage.png` → Semua class
    * `gradecalculator_coverage.png` → Class `GradeCalculator`
    * `enrollmentservice_coverage.png` → Class `EnrollmentService`

## Catatan

* `EnrollmentServiceTest.java` menguji **dua pendekatan**:
    1. **Stub** → Data dummy untuk pengujian logika
    2. **Mock** → Simulasi perilaku repository
* Pastikan semua dependency Maven sudah terunduh sebelum menjalankan test.
* Jangan ubah source code di `src/main/java/`.


