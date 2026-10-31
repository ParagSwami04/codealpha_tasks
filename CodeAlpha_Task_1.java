import java.util.ArrayList;
import java.util.Scanner;

// Task One
public class StudentGradeManager {

    // ---------- Student model ----------
    static class Student {
        private final int id;
        private String name;
        private final ArrayList<Double> scores = new ArrayList<>();

        Student(int id, String name) {
            this.id = id;
            this.name = name;
        }

        int getId() { return id; }
        String getName() { return name; }
        void setName(String name) { this.name = name; }
        ArrayList<Double> getScores() { return scores; }

        void addScore(double score) { scores.add(score); }

        boolean hasScores() { return !scores.isEmpty(); }

        double getAverage() {
            if (scores.isEmpty()) return 0;
            double sum = 0;
            for (double s : scores) sum += s;
            return sum / scores.size();
        }

        double getHighest() {
            double max = Double.NEGATIVE_INFINITY;
            for (double s : scores) if (s > max) max = s;
            return scores.isEmpty() ? 0 : max;
        }

        double getLowest() {
            double min = Double.POSITIVE_INFINITY;
            for (double s : scores) if (s < min) min = s;
            return scores.isEmpty() ? 0 : min;
        }

        String getLetterGrade() {
            if (scores.isEmpty()) return "N/A";
            double avg = getAverage();
            if (avg >= 90) return "A";
            if (avg >= 80) return "B";
            if (avg >= 70) return "C";
            if (avg >= 60) return "D";
            return "F";
        }
    }

    // ---------- State ----------
    private static final ArrayList<Student> students = new ArrayList<>();
    private static final Scanner in = new Scanner(System.in);
    private static int nextId = 1001;

