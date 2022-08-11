package processor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.Iterator;
import java.util.Map;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.TreeMap;

public class OrdersProcessor {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		TreeMap<Integer, Client> newMap = new TreeMap<Integer, Client>();
		ArrayList<Item> itemList = new ArrayList<Item>();
		ArrayList<String> rows = new ArrayList<String>();

		String itemFileName = "";
		String decision = "";
		int orders = 0;
		String baseFileName = "";
		String resultsFileName = "";

		Scanner scanner = new Scanner(System.in);

		System.out.println("Enter item's data file name: ");
		itemFileName = scanner.next();
		System.out.println("Enter 'y' for multiple threads, any other character otherwise: ");
		decision = scanner.next();
		System.out.println("Enter number of orders to process: ");
		orders = scanner.nextInt();
		System.out.println("Enter order's base filename: ");
		baseFileName = scanner.next();
		System.out.println("Enter result's filename: ");
		resultsFileName = scanner.next();

		scanner.close();

		long startTime = System.currentTimeMillis();

		try {

			Scanner itemScanner = new Scanner(new BufferedReader(new FileReader(itemFileName)));

			while (itemScanner.hasNextLine()) {

				itemList.add(new Item(itemScanner.next(), itemScanner.nextDouble()));

			}

		} catch (FileNotFoundException e) {

			e.printStackTrace();

		}

		if (decision.equals("y")) {

			ArrayList<Worker> workerList = new ArrayList<>();
			ArrayList<Thread> threadList = new ArrayList<>();

			for (int i = 0; i < orders; i++) {

				workerList.add(new Worker(i + 1, newMap, itemList, baseFileName, resultsFileName));

			}

			for (int i = 0; i < workerList.size(); i++) {

				threadList.add(new Thread(workerList.get(i)));

			}

			for (int i = 0; i < threadList.size(); i++) {

				threadList.get(i).start();

			}

			try {

				for (int i = 0; i < threadList.size(); i++) {

					threadList.get(i).join();

				}

			} catch (InterruptedException e) {

				e.printStackTrace();

			}

			ArrayList<String> newList = new ArrayList<>();

			newList.add("***** Summary of all orders *****");

			double grandTotal = 0;

			for (int checker = 0; checker < itemList.size(); checker++) {

				int totCount = 0;

				for (Map.Entry<Integer, Client> entry : newMap.entrySet()) {

					if (entry.getValue().items.contains(itemList.get(checker).name)) {

						totCount += entry.getValue().itemInstances(itemList.get(checker).name);

					}

				}

				if (totCount > 0) {

					double total = totCount * itemList.get(checker).price;

					grandTotal += totCount * itemList.get(checker).price;

					newList.add("Summary - Item's name: " + itemList.get(checker).name + ", Cost per item: "
							+ NumberFormat.getCurrencyInstance().format(itemList.get(checker).price) + ", Number sold: "
							+ totCount + ", Item's Total: " + NumberFormat.getCurrencyInstance().format(total));
				}

			}
			newList.add("Summary Grand Total: " + NumberFormat.getCurrencyInstance().format(grandTotal));

			try {

				BufferedWriter buffwrite = new BufferedWriter(new FileWriter(resultsFileName, true));

				for (String row : newList) {

					buffwrite.write(row);
					buffwrite.newLine();

				}

				buffwrite.flush();
				buffwrite.close();

			} catch (IOException e) {

				System.err.print(e.getMessage());

			}

			long endTime = System.currentTimeMillis();
			System.out.println("Processing time (msec ): " + (endTime - startTime));

		} else {

			int id = 0;

			String filename = "";

			for (int i = 1; i <= orders; i++) {

				filename = baseFileName + i + ".txt";

				try {

					Scanner newScanner = new Scanner(new BufferedReader(new FileReader(filename)));

					String unncessary = newScanner.next();

					id = newScanner.nextInt();

					System.out.println("Reading order for client with id: " + id);

					newMap.put(id, new Client(id, new ArrayList<String>()));

					while (newScanner.hasNextLine()) {

						newMap.get(id).items.add(newScanner.next());

					}

				} catch (FileNotFoundException e) {

					e.printStackTrace();

				}

			}

			Collections.sort(itemList);

			Iterator<Entry<Integer, Client>> iterator = newMap.entrySet().iterator();

			while (iterator.hasNext()) {

				double clientTotal = 0;

				Entry<Integer, Client> entry = iterator.next();

				rows.add("----- Order details for client with Id: " + entry.getKey() + " -----");

				for (int count = 0; count < itemList.size(); count++) {

					if (entry.getValue().items.contains(itemList.get(count).name)) {

						int casesItem = entry.getValue().itemInstances(itemList.get(count).name);

						double itemCost = itemList.get(count).price;

						double totalCost = itemCost * casesItem;

						rows.add("Item's name: " + itemList.get(count).name + ", Cost per item: "
								+ NumberFormat.getCurrencyInstance().format(itemCost) + ", Quantity: " + casesItem
								+ ", Cost: " + NumberFormat.getCurrencyInstance().format(totalCost));

						clientTotal += totalCost;

					}

				}

				rows.add("Order Total: " + NumberFormat.getCurrencyInstance().format(clientTotal));

			}

			rows.add("***** Summary of all orders *****");

			double grandTotal = 0;

			for (int checker = 0; checker < itemList.size(); checker++) {

				int totCount = 0;

				for (Map.Entry<Integer, Client> entry : newMap.entrySet()) {

					if (entry.getValue().items.contains(itemList.get(checker).name)) {

						totCount += entry.getValue().itemInstances(itemList.get(checker).name);

					}

				}

				if (totCount > 0) {

					double total = totCount * itemList.get(checker).price;

					grandTotal += totCount * itemList.get(checker).price;

					rows.add("Summary - Item's name: " + itemList.get(checker).name + ", Cost per item: "
							+ NumberFormat.getCurrencyInstance().format(itemList.get(checker).price) + ", Number sold: "
							+ totCount + ", Item's Total: " + NumberFormat.getCurrencyInstance().format(total));
				}

			}

			rows.add("Summary Grand Total: " + NumberFormat.getCurrencyInstance().format(grandTotal));

			try {

				BufferedWriter buffwrite = new BufferedWriter(new FileWriter(resultsFileName, false));

				for (String row : rows) {

					buffwrite.write(row);
					buffwrite.newLine();

				}

				buffwrite.flush();
				buffwrite.close();

			} catch (IOException e) {

				System.err.print(e.getMessage());

			}

			long endTime = System.currentTimeMillis();
			System.out.println("Processing time (msec ): " + (endTime - startTime));
			System.out.println("Results can be found in the file: " + resultsFileName);

		}
	}

}
