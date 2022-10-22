package schedule;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import vos.DB;

public class SCDdao {
	private Connection con;
	private Statement stmt;
	private ResultSet rs;
	private ResultSet rs2;
	Scanner sc = new Scanner(System.in);

	public void setScdbyDept() { // 출근스케줄 입력 (부서별)
		System.out.println("근무일정을 입력할 부서 :");
		String deptno = sc.nextLine();
		System.out.println("근무일(yyyy-mm-dd): ");
		String datt = sc.nextLine();
		System.out.println("업무 시작 시각(00:00): ");
		String timee = sc.nextLine();
		System.out.println("근무시간(시간단위로 입력하세요): ");
		String hourlong = sc.nextLine();

		try {
			con = DB.con();
			con.setAutoCommit(false);
			stmt = con.createStatement();
			String sql = "INSERT INTO schedule (\r\n" + "SELECT e.empno , to_date('" + datt + " " + timee
					+ "','yyyy-mm-dd HH24:MI'),\r\n" + "		to_date('" + datt + " " + timee
					+ "','yyyy-mm-dd HH24:MI')+(" + hourlong + "/24)\r\n" + "FROM employee e \r\n"
					+ "WHERE e.empno !=(SELECT r.EMPNO FROM RETIREMENT r WHERE state LIKE '퇴사') AND deptno =" + deptno
					+ " )";
			System.out.print(stmt.executeUpdate(sql) + "건 입력");
			con.commit();
			System.out.print(" 완료\n");
		} catch (SQLException e) {
			System.out.println("SQL예외: " + e.getMessage());
			System.out.println("입력 실패 : 형식을 지켜 입력했는지 확인하세요");
			try {
				con.rollback();
			} catch (SQLException e1) {
				System.out.println("롤백예외:" + e1.getMessage());
			}
		} catch (Exception e) {
			System.out.println("일반예외:" + e.getMessage());
		} finally {
			DB.close(rs, stmt, con);
		}
	}

	public void setScdbyEmp() { // 출근스케줄 입력 (개인)
		System.out.println("근무일정을 입력할 사원 :");
		String empno = sc.nextLine();
		System.out.println("근무일(yyyy-mm-dd): ");
		String datt = sc.nextLine();
		System.out.println("업무 시작 시각(00:00): ");
		String timee = sc.nextLine();
		System.out.println("근무시간(시간단위로 입력하세요): ");
		String hourlong = sc.nextLine();

		try {
			con = DB.con();
			con.setAutoCommit(false);
			stmt = con.createStatement();
			String sql = "INSERT INTO schedule VALUES \r\n" + "(" + empno + ",to_date('" + datt + " " + timee
					+ "','yyyy-mm-dd HH24:MI'),\r\n" + "to_date('" + datt + " " + timee + "','yyyy-mm-dd HH24:MI')+("
					+ hourlong + "/24))";
			System.out.print(stmt.executeUpdate(sql) + "건 입력");
			con.commit();
			System.out.print(" 완료\n");
		} catch (SQLException e) {
			System.out.println("SQL예외: " + e.getMessage());
			System.out.println("입력 실패 : 형식을 지켜 입력했는지 확인하세요");
			try {
				con.rollback();
			} catch (SQLException e1) {
				System.out.println("롤백예외:" + e1.getMessage());
			}
		} catch (Exception e) {
			System.out.println("일반예외:" + e.getMessage());
		} finally {
			DB.close(rs, stmt, con);
		}
	}

	public void showScdEmpMonth() { // 개인별, 월별
		System.out.println("조회할 사원 번호 입력:");
		String empno = sc.nextLine();
		System.out.println("조회 연월(yyyy-mm): ");
		String yymm = sc.nextLine();
		String sql = "SELECT to_char(intime,'yyyy-mm-dd') 출근일 ,to_char(intime,'HH24:mi') 출근시간 ,to_char(OUTTIME,'HH24:mi') 퇴근시간 FROM SCHEDULE s WHERE EMPNO ="
				+ empno + " and trunc(intime,'mm') IN to_date('" + yymm + "','yyyy-mm')";
		try {
			con = DB.con();
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			System.out.println("출근일\t\t출근시간\t퇴근시간");
			while (rs.next()) {
				System.out.print(rs.getString("출근일") + "\t");
				System.out.print(rs.getString("출근시간") + "\t");
				System.out.print(rs.getString("퇴근시간") + "\n");
			}
		} catch (SQLException e) {
			System.out.println("SQL예외: " + e.getMessage());
		} catch (Exception e) {
			System.out.println("일반예외:" + e.getMessage());
		} finally {
			DB.close(rs, stmt, con);
		}
	}

