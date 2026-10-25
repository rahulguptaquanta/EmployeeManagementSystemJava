# Employee Management System (Java)

Week 3 Java Programming mini project using Collections, file handling, sorting, searching, and exception handling.

## Features
- Add, view, search, update, and delete employee records
- Store employee ID, name, department, and salary
- Sort employees by salary from highest to lowest
- Save employee data to `employees.txt`
- Load saved employee data automatically at startup
- Validate inputs and handle invalid values
- Prevent duplicate employee IDs

## Java concepts used
- `ArrayList` to store employee records
- `HashSet` to check IDs while saving
- `Comparator` and `Collections.sort()` to sort by salary
- `BufferedReader` and `BufferedWriter` for file handling
- `try-catch` and try-with-resources for exception handling
- Search using a linear search method

## Requirements
- JDK 8 or newer
- VS Code, IntelliJ IDEA, Eclipse, or terminal

## Run
Open a terminal in the project folder and run:

```bash
javac EmployeeManagementSystem.java
java EmployeeManagementSystem
```

The program creates `employees.txt` in the current working directory when you save records. Keep this file in the same working directory to load the records next time.

## Suggested test
1. Choose `2` and add employee ID `E101`, name `Rahul Gupta`, department `IT`, salary `30000`.
2. Add another employee with a different ID and salary.
3. Choose `1` to display records.
4. Choose `6` to sort by salary.
5. Choose `7` to save data.
6. Exit and run the program again to verify records load from `employees.txt`.
7. Try entering a duplicate ID or invalid salary to test validation.

## Note
This is an educational console project. It does not use a database, login system, or payroll integrations.
