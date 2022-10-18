package hrs;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import vos.DB;
import welcome.Welcome1;

/*
 * 인사평가 문항 작성
 * 인사평가 하기 (올해것만)
 * 자기 평가 보기(항목별 평균, 총 평균)
 * (관리자)팀원별 평가 보기(항목별 평균, 총 평균)
 * (관리자)팀별 평가 보기(항목별 평균, 총 평균)
 * 
 */
public class Appre {
	private Connection con;
	private Statement stmt;
	private Statement stmt2;
	private Statement stmt3;
	private ResultSet rs;
	private ResultSet rs2;
	Date today = new Date();
	SimpleDateFormat formatyear = new SimpleDateFormat("yyyy");
	public String year = formatyear.format(today);

	Scanner sc = new Scanner(System.in);

	public void insertQues() { // 문항 생성
		while (true) {
			System.out.println("평가 항목을 입력하세요(33자 이내로)");
			String question = sc.nextLine();
			if (question.length() > 30) {
				System.out.println("길이를 초과했습니다");
				break;
			}
			String sql = "INSERT INTO appraisalQue VALUES (" + year + "||seqforAppraQ.nextval ,'" + question + "')";

			try {
				con = DB.con();
				con.setAutoCommit(false);
				stmt = con.createStatement();
				int cnt = stmt.executeUpdate(sql);
				if (cnt == 1)
					System.out.println("항목이 등록되었습니다");
				con.commit();
			} catch (SQLException e) {
				System.out.println("sql예외:" + e.getMessage());
				try {
					con.rollback();
				} catch (SQLException e1) {
					System.out.println("롤백예외발생:" + e1.getMessage());
				}
			} finally {
				DB.close(rs, stmt, con);
			}
			break;
		}
	}

	public void insertAns() { // 답변 등록
		// 반복 질문번호별로
		// 연도에 맞게 질문 가져와서 출력 (질문 테이블)
		String que = "SELECT * FROM APPRAISALQUE a  WHERE substr(queno,1,4)=" + year;
		// 점수 입력(답변 테이블)
		String anw = null; // "INSERT INTO APPRAISALANSWER VALUES (20225,4,2022201020,2022301022)";

		try {
			con = DB.con();
			con.setAutoCommit(false);
			stmt = con.createStatement();
			stmt2 = con.createStatement();
			rs = stmt.executeQuery(que); // 올해 질문만 뽑아오기
			rs2 = stmt2.executeQuery("select * from employee where deptno = " + Welcome1.user.getDeptno()
					+ " AND empno <> " + Welcome1.user.getEmpno()); // 내 로그인 정보를 사용해서 나랑 같은 부서 사람들 선택
			while (rs2.next()) {
				System.out.println("┏━━━━━━━━━━━━━━━━━━━┓");
				System.out.println(rs2.getString("name"));
				while (rs.next()) {
					int objnumber = rs2.getInt("Empno");
					System.out.println(rs.getString("question")); // 뽑아온 질문 출력
					String point = sc.nextLine();
					anw = "INSERT INTO APPRAISALANSWER VALUES (" + rs.getInt("Queno") + "," + point + "," + objnumber
							+ "," + Welcome1.user.getEmpno() + ")";
					try {
						stmt3 = con.createStatement();
						stmt3.executeUpdate(anw);
					} catch (SQLException e) {
						System.out.println("이미 평가를 했습니다");
						try {
							con.rollback();
						} catch (SQLException e1) {
							System.out.println("롤백예외발생:" + e1.getMessage());
						}
					}
				}
			}
			System.out.println("┗━━━━━━━━━━━━━━━━━━━┛");
			con.commit();
			System.out.println("평가 완료");
		} catch (SQLException e) {
			System.out.println("sql예외:" + e.getMessage());
			try {
				con.rollback();
			} catch (SQLException e1) {
				System.out.println("롤백예외발생:" + e1.getMessage());
			}
		} finally {
			DB.close(rs, stmt, con);
		}

	}

	public void myAppre() { // 내 결과 보기
		// 사람별 항목별 이름 나오게 평균점수
		String byObjbyQueno = "SELECT DISTINCT name, question , 평균점수 \r\n"
				+ "FROM appraisalQue Q, EMPLOYEE e ,(SELECT obj, queno, ROUND(avg(point),3) \"평균점수\" FROM APPRAISALANSWER aa GROUP BY obj, QUENO) \"그룹테이블\" \r\n"
				+ "WHERE q.queno=그룹테이블.queno AND 그룹테이블.obj = e.empno\r\n" + "AND name = '" + Welcome1.user.getName()
				+ "'";
		try {
			con = DB.con();
			stmt = con.createStatement();
			rs = stmt.executeQuery(byObjbyQueno);
			while (rs.next()) {
				System.out.println(rs.getString("QUESTION") + " : " + rs.getString("평균점수"));
			}
			// 내 전체 평균
			String myallAvg = "SELECT ROUND(avg(point),3) FROM APPRAISALANSWER aa  WHERE OBJ="+Welcome1.user.getEmpno()+" GROUP BY OBJ";
			rs = stmt.executeQuery(myallAvg);
			while (rs.next()) {
				System.out.println("-------------------");
				System.out.println("총 평균 : " + rs.getString(1));
			}

		} catch (SQLException e) {
			System.out.println("SQL예외: " + e.getMessage());
		}

	}

