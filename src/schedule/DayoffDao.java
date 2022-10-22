package schedule;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import vos.DB;
import vos.DayoffApply;
import welcome.Welcome1;

public class DayoffDao {
//1.연차 초기화 (매년 1월1일)+수당자동신청
//2. 연차 신청 하면 연차 까임
//3. 연차 수락 또는 반환

	private Connection con;
	private Statement stmt;
	private Statement stmt2;
	private ResultSet rs;
	Scanner sc = new Scanner(System.in);

	// 연차 초기화
	public void resetDayoff() {

		try {
			con = DB.con();
			con.setAutoCommit(false);
			stmt = con.createStatement();
			stmt2 = con.createStatement();
			String offsql="INSERT INTO extrapay(empno, payno,amount,state) \r\n"
					+ "(SELECT dd.empno,origin.nextval||4, usable * salary/200*1.5\r\n"
					+ "FROM dayoff dd,(SELECT h.EMPNO, salary FROM history h WHERE (empno, MOVEDAY) IN (SELECT empno, max(moveday) FROM HISTORY h GROUP BY empno) ) hi\r\n"
					+ "WHERE dd.EMPNO =hi.empno AND usable>0)";
			System.out.println(stmt2.executeUpdate(offsql)+"건 연차수당 입력");
			
			String sql = "INSERT INTO dayoff(empno,usable) (SELECT empno , \r\n" + "CASE WHEN 몇개월 < 12 THEN 몇개월\r\n"
					+ "	WHEN 추가연차 <11 THEN 추가연차+15\r\n" + "	ELSE 25 END AS \"총연차\"\r\n" + "FROM \r\n"
					+ "(SELECT empno,trunc((sysdate - min(moveday))/30) \"몇개월\", trunc(((sysdate - min(moveday))/365)/3) \"추가연차\"  \r\n"
					+ "	FROM history h GROUP BY empno))";
			System.out.print(stmt.executeUpdate(sql) + "건 입력");
			con.commit();
			System.out.print(" 완료\n");
		} catch (SQLException e) {
			System.out.println("SQL예외: " + e.getMessage());
			System.out.println("연차 초기화에 실패했습니다");
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

	public void useDayoff() { // 연차 신청
		System.out.println("연차 시작일(yyyy-mm-dd)");
		String offuntil = sc.nextLine();
		System.out.println("사용일수");
		String offduring = sc.nextLine();
		try {
			con = DB.con();
			con.setAutoCommit(false);
			stmt = con.createStatement();
			String sql = "UPDATE dayoff SET applyday = to_date('"+ offuntil + "','yyyy-mm-dd') ," 
			+ "	applyduring = " + offduring + ",	usable = usable- "+ offduring + ",	spend = nvl(spend,0)+ " + offduring + "WHERE empno = "+ Welcome1.user.getEmpno();
			System.out.print(stmt.executeUpdate(sql) + "건 입력");
			con.commit();
			System.out.print(" 완료\n");
		} catch (SQLException e) {
			System.out.println("SQL예외: " + e.getMessage());
			System.out.println("연차 신청에 실패했습니다");
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

//담당자가 연차 신청 들어온거 있는지 보기 
	public void newapply() {
		List<DayoffApply> dlist = new ArrayList<DayoffApply>();
		String sql = "SELECT * FROM dayoff WHERE applyday IS NOT NULL";
		try {
			con = DB.con();
			con.setAutoCommit(false);
			stmt = con.createStatement();
			stmt2 = con.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				System.out.print("신청인: " + rs.getInt("empno"));
				System.out.print("신청일자: " + rs.getString("applyday"));
				System.out.print("신청기간: " + rs.getInt("applyduring"));
				System.out.println("승인할까요?(Y/N)");
				String accept = sc.nextLine();
				if (accept.equals("N")) {
					String refusing=
							"UPDATE dayoff SET usable = usable + "+rs.getInt("applyduring")+", spend = spend - "+rs.getInt("applyduring")+" , applyday = NULL, applyduring = null WHERE empno= "+rs.getInt("empno");
					stmt2.executeUpdate(refusing);
				}
				if (accept.equals("Y")) {
					dlist.add(new DayoffApply(rs.getInt("empno"), rs.getString("applyday"), rs.getInt("applyduring")));
					String removerequest="UPDATE dayoff SET applyday = NULL, applyduring = null WHERE empno= " + rs.getInt("empno");
					stmt2.executeUpdate(removerequest);
				}
			}con.commit();
			System.out.print(" 완료\n");
		} catch (SQLException e) {
			System.out.println("SQL예외: " + e.getMessage());
			System.out.println("연차 반환에 실패했습니다");
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
		
		try {
			con = DB.con();
			con.setAutoCommit(false);
			stmt = con.createStatement();
			
			for(DayoffApply dd:dlist) {
				for(int i=0; i<dd.getOffduring();i++) {
					System.out.println(dd.getOffuntil());
				String acceptsql="INSERT INTO schedule(empno,intime,OUTTIME) \r\n"
						+ "	(select "+dd.getEmpno()+", to_date('"+dd.getOffuntil()+" 00:00','yyyy-mm-dd HH24:MI') + "+i
						+ ", to_date('"+dd.getOffuntil()+" 00:00','yyyy-mm-dd HH24:MI')+ "+i+" FROM dual \r\n"
						+ "WHERE NOT EXISTS (SELECT EMPNO  FROM schedule \r\n"
						+ "WHERE trunc(intime,'dd') = to_date('"+dd.getOffuntil()+"','yyyy-mm-dd') +"+i+" AND empno = "+dd.getEmpno()+" ))";
				stmt.executeUpdate(acceptsql);
				}
			}	
			con.commit();
			System.out.print(" 완료\n");
		} catch (SQLException e) {
			System.out.println("SQL예외: " + e.getMessage());
			System.out.println("연차 승인 및 스케줄 추가에 실패했습니다");
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

}
