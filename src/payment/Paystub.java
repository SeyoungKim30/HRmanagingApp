package payment;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import vos.DB;
import vos.Pay;
import welcome.Welcome1;

public class Paystub {
	private Connection con;
	private Statement stmt;
	private ResultSet rs;
	
	Scanner sc = new Scanner(System.in);

	public void myMonthtotal() {		//내소득 조회
		List<Pay> plist = new ArrayList<Pay>();
		try {
			con=DB.con();
			stmt=con.createStatement();
			String sqlsal="SELECT to_char(payday,'YYYY') ,to_char(payday,'mm'),salary FROM pay WHERE empno =" + Welcome1.user.getEmpno();
			String sqlext="SELECT to_char(payday,'YYYY'),to_char(payday,'mm'),amount FROM extrapay WHERE empno ="+Welcome1.user.getEmpno()+" AND payday IS NOT NULL";
	rs=stmt.executeQuery(sqlsal);
	while(rs.next()) {
		int empno = Welcome1.user.getEmpno();
		String month = rs.getString(2);
		String year = rs.getString(1);
		int amount  = rs.getInt(3);
		String cate = "급여";
		plist.add(new Pay(empno,month,year,amount,cate));
	}
	rs=stmt.executeQuery(sqlext);
	while(rs.next()) {
		int empno = Welcome1.user.getEmpno();
		String month = rs.getString(2);
		String year = rs.getString(1);
		int amount  = rs.getInt(3);
		String cate = "수당";
		plist.add(new Pay(empno,month,year,amount,cate));
	}			
    } catch (SQLException e) {
		System.out.println("SQL예외: "+e.getMessage());
		}catch(Exception e) {
			System.out.println("일반예외:"+e.getMessage());
		}
		finally {
			DB.close(rs, stmt, con);
		}

		System.out.println("━━━━━━━━━━━━ 최근 소득 ━━━━━━━━━━━━");
		int totalpay=0;
		for(Pay pp:plist) {
			System.out.println(pp.getYear()+"년 "+pp.getMonth()+"월 " + pp.getCate() +" : " +pp.getAmount());
		totalpay+=pp.getAmount();
		}
		System.out.println("━━━━━━━━총 소득: "+totalpay+" ━━━━━━━━");
		plist=null;
	}
	public void empMonthtotal() {		//남의 소득 조회
		List<Pay> plist = new ArrayList<Pay>();
		System.out.println("조회할 사원 번호 입력:");
		int empno = sc.nextInt();
		try {
			con=DB.con();
			stmt=con.createStatement();
			String sqlsal="SELECT to_char(payday,'YYYY') ,to_char(payday,'mm'),salary FROM pay WHERE empno =" + empno;
			String sqlext="SELECT to_char(payday,'YYYY'),to_char(payday,'mm'),amount FROM extrapay WHERE empno ="+empno+" AND payday IS NOT NULL";
	rs=stmt.executeQuery(sqlsal);
	while(rs.next()) {
		String month = rs.getString(2);
		String year = rs.getString(1);
		int amount  = rs.getInt(3);
		String cate = "급여";
		plist.add(new Pay(empno,month,year,amount,cate));
	}
	rs=stmt.executeQuery(sqlext);
	while(rs.next()) {
		String month = rs.getString(2);
		String year = rs.getString(1);
		int amount  = rs.getInt(3);
		String cate = "수당";
		plist.add(new Pay(empno,month,year,amount,cate));
	}			
    } catch (SQLException e) {
		System.out.println("SQL예외: "+e.getMessage());
		}catch(Exception e) {
			System.out.println("일반예외:"+e.getMessage());
		}
		finally {
			DB.close(rs, stmt, con);
		}

		System.out.println("━━━━━━━━━━━━ 최근 소득 ━━━━━━━━━━━━");
		int totalpay=0;
		for(Pay pp:plist) {
			System.out.println(pp.getYear()+"년 "+pp.getMonth()+"월 " + pp.getCate() +" : " +pp.getAmount());
		totalpay+=pp.getAmount();
		}
		System.out.println("━━━━━━━━총 소득: "+totalpay+" ━━━━━━━━");
		plist=null;
	}

	
	
}
/*
 * 
 * SELECT empno,to_char(payday,'YYYY'),to_char(payday,'mm'),salary FROM pay WHERE empno =2022101001 ;
SELECT empno,to_char(payday,'YYYY'),to_char(payday,'mm'),amount FROM extrapay WHERE empno =2022101001 AND payday IS NOT NULL;
 * */
