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

	public void insertAns() {
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
					System.out.println(rs.getString("question")); // 뽑아온 질문 출력
					String point = sc.nextLine();
					anw = "INSERT INTO APPRAISALANSWER VALUES (" + rs.getInt("Queno") + "," + point + ","
							+ Welcome1.user.getEmpno() + "," + rs2.getInt("empno") + ")";
					stmt3 = con.createStatement();
					stmt3.executeUpdate(anw);

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

}
