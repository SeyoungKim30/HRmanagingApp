package payment;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import vos.DB;

public class Saldao {
	private Connection con;
	private Statement stmt;
	private ResultSet rs;
	Scanner sc = new Scanner(System.in);
	
	Date today = new Date();
	SimpleDateFormat formathy = new SimpleDateFormat("yyyy-MM-dd");
	String todaystring = formathy.format(today);

	
	
	public void insertPayAll() {		//돈주고 보냈다고 입력
		//사원별, 부서별 ,전체
		String sql="INSERT INTO PAY (empno,payday,salary) (\r\n"
				+ "SELECT h.EMPNO,to_date('"+todaystring+"','yyyy-mm-dd'),salary \r\n"
				+ "FROM history h WHERE h.empno !=(SELECT r.EMPNO FROM RETIREMENT r WHERE state LIKE '퇴사') \r\n"
				+ "		AND (empno, MOVEDAY) \r\n"
				+ "	IN (SELECT empno, max(moveday) FROM HISTORY h GROUP BY empno))";
	try {
		con = DB.con();
		con.setAutoCommit(false);
		stmt = con.createStatement();
		System.out.println(todaystring+" : "+stmt.executeUpdate(sql)+"건의 급여내역을 저장했습니다");
		con.commit();
		System.out.println("수정종료");

	} catch (SQLException e) {
		System.out.println(e.getMessage());
		try {
			con.rollback();
		} catch (SQLException e1) {
			System.out.println("롤백예외: " + e1.getMessage());
		}
	} finally {
		DB.close(rs, stmt, con);
	}
	
	}
	public void insertPayDept() {		//돈주고 보냈다고 입력
		System.out.println("부서번호를 입력하세요");
		String deptno=sc.nextLine();
		String sql="INSERT INTO PAY (empno,payday,salary) (\r\n"
				+ "SELECT h.EMPNO,to_date('"+todaystring+"','yyyy-mm-dd'),salary \r\n"
				+ "FROM history h WHERE h.empno !=(SELECT r.EMPNO FROM RETIREMENT r WHERE state LIKE '퇴사') \r\n"
				+ "		AND (empno, MOVEDAY) \r\n"
				+ "	IN (SELECT empno, max(moveday) FROM HISTORY h GROUP BY empno)AND deptno="+deptno+")";
	try {
		con = DB.con();
		con.setAutoCommit(false);
		stmt = con.createStatement();
		System.out.println(todaystring+" : "+stmt.executeUpdate(sql)+"건의 급여내역을 저장했습니다");
		con.commit();
		System.out.println("수정종료");

	} catch (SQLException e) {
		System.out.println(e.getMessage());
		try {
			con.rollback();
		} catch (SQLException e1) {
			System.out.println("롤백예외: " + e1.getMessage());
		}
	} finally {
		DB.close(rs, stmt, con);
	}
	
	}
	public void insertManual() {		//돈주고 보냈다고 입력
		System.out.println("사원번호를 입력하세요");
		String empno=sc.nextLine();
		System.out.println("지급날짜를 입력하세요(yyyy-mm-dd)");
		String payday=sc.nextLine();
		System.out.println("금액을 입력하세요");
		String salary=sc.nextLine();
		System.out.println("계좌번호를 입력하세요");
		String acnum=sc.nextLine();
		String sql="INSERT INTO pay VALUES ("+empno+",to_date('"+payday+"','yyyy-mm-dd'),"+salary+","+acnum+")";
	try {
		con = DB.con();
		con.setAutoCommit(false);
		stmt = con.createStatement();
		System.out.println(todaystring+" : "+stmt.executeUpdate(sql)+"건의 급여내역을 저장했습니다");
		con.commit();
		System.out.println("수정종료");

	} catch (SQLException e) {
		System.out.println(e.getMessage());
		try {
			con.rollback();
		} catch (SQLException e1) {
			System.out.println("롤백예외: " + e1.getMessage());
		}
	} finally {
		DB.close(rs, stmt, con);
	}
	
	}
	//명세서 조회
	//월별로 명세서
	//수당 주세요 insert
	//수당 주고나서 update
	//정보 보기 : 지금까지 받은거, 올해 받은거, 급여만, 수당
	
}
