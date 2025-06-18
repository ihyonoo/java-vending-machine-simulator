package VendingMachine;

import java.io.*;
import java.util.*;

public class Beverage {
    private String name;   //음료이름
    private int price;	//음료가격
    private int stock;	//음료재고

    public Beverage(String name, int price, int stock) { //초기화
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    //파일에서 음료의 정보를 읽어와 저장하는 함수
    public static List<Beverage> loadBeveragesFromFile(String filePath) {
        List<Beverage> beverages = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File(filePath))) {
            while (scanner.hasNextLine()) {
                String[] parts = scanner.nextLine().split(","); // ','로 구분되어 있음
                String name = parts[0];
                int price = Integer.parseInt(parts[1]); //정수형으로 변환하여 저장
                int stock = Integer.parseInt(parts[2]); //정수형으로 변환하여 저장
                beverages.add(new Beverage(name, price, stock)); //삽입
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return beverages;
    }

    public String getName() { //이름 반환
        return name;
    }

    public int getPrice() { //가격 반환
        return price;
    }

    public int getStock() { //재고 반환
        return stock;
    }

    public void dispense() { //재고를 -1하는 함수 (음료가 팔렸을 때)
        stock--;
    }

    public boolean isSoldOut() {  //재고가 없으면 fauls, 아니면 true
        return stock <= 0;
    }
    
    public void replenishStock(int quantity) {  //인자만큼 재고를 추가
        stock += quantity;
    }
    
    public void setName(String name) {  //이름 변경
        this.name = name;
    }

    public void setPrice(int price) {  //가격 변경
        this.price = price;
    }
}
