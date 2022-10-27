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

import vos.DB;
import vos.Retirement;
import welcome.Welcome1;

public class Retiring {
	private Connection con;
	private Statement stmt;
	private PreparedStatement pstmt;
	private ResultSet rs;
	Date today = new Date();
	SimpleDateFormat formatyear = new SimpleDateFormat("yyyy");
	SimpleDateFormat formattoday = new SimpleDateFormat("yyyy-mm-dd");
	public String year = formatyear.format(today);
	public String todate = formattoday.format(today);
	Scanner sc = new Scanner(System.in);

	// 내 퇴사 신청 내용 여기서 보게 추가하기
	public void applyretire() { // 내 퇴사신청 하기
		System.out.println("퇴사 희망일을 입력하세요 (YYYY-MM-DD)");
		String retireday = sc.nextLine();
		System.out.println("퇴사 사유를 입력하세요");
		String reason = sc.nextLine();
		String sql = "INSERT INTO retirement(empno,RETIREDAY,REASON) VALUES (" + Welcome1.user.getEmpno() + ",to_date('"
				+ retireday + "','yyyy-mm-dd'),'" + reason + "')";
		try {
			con = DB.con();
			con.setAutoCommit(false);
			stmt = con.createStatement();
			int cnt = stmt.executeUpdate(sql);
			if (cnt == 1)
				System.out.println("신청이 저장되었습니다");
			con.commit();
		} catch (SQLException e) {
			System.out.println("SQL예외: " + e.getMessage());
			try {
				con.rollback();
			} catch (SQLException e1) {
				System.out.println("롤백예외:" + e1.getMessage());
			}
		} finally {
			DB.close(rs, stmt, con);
		}
	}

	public void insertretire() { // 남의거 퇴사신청 입력
		System.out.println("퇴사자 사원번호를 입력하세요");
		String empno = sc.nextLine();
		System.out.println("퇴사 희망일을 입력하세요 (YYYY-MM-DD)");
		String retireday = sc.nextLine();
		System.out.println("퇴사 사유를 입력하세요");
		String reason = sc.nextLine();
		System.out.println("현재 처리 상태를 입력하세요('신청','승인','퇴사','취소'만 가능합니다)");
		String state = sc.nextLine();
		String sql = "INSERT INTO retirement VALUES (" + empno + ",to_date('" + retireday + "','yyyy-mm-dd'),'" + reason
				+ "','" + state + "')";
		try {
			con = DB.con();
			con.setAutoCommit(false);
			stmt = con.createStatement();
			int cnt = stmt.executeUpdate(sql);
			if (cnt == 1)
				System.out.println("신청이 저장되었습니다");
			con.commit();
		} catch (SQLException e) {
			System.out.println("SQL예외: " + e.getMessage());
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

	public void selectApply(Retirement vo) { // 관리자가 퇴사신청서 내역 조건별로 볼 수 있게 동적 sql
		List<Retirement> rlist = new ArrayList<Retirement>();
		System.out.println("검색조건 번호를 선택하세요");
		while (true) { // while문으로 필터를 선택해서 입력할 수 있게
			System.out.println("1.사원명  2.사원번호  3.퇴직연도  4.상태  5.부서번호  6.직급");
			System.out.println("다른 키를 누르면 필터 선택이 끝납니다");
			String filter = sc.nextLine();
			switch (filter) {
			case "1":
				System.out.println("사원명: ");
				String name = sc.nextLine();
				vo.setName(name);
				break;
			case "2":
				System.out.println("사원번호: ");
				String empno = sc.nextLine();
				vo.setEmpno(empno);
				break;
			case "3":
				System.out.println("퇴직연도: ");
				String retireyear = sc.nextLine();
				vo.setRetireyear(retireyear);
				break;
			case "4":
				System.out.println("상태 ('신청','승인','퇴사','취소')");
				String state = sc.nextLine();
				vo.setState(state);
				break;
			case "5":
				System.out.println("부서번호");
				String deptno = sc.nextLine();
				vo.setDeptno(deptno);
				break;
			case "6":
				System.out.println("직급");
				String rank = sc.nextLine();
				vo.setRank(rank);
				break;
			default:
				filter = "0";
			}
			if (filter.equals("0"))
				break;
		}
		String sql = vo.getSearchline();
		try {
			con = DB.con();
			pstmt = con.prepareStatement(sql);
			if (vo.getNoName() != 0)
				pstmt.setString(vo.getNoName(), vo.getName());
			if (vo.getNoEmpno() != 0)
				pstmt.setString(vo.getNoEmpno(), vo.getEmpno());
			if (vo.getNoDeptno() != 0)
				pstmt.setString(vo.getNoDeptno(), vo.getDeptno());
			if (vo.getNoRank() != 0)
				pstmt.setString(vo.getNoRank(), vo.getRank());
			if (vo.getNoRetireyear() != 0)
				pstmt.setString(vo.getNoRetireyear(), vo.getRetireyear());
			if (vo.getNoState() != 0)
				pstmt.setString(vo.getNoState(), vo.getState());
			rs = pstmt.executeQuery();
			while (rs.next()) {
				rlist.add(new Retirement(rs.getString("empno"), rs.getString("name"), rs.getString("deptno"),
						rs.getString("rank"), rs.getString("retireday"), rs.getString("reason"),
						rs.getString("state")));
			}

		} catch (SQLException e) {
			System.out.println("SQL예외: " + e.getMessage());
		} catch (Exception e) {
			System.out.println("일반예외:" + e.getMessage());
		} finally {
			DB.close(rs, stmt, con);
		}
		for (Retirement r : rlist) {
			r.printAll();
		}
	}

	public void changeState() { // 퇴사 승인,취소 등 관리자메뉴
		System.out.println("퇴사신청 상태를 변경할 사원의 사원번호를 입력하세요");
		String empno = sc.nextLine();
		System.out.println("변경할 상태를 입력하세요('신청','승인','퇴사','취소')");
		String state = sc.nextLine();
		String checksql = "select * from RETIREMENT where empno = " + empno;
		String sql = "UPDATE RETIREMENT SET STATE = '" + state + "' WHERE EMPNO ='" + empno + "'";
		try {
			con = DB.con();
			con.setAutoCommit(false);
			stmt = con.createStatement();
			rs = stmt.executeQuery(checksql);
			if (rs.next()) {
				System.out.println(stmt.executeUpdate(sql) + "건의 상태가 변경되었습니다");
			} else {
				System.out.println("해당 사원의 퇴사신청 정보가 없습니다");
				con.commit();
			}
		} catch (SQLException e) {
			System.out.println("SQL예외: " + e.getMessage());
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

	public void changeStateAuto() {
		System.out.println("퇴사신청일이 지난 승인된 퇴사신청을 자동으로 처리합니다 (Y/N)");
		String check = sc.nextLine();
		if (check.equals("Y")) {
			String sql = "UPDATE RETIREMENT SET STATE = '퇴사' WHERE STATE = '승인' AND retireday < sysdate";
			try {
				con = DB.con();
				con.setAutoCommit(false);
				stmt = con.createStatement();
				System.out.print(stmt.executeUpdate(sql) + "건의 승인된 퇴사신청의 상태를");
				con.commit();
				System.out.print(" 퇴사로 변경했습니다\n");
			} catch (SQLException e) {
				System.out.println("SQL예외: " + e.getMessage());
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
		} else {
			System.out.println("자동퇴사를 취소합니다");
		}

	}
}
