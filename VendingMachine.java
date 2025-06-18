package VendingMachine;

import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class VendingMachine {
	// 파일 경로
	private final String beveragesFile = "beverages.txt";
	private final String changeFile = "change.txt";
	private final String soldOutFile = "soldOut.txt";
	private final String salesFile = "sales.txt";

	// 초기화
	private Map<Integer, Beverage> beverages; // 가격, beverage
	private HashMap<Integer, Integer> change; // 화폐종류, 화폐개수
	private final int maxMoney = 7000; // 투입될 수 있는 화페 최대값
	private final int maxInsertion = 5000; // 한 번에 투입할 수 있는 최대값
	private int totalMoney;

	// 초기화
	public VendingMachine() {
		beverages = new TreeMap<>();
		change = new HashMap<>();
		totalMoney = 0;

		loadBeverages();
		loadChange();
	}

	private void loadBeverages() {
		List<Beverage> loadedBeverages = Beverage.loadBeveragesFromFile(beveragesFile);
		for (Beverage beverage : loadedBeverages) {
			beverages.put(beverage.getPrice(), beverage);
		}
	}

	private void loadChange() {
		try (BufferedReader reader = new BufferedReader(new FileReader(changeFile))) {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split(",");
				int denomination = Integer.parseInt(parts[0]);
				int count = Integer.parseInt(parts[1]);
				change.put(denomination, count);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 파일에 음료 정보를 저장 ,음료의 정보가 변경될 때마다 사용
	public void saveBeverages() {
		try (PrintWriter writer = new PrintWriter(new FileWriter(beveragesFile))) {
			for (Beverage beverage : beverages.values()) {
				writer.println(beverage.getName() + "," + beverage.getPrice() + "," + beverage.getStock());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 파일에 화폐 정보를 저장 ,화폐의 개수가 변경될 때마다 사용
	public void saveChange() {
		try (PrintWriter writer = new PrintWriter(new FileWriter(changeFile))) {
			for (Map.Entry<Integer, Integer> entry : change.entrySet()) {
				writer.println(entry.getKey() + "," + entry.getValue());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 금액이 투입 되었을 때
	public String insertMoney(int money) {
		if (money % 10 != 0) { // 10원 단위로 입력하지 않았을 때
			return "10원 단위로만 투입 가능합니다.";
		}

		if (totalMoney + money > maxMoney) { // 투입된 총 금액이 7000원보다 크면
			return "투입 금액은 7000원을 넘을 수 없습니다.";
		}

		if (money > maxInsertion) { // 투입될 금액이 5000원보다 크면
			return "한 번에 투입할 수 있는 금액은 5000원을 넘을 수 없습니다.";
		}

		totalMoney += money; // 총금액
		updateChange(money); // 잔돈에 반영
		saveChange(); // 파일에 저장
		return "성공적으로 " + money + "원이 투입되었습니다.";
	}

	// 사용자가 반환을 하면.
	public HashMap<Integer, Integer> returnMoney() {
		HashMap<Integer, Integer> returnedChange = pushChange(totalMoney);
		totalMoney = 0; // 반환을 했으니 금액은 0으로 초기화
		saveChange(); // 파일에 저장
		return returnedChange; // <화폐,개수>를 반환
	}

	// 잔돈을 업데이트하는 함수
	private void updateChange(int money) {
		int remaining = money;
		int[] coinValues = { 1000, 500, 100, 50, 10 }; // 큰 단위부터 처리하기 위해 배열 사용
		for (int coin : coinValues) {
			if (remaining >= coin) {
				int numCoins = remaining / coin;
				remaining %= coin;
				change.put(coin, change.get(coin) + numCoins);
			}
		}
	}

	// 음료 선택 함수
	public String selectBeverage(int price) { // 인자로 각 음료의 가격을 받음.
		Beverage beverage = beverages.get(price);

		if (totalMoney < price) { // 투입된 금액이 가격보다 작을 경우
			return "잔액이 부족합니다.";
		}

		if (beverage.isSoldOut()) { // 품절되어 있는 음료를 선택했을 경우
			return "음료가 품절되었습니다.";
		}

		if (beverage.getStock() == 1) { // 마지막 남은 음료를 선택했을 경우 품절기록
			recordSoldOut(beverage.getName());
		}

		beverage.dispense(); // 개수 -1
		totalMoney -= price; // 잔고 반영
		saveBeverages(); // 파일에 저장
		recordSale(price); // 매출 기록

		return beverage.getName() + "를 선택하셨습니다.";
	}

	private HashMap<Integer, Integer> pushChange(int totalMoney) {
		// 남은 거스름돈을 저장
		int remainingChange = totalMoney;
		// 반환할 거스름돈을 저장할 HashMap
		HashMap<Integer, Integer> returnedChange = new HashMap<>();

		// 거스름돈의 동전 단위를 내림차순으로 정렬하여 처리
		TreeSet<Integer> denominations = new TreeSet<>(change.keySet());

		for (int denomination : denominations.descendingSet()) {
			// 현재 동전 단위로 거스름돈을 최대한 지불할 수 있는 동전의 개수 계산
			int numBills = Math.min(remainingChange / denomination, change.get(denomination));
			// 반환할 거스름돈에 동전의 종류와 개수 추가
			returnedChange.put(denomination, numBills);
			// 남은 거스름돈에서 사용한 동전의 금액만큼 차감
			remainingChange -= numBills * denomination;
			// 화폐 현황에 반영
			change.put(denomination, change.get(denomination) - numBills);
		}

		return returnedChange;
	}

	// 잔돈의 현황을 반환해주는 함수
	public HashMap<Integer, Integer> getChangeCounts() {
		return new HashMap<>(change);
	}

	// 남은 잔돈의 총액을 반환하는 함수
	public int getRemainingChangeCount() {
		int totalChangeCount = 0;

		for (int count : change.values()) {
			totalChangeCount += count;
		}

		return totalChangeCount;
	}

	// 투입된 총액 반환하는 함수
	public int getTotalMoney() {
		return totalMoney;
	}

	// 음료 정보 반환하는 함수
	public Map<Integer, Beverage> getBeverages() {
		return beverages;
	}

	// 음료가 품절 되었을 시에 파일에 저장하는 함수
	private void recordSoldOut(String beverageName) {

		try (PrintWriter writer = new PrintWriter(new FileWriter(soldOutFile, true))) {
			String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

			writer.println(dateStr + ", 상품명: " + beverageName);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 음료가 판매 되었을 시 파일에 저장하는 함수(매출기록)
	public void recordSale(int price) {

		try (PrintWriter writer = new PrintWriter(new FileWriter(salesFile, true))) {
			String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

			writer.println(currentDate + "," + price);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 파일에서 일별 매출을 읽어오는 함수
	public static Map<String, Integer> getDailySales() {
		Map<String, Integer> dailySales = new HashMap<>();

		try (BufferedReader reader = new BufferedReader(new FileReader("sales.txt"))) {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split(",");
				String date = parts[0];
				int price = Integer.parseInt(parts[1]); // 정수형으로 변환

				// Key값이 일수 이기 때문에 일별매출을 저장
				dailySales.put(date, dailySales.getOrDefault(date, 0) + price);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return dailySales;
	}

	// 파일에서 월별 매출을 읽어오는 함수
	public static Map<String, Integer> getMonthlySales() {
		Map<String, Integer> monthlySales = new HashMap<>();

		try (BufferedReader reader = new BufferedReader(new FileReader("sales.txt"))) {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split(",");
				String date = parts[0];
				int price = Integer.parseInt(parts[1]); // 정수형으로 변환
				String month = date.substring(0, 7); // 월까지만 반환(yyyy-mm)

				// Key값이 월이기 때문에 월별매출을 저장 (월이 같으면 Key값도 같기 때문)
				monthlySales.put(month, monthlySales.getOrDefault(month, 0) + price);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return monthlySales;
	}

	// 재고 보충하는 함수 (콤보박스로 구현하기 때문에 예외처리 X)
	public void replenishInventory(String beverageName, int quantity) {
		Beverage selectedBeverage = null;

		for (Beverage beverage : beverages.values()) {
			if (beverage.getName().equals(beverageName)) {
				selectedBeverage = beverage;
				break;
			}
		}

		selectedBeverage.replenishStock(quantity);
		saveBeverages(); // 재고 보충 후 변경된 재고를 파일에 저장

	}

	// 음료 정보 수정하는 함수
	public void modifyBeverage(int currentPrice, String newName, int newPrice) {
		Beverage beverage = beverages.get(currentPrice); // 현재 가격으로 음료를 찾습니다.

		beverage.setName(newName); // 새로운 이름 설정
		beverage.setPrice(newPrice); // 새로운 가격 설정
		beverages.remove(currentPrice); // 기존 가격의 음료 삭제
		beverages.put(newPrice, beverage); // 새로운 가격으로 음료 추가
		saveBeverages(); // 변경사항 파일에 저장
	}
	
	//화폐 수금 함수
	public String collectMoneyByDenomination(int denomination, int amount) {
		int availableCoins = change.get(denomination);
		int collectableCoins = availableCoins - 5; //최소한 5개의 화폐는 남겨둠
		int coinsToCollect = Math.min(amount / denomination, collectableCoins);
		
		if (collectableCoins <= 0) { //부족한 경우
			return "수금할 수 있는 충분한 " + denomination + "원 단위의 화폐가 없습니다.";
		}
		
		if (coinsToCollect <= 0) {
			return denomination + "원 단위의 화폐는 최소 5개를 남겨둬야 합니다.";
		}

		//반영
		change.put(denomination, availableCoins - coinsToCollect);
		saveChange();
		
		return "성공적으로 " + (coinsToCollect * denomination) + "원을 수금했습니다.";
	}

}
