import java.util.*;
import java.io.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

// Task 4
class Room {
    private int roomNumber;
    private String category;
    private double price;

    public Room(int roomNumber, String category, double price) {
        this.roomNumber = roomNumber;
        this.category = category;
        this.price = price;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public String getCategory() {
        return category;
    }

    public double getPrice() {
        return price;
    }

    public void displayRoom() {
        System.out.printf(
            "%-12d %-15s Rs. %.2f per night%n",
            roomNumber, category, price
        );
    }
}

// Reservation class
class Reservation {
    private String bookingId;
    private String guestName;
    private String phone;
    private int roomNumber;
    private String category;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private double totalAmount;
    private String paymentStatus;
    private String bookingStatus;

    public Reservation(
            String bookingId,
            String guestName,
            String phone,
            int roomNumber,
            String category,
            LocalDate checkIn,
            LocalDate checkOut,
            double totalAmount,
            String paymentStatus,
            String bookingStatus) {

        this.bookingId = bookingId;
        this.guestName = guestName;
        this.phone = phone;
        this.roomNumber = roomNumber;
        this.category = category;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.totalAmount = totalAmount;
        this.paymentStatus = paymentStatus;
        this.bookingStatus = bookingStatus;
    }

    public String getBookingId() {
        return bookingId;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void cancel() {
        bookingStatus = "CANCELLED";
        paymentStatus = "REFUND SIMULATED";
    }

    public String toFileString() {
        return bookingId + "|" +
               guestName + "|" +
               phone + "|" +
               roomNumber + "|" +
               category + "|" +
               checkIn + "|" +
               checkOut + "|" +
               totalAmount + "|" +
               paymentStatus + "|" +
               bookingStatus;
    }

    public void displayDetails() {
        System.out.println("\n========== BOOKING DETAILS ==========");
        System.out.println("Booking ID      : " + bookingId);
        System.out.println("Guest Name      : " + guestName);
        System.out.println("Phone Number    : " + phone);
        System.out.println("Room Number     : " + roomNumber);
        System.out.println("Room Category   : " + category);
        System.out.println("Check-in Date   : " + checkIn);
        System.out.println("Check-out Date  : " + checkOut);
        System.out.printf("Total Amount    : Rs. %.2f%n", totalAmount);
        System.out.println("Payment Status  : " + paymentStatus);
        System.out.println("Booking Status  : " + bookingStatus);
        System.out.println("=====================================");
    }
}

// Main application
public class HotelReservationSystem {

    private static final Scanner sc = new Scanner(System.in);

    private static final List<Room> rooms = new ArrayList<>();

    private static final List<Reservation> reservations =
            new ArrayList<>();

    private static final String FILE_NAME = "reservations.txt";

    private static int bookingCounter = 1001;

    // Initialize rooms
    private static void initializeRooms() {
        for (int i = 1; i <= 5; i++) {
            rooms.add(new Room(i, "Standard", 1500));
        }

        for (int i = 6; i <= 10; i++) {
            rooms.add(new Room(i, "Deluxe", 2500));
        }

        for (int i = 11; i <= 13; i++) {
            rooms.add(new Room(i, "Suite", 4500));
        }
    }

    // Find room by number
    private static Room findRoom(int roomNumber) {
        for (Room room : rooms) {
            if (room.getRoomNumber() == roomNumber) {
                return room;
            }
        }
        return null;
    }

    // Check overlapping reservations
    private static boolean isRoomAvailable(
            int roomNumber,
            LocalDate checkIn,
            LocalDate checkOut) {

        for (Reservation reservation : reservations) {

            if (reservation.getRoomNumber() == roomNumber
                    && reservation.getBookingStatus().equals("CONFIRMED")) {

                boolean overlaps =
                    checkIn.isBefore(reservation.getCheckOut())
                    && checkOut.isAfter(reservation.getCheckIn());

                if (overlaps) {
                    return false;
                }
            }
        }

        return true;
    }

    // Display all rooms
    private static void displayRooms() {
        System.out.println("\n========== HOTEL ROOMS ==========");
        System.out.printf(
            "%-12s %-15s %s%n",
            "Room Number", "Category", "Price"
        );

        for (Room room : rooms) {
            room.displayRoom();
        }
    }

