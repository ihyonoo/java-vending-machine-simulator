package VendingMachine;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class LoginGUI extends JFrame {
	private JLabel resultLabel;
	private String correctPassword; // 기존 비밀번호 저장 변수
	// 비밀번호 파일
	private static final String PASSWORD_FILE_PATH = "password.txt";

	public LoginGUI() {
		setTitle("Login");
		setSize(300, 150);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		correctPassword = readPasswordFromFile(); // 파일에서 기존 비밀번호 읽어서 저장

		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());

		JLabel passwordText = new JLabel("Password : ");
		
		JPasswordField passwordField = new JPasswordField(10);
		
		JButton checkButton = new JButton("확인");
		CheckButtonListener checkButtonListener = new CheckButtonListener(passwordField);
		checkButton.addActionListener(checkButtonListener);

		JButton changeButton = new JButton("비밀번호 변경");
		ChangeButtonListener changeButtonListener = new ChangeButtonListener();
		changeButton.addActionListener(changeButtonListener);

		// 안내 텍스트 출력 창
		resultLabel = new JLabel("");

		// 텍스트 필드에서 엔터 키를 눌렀을 때 확인 버튼이 클릭된 것과 동일한 동작 수행
		passwordField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					checkButton.doClick();
				}
			}
		});

		panel.add(passwordText);
		panel.add(passwordField);
		panel.add(checkButton);
		panel.add(resultLabel);
		panel.add(changeButton);
		add(panel);
	}

	//확인 버튼 리스너 (비밀번호 검증)
	private class CheckButtonListener implements ActionListener {
	    private JPasswordField passwordField;

	    public CheckButtonListener(JPasswordField passwordField) {
	        this.passwordField = passwordField;
	    }
		@Override
		public void actionPerformed(ActionEvent e) {
			// 입력된 비밀번호 가져오기
			String inputPassword = new String(passwordField.getPassword());

			// 비밀번호 검증
			if (inputPassword.equals(correctPassword)) { //비밀번호가 맞으면
				Admin AdminGUI = new Admin(); //관리자메뉴창 실행
				AdminGUI.setVisible(true);
				dispose(); //현재 창 닫기
			} else {
				resultLabel.setText("비밀번호가 틀렸습니다.");
			}
		}
	}

	// 비밀번호 변경 버튼 리스너
	private class ChangeButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			//새 비밀번호 입력 다이얼로그 띄우기
			String newPassword = JOptionPane.showInputDialog(null, "새로운 비밀번호를 입력하세요:");
			
			if (isValidPassword(newPassword)) {//비밀번호 조건 검사
				//비밀번호 변경 후 파일에 저장
				writePasswordToFile(newPassword);
				correctPassword = newPassword; // 비밀번호 변경
				resultLabel.setText("비밀번호가 변경되었습니다.");
			} else {
				resultLabel.setText("비밀번호는 8자 이상이어야 하며, 숫자와 특수문자가 각각 하나 이상 포함되어야 합니다.");
			}
		}
	}
	

	//파일에서 패스워드 읽어오기
	private String readPasswordFromFile() {
		String password = "";
		try {
			BufferedReader reader = new BufferedReader(new FileReader(PASSWORD_FILE_PATH));
			password = reader.readLine();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return password;
	}

	//비밀번호 유효성 검사
	private boolean isValidPassword(String password) { //8자리 이상, 영어포함, 특수문자포함
		return password.length() >= 8 && password.matches(".*\\d.*") && password.matches(".*[!@#$%^&*()\\-+].*");
	} 

	//파일에 변경된 패스워드 저장 함수
	private void writePasswordToFile(String password) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(PASSWORD_FILE_PATH));
			writer.write(password);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
