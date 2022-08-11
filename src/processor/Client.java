package processor;

import java.util.ArrayList;

public class Client {

	int id;
	ArrayList<String> items = new ArrayList<>();

	public Client(int id, ArrayList<String> arr) {

		this.id = id;
		this.items = arr;

	}

	public void setID(int id) {

		this.id = id;

	}

	public void setItems(ArrayList<String> newArr) {

		this.items = newArr;

	}

	public int itemInstances(String item) {

		int counter = 0;

		for (int i = 0; i < items.size(); i++) {

			if (item.equals(items.get(i))) {

				counter++;

			}

		}

		return counter;

	}

}
