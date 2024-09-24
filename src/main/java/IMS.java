
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;

public class IMS {

    public static void main(String[] args) throws FileNotFoundException, IOException {
        Scanner scanner = new Scanner(System.in);
        int choice = 0;

        User user = new User();

        do {
            header();
            System.out.println("Main Menu");
            System.out.println("1.Login");
            System.out.println("2.Signup");
            System.out.println("3.Exit Program");
            System.out.print("Enter your choice > ");
            try {
                choice = scanner.nextInt();

            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
                scanner.next();  // Clear the invalid input from the scanner buffer
            }

            switch (choice) {
                case 1:
                    user.login();
                    break;
                case 2:
                    user.signup();
                    break;
                case 3:
                    System.out.println("Program exitting...");
                    System.exit(0);
                default:
                    System.out.println("Invalid input.");
            }
            clearConsole();
        } while (choice != 3);

    }

    public static void dashboard() {
        Scanner scanner = new Scanner(System.in);
        int choice = 0;

        Item item = new Item();
        item.loadInventoryFromFile();

        Map<String, Item> inventory = item.getInventory();
        int totalItem = 0;
        int totalItemQuantity = 0;
        int itemGroup = 0;
        int lowStockItems = 0;
        ArrayList<String> lowStockItem = new ArrayList<>();
        int quantityOrdered = 0;
        float totalCost = 0;
        int quantityToReceive = 0;
        ArrayList<String> itemGroups = new ArrayList<>();
        double totalInvValue = 0;

        for (Item items : inventory.values()) {
            if (items.getItemQuantity() < items.getMinInvLV()) {
                lowStockItem.add(items.getItemId());
                lowStockItems++;
            }
            if (!itemGroups.contains(items.getItemGroup().getGroupName())) {
                itemGroups.add(items.getItemGroup().getGroupName());
                itemGroup++;
            }
            totalItemQuantity += items.getItemQuantity();
            totalInvValue += (items.getItemQuantity() * items.getUnitPrice());
            totalItem++;
        }

        PurchaseOrder purchaseOrder = new PurchaseOrder();
        ArrayList<PurchaseOrder> poList = purchaseOrder.getPOFromFile();

        for (PurchaseOrder po : poList) {
            quantityOrdered += po.getItemCount();
            totalCost += po.getTotalPOprice();
            if (po.getStatus().equalsIgnoreCase("pending")) {
                for (int i = 0; i < po.getItemCount(); i++) {
                    quantityToReceive += po.getOrderItems().get(i).getQuantity();
                }
            }
        }
        do {

            header();
            System.out.printf("\n%-35s%-40s%-40s\n", "PRODUCT DETAILS", "INVENTORY SUMMARY", "TOTAL PURCHASE ORDER");
            System.out.printf("Low Stock Items     %-15d", lowStockItems);
            System.out.printf("Quantity in HAND          %-14d", totalItemQuantity);
            System.out.printf("Quantity Ordered    %-20d\n", quantityOrdered);

            System.out.printf("All Item Group      %-15d", itemGroup);
            System.out.printf("Quantity to be RECEIVED   %-14d", quantityToReceive);
            System.out.printf("Total Cost          RM%-18.2f\n", totalCost);

            System.out.printf("All Items           %-15d", totalItem);
            System.out.printf("Total Inventory Value     RM%-12.2f\n\n", totalInvValue);

            System.out.println("Low Stock Items List :");
            for (int i = 0; i < lowStockItem.size(); i++) {
                System.out.println(i + 1 + ". " + lowStockItem.get(i));
            }

            System.out.println("\n\n1. Purchase Order Menu");
            System.out.println("2. Item Menu");
            System.out.println("3. Goods Receive Menu");
            System.out.println("4. Supplier Menu");
            System.out.println("5. Logout");
            System.out.print("Enter your choice > ");
            try {
                choice = scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("  Input Error, Please Try Again!!!");
                scanner.nextLine();
            }
            switch (choice) {
                case 1:
                    PurchaseOrder.poMenu();
                    break;
                case 2:
                    Item.manageInventory();
                    break;
                case 3:
                    GoodsReceive.goodsMenu();
                    break;
                case 4:
                    Supplier.editSupplier();
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Input invalid. Please try again.");
            }
        } while (choice != 5);
    }

    public static void line() {
        System.out.println("==========================================================================================================");
    }

    public static void header() {
        try {
            FileReader reader = new FileReader("art.txt");
            int data = reader.read();
            while (data != -1) {
                System.out.print((char) data);

                data = reader.read();
            }

            System.out.println("");
            reader.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        line();
        System.out.println("================================= Welcome To Inventory Management System =================================");
        line();

        String todayDay = LocalDate.now().getDayOfWeek().toString().toLowerCase();
        String todayDate = LocalDate.now().toString();
        String todayTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        System.out.println("Day : " + todayDay.substring(0, 1).toUpperCase() + todayDay.substring(1) + "\t\t\t\tDate : " + todayDate + "\t\t\t\tTime : " + todayTime);
    }
    
    //For console clearing screen function
    public static void clearConsole() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void systemPause() {
        Scanner sc = new Scanner(System.in);
        System.out.println("\nPress Enter to continue...");
        sc.nextLine();
    }
}
