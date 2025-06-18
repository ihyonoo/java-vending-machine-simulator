package VendingMachine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

public class Admin extends JFrame {
    private VendingMachine vendingMachine;

    public Admin() {
        vendingMachine = new VendingMachine();

        setTitle("Admin Menu");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 1));

        JButton salesReportButton = new JButton("매출 확인");
        JButton currencyStatusButton = new JButton("화폐 현황");
        JButton inventoryReplenishmentButton = new JButton("재고 보충");
        JButton beverageModificationButton = new JButton("음료 정보 수정");

        //매출 확인 리스너
        salesReportButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showSalesReportDialog();
            }
        });
        
        //화폐 현황 리스너
        currencyStatusButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showCurrencyStatus(); // 
            }
        });

        //재고 보충 리스너
        inventoryReplenishmentButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showInventoryReplenishmentDialog();
            }
        });

        //음료 정보 수정 리스너
        beverageModificationButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showBeverageModificationDialog();
            }
        });


        panel.add(salesReportButton);
        panel.add(currencyStatusButton);
        panel.add(inventoryReplenishmentButton);
        panel.add(beverageModificationButton);
        add(panel);
    }

    //매출 현황
    private void showSalesReportDialog() {
        JDialog dialog = new JDialog();
        dialog.setTitle("매출 확인");
        dialog.setSize(300, 150);
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        JButton dailySalesButton = new JButton("일별 매출");
        dailySalesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showDailySales();
            }
        });
        
        JButton monthlySalesButton = new JButton("월별 매출");
        monthlySalesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showMonthlySales();
            }
        });
        
        panel.add(dailySalesButton);
        panel.add(monthlySalesButton);

        dialog.add(panel);
        dialog.setVisible(true);
    }
    

    private void showDailySales() {
        Map<String, Integer> dailySales = VendingMachine.getDailySales();

        JFrame frame = new JFrame("일별 매출");
        frame.setSize(300, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTextArea textArea = new JTextArea(15, 25);
        textArea.setEditable(false); //입력 불가

        //출력
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> entry : dailySales.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("원\n");
        }
        textArea.setText(sb.toString());

        JScrollPane scrollPane = new JScrollPane(textArea);
        frame.add(scrollPane);
        frame.setVisible(true);
    }

    private void showMonthlySales() {
        Map<String, Integer> monthlySales = VendingMachine.getMonthlySales();

        JFrame frame = new JFrame("월별 매출");
        frame.setSize(300, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTextArea textArea = new JTextArea(15, 25);
        textArea.setEditable(false); //입력 불가

        //출력
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> entry : monthlySales.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("원\n");
        }
        textArea.setText(sb.toString());

        JScrollPane scrollPane = new JScrollPane(textArea);
        frame.add(scrollPane);
        frame.setVisible(true);
    }

    //화폐 현황
    private void showCurrencyStatus() {
        Map<Integer, Integer> changeCounts = vendingMachine.getChangeCounts();

        JFrame frame = new JFrame("화폐 현황");
        frame.setSize(300, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JTextArea textArea = new JTextArea(15, 25);
        textArea.setEditable(false); //입력 불가
        
        JButton collectMoneyButton = new JButton("수금"); // 수금 버튼 생성
        collectMoneyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showCollectMoneyDialog(); //수금 다이얼로그 표시
            }
        });

        //출력
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Integer, Integer> entry : changeCounts.entrySet()) {
            sb.append(entry.getKey()).append("원: ").append(entry.getValue()).append("개\n");
        }
        textArea.setText(sb.toString());
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(collectMoneyButton, BorderLayout.SOUTH); // "수금" 버튼을 패널에 추가
        frame.add(panel);
        frame.setVisible(true);
    }

    private void showCollectMoneyDialog() {
    	//콤보박스 생성
        JComboBox<String> denominationComboBox = new JComboBox<>(new String[]{"10", "50", "100", "500", "1000"});
        //수금 금액 입력 필드
        JTextField amountField = new JTextField(10);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        panel.add(new JLabel("화폐 단위:"));
        panel.add(denominationComboBox);
        panel.add(new JLabel("수금할 금액:"));
        panel.add(amountField);

        int result = JOptionPane.showConfirmDialog(null, panel, "수금", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) { //확인을 누르면
        	//입력된 값 가져오기
            int denomination = Integer.parseInt((String) denominationComboBox.getSelectedItem());
            int amount = Integer.parseInt(amountField.getText());

            String message = vendingMachine.collectMoneyByDenomination(denomination, amount);
            JOptionPane.showMessageDialog(null, message);
        }
    }

    //재고 보충
    private void showInventoryReplenishmentDialog() {
        JComboBox<String> comboBox = new JComboBox<>();
        
        //콤보박스에 아이템 추가
        for (Beverage beverage : vendingMachine.getBeverages().values()) {
            comboBox.addItem(beverage.getName());
        }

        //개수 입력 필드
        JTextField quantityField = new JTextField(10);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("음료 종류:"));
        panel.add(comboBox);
        panel.add(new JLabel("추가할 개수:"));
        panel.add(quantityField);

        int result = JOptionPane.showConfirmDialog(null, panel, "재고 보충", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) { //확인 누르면
            String selectedBeverageName = (String) comboBox.getSelectedItem();
            int quantity = Integer.parseInt(quantityField.getText());
            vendingMachine.replenishInventory(selectedBeverageName, quantity);
            JOptionPane.showMessageDialog(null, selectedBeverageName + "의 재고가 성공적으로 보충되었습니다.");
        }
    }

    //음료 정보 수정
    private void showBeverageModificationDialog() {
        JComboBox<String> comboBox = new JComboBox<>();
        
        Map<Integer, Beverage> beverages = vendingMachine.getBeverages();
        //콤보박스에 아이템 추가
        for (Beverage beverage : beverages.values()) {
            comboBox.addItem(beverage.getName());
        }

        JTextField newNameField = new JTextField(10); //수정할 음료 이름 입력 필드
        JTextField newPriceField = new JTextField(10); //수정할 음료 가격 입력 필드

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("수정할 음료 선택:"));
        panel.add(comboBox);
        panel.add(new JLabel("새 음료 이름:"));
        panel.add(newNameField);
        panel.add(new JLabel("새 판매 가격:"));
        panel.add(newPriceField);

        int result = JOptionPane.showConfirmDialog(null, panel, "음료 정보 수정", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) { //확인 누르면
            String selectedBeverageName = (String) comboBox.getSelectedItem();
            
            //modifyBeverage 함수 사용을 위한 로직
            int currentPrice = 0;
            for (Beverage beverage : beverages.values()) { //순환하며 해당 음료 찾기
                if (beverage.getName().equals(selectedBeverageName)) { 
                    currentPrice = beverage.getPrice(); // 해당 음료 가격을 변수에 삽입
                    break;
                }
            }
            
            String newName = newNameField.getText();
            int newPrice = Integer.parseInt(newPriceField.getText());

            //정보 수정
            vendingMachine.modifyBeverage(currentPrice, newName, newPrice);

            JOptionPane.showMessageDialog(null, selectedBeverageName + "의 정보가 성공적으로 수정되었습니다.");
        }
    }
}
