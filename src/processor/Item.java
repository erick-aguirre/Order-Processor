package processor;

public class Item implements Comparable<Item> {

	double price;
	String name;

	public Item(String name, double price) {

		this.price = price;
		this.name = name;

	}

	public int compareTo(Item ite) {

		return name.compareTo(ite.name);

	}
}