    // Search available rooms
    private static void searchRooms() {
        System.out.println("\nRoom Categories:");
        System.out.println("1. Standard");
        System.out.println("2. Deluxe");
        System.out.println("3. Suite");

        System.out.print("Select category: ");
        int choice = readInt();

        String category;

        switch (choice) {
            case 1:
                category = "Standard";
                break;
            case 2:
                category = "Deluxe";
                break;
            case 3:
                category = "Suite";
                break;
            default:
                System.out.println("Invalid category.");
                return;
        }

        LocalDate checkIn = readDate("Enter check-in date (YYYY-MM-DD): ");
        LocalDate checkOut = readDate("Enter check-out date (YYYY-MM-DD): ");

        if (checkIn == null || checkOut == null
                || !validDates(checkIn, checkOut)) {
            System.out.println("Invalid dates.");
            return;
        }

        boolean found = false;

        System.out.println("\nAvailable Rooms:");

        for (Room room : rooms) {
            if (room.getCategory().equalsIgnoreCase(category)
                    && isRoomAvailable(
                        room.getRoomNumber(), checkIn, checkOut)) {

                room.displayRoom();
                found = true;
            }
        }

        if (!found) {
            System.out.println("No rooms available for those dates.");
        }
    }

    // Book a room
    private static void bookRoom() {
        System.out.print("Enter guest name: ");
        String guestName = sc.nextLine().trim();

        System.out.print("Enter phone number: ");
        String phone = sc.nextLine().trim();

        if (guestName.isEmpty() || phone.isEmpty()
                || phone.contains("|")) {
            System.out.println("Invalid guest details.");
            return;
        }

        LocalDate checkIn = readDate("Enter check-in date (YYYY-MM-DD): ");
        LocalDate checkOut = readDate("Enter check-out date (YYYY-MM-DD): ");

        if (checkIn == null || checkOut == null
                || !validDates(checkIn, checkOut)) {
            System.out.println(
                "Check-in must be today or later, and check-out must be after check-in."
            );
            return;
        }

        System.out.println("\nSelect Room Category:");
        System.out.println("1. Standard");
        System.out.println("2. Deluxe");
        System.out.println("3. Suite");

        System.out.print("Enter choice: ");
        int choice = readInt();

        String category;

        switch (choice) {
            case 1:
                category = "Standard";
                break;
            case 2:
                category = "Deluxe";
                break;
            case 3:
                category = "Suite";
                break;
            default:
                System.out.println("Invalid choice.");
                return;
        }

        List<Room> availableRooms = new ArrayList<>();

        for (Room room : rooms) {
            if (room.getCategory().equalsIgnoreCase(category)
                    && isRoomAvailable(
                        room.getRoomNumber(), checkIn, checkOut)) {
                availableRooms.add(room);
            }
        }

        if (availableRooms.isEmpty()) {
            System.out.println("No rooms available in this category.");
            return;
        }

        System.out.println("\nAvailable Rooms:");

        for (Room room : availableRooms) {
            room.displayRoom();
        }

        System.out.print("Enter room number: ");
        int roomNumber = readInt();

        Room selectedRoom = null;

        for (Room room : availableRooms) {
            if (room.getRoomNumber() == roomNumber) {
                selectedRoom = room;
                break;
            }
        }

        if (selectedRoom == null) {
            System.out.println("Invalid or unavailable room.");
            return;
        }

        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);

        double totalAmount = nights * selectedRoom.getPrice();

        System.out.println("\n========== PAYMENT ==========");
        System.out.println("Guest: " + guestName);
        System.out.println("Room: " + roomNumber);
        System.out.println("Nights: " + nights);
        System.out.printf("Total Amount: Rs. %.2f%n", totalAmount);

        System.out.println("1. Simulate Successful Payment");
        System.out.println("2. Cancel Booking");

        System.out.print("Enter choice: ");
        int paymentChoice = readInt();

        if (paymentChoice != 1) {
            System.out.println("Booking cancelled before confirmation.");
            return;
        }

        String bookingId = "HTL" + bookingCounter++;

        Reservation reservation = new Reservation(
            bookingId,
            guestName,
            phone,
            roomNumber,
            category,
            checkIn,
            checkOut,
            totalAmount,
            "PAID (SIMULATED)",
            "CONFIRMED"
        );

        reservations.add(reservation);

        saveReservations();

        System.out.println("\nBooking successful!");
        reservation.displayDetails();
    }