	public void showScdDeptMonth() { // 부서별, 월별
		System.out.println("조회할 부서 번호 입력:");
		String deptno = sc.nextLine();
		System.out.println("조회 연월(yyyy-mm): ");
		String yymm = sc.nextLine();
		List<String> slist = new ArrayList<String>();

		String gotdate = "SELECT DISTINCT to_char(intime,'yyyy-mm-dd') FROM SCHEDULE s WHERE TO_CHAR(intime,'yyyy-mm') LIKE '"
				+ yymm + "' ";
		try {
			con = DB.con();
			stmt = con.createStatement();
			rs = stmt.executeQuery(gotdate);
			while (rs.next()) {		//근무자 있는 날짜를 구해서 리스트로 저장
				slist.add(rs.getString(1));
			}
			Collections.sort(slist,String.CASE_INSENSITIVE_ORDER);
			for (String days : slist) {	//날짜 대입해서 다시 돌림
				System.out.println("━━━━"+days+"근무자━━━━");
				String gotemp = "SELECT s.empno , name FROM employee e,SCHEDULE s WHERE e.EMPNO =s.EMPNO and DEPTNO LIKE "
						+ deptno + " AND trunc(intime,'dd') IN to_date('"+days+"','yyyy-mm-dd')";

				rs2 = stmt.executeQuery(gotemp);
				while (rs2.next()) {
					System.out.println(rs2.getString("empno") + ": " + rs2.getString("name"));
				}
			}

		} catch (SQLException e) {
			System.out.println("SQL예외: " + e.getMessage());
		} catch (Exception e) {
			System.out.println("일반예외:" + e.getMessage());
		} finally {
			DB.close(rs, stmt, con);
		}

	}
	
	public void showScdDeptDay() {	//부서별, 특정일
		System.out.println("조회할 부서 번호 입력:");
		String deptno = sc.nextLine();
		System.out.println("조회 날짜(yyyy-mm-dd): ");
		String yymmdd = sc.nextLine();
		String sql = "SELECT name, to_char(intime,'HH24:mi') 출근시간 ,to_char(OUTTIME,'HH24:mi') 퇴근시간 \r\n"
				+ "FROM SCHEDULE s, EMPLOYEE e  WHERE s.EMPNO =e.EMPNO AND e.DEPTNO like "+deptno 
				+ "and trunc(intime,'dd') IN to_date('"+yymmdd+"','yyyy-mm-dd')";
		try {
			con = DB.con();
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			System.out.println("┏━━━━━━━━━━━━━━━━━━━━━━━━━┓");
			System.out.println(" 근무자\t출근시간\t퇴근시간");
			while (rs.next()) {
				System.out.print(" "+rs.getString("name") + "\t");
				System.out.print(" "+rs.getString("출근시간") + "\t");
				System.out.print(" "+rs.getString("퇴근시간") + "\n");
			}
			System.out.println("┗━━━━━━━━━━━━━━━━━━━━━━━━━┛");
		} catch (SQLException e) {
			System.out.println("SQL예외: " + e.getMessage());
		} catch (Exception e) {
			System.out.println("일반예외:" + e.getMessage());
		} finally {
			DB.close(rs, stmt, con);
		}
	}
	