    // ---------- Main ----------
    public static void main(String[] args) {
        System.out.println("=== Student Grade Manager ===");
        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Choose an option: ");
            switch (choice) {
                case 1 -> addStudent();
                case 2 -> addScores();
                case 3 -> viewStudent();
                case 4 -> renameStudent();
                case 5 -> removeStudent();
                case 6 -> classStatistics();
                case 7 -> summaryReport();
                case 0 -> running = false;
                default -> System.out.println("Invalid option. Try again.");
            }
        }
        System.out.println("Goodbye!");
    }

    private static void printMenu() {
        System.out.println("\n----------- MENU -----------");
        System.out.println("1. Add student");
        System.out.println("2. Add scores to a student");
        System.out.println("3. View a student's details");
        System.out.println("4. Rename a student");
        System.out.println("5. Remove a student");
        System.out.println("6. Class statistics");
        System.out.println("7. Summary report (all students)");
        System.out.println("0. Exit");
        System.out.println("----------------------------");
    }

    // ---------- Actions ----------
    private static void addStudent() {
        String name = readLine("Student name: ");
        if (name.isEmpty()) {
            System.out.println("Name cannot be empty.");
            return;
        }
        Student s = new Student(nextId++, name);
        students.add(s);
        System.out.println("Added " + name + " with ID " + s.getId());

        String more = readLine("Enter scores now? (y/n): ");
        if (more.equalsIgnoreCase("y")) enterScoresFor(s);
    }

    private static void addScores() {
        Student s = selectStudent();
        if (s != null) enterScoresFor(s);
    }

    private static void enterScoresFor(Student s) {
        System.out.println("Enter scores (0-100) for " + s.getName() + ". Type -1 to finish.");
        while (true) {
            double score = readDouble("Score: ");
            if (score == -1) break;
            if (score < 0 || score > 100) {
                System.out.println("Score must be between 0 and 100.");
                continue;
            }
            s.addScore(score);
        }
        System.out.println("Total scores recorded: " + s.getScores().size());
    }

    private static void viewStudent() {
        Student s = selectStudent();
        if (s == null) return;
        System.out.println("\nID:      " + s.getId());
        System.out.println("Name:    " + s.getName());
        System.out.println("Scores:  " + (s.hasScores() ? s.getScores() : "none"));
        if (s.hasScores()) {
            System.out.printf("Average: %.2f%n", s.getAverage());
            System.out.printf("Highest: %.2f%n", s.getHighest());
            System.out.printf("Lowest:  %.2f%n", s.getLowest());
            System.out.println("Grade:   " + s.getLetterGrade());
        }
    }

    private static void renameStudent() {
        Student s = selectStudent();
        if (s == null) return;
        String name = readLine("New name: ");
        if (!name.isEmpty()) {
            s.setName(name);
            System.out.println("Name updated.");
        }
    }

    private static void removeStudent() {
        Student s = selectStudent();
        if (s == null) return;
        students.remove(s);
        System.out.println("Removed " + s.getName() + ".");
    }

    private static void classStatistics() {
        ArrayList<Student> graded = gradedStudents();
        if (graded.isEmpty()) {
            System.out.println("No scores recorded yet.");
            return;
        }
        double allSum = 0;
        int allCount = 0;
        double highScore = Double.NEGATIVE_INFINITY, lowScore = Double.POSITIVE_INFINITY;
        Student topStudent = graded.get(0), bottomStudent = graded.get(0);

        for (Student s : graded) {
            for (double sc : s.getScores()) {
                allSum += sc;
                allCount++;
                if (sc > highScore) highScore = sc;
                if (sc < lowScore) lowScore = sc;
            }
            if (s.getAverage() > topStudent.getAverage()) topStudent = s;
            if (s.getAverage() < bottomStudent.getAverage()) bottomStudent = s;
        }

        System.out.println("\n===== CLASS STATISTICS =====");
        System.out.println("Students with scores: " + graded.size());
        System.out.printf("Overall average score: %.2f%n", allSum / allCount);
        System.out.printf("Highest single score:  %.2f%n", highScore);
        System.out.printf("Lowest single score:   %.2f%n", lowScore);
        System.out.printf("Top student:    %s (avg %.2f)%n", topStudent.getName(), topStudent.getAverage());
        System.out.printf("Lowest student: %s (avg %.2f)%n", bottomStudent.getName(), bottomStudent.getAverage());
    }

    private static void summaryReport() {
        if (students.isEmpty()) {
            System.out.println("No students to report.");
            return;
        }
        String line = "-".repeat(78);
        System.out.println("\n" + "=".repeat(30) + " SUMMARY REPORT " + "=".repeat(31));
        System.out.printf("%-6s %-20s %7s %9s %9s %9s %6s%n",
                "ID", "Name", "Scores", "Average", "Highest", "Lowest", "Grade");
        System.out.println(line);

        for (Student s : students) {
            if (s.hasScores()) {
                System.out.printf("%-6d %-20s %7d %9.2f %9.2f %9.2f %6s%n",
                        s.getId(), truncate(s.getName(), 20), s.getScores().size(),
                        s.getAverage(), s.getHighest(), s.getLowest(), s.getLetterGrade());
            } else {
                System.out.printf("%-6d %-20s %7d %9s %9s %9s %6s%n",
                        s.getId(), truncate(s.getName(), 20), 0, "-", "-", "-", "N/A");
            }
        }
        System.out.println(line);

        ArrayList<Student> graded = gradedStudents();
        if (!graded.isEmpty()) {
            double sumAvg = 0;
            for (Student s : graded) sumAvg += s.getAverage();
            System.out.printf("Total students: %d | Class average: %.2f%n",
                    students.size(), sumAvg / graded.size());
        } else {
            System.out.println("Total students: " + students.size() + " | No scores yet.");
        }
    }

    // ---------- Helpers ----------
    private static ArrayList<Student> gradedStudents() {
        ArrayList<Student> list = new ArrayList<>();
        for (Student s : students) if (s.hasScores()) list.add(s);
        return list;
    }

    private static Student selectStudent() {
        if (students.isEmpty()) {
            System.out.println("No students yet. Add one first.");
            return null;
        }
        int id = readInt("Enter student ID: ");
        for (Student s : students) if (s.getId() == id) return s;
        System.out.println("No student found with ID " + id);
        return null;
    }

    private static String truncate(String s, int max) {
        return s.length() <= max ? s : s.substring(0, max - 1) + "…";
    }

    private static String readLine(String prompt) {
        System.out.print(prompt);
        return in.nextLine().trim();
    }

    private static int readInt(String prompt) {
        while (true) {
            try {
                return Integer.parseInt(readLine(prompt));
            } catch (NumberFormatException e) {
                System.out.println("Please enter a whole number.");
            }
        }
    }

    private static double readDouble(String prompt) {
        while (true) {
            try {
                return Double.parseDouble(readLine(prompt));
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}
