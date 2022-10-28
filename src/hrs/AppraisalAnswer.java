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
public class AppraisalAnswer {
	private Connection con;
	private Statement stmt;
	private Statement stmt2;
	private PreparedStatement pstmt;
	private PreparedStatement pstmt2;
	private ResultSet rs;
	private ResultSet rs2;
	private ResultSet rs3;
	Date today = new Date();
	SimpleDateFormat formatyear = new SimpleDateFormat("yyyy");
	public String year = formatyear.format(today);

	Scanner sc = new Scanner(System.in);
	int userdeptno=50;
	int userempno=2022501028;	//userempno
	
	public static void main(String[] args) {
		AppraisalAnswer aa = new AppraisalAnswer();
		aa.insertAns();
	}
	public void insertAns() { // 답변 등록
		// 반복 질문번호별로
		// 점수 입력(답변 테이블)
		String anw = null; 
		List<Employee> objlist=new ArrayList<Employee> ();
		List<Appraisal> quelist=new ArrayList<Appraisal> ();
		List<Appraisal> activequelist=new ArrayList<Appraisal> ();
		

		try {
			con = DB.con();
			con.setAutoCommit(false);
			stmt = con.createStatement();
			stmt2 = con.createStatement();
			// 내 로그인 정보를 사용해서 나랑 같은 부서 사람들 선택
			rs2 = stmt2.executeQuery("select * from employee where deptno = " + userdeptno
					+ " AND empno <> " + userempno); 
			while (rs2.next()) {	//내 부서 사람들
			objlist.add(new Employee(rs2.getInt("Empno"),rs2.getString("name")));
			}
			
			String que = "SELECT * FROM APPRAISALQUE a  WHERE substr(queno,1,4)=" + year;	// 연도에 맞게 질문 가져와서 출력 (질문 테이블)
			rs = stmt.executeQuery(que); // 올해 질문만 뽑아오기
			while(rs.next()) {
			quelist.add(new Appraisal(rs.getInt("queno"),rs.getString("Question")));
			}
			/////목록 뽑아온걸로 답변 했는지 확인하기
			for(Employee ee:objlist) {
				for(Appraisal aa:quelist) {
					String check="SELECT 1 FROM DUAL WHERE NOT EXISTS( SELECT * FROM APPRAISALANSWER a WHERE sub= ? AND obj = ? AND queno = ?)";
					pstmt2=con.prepareStatement(check);
					pstmt2.setInt(5,userempno);
					pstmt2.setInt(6,ee.getEmpno());
					pstmt2.setInt(7,aa.getQueno());
					rs3=pstmt2.executeQuery();
					while(rs3.next()) {
						activequelist.add(new Appraisal(aa.getQueno(),aa.getQuestion(),userempno,ee.getEmpno()));
					}
				}
			}
			for(Appraisal ra:activequelist) {
				System.out.println(ra.getQueno()+"번"+ra.getQuestion());
				System.out.println("평가자"+ra.getSub());
				System.out.println("평가자"+ra.getObj());
			}
			
/*			for(Employee ob:objlist) {
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
				pstmt.setInt(3,userempno);
				pstmt.setInt(4,obj);
				pstmt.setInt(5,userempno);
				pstmt.setInt(6,obj);
				pstmt.setInt(7,ap.getQueno());
				if(pstmt.executeUpdate()!=1) System.out.println("중복된 평가는 저장되지 않습니다");
				}
			System.out.println("┗━━━━━━━━━━━━━━━━━━━┛");
			}*/
			
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


}
