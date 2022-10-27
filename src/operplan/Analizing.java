package operplan;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import vos.DB;

public class Analizing {
	private Connection con;
	private Statement stmt;
	private ResultSet rs;
	private ResultSet rs2;
	private ResultSet rs3;
	Scanner sc = new Scanner(System.in);

	public void showpointbyfilter() {		//조건에 따른 인사평가 통계 조회
		String summery="SELECT  AVG(점수) 평균 ,MAX(점수) 최대 ,MIN(점수) 최소\r\n"
				+ "FROM \r\n"
				+ "	(SELECT EMPNO, \"RANK\",DEPTNO,SALARY  FROM HISTORY H \r\n"
				+ "		WHERE (EMPNO ,MOVEDAY ) IN (SELECT EMPNO, MAX(MOVEDAY) FROM HISTORY h1 GROUP BY EMPNO)  ";
		
		String sql1 = "SELECT  EMPNO, \"RANK\",DEPTNO, SALARY, 점수\r\n"
				+ "FROM (SELECT EMPNO, \"RANK\",DEPTNO,SALARY  FROM HISTORY H WHERE (EMPNO ,MOVEDAY ) IN (SELECT EMPNO, MAX(MOVEDAY) FROM HISTORY h1 GROUP BY EMPNO)  \r\n";
		String filters="";
		String sql2= " ) FILTER1, (SELECT OBJ, ROUND(AVG(POINT),2) 점수 FROM appraisalAnswer GROUP BY OBJ) APPRE1\r\n"
				+ "WHERE APPRE1.OBJ(+)=FILTER1.EMPNO ORDER BY 점수";
		
		System.out.println("급여조건 입력하기 (Y/N)");
		String salfilter = sc.nextLine();
		if (salfilter.equals("Y")) {
			System.out.println("최소 급여조건");
			String minsal = sc.nextLine();
			System.out.println("최대 급여조건");
			String maxsal = sc.nextLine();
			filters += " AND SALARY BETWEEN "+minsal+" AND " +maxsal ;
		}
		System.out.println("부서 선택하기 (부서번호/N)");
		String deptfilter = sc.nextLine();
		if (deptfilter.equals("N")) {
		}else {
			filters+= " AND DEPTNO  LIKE '%'||'"+deptfilter+"'||'%' ";
		}
		System.out.println("직급 조건 입력하기 (직급/N)");
		String rankfilter=sc.nextLine();
		if (rankfilter.equals("N")) {
		}else {
			filters+= " AND \"RANK\"  LIKE '%'||'"+rankfilter+"'||'%' ";
		}
		System.out.println(sql1+sql2);
		try {
			con = DB.con();
			stmt = con.createStatement();
			rs=stmt.executeQuery(sql1+filters+sql2);
			System.out.println("┏━━사번\t\t직급\t부서\t급여\t평가점수━━┓");
			while(rs.next()) {
				System.out.println("┃"+rs.getString("empno")+"\t"+rs.getString("rank")+"\t"+rs.getString("deptno")+"\t"+rs.getString("salary")+"\t"+rs.getString("점수")+"\t┃");
			}
			System.out.println("┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛");
		rs2=stmt.executeQuery(summery+filters+sql2)	;
		while(rs2.next()) {
			System.out.println("평균점수: "+rs2.getString("평균"));
			System.out.println("최대점수: "+rs2.getString("최대"));
			System.out.println("최소점수: "+rs2.getString("최소"));
		}
			
		} catch (SQLException e) {
			System.out.println("SQL예외: " + e.getMessage());
		} catch (Exception e) {
			System.out.println("일반예외:" + e.getMessage());
		} finally {
			DB.close(rs, stmt, con);
		}
	}
	public void retirementstudy1() {	//퇴사분석: 부서별 직급별 급여별
		String bydept="SELECT dname , 인원 FROM DEPARTMENT d ,\r\n"
				+ "(SELECT deptno, COUNT(deptno) 인원\r\n"
				+ "FROM history WHERE (empno, moveday) in(\r\n"
				+ "SELECT h.empno, MIN(moveday) FROM history h\r\n"
				+ "	WHERE h.empno in (SELECT EMPNO FROM RETIREMENT WHERE state LIKE '퇴사') GROUP BY h.empno)\r\n"
				+ "GROUP BY DEPTNO ) d1 \r\n"
				+ "WHERE d.DEPTNO =d1.deptno";
		String byrank="SELECT \"RANK\" , COUNT(*) \r\n"
				+ "FROM history WHERE (empno, moveday) in(\r\n"
				+ "SELECT h.empno, MIN(moveday) FROM history h\r\n"
				+ "	WHERE h.empno in (SELECT EMPNO FROM RETIREMENT WHERE state LIKE '퇴사') GROUP BY h.empno)\r\n"
				+ "GROUP BY \"RANK\"";
		String bysalary="SELECT round(SALARY,-2) \"급여\",COUNT(*)  \r\n"
				+ "FROM history WHERE (empno, moveday) in(\r\n"
				+ "SELECT h.empno, MIN(moveday) FROM history h\r\n"
				+ "	WHERE h.empno in (SELECT EMPNO FROM RETIREMENT WHERE state LIKE '퇴사') GROUP BY h.empno)\r\n"
				+ "GROUP BY round(SALARY,-2)";
		try {
			con = DB.con();
			stmt = con.createStatement();
			rs=stmt.executeQuery(bydept);
			System.out.println("━━부서별 퇴사인원━━");
			while(rs.next()) {
				System.out.println(rs.getString("dname")+" 부서\t"+rs.getString("인원"));
			}
		
		rs2=stmt.executeQuery(byrank)	;
		System.out.println("━━직급별 퇴사인원━━");
		while(rs2.next()) {
			System.out.println(rs2.getString(1)+": \t"+rs2.getString(2));
		}
		rs3=stmt.executeQuery(byrank)	;
		System.out.println("━━급여별(100단위) 퇴사인원━━");
		while(rs3.next()) {
			System.out.println(rs3.getString(1)+": \t"+rs3.getString(2));
		}
			
		} catch (SQLException e) {
			System.out.println("SQL예외: " + e.getMessage());
		} catch (Exception e) {
			System.out.println("일반예외:" + e.getMessage());
		} finally {
			DB.close(rs, stmt, con);
		}
	}
	public void retirementstudy2() {	//퇴사분석: 근무기간, 직원평가 점수
		String termstat="SELECT round(avg(RETIREDAY -moveday)) 평균, round(min(RETIREDAY -moveday)) 최소 , round(max(RETIREDAY -moveday)) 최대\r\n"
				+ "FROM RETIREMENT r , HISTORY h2 \r\n"
				+ "WHERE r.EMPNO =h2.EMPNO \r\n"
				+ "AND (r.EMPNO ,moveday) IN \r\n"
				+ "	(SELECT h.empno, MIN(moveday) FROM history h\r\n"
				+ "	WHERE h.empno in (SELECT EMPNO FROM RETIREMENT WHERE state LIKE '퇴사') GROUP BY h.empno)";
		
		String appraisal="SELECT AVG(point) FROM APPRAISALANSWER a WHERE (obj, substr(QUENO,1,4) ) \r\n"
				+ "in (SELECT EMPNO, to_char(retireday,'yyyy') FROM RETIREMENT WHERE state LIKE '퇴사')";
		try {
			con = DB.con();
			stmt = con.createStatement();
			rs=stmt.executeQuery(termstat);
			System.out.println("━━퇴사자 근무기간━━");
			while(rs.next()) {
				System.out.println("평균: "+rs.getString(1)+"\t 최소: "+rs.getString(2)+"\t 최대: "+rs.getString(3));
			}
		
		rs2=stmt.executeQuery(appraisal)	;
		System.out.print("━━퇴사연도 직원평가 점수 : ");
		while(rs2.next()) {
			System.out.print(rs2.getString(1)+" ━━\n");
		}
		} catch (SQLException e) {
			System.out.println("SQL예외: " + e.getMessage());
		} catch (Exception e) {
			System.out.println("일반예외:" + e.getMessage());
		} finally {
			DB.close(rs, stmt, con);
		}
	}
}
