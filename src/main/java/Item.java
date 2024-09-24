import java.io.*;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Map;
import java.util.TreeMap;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;

public class Item {
    private static final AtomicInteger idCounter = new AtomicInteger(0);
    private String itemName;
    private int itemQuantity;
    private ItemGroups itemGroup;  // Association with ItemGroups
    private String itemId;
    private static final Map<String, Item> inventory = new TreeMap<>();
    private double unitPrice;
    private int minInvLV;

    // Constructors
    public Item(String itemName, int itemQuantity, ItemGroups itemGroup, int minInvLV, double unitPrice) {
        this.itemName = itemName;
        this.itemQuantity = itemQuantity;
        this.itemGroup = itemGroup;
        this.minInvLV = minInvLV;
        this.unitPrice = unitPrice;
        this.itemId = generateItemId();
        getInventory().put(this.itemId, this);  // Add item to inventory
        itemGroup.addItem(this);  // Add item to group
    }

    public Item(){

    }

    public Item(String itemId, String itemName, int itemQuantity, ItemGroups itemGroup, int minInvLV, double unitPrice) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemQuantity = itemQuantity;
        this.itemGroup = itemGroup;
        this.minInvLV = minInvLV;
        this.unitPrice = unitPrice;
        inventory.put(itemId, this);
    }

    public Item(String itemId, String itemName){
        this.itemId = itemId;
        this.itemName = itemName;
    }
    
    //For Saving Item Array List
    public Item(String itemId, String itemName, int itemQuantity, ItemGroups itemGroup, int minInvLV) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemQuantity = itemQuantity;
        this.itemGroup = itemGroup;
        this.minInvLV = minInvLV;
    }

    // Getters and Setters
    public static int getNumberOfItemsAvailable() {
        return inventory.size();
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        if (itemName.length() > 20) {
            throw new IllegalArgumentException("Item name cannot be more than 20 characters.");
        }
        this.itemName = itemName;
    }

    public int getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(int itemQuantity) {
        if (itemQuantity < 0) {
            throw new IllegalArgumentException("Item quantity cannot be negative.");
        }
        this.itemQuantity = itemQuantity;
    }

    public String getItemId() {
        return itemId;
    }

    public ItemGroups getItemGroup() {
        return itemGroup;
    }

    public void setItemGroup(ItemGroups itemGroup) {
        if (itemGroup.getGroupName().length() > 20) {
            throw new IllegalArgumentException("Item group name cannot be more than 20 characters.");
        }
        this.itemGroup = itemGroup;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        if (unitPrice > 1000) {
            throw new IllegalArgumentException("Unit price cannot be more than 1000.");
        }
        this.unitPrice = unitPrice;
    }

    public int getMinInvLV() {
        return minInvLV;
    }

    public void setMinInvLV(int minInvLV) {
        if (minInvLV < 0) {
            throw new IllegalArgumentException("Minimum inventory level cannot be negative.");
        }
        this.minInvLV = minInvLV;
    }

    // Static Methods
    public static Map<String, Item> getInventory() {
        return inventory;
    }

    public static void saveInventoryToFile() {
        String fileName = "items.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (Item item : getInventory().values()) {
                writer.write(item.getItemId() + "," + item.getItemName() + "," + item.getItemQuantity() + "," + item.getItemGroup().getGroupName() + "," + item.getMinInvLV() + "," + item.getUnitPrice());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Can't Save File");
        }
    }

    public static void loadInventoryFromFile() {
        String fileName = "items.txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            Map<String, ItemGroups> groupMap = new TreeMap<>();
            Set<String> itemIds = new HashSet<>(); // To track unique item IDs
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 6) {  // Check if line is valid
                    String itemId = parts[0];
                    if (itemIds.contains(itemId)) {
                        continue; // Skip duplicate item
                    }
                    itemIds.add(itemId);
                    String itemName = parts[1];
                    int itemQuantity = Integer.parseInt(parts[2]);
                    String groupName = parts[3];
                    int minInvLV = Integer.parseInt(parts[4]);
                    double unitPrice = Double.parseDouble(parts[5]);
                    ItemGroups itemGroup = groupMap.computeIfAbsent(groupName, ItemGroups::new);
                    Item item = new Item(itemId, itemName, itemQuantity, itemGroup, minInvLV, unitPrice);
                    getInventory().put(itemId, item);
                }
            }

        } catch (IOException e) {
            System.out.println("Can't Read File");
        }
        initializeIdCounter();  // Initialize ID counter
    }

    public static void promptUserToEditItemDetails() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\nPress 'e' to exit at any time.");
        System.out.print("Enter the item ID to edit: ");
        String itemId = scanner.nextLine();
        if (itemId.equalsIgnoreCase("e")) return;

        Item item = getInventory().get(itemId);
        if (item != null) {
            boolean editing = true;
            while (editing) {
                System.out.println("\nItem Details:");
                System.out.printf("%-10s | %-20s | %-10s | %-15s | %-10s | %-10s%n", "ID", "Name", "Quantity", "Category", "Min Stock", "Unit Price");
                System.out.println("------------------------------------------------------------------------------------------");
                System.out.printf("%-10s | %-20s | %-10d | %-15s | %-10d | %-10.2f%n", item.getItemId(), item.getItemName(), item.getItemQuantity(), item.getItemGroup().getGroupName(), item.getMinInvLV(), item.getUnitPrice());

                System.out.println("\nEditing item: " + item.getItemName());
                System.out.println("1. Edit Name");
                System.out.println("2. Edit Quantity");
                System.out.println("3. Edit Group");
                System.out.println("4. Edit Min Stock Level");
                System.out.println("5. Edit Unit Price");
                System.out.println("6. Exit");
                System.out.print("Choose an option: ");
                String choiceInput = scanner.nextLine();
                if (choiceInput.equalsIgnoreCase("e")) return;
                while (choiceInput.isEmpty() || !isValidChoice(choiceInput, 6)) {
                    System.out.println("Invalid choice. Please enter a number between 1 and 6.");
                    choiceInput = scanner.nextLine();
                    if (choiceInput.equalsIgnoreCase("e")) return;
                }
                int choice = Integer.parseInt(choiceInput);

                switch (choice) {
                    case 1:
                        System.out.print("Enter new name: ");
                        String newName = scanner.nextLine();
                        if (newName.equalsIgnoreCase("e")) return;
                        while (newName.isEmpty()) {
                            System.out.println("Item name cannot be empty. Please enter a valid item name.");
                            System.out.print("Enter new name: ");
                            newName = scanner.nextLine();
                            if (newName.equalsIgnoreCase("e")) return;
                        }
                        item.setItemName(newName);
                        break;
                    case 2:
                        System.out.print("Enter new quantity: ");
                        String quantityInput = scanner.nextLine();
                        if (quantityInput.equalsIgnoreCase("e")) return;
                        while (!isValidItemQuantity(quantityInput)) {
                            System.out.println("Invalid quantity. Please enter a positive integer between 0 and 100.");
                            System.out.print("Enter new quantity: ");
                            quantityInput = scanner.nextLine();
                            if (quantityInput.equalsIgnoreCase("e")) return;
                        }
                        int newQuantity = Integer.parseInt(quantityInput);
                        item.setItemQuantity(newQuantity);
                        break;
                    case 3:
                        System.out.println("Existing item groups:");
                        List<ItemGroups> existingGroups = getExistingItemGroups();
                        for (int i = 0; i < existingGroups.size(); i++) {
                            System.out.println((i + 1) + ". " + existingGroups.get(i).getGroupName());
                        }
                        System.out.println((existingGroups.size() + 1) + ". Create new group");

                        int groupChoice = -1;
                        while (groupChoice < 1 || groupChoice > existingGroups.size() + 1) {
                            System.out.print("Choose an option: ");
                            String groupChoiceInput = scanner.nextLine();
                            if (groupChoiceInput.equalsIgnoreCase("e")) return;
                            try {
                                groupChoice = Integer.parseInt(groupChoiceInput);
                                if (groupChoice < 1 || groupChoice > existingGroups.size() + 1) {
                                    System.out.println("Invalid choice. Please choose a valid option.");
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid input. Please enter a number.");
                            }
                        }

                        if (groupChoice > 0 && groupChoice <= existingGroups.size()) {
                            item.setItemGroup(existingGroups.get(groupChoice - 1));
                        } else if (groupChoice == existingGroups.size() + 1) {
                            System.out.print("Enter the new group name: ");
                            String groupName = scanner.nextLine();
                            if (groupName.equalsIgnoreCase("e")) return;
                            while (groupName.isEmpty()) {
                                System.out.println("Group name cannot be empty. Please enter a valid group name.");
                                System.out.print("Enter the new group name: ");
                                groupName = scanner.nextLine();
                                if (groupName.equalsIgnoreCase("e")) return;
                            }
                            ItemGroups newGroup = new ItemGroups(groupName);
                            existingGroups.add(newGroup);
                            item.setItemGroup(newGroup);
                        }
                        break;
                    case 4:
                        System.out.print("Enter new minimum stock level: ");
                        String minStockInput = scanner.nextLine();
                        if (minStockInput.equalsIgnoreCase("e")) return;
                        while (!isValidMinStockLevel(minStockInput)) {
                            System.out.println("Invalid minimum stock level. Please enter an integer between 1 and 100.");
                            System.out.print("Enter new minimum stock level: ");
                            minStockInput = scanner.nextLine();
                            if (minStockInput.equalsIgnoreCase("e")) return;
                        }
                        int newMinInvLV = Integer.parseInt(minStockInput);
                        item.setMinInvLV(newMinInvLV);
                        break;
                    case 5:
                        System.out.print("Enter new unit price: ");
                        String priceInput = scanner.nextLine();
                        if (priceInput.equalsIgnoreCase("e")) return;
                        while (priceInput.isEmpty() || !isValidUnitPrice(priceInput)) {
                            System.out.println("Invalid unit price. Please enter a value between 1 and 1000.");
                            System.out.print("Enter new unit price: ");
                            priceInput = scanner.nextLine();
                            if (priceInput.equalsIgnoreCase("e")) return;
                        }
                        double newUnitPrice = Double.parseDouble(priceInput);
                        item.setUnitPrice(newUnitPrice);
                        break;
                    case 6:
                        editing = false;
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
                saveInventoryToFile();  // Save changes to file
                removeEmptyGroups();  // Remove empty groups
            }
        } else {
            System.out.println("Item ID not found in inventory.");
        }
    }

    private static boolean isValidChoice(String choiceInput, int maxOption) {
        try {
            int choice = Integer.parseInt(choiceInput);
            return choice >= 1 && choice <= maxOption;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static void removeEmptyGroups() {
        List<ItemGroups> existingGroups = getExistingItemGroups();
        existingGroups.removeIf(group -> group.getItems().isEmpty());
    }

    public static void promptUserToAddNewItem() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("\nPress 'e' to exit at any time.");
        System.out.print("\nEnter the item name: ");
        String itemName = scanner.nextLine();
        if (itemName.equalsIgnoreCase("e")) return;
        while (itemName.isEmpty()) {
            System.out.println("Item name cannot be empty. Please enter a valid item name.");
            itemName = scanner.nextLine();
            if (itemName.equalsIgnoreCase("e")) return;
        }

        System.out.print("Enter the item quantity: ");
        String quantityInput = scanner.nextLine();
        if (quantityInput.equalsIgnoreCase("e")) return;
        while (quantityInput.isEmpty()) {
            System.out.println("Quantity cannot be empty. Please enter a valid quantity.");
            quantityInput = scanner.nextLine();
            if (quantityInput.equalsIgnoreCase("e")) return;
        }
        int itemQuantity = Integer.parseInt(quantityInput);
        if (itemQuantity < 0) {
            System.out.println("Quantity cannot be negative.");
            return;
        }

        System.out.print("Enter the minimum stock level (1-100): ");
        String minStockInput = scanner.nextLine();
        if (minStockInput.equalsIgnoreCase("e")) return;
        while (!isValidMinStockLevel(minStockInput)) {
            System.out.println("Invalid minimum stock level. Please enter an integer between 1 and 100.");
            minStockInput = scanner.nextLine();
            if (minStockInput.equalsIgnoreCase("e")) return;
        }
        int minInvLV = Integer.parseInt(minStockInput);

        System.out.print("Enter the unit price: ");
        String priceInput = scanner.nextLine();
        if (priceInput.equalsIgnoreCase("e")) return;
        while (priceInput.isEmpty()) {
            System.out.println("Unit price cannot be empty. Please enter a valid unit price.");
            priceInput = scanner.nextLine();
            if (priceInput.equalsIgnoreCase("e")) return;
        }
        double unitPrice = Double.parseDouble(priceInput);
        if (unitPrice < 0) {
            System.out.println("Unit price cannot be negative.");
            return;
        }

        System.out.println("\nExisting item groups:");
        List<ItemGroups> existingGroups = getExistingItemGroups();
        for (int i = 0; i < existingGroups.size(); i++) {
            System.out.println((i + 1) + ". " + existingGroups.get(i).getGroupName());
        }
        System.out.println((existingGroups.size() + 1) + ". Create new group");

        System.out.print("Choose an option: ");
        String groupChoiceInput = scanner.nextLine();
        if (groupChoiceInput.equalsIgnoreCase("e")) return;
        while (groupChoiceInput.isEmpty() || !isValidItemGroup(groupChoiceInput, existingGroups.size())) {
            System.out.println("Invalid choice. Please choose a valid option.");
            groupChoiceInput = scanner.nextLine();
            if (groupChoiceInput.equalsIgnoreCase("e")) return;
        }
        int groupChoice = Integer.parseInt(groupChoiceInput);

        ItemGroups itemGroup;
        if (groupChoice > 0 && groupChoice <= existingGroups.size()) {
            itemGroup = existingGroups.get(groupChoice - 1);
        } else {
            System.out.print("Enter the new group name: ");
            String groupName = scanner.nextLine();
            if (groupName.equalsIgnoreCase("e")) return;
            while (groupName.isEmpty()) {
                System.out.println("Group name cannot be empty. Please enter a valid group name.");
                groupName = scanner.nextLine();
                if (groupName.equalsIgnoreCase("e")) return;
            }
            itemGroup = new ItemGroups(groupName);
            existingGroups.add(itemGroup);
        }

        Item newItem = new Item(itemName, itemQuantity, itemGroup, minInvLV, unitPrice);
        newItem.setUnitPrice(unitPrice);
        getInventory().put(newItem.getItemId(), newItem);
        saveInventoryToFile();
        System.out.println("New item added successfully.");
    }

    public static List<ItemGroups> getExistingItemGroups() {
        // Collect all unique item groups from the inventory
        Set<String> groupNamesSet = new HashSet<>();
        List<ItemGroups> uniqueGroups = new ArrayList<>();
        for (Item item : getInventory().values()) {
            if (groupNamesSet.add(item.getItemGroup().getGroupName())) {
                uniqueGroups.add(item.getItemGroup());
            }
        }
        return uniqueGroups;
    }

    public static void displayInventory() {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.println("\nDisplay Inventory Options:");
            System.out.println("1. Display All Items");
            System.out.println("2. Display Items by Group");
            System.out.println("3. Display Item by ID");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");

            String choiceInput = scanner.nextLine();
            while (!isValidDisplayMenuChoice(choiceInput)) {
                System.out.println("Invalid choice. Please enter a number between 1 and 4.");
                choiceInput = scanner.nextLine();
            }
            int choice = Integer.parseInt(choiceInput);

            switch (choice) {
                case 1:
                    displayAllItems();
                    break;
                case 2:
                    displayItemsByGroup(scanner);
                    break;
                case 3:
                    displayItemById(scanner);
                    break;
                case 4:
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    public static void displayAllItems() {
        double totalInvValue = 0;
        
        System.out.println("\nCurrent Inventory:");
        System.out.printf("%-10s | %-20s | %-10s | %-15s | %-10s | %-10s%n", "ID", "Name", "Quantity", "Category", "Min Stock", "Unit Price");
        System.out.println("------------------------------------------------------------------------------------------");
        for (Item item : getInventory().values()) {
            System.out.printf("%-10s | %-20s | %-10d | %-15s | %-10d | %-10.2f%n", item.getItemId(), item.getItemName(), item.getItemQuantity(), item.getItemGroup().getGroupName(), item.getMinInvLV(), item.getUnitPrice());
            totalInvValue += (item.getItemQuantity() * item.getUnitPrice());
        }
        System.out.printf("Total Inventory Value: RM%.2f\n", totalInvValue);
    }

    private static void displayItemsByGroup(Scanner scanner) {
        System.out.println("\nExisting item groups:");
        List<ItemGroups> existingGroups = getExistingItemGroups();
        for (int i = 0; i < existingGroups.size(); i++) {
            System.out.println((i + 1) + ". " + existingGroups.get(i).getGroupName());
        }
        System.out.print("Choose a group to display: ");
        int groupChoice = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        if (groupChoice > 0 && groupChoice <= existingGroups.size()) {
            ItemGroups selectedGroup = existingGroups.get(groupChoice - 1);
            System.out.println("\nItems in group: " + selectedGroup.getGroupName());
            System.out.printf("%-10s | %-20s | %-10s | %-15s | %-10s | %-10s%n", "ID", "Name", "Quantity", "Category", "Min Stock", "Unit Price");
            System.out.println("------------------------------------------------------------------------------------------");
            for (Item item : selectedGroup.getItems()) {
                System.out.printf("%-10s | %-20s | %-10d | %-15s | %-10d | %-10.2f%n", item.getItemId(), item.getItemName(), item.getItemQuantity(), item.getItemGroup().getGroupName(), item.getMinInvLV(), item.getUnitPrice());
            }
        } else {
            System.out.println("Invalid group choice. Please try again.");
        }
    }

    private static void displayItemById(Scanner scanner) {
        System.out.print("\nEnter the item ID: ");
        String itemId = scanner.nextLine();

        Item item = getInventory().get(itemId);
        if (item != null) {
            System.out.println("\nItem Details:");
            System.out.printf("%-10s | %-20s | %-10s | %-15s | %-10s | %-10s%n", "ID", "Name", "Quantity", "Category", "Min Stock", "Unit Price");
            System.out.println("------------------------------------------------------------------------------------------");
            System.out.printf("%-10s | %-20s | %-10d | %-15s | %-10d | %-10.2f%n", item.getItemId(), item.getItemName(), item.getItemQuantity(), item.getItemGroup().getGroupName(), item.getMinInvLV(), item.getUnitPrice());

            System.out.print("\nDo you want to change the item details? (y/n): ");
            String response = scanner.nextLine();

            if (response.equalsIgnoreCase("y")) {
                promptUserToEditItemDetails(item);
            }
        } else {
            System.out.println("Item ID not found in inventory.");
        }
    }

    public static void promptUserToEditItemDetails(Item item) {
        Scanner scanner = new Scanner(System.in);

        boolean editing = true;
        while (editing) {
            System.out.print("\nPress 'e' to exit at any time.");
            System.out.println("\nEditing item: " + item.getItemName());
            System.out.println("1. Edit Name");
            System.out.println("2. Edit Quantity");
            System.out.println("3. Edit Group");
            System.out.println("4. Edit Min Stock Level");
            System.out.println("5. Edit Unit Price");
            System.out.println("6. Exit");
            System.out.print("Choose an option: ");
            String choiceInput = scanner.nextLine();
            if (choiceInput.equalsIgnoreCase("e")) return;
            while (choiceInput.isEmpty() || !isValidChoice(choiceInput, 6)) {
                System.out.println("Invalid choice. Please enter a number between 1 and 6.");
                choiceInput = scanner.nextLine();
                if (choiceInput.equalsIgnoreCase("e")) return;
            }
            int choice = Integer.parseInt(choiceInput);

            switch (choice) {
                case 1:
                    System.out.print("Enter new name: ");
                    String newName = scanner.nextLine();
                    if (newName.equalsIgnoreCase("e")) return;
                    while (newName.isEmpty()) {
                        System.out.println("Item name cannot be empty. Please enter a valid item name.");
                        newName = scanner.nextLine();
                        if (newName.equalsIgnoreCase("e")) return;
                    }
                    item.setItemName(newName);
                    break;
                case 2:
                    System.out.print("Enter new quantity: ");
                    String quantityInput = scanner.nextLine();
                    if (quantityInput.equalsIgnoreCase("e")) return;
                    while (!isValidItemQuantity(quantityInput)) {
                        System.out.println("Invalid quantity. Please enter a positive integer between 0 and 100.");
                        quantityInput = scanner.nextLine();
                        if (quantityInput.equalsIgnoreCase("e")) return;
                    }
                    int newQuantity = Integer.parseInt(quantityInput);
                    item.setItemQuantity(newQuantity);
                    break;
                case 3:
                    System.out.println("Existing item groups:");
                    List<ItemGroups> existingGroups = getExistingItemGroups();
                    for (int i = 0; i < existingGroups.size(); i++) {
                        System.out.println((i + 1) + ". " + existingGroups.get(i).getGroupName());
                    }
                    System.out.println((existingGroups.size() + 1) + ". Create new group");

                    int groupChoice = -1;
                    while (groupChoice < 1 || groupChoice > existingGroups.size() + 1) {
                        System.out.print("Choose an option: ");
                        String groupChoiceInput = scanner.nextLine();
                        if (groupChoiceInput.equalsIgnoreCase("e")) return;
                        try {
                            groupChoice = Integer.parseInt(groupChoiceInput);
                            if (groupChoice < 1 || groupChoice > existingGroups.size() + 1) {
                                System.out.println("Invalid choice. Please choose a valid option.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid input. Please enter a number.");
                        }
                    }

                    if (groupChoice > 0 && groupChoice <= existingGroups.size()) {
                        item.setItemGroup(existingGroups.get(groupChoice - 1));
                    } else if (groupChoice == existingGroups.size() + 1) {
                        System.out.print("Enter the new group name: ");
                        String groupName = scanner.nextLine();
                        if (groupName.equalsIgnoreCase("e")) return;
                        while (groupName.isEmpty()) {
                            System.out.println("Group name cannot be empty. Please enter a valid group name.");
                            groupName = scanner.nextLine();
                            if (groupName.equalsIgnoreCase("e")) return;
                        }
                        ItemGroups newGroup = new ItemGroups(groupName);
                        existingGroups.add(newGroup);
                        item.setItemGroup(newGroup);
                    }
                    break;
                case 4:
                    System.out.print("Enter new minimum stock level: ");
                    String minStockInput = scanner.nextLine();
                    if (minStockInput.equalsIgnoreCase("e")) return;
                    while (!isValidMinStockLevel(minStockInput)) {
                        System.out.println("Invalid minimum stock level. Please enter an integer between 1 and 100.");
                        minStockInput = scanner.nextLine();
                        if (minStockInput.equalsIgnoreCase("e")) return;
                    }
                    int newMinInvLV = Integer.parseInt(minStockInput);
                    item.setMinInvLV(newMinInvLV);
                    break;
                case 5:
                    System.out.print("Enter new unit price: ");
                    String priceInput = scanner.nextLine();
                    if (priceInput.equalsIgnoreCase("e")) return;
                    while (priceInput.isEmpty() || !isValidUnitPrice(priceInput)) {
                        System.out.println("Invalid unit price. Please enter a value between 1 and 1000.");
                        priceInput = scanner.nextLine();
                        if (priceInput.equalsIgnoreCase("e")) return;
                    }
                    double newUnitPrice = Double.parseDouble(priceInput);
                    item.setUnitPrice(newUnitPrice);
                    break;
                case 6:
                    editing = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
            saveInventoryToFile();  // Save changes to file
            removeEmptyGroups();  // Remove empty groups
        }
    }

    public static void manageInventory() {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        // Load inventory from file at the start
        loadInventoryFromFile();

        while (!exit) {
            System.out.println("\nInventory Management System");
            System.out.println("1. Add New Item");
            System.out.println("2. Edit Item Details");
            System.out.println("3. Display Inventory");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");

            String choiceInput = scanner.nextLine();
            while (!isValidMainMenuChoice(choiceInput)) {
                System.out.println("Invalid choice. Please enter a number between 1 and 7.");
                choiceInput = scanner.nextLine();
            }
            int choice = Integer.parseInt(choiceInput);

            switch (choice) {
                case 1:
                    promptUserToAddNewItem(); // Add New Item
                    break;
                case 2:
                    promptUserToEditItemDetails(); // Edit Item Details
                    break;
                case 3:
                    displayInventory(); // Display inventory
                    break;
                case 4:
                    saveInventoryToFile(); // Save inventory to file before exiting
                    exit = true;
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        scanner.close();
    }

    // Validation methods
    private static boolean isValidItemGroup(String choice, int maxSize) {
        try {
            int choiceInt = Integer.parseInt(choice);
            return choiceInt > 0 && choiceInt <= maxSize + 1;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean isValidItemQuantity(String quantityInput) {
        try {
            int quantity = Integer.parseInt(quantityInput);
            return quantity >= 0 && quantity <= 100;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean isValidMinStockLevel(String minStockInput) {
        try {
            int minStockLevel = Integer.parseInt(minStockInput);
            return minStockLevel >= 1 && minStockLevel <= 100;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean isValidUnitPrice(String priceInput) {
        try {
            double unitPrice = Double.parseDouble(priceInput);
            return unitPrice >= 1 && unitPrice <= 1000;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean isValidMainMenuChoice(String choiceInput) {
        try {
            int choice = Integer.parseInt(choiceInput);
            return choice >= 1 && choice <= 7;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean isValidDisplayMenuChoice(String choiceInput) {
        try {
            int choice = Integer.parseInt(choiceInput);
            return choice >= 1 && choice <= 4;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Generate unique item ID
    public String generateItemId() {
        return String.format("I%04d", idCounter.incrementAndGet());
    }

    public static void initializeIdCounter() {
        int maxId = inventory.keySet().stream()
                .mapToInt(id -> Integer.parseInt(id.substring(1)))
                .max()
                .orElse(0);
        idCounter.set(maxId);
    }

    //Add quantity after receiving order
    public static void addItemQty(String restockID, int restockQty){
        ArrayList<Item> tempItem = GoodsReceive.readItemIntoArray("items.txt");
        int index = 0;
        for (Item item : tempItem){
            if (restockID.equals(item.getItemId())) {
                tempItem.get(index).setItemQuantity(item.getItemQuantity() + restockQty);
            }
            index++;
        }
        //Empty Item File for Overwriting
        try {
            new FileWriter("items.txt", false).close();
        } catch (IOException e) {
            System.out.println("Empty file failed!");
        }
        GoodsReceive.saveItemArrayToFile(tempItem);
    }
}