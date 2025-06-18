//main
package VendingMachine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class VendingMachineGUI extends JFrame {
    private VendingMachine vendingMachine;
    private JTextArea displayArea;
    private JTextArea displayArray;
    private JTextField moneyInputField;
    private JPanel beveragePanel;

    public VendingMachineGUI() {
        vendingMachine = new VendingMachine();

        setTitle("Vending Machine");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(2, 2));

        inputPanel.add(new JLabel("투입 금액:"));
        moneyInputField = new JTextField();
        inputPanel.add(moneyInputField);

        JButton insertMoneyButton = new JButton("투입");
        insertMoneyButton.addActionListener(new InsertMoneyListener());
        inputPanel.add(insertMoneyButton);

        JButton returnMoneyButton = new JButton("반환");
        returnMoneyButton.addActionListener(new ReturnMoneyListener());
        inputPanel.add(returnMoneyButton);
        add(inputPanel, BorderLayout.NORTH);

        //사용자에게 안내사항을 출력하는 창
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        JScrollPane scrollPane1 = new JScrollPane(displayArea);
        scrollPane1.setPreferredSize(new Dimension(330, 400));
        add(scrollPane1, BorderLayout.WEST);

        //거스름돈 개수 출력 창
        displayArray = new JTextArea();
        displayArray.setEditable(false);
        JScrollPane scrollPane2 = new JScrollPane(displayArray);
        scrollPane2.setPreferredSize(new Dimension(100, 200));
        add(scrollPane2, BorderLayout.CENTER);
        displayRemainingChangeCount(); //거스름돈 현황 표시
        displayTotalMoney(); // 총 투입 금액 표시
        
      //관리자 메뉴창
        JButton adminMenuButton = new JButton("관리자 메뉴");
        adminMenuButton.addActionListener(new AdminMenuListener());
        add(adminMenuButton, BorderLayout.EAST);

        //음료 선택창
        beveragePanel = new JPanel();
        beveragePanel.setLayout(new GridLayout(2, 3));
        addBeverageButtons();
        add(beveragePanel, BorderLayout.SOUTH);        
    }

    //음료선택창
    private void addBeverageButtons() {
        Map<Integer, Beverage> beverages = vendingMachine.getBeverages();

        for (Integer price : beverages.keySet()) {
            Beverage beverage = beverages.get(price);

            JButton beverageButton = new JButton(beverage.getName() + " (" + price + "원)");
            beverageButton.addActionListener(new SelectBeverageListener());

            JLabel stockLabel = new JLabel("재고: " + beverage.getStock());

            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new BorderLayout());
            buttonPanel.add(beverageButton, BorderLayout.CENTER);
            buttonPanel.add(stockLabel, BorderLayout.SOUTH);
            beveragePanel.add(buttonPanel);
        }
    }

    //거스름돈 출력 함수
    private void displayRemainingChangeCount() {
        Map<Integer, Integer> changeCounts = vendingMachine.getChangeCounts();
        
        displayArray.append("현재 남아있는 거스름돈 개수:\n");
        
        for (Map.Entry<Integer, Integer> entry : changeCounts.entrySet()) {
            int denomination = entry.getKey();
            int count = entry.getValue();
            displayArray.append(denomination + "원 짜리: " + count + "개\n");
        }
    }

    //총 투입 금액 출력 함수
    private void displayTotalMoney() {
        int totalMoney = vendingMachine.getTotalMoney();
        displayArray.append("총 투입 금액: " + totalMoney + "원\n");
    }

    //투입 리스너
    private class InsertMoneyListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                int inputMoney = Integer.parseInt(moneyInputField.getText()); //정수형 변환
                String a = vendingMachine.insertMoney(inputMoney);
                
                moneyInputField.setText("");
                displayArea.append(a + "\n");
                displayArray.setText(""); // 투입 금액 변경 시 기존 내용을 지우고 다시 표시
                
                displayTotalMoney(); // 투입 금액 업데이트
                displayRemainingChangeCount(); // 거스름돈 개수 업데이트
            } catch (NumberFormatException ex) {
                displayArea.append("잘못된 입력입니다. 금액을 입력하세요.\n");
            }
        }
    }
    
    //반환 버튼 리스너
    private class ReturnMoneyListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Map<Integer, Integer> returnedChange = vendingMachine.returnMoney();
            
            displayArea.append("반환된 거스름돈: " + returnedChange + "\n");
            displayArray.setText(""); // 반환 시 기존 내용을 지우고 다시 표시
            
            displayTotalMoney(); // 투입 금액 업데이트
            displayRemainingChangeCount(); // 거스름돈 개수 업데이트
        }
    }
    
    //관리자메뉴 버튼 리스너
    private class AdminMenuListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
        	//로그인 창 실행
            LoginGUI login = new LoginGUI();
            login.setVisible(true);
            dispose(); //현재 창 닫기
        }
    }
    
    //음료를 선택했을 경우 리스너
    private class SelectBeverageListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
        	//해당 음료 가격 정보를 얻기 위한 코드
            JButton button = (JButton) e.getSource();
            String buttonText = button.getText();
            String priceString = buttonText.substring(buttonText.indexOf("(") + 1, buttonText.indexOf("원")).trim();
            int price = Integer.parseInt(priceString);
            
            //결과 출력
            String result = vendingMachine.selectBeverage(price);
            displayArea.append(result + "\n");

            updateStockDisplay(); //잔량 업데이트
        }
        
        private void updateStockDisplay() {
            Map<Integer, Beverage> beverages = vendingMachine.getBeverages();
            
            for (Integer price : beverages.keySet()) {
                Beverage beverage = beverages.get(price);
                for (Component component : beveragePanel.getComponents()) {
                    if (component instanceof JPanel) { //component가 JPnel이라면
                        JPanel buttonPanel = (JPanel) component;
                        JButton beverageButton = (JButton) buttonPanel.getComponent(0);
                        JLabel stockLabel = (JLabel) buttonPanel.getComponent(1);
                        if (beverageButton.getText().contains(beverage.getName())) {
                            stockLabel.setText("재고: " + beverage.getStock());
                        }
                    }
                }
            }
        }
    }

    //main
    public static void main(String[] args) {
        new VendingMachineGUI().setVisible(true); 
    }
}