	public void myApprePast() { // 내 결과+ 연도지정
		// 사람별 항목별 이름 나오게 평균점수
		System.out.println("조회할 연도를 입력해주세요(YYYY)");
		String pastyear=sc.nextLine();
		String byObjbyQueno = "SELECT DISTINCT name, question , 평균점수 \r\n"
				+ "FROM appraisalQue Q, EMPLOYEE e ,(SELECT obj, queno, ROUND(avg(point),3) \"평균점수\" FROM APPRAISALANSWER aa GROUP BY obj, QUENO) \"그룹테이블\" \r\n"
				+ "WHERE q.queno=그룹테이블.queno AND 그룹테이블.obj = e.empno\r\n" + "AND name = '" + Welcome1.user.getName()
				+ "'AND substr(q.queno,1,4)= "+pastyear;
		try {
			con = DB.con();
			stmt = con.createStatement();
			rs = stmt.executeQuery(byObjbyQueno);
			while (rs.next()) {
				System.out.println(rs.getString("QUESTION") + " : " + rs.getString("평균점수"));
			}
			// 내 전체 평균
			String myallAvg = "SELECT ROUND(avg(point),3) FROM APPRAISALANSWER aa  WHERE OBJ="+Welcome1.user.getEmpno()+" AND substr(queno,1,4)="+pastyear+" GROUP BY OBJ";
			rs = stmt.executeQuery(myallAvg);
			while (rs.next()) {
				System.out.println("-------------------");
				System.out.println("총 평균 : " + rs.getString(1));
			}

		} catch (SQLException e) {
			System.out.println("SQL예외: " + e.getMessage());
		}

	}

	public void byQuebyEmp() {
		// 사람별 항목별 이름 나오게 평균점수
		System.out.println("조회 대상자의 이름을 입력하세요");
		String name=sc.nextLine();
		System.out.println("조회할 연도를 입력해주세요(YYYY)");
		String pastyear=sc.nextLine();
				String byObjbyQueno = "SELECT DISTINCT name, question , 평균점수 FROM appraisalQue Q, EMPLOYEE e ,(SELECT obj, queno, ROUND(avg(point),3) \"평균점수\" FROM APPRAISALANSWER aa GROUP BY obj, QUENO) grtb WHERE q.queno=grtb.queno AND grtb.obj = e.empno AND name = '"+name+"' AND substr(q.queno,1,4)="+pastyear;
				try {
					con = DB.con();
					stmt = con.createStatement();
					rs = stmt.executeQuery(byObjbyQueno);
					while (rs.next()) {
						System.out.println(rs.getString("question") + " : " + rs.getString("평균점수"));
					}
					//전체 평균
					String myallAvg = "SELECT ROUND(avg(point),3) FROM APPRAISALANSWER aa,EMPLOYEE e  WHERE aa.OBJ =e.EMPNO AND name = '"+name+"' AND substr(queno,1,4)= "+pastyear+" GROUP BY OBJ ";
					rs = stmt.executeQuery(myallAvg);
					while (rs.next()) {
						System.out.println("-------------------");
						System.out.println("총 평균 : " + rs.getString(1));
					}

				} catch (SQLException e) {
					System.out.println("SQL예외: " + e.getMessage());
				}
	}

	public void byDept() {	//부서별, 연도별, 전체, 항목별이랑 총평균
		String dept="";
		String pastyear="0";
		System.out.println("부서와 연도를 공백으로 하면 모든 정보가 출력됩니다");
		System.out.println("조회 대상 부서명을 입력하세요");
		dept=sc.nextLine();
		System.out.println("조회할 연도를 입력해주세요(YYYY)");
		pastyear=sc.nextLine();
		
		String bydeptque="SELECT dname, question , ROUND(avg(point),3) 평균  FROM APPRAISALANSWER a , EMPLOYEE e,DEPARTMENT d, APPRAISALQUE aq WHERE e.EMPNO =a.OBJ AND d.DEPTNO =e.DEPTNO AND aq.QUENO =a.QUENO AND dname LIKE '%'||'"+dept+"'||'%' AND substr(aq.QUENO,1,4)LIKE '%'||'"+pastyear+"'||'%' GROUP BY dname, Question ORDER BY dname";
		
		try {
			con = DB.con();
			stmt = con.createStatement();
			rs = stmt.executeQuery(bydeptque);
			while (rs.next()) {
				System.out.println(rs.getString("dname")+"┃"+rs.getString("QUESTION") + " :\t" + rs.getString("평균"));
			}
			String deptAllavg ="SELECT dname, ROUND(avg(point),3) 평균 FROM APPRAISALANSWER a , EMPLOYEE e,DEPARTMENT d  WHERE e.EMPNO =a.OBJ AND d.DEPTNO =e.DEPTNO AND dname LIKE '%'||'"+dept+"'||'%' AND substr(queno,1,4) LIKE '%'||'"+pastyear+"'||'%'  GROUP BY dname";
			rs = stmt.executeQuery(deptAllavg);
			while (rs.next()) {
				System.out.println("-------------------");
				System.out.println(rs.getString("dname") + "┃ 총 평균 : " + rs.getString("평균"));
			}
			
		} catch (SQLException e) {
			System.out.println("SQL예외: " + e.getMessage());
		}
		
	}
	
}
