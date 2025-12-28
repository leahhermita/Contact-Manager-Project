/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package contactmanager;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author User
 */
class Contact {

    private final String name;
    private final String phone;
    private final String email;

    public Contact(String name, String phone, String email) {
        this.name = name.trim();
        this.phone = phone.trim();
        this.email = email.trim();
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return String.format("%s | %s | %s", name, phone, email);
    }

    // equals/hashCode based on phone (unique key)
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Contact)) {
            return false;
        }
        Contact c = (Contact) o;
        return phone.equals(c.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(phone);
    }
}

public class ContactManager {

    private final ArrayList<Contact> contacts;
    private final HashMap<String, Contact> phoneIndex;
    private final Stack<Contact> deleteStack;
    private final Scanner scanner;

    public ContactManager() {
        contacts = new ArrayList<>();
        phoneIndex = new HashMap<>();
        deleteStack = new Stack<>();
        scanner = new Scanner(System.in);
    }

    public void start() {
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    addContactFlow();
                    break;
                case "2":
                    searchByPhoneFlow();
                    break;
                case "3":
                    searchByNameFlow();
                    break;
                case "4":
                    deleteByPhoneFlow();
                    break;
                case "5":
                    undoDeleteFlow();
                    break;
                case "6":
                    displayAllSorted();
                    break;
                case "7":
                    running = false;
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid option. Try again.");
                    break;
            }
        }
    }

    private void printMenu() {
        System.out.println("\n=== Contact Directory ===");
        System.out.println("1. Add contact");
        System.out.println("2. Search by phone");
        System.out.println("3. Search by name (partial)");
        System.out.println("4. Delete by phone");
        System.out.println("5. Undo last delete");
        System.out.println("6. Display all (sorted by name)");
        System.out.println("7. Exit");
        System.out.print("Choose: ");
    }

    private void addContactFlow() {
        System.out.print("Enter name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter phone: ");
        String phone = scanner.nextLine().trim();
        System.out.print("Enter email: ");
        String email = scanner.nextLine().trim();

        if (phone.isEmpty() || name.isEmpty()) {
            System.out.println("Name and phone are required.");
            return;
        }

        if (phoneIndex.containsKey(phone)) {
            System.out.println("Phone already exists. Choose a unique phone.");
            return;
        }

        Contact c = new Contact(name, phone, email);
        contacts.add(c);
        phoneIndex.put(phone, c);
        System.out.println("Contact added.");
    }

    private void searchByPhoneFlow() {
        System.out.print("Enter phone: ");
        String phone = scanner.nextLine().trim();
        Contact c = phoneIndex.get(phone);
        if (c == null) {
            System.out.println("Contact not found.");
        } else {
            System.out.println("Found: " + c);
        }
    }

    private void searchByNameFlow() {
        System.out.print("Enter name or partial name: ");
        String q = scanner.nextLine().trim().toLowerCase();
        List<Contact> results = contacts.stream()
                .filter(c -> c.getName().toLowerCase().contains(q))
                .collect(Collectors.toList());
        if (results.isEmpty()) {
            System.out.println("No matches found.");
        } else {
            System.out.println("Matches:");
            int i = 1;
            for (Contact c : results) {
                System.out.println(i++ + ") " + c);
            }
        }
    }

    private void deleteByPhoneFlow() {
        System.out.print("Enter phone to delete: ");
        String phone = scanner.nextLine().trim();
        Contact c = phoneIndex.get(phone);
        if (c == null) {
            System.out.println("Contact not found.");
            return;
        }
        // Remove from ArrayList (first occurrence)
        boolean removed = contacts.remove(c);
        if (!removed) {
            // fallback: remove by iteration (shouldn't normally happen)
            Iterator<Contact> it = contacts.iterator();
            while (it.hasNext()) {
                Contact cc = it.next();
                if (cc.getPhone().equals(phone)) {
                    it.remove();
                    removed = true;
                    break;
                }
            }
        }
        phoneIndex.remove(phone);
        deleteStack.push(c);
        System.out.println("Deleted. You can undo with option 5.");
    }

    private void undoDeleteFlow() {
        if (deleteStack.isEmpty()) {
            System.out.println("Nothing to undo.");
            return;
        }
        Contact c = deleteStack.pop();
        if (phoneIndex.containsKey(c.getPhone())) {
            System.out.println("Cannot undo: another contact uses the same phone.");
            return;
        }
        contacts.add(c);
        phoneIndex.put(c.getPhone(), c);
        System.out.println("Undo successful.");
    }

    private void displayAllSorted() {
        if (contacts.isEmpty()) {
            System.out.println("No contacts to display.");
            return;
        }
        ArrayList<Contact> copy = new ArrayList<>(contacts);
        Collections.sort(copy, Comparator.comparing(Contact::getName, String.CASE_INSENSITIVE_ORDER));
        System.out.println("Contacts (sorted):");
        int i = 1;
        for (Contact c : copy) {
            System.out.println(i++ + ") " + c);
        }
    }

    public static void main(String[] args) {
        ContactManager app = new ContactManager();
        app.start();
    }
}
