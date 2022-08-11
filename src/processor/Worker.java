package processor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;

import java.util.Iterator;

import java.util.Scanner;
import java.util.TreeMap;
import java.util.Map.Entry;

public class Worker implements Runnable {

	int fileNum;
	TreeMap<Integer, Client> clientMap;
	ArrayList<Item> itemsList;
	String base;
	ArrayList<String> list = new ArrayList<String>();
	String results;
	int orders;

	public Worker(int num, TreeMap<Integer, Client> hash, ArrayList<Item> list, String baseName, String results) {

		this.fileNum = num;
		this.clientMap = hash;
		this.itemsList = list;
		this.base = baseName;
		this.results = results;

	}

	@Override
	public void run() {

		synchronized (clientMap) {
			try {

				String fileName = base + fileNum + ".txt";

				Scanner newScanner = new Scanner(new BufferedReader(new FileReader(fileName)));

				String unncessary = newScanner.next();

				int id = newScanner.nextInt();

				System.out.println("Reading order for client with id: " + id);

				clientMap.put(id, new Client(id, new ArrayList<String>()));

				while (newScanner.hasNextLine()) {

					clientMap.get(id).items.add(newScanner.next());

				}

			} catch (FileNotFoundException e) {

				e.printStackTrace();

			}

			Collections.sort(itemsList);

			Iterator<Entry<Integer, Client>> iterator = clientMap.entrySet().iterator();

			while (iterator.hasNext()) {

				double clientTotal = 0;

				Entry<Integer, Client> entry = iterator.next();

				list.add("----- Order details for client with Id: " + entry.getKey() + " -----");

				for (int count = 0; count < itemsList.size(); count++) {

					if (entry.getValue().items.contains(itemsList.get(count).name)) {

						int casesItem = entry.getValue().itemInstances(itemsList.get(count).name);

						double itemCost = itemsList.get(count).price;

						double totalCost = itemCost * casesItem;

						list.add("Item's name: " + itemsList.get(count).name + ", Cost per item: "
								+ NumberFormat.getCurrencyInstance().format(itemCost) + ", Quantity: " + casesItem
								+ ", Cost: " + NumberFormat.getCurrencyInstance().format(totalCost));

						clientTotal += totalCost;

					}

				}

				list.add("Order Total: " + NumberFormat.getCurrencyInstance().format(clientTotal));

			}
		}

		try {

			BufferedWriter buffwrite = new BufferedWriter(new FileWriter(results, false));

			for (String row : list) {

				buffwrite.write(row);
				buffwrite.newLine();

			}

			buffwrite.flush();
			buffwrite.close();

		} catch (IOException e) {

			System.err.print(e.getMessage());

		}

	}

}
