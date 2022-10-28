package hrs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import vos.Appraisal;
import vos.DB;
import vos.Employee;
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
	private PreparedStatement pstmt;
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
		// 점수 입력(답변 테이블)
		String anw = null; 
		List<Employee> objlist=new ArrayList<Employee> ();
		List<Appraisal> quelist=new ArrayList<Appraisal> ();
		

		try {
			con = DB.con();
			con.setAutoCommit(false);
			stmt = con.createStatement();
			stmt2 = con.createStatement();
			// 내 로그인 정보를 사용해서 나랑 같은 부서 사람들 선택
			rs2 = stmt2.executeQuery("select * from employee where deptno = " + Welcome1.user.getDeptno()
					+ " AND empno <> " + Welcome1.user.getEmpno()); 
			while (rs2.next()) {	//내 부서 사람들
			objlist.add(new Employee(rs2.getInt("Empno"),rs2.getString("name")));
			}
			
			String que = "SELECT * FROM APPRAISALQUE a  WHERE substr(queno,1,4)=" + year;	// 연도에 맞게 질문 가져와서 출력 (질문 테이블)
			rs = stmt.executeQuery(que); // 올해 질문만 뽑아오기
			while(rs.next()) {
			quelist.add(new Appraisal(rs.getInt("queno"),rs.getString("Question")));
			}
			
			for(Employee ob:objlist) {
			System.out.println("┏━━━━━━━━━━━━━━━━━━━┓");
			int obj=ob.getEmpno();
			System.out.println(ob.getName()+"의 평가");
			for(Appraisal ap:quelist) {
				System.out.println(ap.getQueno()+":"+ap.getQuestion());
				int point = sc.nextInt();
				anw = "INSERT INTO APPRAISALANSWER ( SELECT ?,?,?,? FROM DUAL WHERE NOT EXISTS( SELECT * FROM APPRAISALANSWER a WHERE sub= ? AND obj = ? AND queno = ?))";
				pstmt=con.prepareStatement(anw);
				pstmt.setInt(1,ap.getQueno());
				pstmt.setInt(2,point);
				pstmt.setInt(3,Welcome1.user.getEmpno());
				pstmt.setInt(4,obj);
				pstmt.setInt(5,Welcome1.user.getEmpno());
				pstmt.setInt(6,obj);
				pstmt.setInt(7,ap.getQueno());
				if(pstmt.executeUpdate()!=1) System.out.println("중복된 평가는 저장되지 않습니다");
				}
			System.out.println("┗━━━━━━━━━━━━━━━━━━━┛");
			}
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
