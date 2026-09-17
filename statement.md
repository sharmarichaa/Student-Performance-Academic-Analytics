# Project Statement

## Project Title

Student Performance & Academic Analytics System

## Problem Statement

Managing student academic information manually can make it difficult to maintain student records, track academic performance, monitor attendance, and identify students who may require academic attention.

The Student Performance & Academic Analytics System provides a centralized command-line application for managing student information, academic records, attendance data, performance analysis, and generated reports. The system uses Java and CSV-based file storage to provide persistent records while keeping the application simple to deploy and operate.

## Scope of the Project

The project focuses on the management and analysis of student academic data.

The system covers:

- Student record management
- Academic marks and grade management
- GPA and weighted academic calculations
- Attendance tracking
- Examination eligibility checking
- Student performance analytics
- Academically at-risk student detection
- Student transcript and report generation
- CSV-based data persistence
- Automated system diagnostics and validation

The project is designed as a command-line Java application and does not require an external database or third-party framework.

## Target Users

The intended users of the system include:

- Academic administrators
- Faculty members
- Academic coordinators
- Educational institutions
- Students who need access to their academic performance information

## High-Level Features

### 1. Student Management

The system allows users to:

- View all registered students
- Add new student records
- Update existing student information
- Delete student records
- Search students using ID, roll number, or name

### 2. Academic Marks & Grade Management

The system provides functionality for:

- Recording academic marks
- Assigning grades
- Calculating grade points
- Calculating GPA
- Calculating weighted academic aggregates

### 3. Attendance & Examination Eligibility

The system allows users to:

- Store attendance records
- Calculate attendance percentages
- Check examination eligibility
- Apply configured attendance eligibility rules

### 4. Performance Analytics

The analytics module provides:

- Academic performance analysis
- Identification of academically at-risk students
- Performance-related insights based on stored academic records

### 5. Report Generation

The system can generate student performance and transcript reports as text files.

### 6. Data Persistence

Student, academic, and attendance information is stored in CSV files so that records can be loaded and saved between application sessions.

### 7. Automated Diagnostics

An automated test suite validates important system functionality including:

- Grade and grade-point calculations
- GPA and weighted aggregate calculations
- Attendance and eligibility calculations
- Student CRUD operations
- At-risk student detection
- CSV persistence
- Report generation

## Technology Used

- Java
- Core Java
- Object-Oriented Programming
- Java Collections
- File I/O
- CSV-based persistence
- Command Line Interface
- Git and GitHub

## Expected Outcome
The expected outcome is a modular java based academic management system that provides reliable student record management, academic calculations, attendance monitoring, performance analysis, and report generation through a single command-line application.
