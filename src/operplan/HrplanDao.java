package operplan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import vos.BudgetVo;
import vos.DB;
import vos.HrplanVo;

public class HrplanDao {
	private Connection con;
	private Statement stmt;
	private PreparedStatement pstmt;
	private ResultSet rs;

	Scanner sc = new Scanner(System.in);

	public void inserthrplan() {
		System.out.println("계획을 입력할 연도를 입력하세요");
		String year = sc.nextLine();
		System.out.println("부서번호를 입력하세요");
		String deptno = sc.nextLine();
		System.out.println("직급을 입력하세요");
		String rank = sc.nextLine();
		System.out.println("인원을 입력하세요");
		String cnt = sc.nextLine();
		String sql = "INSERT INTO hrplan(YEAR,deptno,ranker,cnt) (SELECT to_date('" + year + "','yyyy')," + deptno
				+ ",'" + rank + "', " + cnt + "	FROM dual WHERE NOT EXISTS (SELECT 1 FROM hrplan WHERE deptno= "
				+ deptno + " AND ranker = '" + rank + "' AND TO_CHAR(YEAR,'yyyy')=" + year + " ))";
		try {
			con = DB.con();
			con.setAutoCommit(false);
			stmt = con.createStatement();
			System.out.println(stmt.executeUpdate(sql) + "건 ");
			con.commit();
			System.out.println("저장되었습니다");
		} catch (SQLException e) {
			System.out.println("SQL예외: " + e.getMessage());
			System.out.println("저장에 실패했습니다");
			try {
				con.rollback();
			} catch (SQLException e1) {
				System.out.println("롤백예외:" + e1.getMessage());
			}
		} finally {
			DB.close(rs, stmt, con);
		}
	}

	public void hrplanByRank(HrplanVo hv) {
		System.out.println("부서번호를 입력하세요(N을 누르면 부서번호 필터를 적용하지 않습니다)");
		String deptno = sc.nextLine();
		if (!deptno.equals("N")) {
			hv.setDeptno(deptno);
		}
		System.out.println("직급을 입력하세요(N을 누르면 직급 필터를 적용하지 않습니다)");
		String rank = sc.nextLine();
		if (!rank.equals("N")) {
			hv.setRanker(rank);
		}

		try {
			con = DB.con();
			pstmt = con.prepareStatement(hv.getSearch1() + hv.getSearch2());
			rs = pstmt.executeQuery();
			System.out.println("부서\t 직급\t인원계획\t실제\t달성률\t상태");
			while (rs.next()) {
				System.out.println(rs.getString(1) + "\t" + rs.getString(2) + "\t" + rs.getString(3) + "\t"
						+ rs.getString(4) + "\t" + rs.getString(5) + "\t" + rs.getString(6));
			}
		} catch (SQLException e) {
			System.out.println("SQL예외: " + e.getMessage());
			System.out.println("조회에 실패했습니다");
		} finally {
			DB.close(rs, stmt, con);
		}
	}

	public void hrplanByDept() {
		String sql = "SELECT DNAME, 계획합 , 실제인원 , NVL(ROUND(실제인원/계획합*100),0) ||'%' 달성률,\r\n"
				+ "	CASE WHEN NVL(ROUND(실제인원/계획합*100),0)<90 THEN '미달'\r\n"
				+ "		WHEN NVL(ROUND(실제인원/계획합*100),0) <110 THEN '적정'\r\n" + "		ELSE '초과' END AS 상태\r\n"
				+ "FROM (SELECT d.deptno 부서번호별, COUNT(*) \"실제인원\" FROM EMPLOYEE e , DEPARTMENT d \r\n"
				+ "	WHERE e.DEPTNO =d.deptno GROUP BY d.DEPTNO) 실제 , \r\n"
				+ "	(SELECT DEPTNO, SUM(CNT) 계획합 FROM HRPLAN H GROUP BY DEPTNO) 계획,\r\n" + "	DEPARTMENT DP\r\n"
				+ "WHERE 실제.부서번호별=계획.DEPTNO AND DP.DEPTNO=계획.DEPTNO";
		try {
			con = DB.con();
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			System.out.println("부서\t인원계획\t실제인원\t달성률\t상태");
			while (rs.next()) {
				System.out.println(rs.getString(1) + "\t" + rs.getString(2) + "\t" + rs.getString(3) + "\t"
						+ rs.getString(4) + "\t" + rs.getString(5));
			}
		} catch (SQLException e) {
			System.out.println("SQL예외: " + e.getMessage());
			System.out.println("조회에 실패했습니다");
		} finally {
			DB.close(rs, stmt, con);
		}
	}

