import cards.Card;
import cards.Deck;

/**
 * @author Petter Hannevold
 */

public class Main {

	public static void main(String[] args) {

	}

	private static void writeCardProperties() {
		String[] suits = {"C", "D", "H", "S"};

		StringBuilder sb = new StringBuilder();

		for (String s : suits) {
			String suitText = null;
			switch (s) {
				case "C":
					suitText = "clubs";
					break;
				case "D":
					suitText = "diamonds";
					break;
				case "H":
					suitText = "hearts";
					break;
				case "S":
					suitText = "spades";
					break;
			}
			for (int i = 1; i <=13; i++) {
				sb.append(s + i + "=" + i + " of " + suitText + "\n");
			}
		}
		System.out.print(sb.toString()
				.replaceAll("=1\\s", "=Ace ")
				.replaceAll("=11\\s", "=Jack ")
				.replaceAll("=12\\s", "=Queen ")
				.replaceAll("=13\\s", "=King ")
				.replaceAll("=2\\s", "=two ")
				.replaceAll("=3\\s", "=three  ")
				.replaceAll("=4\\s", "=four ")
				.replaceAll("=5\\s", "=five ")
				.replaceAll("=6\\s", "=six ")
				.replaceAll("=7\\s", "=seven ")
				.replaceAll("=8\\s", "=eight ")
				.replaceAll("=9\\s", "=nine  "));
	}
}
