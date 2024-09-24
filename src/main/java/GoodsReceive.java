
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.TreeMap;

public class GoodsReceive {

    //Display Menu After Choosing Goods Receive Option
    public static void goodsMenu() {
        Scanner sc = new Scanner(System.in);
        boolean invalid;
        boolean leave = false;
        int choice = 0;

        while (!leave) {
            //Read PO File and get Array List
            ArrayList<PurchaseOrder> goodsReceive = PurchaseOrder.readPOFromFile("PO.txt");
            int max = goodsReceive.size(); //Assign max value

            //Remove Returned and Received Order
            for (int i = max - 1; i >= 0; i--) {
                if (goodsReceive.get(i).getStatus().equals("Receive")
                        || goodsReceive.get(i).getStatus().equals("Return")) {
                    goodsReceive.remove(i);
                }
            }
            max = goodsReceive.size(); //Assign new max value

            //Display Goods Receive Order
            displayGoodReceive(goodsReceive);

            do {
                System.out.println("(Enter -1 to Exit)");
                System.out.print("Select Order: ");
                try {
                    choice = sc.nextInt();
                    if (choice == -1) { //If choice = -1, leave
                        leave = true;
                        invalid = false;
                    } else {
                        invalid = choice < 1 || choice > max; //Find invalid
                        if (invalid) {
                            System.out.println("Please choose an option within the range.\n");
                        }
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Invalid Input, Please try again!\n");
                    sc.next();
                    invalid = true;
                }
            } while (invalid);

            if (!leave) {
                //After choosing valid goods Receive Order
                showGoodReceive(goodsReceive.get(choice - 1));
            }
        }
    }

    //Display Goods Receive Order
    private static void displayGoodReceive(ArrayList<PurchaseOrder> goodsReceive) {
        int max = goodsReceive.size();

        System.out.println("\n==================================================");
        System.out.printf("%30s\n", "Goods Receive");
        System.out.println("==================================================");
        System.out.println("Purchase Order");
        System.out.println("--------------");

        //Print all Goods Receive Order out
        for (int i = 0; i < max; i++) {
            int itemCount = goodsReceive.get(i).getItemCount();

            System.out.print(i + 1 + ". " + goodsReceive.get(i).getOrderID() + "\n"
                    + "Item List: ");

            //Loop to print all item
            for (int j = 0; j < itemCount; j++) {
                String itemName = PurchaseOrder.findItemName(goodsReceive.get(i), j);
                System.out.print(itemName + " ");
            }
            System.out.println("\n--------------------------------------------------");
        }
    }

    //Display Choosen Order
    private static void showGoodReceive(PurchaseOrder goodsReceive) {
        Supplier supplier = PurchaseOrder.findSupplier(goodsReceive.getSuppID());
        int itemNum = goodsReceive.getItemCount();

        System.out.println("\n==================================================");
        System.out.printf("%-10s %s\n", goodsReceive.getOrderID(), supplier.getName());
        System.out.println("--------------------------------------------------");
        System.out.printf("%s %-10s %-20s %-5s %s\n", "No", "Item ID", "Item Name", "Qty", "Total Cost");

        for (int i = 0; i < itemNum; i++) {
            String itemID = goodsReceive.getOrderItems().get(i).getOrdItemID();
            String itemName = PurchaseOrder.findItemName(goodsReceive, i);
            int itemQuantity = goodsReceive.getOrderItems().get(i).getQuantity();
            double itemCost = goodsReceive.getOrderItems().get(i).getTotalPrice();

            System.out.printf("%d. %-10s %-20s %-5d %.2f\n", i + 1, itemID, itemName, itemQuantity, itemCost);
        }
        System.out.println("\n");

        //Display Options for user
        goodsOption(goodsReceive);
    }

    //Display Options for user
    private static void goodsOption(PurchaseOrder goodsReceive) {
        Scanner sc = new Scanner(System.in);
        boolean invalid;
        boolean leave = false;
        int choice = 0;

        while (!leave) {
            do {
                System.out.println("1. Confirm Receive");
                System.out.println("2. Goods Return");
                System.out.println("3. Back");
                System.out.print("Select Option: ");

                try {
                    choice = sc.nextInt();
                    invalid = false;
                } catch (InputMismatchException e) {
                    System.out.println("Invalid Input, Please try again!\n");
                    sc.next();
                    invalid = true;
                }
            } while (invalid);

            switch (choice) {
                case 1 -> {
                    confirmReceive(goodsReceive); //Confirm Receive Option
                    leave = true;
                }
                case 2 -> {
                    GoodsReturn.goodsReturnMenu(goodsReceive); //Goods Return Menu
                    leave = true;
                }
                case 3 -> {
                    leave = true; //Leave
                }
                default ->
                    System.out.println("Invalid options! Please choose the option within the range.\n");
            }
        }
    }

    //Save Status Into Purchase Order File
    public static void saveStatusTOPO(PurchaseOrder goodsReceive) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("PO.txt", true))) {
            bw.write(goodsReceive.getOrderID() + "|" + goodsReceive.getSuppID() + "|" + goodsReceive.getOrderDate() + "|"
                    + goodsReceive.getStatus() + "|" + goodsReceive.getTotalPOprice() + "|" + goodsReceive.getItemCount());
            for (int i = 0; i < goodsReceive.getItemCount(); i++) {
                bw.newLine();
                bw.write(goodsReceive.getOrderItems().get(i).getOrdItemID() + "|"
                        + goodsReceive.getOrderItems().get(i).getQuantity() + "|"
                        + goodsReceive.getOrderItems().get(i).getUnitPrice());
            }
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Writing PO file failed!");
            System.exit(1);
        }
    }

    //Confirm Receive Page
    private static void confirmReceive(PurchaseOrder goodsReceive) {
        Scanner sc = new Scanner(System.in);
        ArrayList<PurchaseOrder> purchaseOrder = PurchaseOrder.readPOFromFile("PO.txt");
        boolean invalid;
        String input;

        do {
            System.out.println("\nAre you sure to confirm order?");
            System.out.println("(Y to Confirm, N to Cancel)");
            System.out.print("Y/N: ");
            input = sc.next().toUpperCase(); //To Upper Case

            switch (input) {
                case "Y" -> { //If yes
                    //Empty Purchase Order File for Overwritting
                    try {
                        new FileWriter("PO.txt", false).close();
                    } catch (IOException e) {
                        System.out.println("Empty file failed!");
                    }
                    //Save all back into Purchase Order File with Updated Status
                    for (PurchaseOrder purchaseOrder1 : purchaseOrder) {
                        if (goodsReceive.getOrderID().equals(purchaseOrder1.getOrderID())) {
                            purchaseOrder1.setStatus("Receive");
                            saveStatusTOPO(purchaseOrder1);
                        } else {
                            saveStatusTOPO(purchaseOrder1);
                        }
                    }
                    //Add Quantity into Item File
                    for (int i = 0; i < goodsReceive.getOrderItems().size(); i++) {
                        Item.addItemQty(goodsReceive.getOrderItems().get(i).getOrdItemID(), goodsReceive.getOrderItems().get(i).getQuantity());
                    }
                    System.out.println("Successfully Received.");
                    IMS.systemPause();
                    invalid = false;
                }
                case "N" -> { //If no
                    System.out.println("Order not received.");
                    IMS.systemPause();
                    invalid = false; //Exit
                }
                default -> {
                    System.out.println("Please enter Y or N");
                    invalid = true;
                }
            }
        } while (invalid);
    }

    //Read and get Item Array List
    public static ArrayList<Item> readItemIntoArray(String filename) {
        ArrayList<Item> itemList = new ArrayList<>();
        String fileName = "items.txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            Map<String, ItemGroups> groupMap = new TreeMap<>();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 6) {
                    String itemId = parts[0];
                    String itemName = parts[1];
                    int itemQuantity = Integer.parseInt(parts[2]);
                    String groupName = parts[3];
                    int minInvLV = Integer.parseInt(parts[4]);
                    double unitPrice = Double.parseDouble(parts[5]);
                    ItemGroups itemGroup = groupMap.computeIfAbsent(groupName, ItemGroups::new);
                    Item item = new Item(itemId, itemName, itemQuantity, itemGroup, minInvLV);
                    item.setUnitPrice(unitPrice);
                    Item.getInventory().put(itemId, item);
                    itemList.add(item);
                }
            }
        } catch (IOException e) {
            System.out.println("Can't Read File");
        }
        return itemList;
    }

    //Save Item Array List into Item File
    public static void saveItemArrayToFile(ArrayList<Item> itemList) {
        String fileName = "items.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (Item item : itemList) {
                writer.write(item.getItemId() + "," + item.getItemName() + "," + item.getItemQuantity() + "," + item.getItemGroup().getGroupName() + "," + item.getMinInvLV() + "," + item.getUnitPrice());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Can't Save File");
        }
    }
}
