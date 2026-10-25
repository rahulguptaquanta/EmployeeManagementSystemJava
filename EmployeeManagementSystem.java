import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class EmployeeManagementSystem {
    private static final Scanner SCANNER = new Scanner(System.in);
    private static final List<Employee> EMPLOYEES = new ArrayList<>();
    private static final Path DATA_FILE = Paths.get("employees.txt");

    public static void main(String[] args) {
        loadEmployees();
        boolean running = true;

        System.out.println("==============================================");
        System.out.println("        EMPLOYEE MANAGEMENT SYSTEM");
        System.out.println("==============================================");

        while (running) {
            showMenu();
            int choice = readInt("Choose an option (1-8): ");

            switch (choice) {
                case 1:
                    displayEmployees();
                    break;
                case 2:
                    addEmployee();
                    break;
                case 3:
                    searchEmployee();
                    break;
                case 4:
                    updateEmployee();
                    break;
                case 5:
                    deleteEmployee();
                    break;
                case 6:
                    sortEmployeesBySalary();
                    break;
                case 7:
                    saveEmployees();
                    break;
                case 8:
                    saveEmployees();
                    running = false;
                    System.out.println("Data saved. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please select from 1 to 8.");
            }
        }

        SCANNER.close();
    }

    private static void showMenu() {
        System.out.println("\n---------------- MAIN MENU ----------------");
        System.out.println("1. View all employees");
        System.out.println("2. Add employee");
        System.out.println("3. Search employee by ID");
        System.out.println("4. Update employee");
        System.out.println("5. Delete employee");
        System.out.println("6. Sort employees by salary");
        System.out.println("7. Save data to file");
        System.out.println("8. Save and exit");
        System.out.println("-------------------------------------------");
    }

    private static void addEmployee() {
        System.out.println("\n--- Add Employee ---");
        String id = readNonEmpty("Employee ID: ");

        if (findEmployee(id) != null) {
            System.out.println("An employee with this ID already exists.");
            return;
        }

        String name = readNonEmpty("Employee name: ");
        String department = readNonEmpty("Department: ");
        double salary = readPositiveDouble("Monthly salary: ");

        EMPLOYEES.add(new Employee(id, name, department, salary));
        System.out.println("Employee added successfully.");
    }

    private static void displayEmployees() {
        System.out.println("\n---------------- EMPLOYEE LIST ----------------");
        if (EMPLOYEES.isEmpty()) {
            System.out.println("No employee records found.");
            return;
        }

        System.out.printf("%-12s | %-24s | %-20s | %12s%n",
                "Employee ID", "Name", "Department", "Salary");
        System.out.println("--------------------------------------------------------------------------");

        for (Employee employee : EMPLOYEES) {
            employee.displayDetails();
        }
    }

    private static void searchEmployee() {
        String id = readNonEmpty("Enter employee ID to search: ");
        Employee employee = findEmployee(id);

        if (employee == null) {
            System.out.println("Employee not found.");
        } else {
            System.out.println("\nEmployee found:");
            employee.displayDetails();
        }
    }

    private static void updateEmployee() {
        String id = readNonEmpty("Enter employee ID to update: ");
        Employee employee = findEmployee(id);

        if (employee == null) {
            System.out.println("Employee not found.");
            return;
        }

        System.out.println("Press Enter to keep the current value.");
        System.out.print("Name [" + employee.getName() + "]: ");
        String name = SCANNER.nextLine().trim();
        if (!name.isEmpty()) {
            employee.setName(name);
        }

        System.out.print("Department [" + employee.getDepartment() + "]: ");
        String department = SCANNER.nextLine().trim();
        if (!department.isEmpty()) {
            employee.setDepartment(department);
        }

        while (true) {
            System.out.print("Monthly salary [" + employee.getSalary() + "] (Enter to keep): ");
            String input = SCANNER.nextLine().trim();
            if (input.isEmpty()) {
                break;
            }
            try {
                double salary = Double.parseDouble(input);
                if (!Double.isFinite(salary) || salary <= 0) {
                    System.out.println("Salary must be a positive number.");
                    continue;
                }
                employee.setSalary(salary);
                break;
            } catch (NumberFormatException exception) {
                System.out.println("Invalid salary. Example: 25000 or 25000.50");
            }
        }

        System.out.println("Employee updated successfully.");
    }

    private static void deleteEmployee() {
        String id = readNonEmpty("Enter employee ID to delete: ");
        Employee employee = findEmployee(id);

        if (employee == null) {
            System.out.println("Employee not found.");
            return;
        }

        System.out.print("Delete " + employee.getName() + "? (yes/no): ");
        String confirmation = SCANNER.nextLine().trim();

        if (confirmation.equalsIgnoreCase("yes")) {
            EMPLOYEES.remove(employee);
            System.out.println("Employee deleted successfully.");
        } else {
            System.out.println("Delete cancelled.");
        }
    }

    private static void sortEmployeesBySalary() {
        if (EMPLOYEES.isEmpty()) {
            System.out.println("No employee records to sort.");
            return;
        }

        Collections.sort(EMPLOYEES, Comparator.comparingDouble(Employee::getSalary).reversed());
        System.out.println("Employees sorted by salary (highest to lowest).");
        displayEmployees();
    }

    private static void saveEmployees() {
        Set<String> usedIds = new HashSet<>();
        try (BufferedWriter writer = Files.newBufferedWriter(
                DATA_FILE,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE)) {

            for (Employee employee : EMPLOYEES) {
                if (!usedIds.add(employee.getId().toLowerCase())) {
                    System.out.println("Duplicate ID detected. Save cancelled.");
                    return;
                }
                writer.write(encode(employee.getId()) + "\t"
                        + encode(employee.getName()) + "\t"
                        + encode(employee.getDepartment()) + "\t"
                        + employee.getSalary());
                writer.newLine();
            }
            System.out.println("Saved " + EMPLOYEES.size() + " employee record(s) to " + DATA_FILE + ".");
        } catch (IOException exception) {
            System.out.println("Could not save data: " + exception.getMessage());
        }
    }

    private static void loadEmployees() {
        if (!Files.exists(DATA_FILE)) {
            return;
        }

        int loaded = 0;
        try (BufferedReader reader = Files.newBufferedReader(DATA_FILE)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\t", -1);
                if (parts.length != 4) {
                    System.out.println("Skipping an invalid record in the data file.");
                    continue;
                }

                try {
                    String id = decode(parts[0]);
                    String name = decode(parts[1]);
                    String department = decode(parts[2]);
                    double salary = Double.parseDouble(parts[3]);

                    if (id.isEmpty() || name.isEmpty() || department.isEmpty()
                            || !Double.isFinite(salary) || salary <= 0
                            || findEmployee(id) != null) {
                        System.out.println("Skipping an invalid or duplicate employee record.");
                        continue;
                    }

                    EMPLOYEES.add(new Employee(id, name, department, salary));
                    loaded++;
                } catch (IllegalArgumentException exception) {
                    System.out.println("Skipping an unreadable employee record.");
                }
            }
            if (loaded > 0) {
                System.out.println(loaded + " employee record(s) loaded from " + DATA_FILE + ".");
            }
        } catch (IOException exception) {
            System.out.println("Could not load data: " + exception.getMessage());
        }
    }

    private static Employee findEmployee(String id) {
        for (Employee employee : EMPLOYEES) {
            if (employee.getId().equalsIgnoreCase(id)) {
                return employee;
            }
        }
        return null;
    }

    private static String readNonEmpty(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = SCANNER.nextLine().trim();
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("This field cannot be empty.");
        }
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = SCANNER.nextLine().trim();
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException exception) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    private static double readPositiveDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = SCANNER.nextLine().trim();
            try {
                double amount = Double.parseDouble(value);
                if (!Double.isFinite(amount) || amount <= 0) {
                    System.out.println("Please enter a positive number.");
                } else {
                    return amount;
                }
            } catch (NumberFormatException exception) {
                System.out.println("Invalid amount. Example: 25000 or 25000.50");
            }
        }
    }

    private static String encode(String value) {
        return value.replace("%", "%25").replace("\t", "%09").replace("\n", "%0A").replace("\r", "%0D");
    }

    private static String decode(String value) {
        return value.replace("%0D", "\r").replace("%0A", "\n").replace("%09", "\t").replace("%25", "%");
    }
}

class Employee {
    private final String id;
    private String name;
    private String department;
    private double salary;

    public Employee(String id, String name, String department, double salary) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.salary = salary;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }

    public double getSalary() {
        return salary;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public void displayDetails() {
        System.out.printf("%-12s | %-24s | %-20s | Rs. %10.2f%n",
                id, name, department, salary);
    }
}