	public void insertBudget() {
		System.out.println("금년도 초기예산이면 1, 예산추가면 2를 눌러주세요");
		String year = sc.nextLine();
		if (year.equals("1")) {
			year = "trunc(sysdate,'YYYY')";
		} else {
			year = "sysdate";
		}
		System.out.println("부서번호를 입력하세요");
		String deptno = sc.nextLine();
		System.out.println("항목을 입력하세요");
		String type = sc.nextLine();
		System.out.println("금액을 입력하세요");
		String budget = sc.nextLine();
		String sql = "INSERT INTO BUDGETPLAN VALUES ("+year+"," + deptno + ",'" + type + "',"
				+ budget + ")";
		try {
			con = DB.con();
			con.setAutoCommit(false);
			stmt = con.createStatement();
			System.out.println(stmt.executeUpdate(sql) + "건 ");
			con.commit();
			System.out.println("저장되었습니다");
		} catch (SQLException e) {
			System.out.println("SQL예외: " + e.getMessage());
			System.out.println("저장에 실패했습니다");
			try {
				con.rollback();
			} catch (SQLException e1) {
				System.out.println("롤백예외:" + e1.getMessage());
			}
		} finally {
			DB.close(rs, stmt, con);
		}
	}

	public void budgetByAccount(BudgetVo bb) { // 계정별로 예산보기 (연도, 부서 필터)
		System.out.println("조회연도를 입력하세요(N을 누르면 부서번호 필터를 적용하지 않습니다)");
		String year = sc.nextLine();
		if (!year.equals("N")) {
			bb.setYear(year);
			}
		System.out.println("부서번호를 입력하세요(N을 누르면 부서 필터를 적용하지 않습니다)");
		String deptno = sc.nextLine();
		if (!deptno.equals("N")) {
			bb.setDeptno(deptno);
		}

		try {
			con = DB.con();
			stmt = con.createStatement();
			rs = stmt.executeQuery(bb.getSql());
			System.out.println("계정\t초기예산\t예산결과\t초과비용\t사용률");
			while (rs.next()) {
				System.out.println(rs.getString(1) + "\t" + rs.getString(2) + "\t" + rs.getString(3) + "\t"
						+ rs.getString(4) + "\t" + rs.getString(5));
			}
		} catch (SQLException e) {
			System.out.println("SQL예외: " + e.getMessage());
			System.out.println("조회에 실패했습니다");
		} finally {
			DB.close(rs, stmt, con);
		}
	}
	public void budgetByDept() {	//부서별로 예산보기 (전체 항목, 연도 지정)
		System.out.println("조회연도를 입력하세요");
		String year = sc.nextLine();
		String sql = "SELECT DNAME , 초기예산, 예산결과 , 예산결과-초기예산 \"초과비용\" , ROUND(NVL(예산결과/초기예산*100,0))||'%' \"사용률\"\r\n"
				+ "FROM \r\n"
				+ "(SELECT DEPTNO, SUM(BUDGET) 초기예산 FROM BUDGETPLAN WHERE YEAR=to_date('"+year+"-01-01','yyyy-MM-DD') GROUP BY DEPTNO) B1,\r\n"
				+ "(SELECT DEPTNO, SUM(BUDGET) 예산결과 FROM BUDGETPLAN WHERE to_CHAR(YEAR,'yyyy') LIKE "+year+" GROUP BY DEPTNO) B2,\r\n"
				+ "DEPARTMENT D\r\n"
				+ "WHERE B1.DEPTNO = B2.DEPTNO AND B1.DEPTNO=D.DEPTNO";
		try {
			con = DB.con();
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			System.out.println("부서\t예산계획\t예산결과\t초과비용\t사용률");
			while (rs.next()) {
				System.out.println(rs.getString(1) + "\t" + rs.getString(2) + "\t" + rs.getString(3) + "\t"
						+ rs.getString(4) + "\t" + rs.getString(5));
			}
		} catch (SQLException e) {
			System.out.println("SQL예외: " + e.getMessage());
			System.out.println("조회에 실패했습니다");
		} finally {
			DB.close(rs, stmt, con);
		}
		
	}
}