    // Cancel a reservation
    private static void cancelReservation() {
        System.out.print("Enter booking ID: ");
        String bookingId = sc.nextLine().trim();

        for (Reservation reservation : reservations) {

            if (reservation.getBookingId().equalsIgnoreCase(bookingId)
                    && reservation.getBookingStatus().equals("CONFIRMED")) {

                reservation.cancel();
                saveReservations();

                System.out.println(
                    "Reservation cancelled. Refund simulated."
                );

                reservation.displayDetails();
                return;
            }
        }

        System.out.println("Active reservation not found.");
    }

    // View a booking
    private static void viewBooking() {
        System.out.print("Enter booking ID: ");
        String bookingId = sc.nextLine().trim();

        for (Reservation reservation : reservations) {
            if (reservation.getBookingId().equalsIgnoreCase(bookingId)) {
                reservation.displayDetails();
                return;
            }
        }

        System.out.println("Booking not found.");
    }

    // Save all reservations
    private static void saveReservations() {
        try (PrintWriter writer =
                new PrintWriter(new FileWriter(FILE_NAME))) {

            for (Reservation reservation : reservations) {
                writer.println(reservation.toFileString());
            }

        } catch (IOException e) {
            System.out.println(
                "Error saving reservations: " + e.getMessage()
            );
        }
    }

    // Load reservations from file
    private static void loadReservations() {
        File file = new File(FILE_NAME);

        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader =
                new BufferedReader(new FileReader(file))) {

            String line;

            while ((line = reader.readLine()) != null) {
                try {
                    String[] data = line.split("\\|", -1);

                    if (data.length != 10) {
                        continue;
                    }

                    Reservation reservation = new Reservation(
                        data[0],
                        data[1],
                        data[2],
                        Integer.parseInt(data[3]),
                        data[4],
                        LocalDate.parse(data[5]),
                        LocalDate.parse(data[6]),
                        Double.parseDouble(data[7]),
                        data[8],
                        data[9]
                    );

                    reservations.add(reservation);

                    if (data[0].startsWith("HTL")) {
                        try {
                            int number = Integer.parseInt(
                                data[0].substring(3)
                            );

                            bookingCounter = Math.max(
                                bookingCounter, number + 1
                            );
                        } catch (NumberFormatException ignored) {
                            // Ignore malformed booking ID.
                        }
                    }

                } catch (RuntimeException e) {
                    System.out.println(
                        "Skipping invalid reservation record."
                    );
                }
            }

        } catch (IOException e) {
            System.out.println(
                "Error loading reservations: " + e.getMessage()
            );
        }
    }

    // Read integer safely
    private static int readInt() {
        while (true) {
            String input = sc.nextLine().trim();

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("Enter a valid number: ");
            }
        }
    }

    // Read date safely
    private static LocalDate readDate(String message) {
        System.out.print(message);

        try {
            return LocalDate.parse(sc.nextLine().trim());
        } catch (Exception e) {
            System.out.println("Please use YYYY-MM-DD format.");
            return null;
        }
    }

    // Validate dates
    private static boolean validDates(
            LocalDate checkIn,
            LocalDate checkOut) {

        return !checkIn.isBefore(LocalDate.now())
                && checkOut.isAfter(checkIn);
    }

    // Main menu
    public static void main(String[] args) {

        initializeRooms();
        loadReservations();

        int choice;

        do {
            System.out.println("\n==================================");
            System.out.println("     HOTEL RESERVATION SYSTEM");
            System.out.println("==================================");
            System.out.println("1. View All Rooms");
            System.out.println("2. Search Available Rooms");
            System.out.println("3. Book a Room");
            System.out.println("4. Cancel Reservation");
            System.out.println("5. View Booking Details");
            System.out.println("6. Exit");

            System.out.print("Enter your choice: ");
            choice = readInt();

            switch (choice) {
                case 1:
                    displayRooms();
                    break;

                case 2:
                    searchRooms();
                    break;

                case 3:
                    bookRoom();
                    break;

                case 4:
                    cancelReservation();
                    break;

                case 5:
                    viewBooking();
                    break;

                case 6:
                    saveReservations();
                    System.out.println("Data saved successfully.");
                    System.out.println("Thank you for using our hotel!");
                    break;

                default:
                    System.out.println("Invalid choice.");
            }

        } while (choice != 6);

        sc.close();
    }
}
