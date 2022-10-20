package payment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import vos.DB;
import welcome.Welcome1;

public class ExtraDao {
	private Connection con;
	private Statement stmt;
	private ResultSet rs;
	
	Scanner sc = new Scanner(System.in);

	
	public void insertExtra() { // 수당신청
		System.out.println("신청할 수당의 번호를 입력하세요");
		System.out.println("1.야간수당 2.휴일수당 3.시간외근무수당 4.연차수당");
		String payno = sc.nextLine();
		System.out.println("신청할 금액을 입력하세요");
		String amount = sc.nextLine();
		String sql = "INSERT INTO extrapay (empno,payno,amount,state) VALUES ("+Welcome1.user.getEmpno()+",origin.nextval||"+payno+","+amount+",'신청')";
		try {
			con = DB.con();
			con.setAutoCommit(false);
			stmt = con.createStatement();
			int cnt = stmt.executeUpdate(sql);
			if (cnt == 1)
				System.out.println("신청이 저장되었습니다");
			con.commit();
		} catch (SQLException e) {
			System.out.println("SQL예외: " + e.getMessage());
			try {
				con.rollback();
			} catch (SQLException e1) {
				System.out.println("롤백예외:" + e1.getMessage());
			}
		} finally {
			DB.close(rs, stmt, con);
		}
	}

	public void insertExtraAdmin() { // 관리자가 입력해주기
		System.out.println("수당 대상자의 사원번호를 입력하세요");
		String empno = sc.nextLine();
		System.out.println("신청할 수당의 번호를 입력하세요");
		System.out.println("1.야간수당 2.휴일수당 3.시간외근무수당 4.연차수당");
		String payno = sc.nextLine();
		System.out.println("신청할 금액을 입력하세요");
		String amount = sc.nextLine();
		System.out.println("현재 상태를 입력하세요");
		String state = sc.nextLine();
		String sql = "INSERT INTO extrapay (empno,payno,amount,state) VALUES ("+empno+",origin.nextval||"+payno+","+amount+",'"+state+"')";
		try {
			con = DB.con();
			con.setAutoCommit(false);
			stmt = con.createStatement();
			int cnt = stmt.executeUpdate(sql);
			if (cnt == 1)
				System.out.println("신청이 저장되었습니다");
			con.commit();
		} catch (SQLException e) {
			System.out.println("SQL예외: " + e.getMessage());
			try {
				con.rollback();
			} catch (SQLException e1) {
				System.out.println("롤백예외:" + e1.getMessage());
			}
		} finally {
			DB.close(rs, stmt, con);
		}
	}

	public void stateConfirm() {
		System.out.println("수당 대상자의 사원번호를 입력하세요");
		String empno = sc.nextLine();
		System.out.println("변경할 수당번호를 입력하세요");
		String payno = sc.nextLine();
		System.out.println("금액을 입력하세요");
		String amount = sc.nextLine();
		String sql = "UPDATE extrapay SET STATE = '승인' WHERE EMPNO ='"+empno+"' AND state ='승인' And payno = "+payno+" AND amount = "+amount;
		try {
			con = DB.con();
			con.setAutoCommit(false);
			stmt = con.createStatement();
			int cnt = stmt.executeUpdate(sql);
			if (cnt == 1)
				System.out.println("변경이 저장되었습니다");
			con.commit();
		} catch (SQLException e) {
			System.out.println("SQL예외: " + e.getMessage());
			try {
				con.rollback();
			} catch (SQLException e1) {
				System.out.println("롤백예외:" + e1.getMessage());
			}
		} finally {
			DB.close(rs, stmt, con);
		}
	
	}
	public void statePaid() {
		System.out.println("수당 대상자의 사원번호를 입력하세요");
		String empno = sc.nextLine();
		System.out.println("변경할 수당의 번호를 입력하세요");
		System.out.println("1.야간수당 2.휴일수당 3.시간외근무수당 4.연차수당");
		String payno = sc.nextLine();
		System.out.println("금액을 입력하세요");
		String amount = sc.nextLine();
		String sql = "UPDATE extrapay SET STATE = '승인', payday=sysdate WHERE EMPNO ='"+empno+"' AND state ='승인' And payno = "+payno+" AND amount = "+amount;
		try {
			con = DB.con();
			con.setAutoCommit(false);
			stmt = con.createStatement();
			int cnt = stmt.executeUpdate(sql);
			if (cnt == 1)
				System.out.println("변경이 저장되었습니다");
			con.commit();
		} catch (SQLException e) {
			System.out.println("SQL예외: " + e.getMessage());
			try {
				con.rollback();
			} catch (SQLException e1) {
				System.out.println("롤백예외:" + e1.getMessage());
			}
		} finally {
			DB.close(rs, stmt, con);
		}
	
	}
	
}