	public void showScdDay() {	//전체직원, 특정일
		System.out.println("조회 날짜(yyyy-mm-dd): ");
		String yymmdd = sc.nextLine();
		String sql = "SELECT name, to_char(intime,'HH24:mi') 출근시간 ,to_char(OUTTIME,'HH24:mi') 퇴근시간 \r\n"
				+ "FROM SCHEDULE s, EMPLOYEE e  WHERE s.EMPNO =e.EMPNO "
				+ "and trunc(intime,'dd') IN to_date('"+yymmdd+"','yyyy-mm-dd')";
		try {
			con = DB.con();
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			System.out.println("┏━━━━━━━━━━━━━━━━━━━━━━━━━┓");
			System.out.println(" 근무자\t출근시간\t퇴근시간");
			System.out.println(" ━━━━━━━━━━━━━━━━━━━━━━━━━");
			while (rs.next()) {
				System.out.print(" "+rs.getString("name") + "\t");
				System.out.print(" "+rs.getString("출근시간") + "\t");
				System.out.print(" "+rs.getString("퇴근시간") + "\n");
			}
			System.out.println("┗━━━━━━━━━━━━━━━━━━━━━━━━━┛");
		} catch (SQLException e) {
			System.out.println("SQL예외: " + e.getMessage());
		} catch (Exception e) {
			System.out.println("일반예외:" + e.getMessage());
		} finally {
			DB.close(rs, stmt, con);
		}
	}
	
	public void showOvertime() {
		System.out.println("조회 달(yyyy-mm): ");
		String yymm = sc.nextLine();
		System.out.println("조회할 사원 번호");
		String empno = sc.nextLine();
		String sql = "SELECT DISTINCT w.empno, timeon 실제출근, intime 출근일정 , timeoff 실제퇴근, outtime 퇴근일정, \r\n"
				+ "	(intime - timeon)*24 일찍온시간 ,(timeoff-outtime)*24 초과시간, "
				+ "round(nvl(nvl((intime - timeon)*24  + (timeoff-outtime)*24 , (outtime-intime)*24),(timeoff-timeon)*24)) 총초과\r\n"
				+ "FROM WORKTABLE w FULL OUTER JOIN SCHEDULE s ON Trunc(w.TIMEON,'dd') =TRUNC(s.INTIME,'dd')\r\n"
				+ "WHERE w.empno like '%'||"+empno+"||'%' \r\n"
				+ "AND TRUNC(s.INTIME,'mm') like to_date('"+yymm+"','yyyy-mm') ORDER BY timeon";
		try {
			con = DB.con();
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			System.out.println("┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓");
			System.out.println(" 출근일정\t━━━━━━━━━━━━\t실제출근\t━━━━━━━━━━━━\t퇴근일정\t━━━━━━━━━━━━\t실제퇴근\t━━━━━━━━━━━━\t초과근무");
			System.out.println(" ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
			while (rs.next()) {
				System.out.print(" "+rs.getString("출근일정") + "\t");
				System.out.print(" "+rs.getString("실제출근") + "\t");
				System.out.print(" "+rs.getString("퇴근일정") + "\t");
				System.out.print(" "+rs.getString("실제퇴근") + "\t");
				System.out.print(" "+rs.getString("총초과") + "시간 \n");
			}
			System.out.println(" ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
	
		String totalover="SELECT sum(총초과) FROM ( SELECT DISTINCT timeon,intime, \r\n"
				+ "round(nvl(nvl((intime - timeon)*24  + (timeoff-outtime)*24 , (outtime-intime)*24),(timeoff-timeon)*24)) 총초과\r\n"
				+ "FROM WORKTABLE w FULL OUTER JOIN SCHEDULE s ON TRUNC(s.INTIME,'dd') =Trunc(w.TIMEON,'dd') \r\n"
				+ "WHERE w.empno like '%'||"+empno+"||'%' AND TRUNC(s.INTIME,'mm') like to_date('"+yymm+"','yyyy-mm'))";
		
		rs2=stmt.executeQuery(totalover);
		if(rs2.next()) {
			System.out.println(yymm+"의 초과근무시간은 "+rs2.getString(1)+" 시간 입니다");
			System.out.println("┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛");
		}
		} catch (SQLException e) {
			System.out.println("SQL예외: " + e.getMessage());
		} catch (Exception e) {
			System.out.println("일반예외:" + e.getMessage());
		} finally {
			DB.close(rs, stmt, con);
		}
		
		
	}
	/*
	 * --출근 스케줄 보기 : 전체/연월 입력 SELECT * FROM SCHEDULE s WHERE trunc(intime,'mm') IN
	 * to_date('2022-09','yyyy-mm'); --출근 스케줄 보기 : 전체/특정날짜입력 SELECT * FROM SCHEDULE
	 * s WHERE trunc(intime,'dd') IN to_date('2022-10-22','yyyy-mm-dd');
	 */

}
