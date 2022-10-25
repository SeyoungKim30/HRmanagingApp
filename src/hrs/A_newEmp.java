package hrs;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import vos.DB;
import vos.Employee;

//등록 수정 삭제
public class A_newEmp {
	private Connection con;
	private Statement stmt;
	private ResultSet rs;
	Scanner sc = new Scanner(System.in);

	// 올해 연도를 구하고 데이터를 입력받아서 적절한 위치에 배치하기
	Date today = new Date();
	SimpleDateFormat formatyear = new SimpleDateFormat("yyyy");
	String year = formatyear.format(today);


	
	// 사원 등록
	public void registerEmp() {
		System.out.println("새로운 사원의 등록을 시작합니다.");
		System.out.println("이름:");
		String name = sc.nextLine();
		System.out.println("생년월일 (yyyy-mm-dd형태로 입력하세요):");
		String birth = sc.nextLine();
		System.out.println("주소:");
		String address = sc.nextLine();
		System.out.println("학력:");
		String educational = sc.nextLine();
		System.out.println("부서명:");
		String dname = sc.nextLine();
		String finddeptno = "select deptno from department where dname like '%" + dname + "%'";
		int deptno = 0; // 찾을거
		System.out.println("직급");
		String rank = sc.nextLine();
		System.out.println("입사일");
		String joindate = sc.nextLine();
		System.out.println("급여");
		String salary = sc.nextLine();

		String pass = createpass(); // 랜덤생성

		try {
			con = DB.con();
			con.setAutoCommit(false);
			stmt = con.createStatement();
			rs = stmt.executeQuery(finddeptno);
			if (rs.next()) {
				deptno = rs.getInt(1);
			} else {
				System.out.println("부서명 " + dname + "이 없습니다. 다시 입력해주세요.");
			}
			String registerEmp = "INSERT INTO Employee VALUES (" + year + "||" + deptno + "||ORIGIN.NEXTVAL,'" + name
					+ "',TO_DATE('" + birth + "','YYYY-MM-DD'),'" + address + "','" + educational + "'," + deptno + ",'"
					+ rank + "','" + pass + "')";
			stmt.execute(registerEmp);
			String insertHistory = "insert into History values (" + year + "||" + deptno + "||ORIGIN.currval"
					+ ",to_date('" + joindate + "','YYYY-MM-DD'),'" + rank + "'," + deptno +" , "+ salary+ ")";
			stmt.execute(insertHistory);
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
	}

	// 사원 검색해서 vo객체로 만들어서 출력
	public void searchEmp() {
		List<Employee> elist = new ArrayList<Employee>();
		try {
			con = DB.con();
			stmt = con.createStatement();
			System.out.println("검색할 사원의 이름을 입력하세요");
			String name = sc.nextLine();
			String sql = "select * from employee where name like '%'||'" + name + "'||'%' ";
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				elist.add(new Employee(rs.getInt("empno"), rs.getString("name"), rs.getString("birth"),
						rs.getString("address"), rs.getString("educational"), rs.getInt("deptno"), rs.getString("rank"),
						rs.getString("pass")));
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		for (Employee ee : elist) {
			ee.printAll();
		}
	}
	// 사원정보 수정
	public void updateEmp() {
		try {
			con = DB.con();
			con.setAutoCommit(false);
			stmt = con.createStatement();
			String colname = null;
			String empno;
			String newSet = null;

			while (true) {
				System.out.println("변경할 사원의 사원번호를 입력하세요");
				empno = sc.nextLine();
				System.out.println("변경하고자 하는 정보 번호를 입력하세요");
				rs = stmt.executeQuery("select * from employee where empno ='" + empno + "'");
				if (!rs.next()) {
					System.out.println("사원번호를 잘못 입력했습니다");
					break;
				}
				System.out.println("1.주소\t2.학력\t3.이름");
				String objCol = sc.nextLine();
				switch (objCol) {
				case "1":
					colname = "address";
					break;
				case "2":
					colname = "educational";
					break;
				case "3":
					colname = "NAME";
					break;
				default:
					System.out.println(objCol + "은 잘못된 번호입니다.");
					break;
				}
				if (colname == null)
					break;

				System.out.println("변경할 내용을 입력하세요");
				newSet = sc.nextLine();
				String modiSql = "UPDATE EMPLOYEE SET " + colname + "= '" + newSet + "' WHERE empno = '" + empno + "'";
				if (stmt.executeUpdate(modiSql) == 1) {
					System.out.println("변경되었습니다.");
					break;
				} else {
					System.out.println("변경에 실패했습니다. 입력한 내용을 확인해주세요");
					break;
				}
			} // while문 끝
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

	// 사원정보 삭제
	public void deleteEmp() {
		// 인적사항 테이블, 히스토리 테이블 둘 다 삭제해야됨
		System.out.println("삭제할 직원의 이름을 입력하세요");
		String name = sc.nextLine();
		System.out.println("삭제할 직원의 사번을 입력하세요");
		String empno = sc.nextLine();
		String delHis = "DELETE FROM history where empno = " + empno;
		String delEmp = "DELETE FROM employee WHERE name = '" + name + "' AND empno = " + empno;
		try {
			con = DB.con();
			con.setAutoCommit(false);
			stmt = con.createStatement();
			stmt.executeUpdate(delHis);
			stmt.executeUpdate(delEmp);
			System.out.println(name + "님의 정보가 삭제되었습니다");
			con.commit();
		} catch (SQLException e) {
			System.out.println("SQL오류발생: " + e.getMessage());
			try {
				con.rollback();
			} catch (SQLException e1) {
				System.out.println("롤백오류:" + e1.getMessage());
			}
		}

	}

	// 히스토리 생성
	//진급이나 부서이동 발생시 history와 인사정보 동시 수정
	public void insertHistory() {
		while (true) {			// 직원번호 부서번호 잘못입력했을때 돌아가기
		System.out.println("인사이동이 이루어진 직원의 사번을 입력하세요");
		String empno = sc.nextLine();
		System.out.println("인사이동이 실행된 날짜를 입력하세요('yyyy-mm-dd'형식)");
		String moveday = sc.nextLine();
		System.out.println("직급을 입력하세요");
		String rank = sc.nextLine();
		System.out.println("부서번호를 입력하세요");
		String deptno = sc.nextLine();
		System.out.println("급여를 입력하세요");
		String salary = sc.nextLine();
			String ins = "insert into history values ('" + empno + "',to_date('" + moveday + "','YYYY-MM-DD'),'" + rank+"'," + deptno +" , "+ salary +")";
			try {
				con = DB.con();
				con.setAutoCommit(false);
				stmt = con.createStatement();
				//사원번호 확인
				rs=stmt.executeQuery("select * from Employee where empno = " + empno );
				if(!rs.next()) {
					System.out.println("존재하지 않는 사원번호입니다");
					break;
					}
				//사원번호 확인 끝
				//부서번호 확인
				rs=stmt.executeQuery("select * from Department where deptno = " + deptno);
				if(!rs.next()) {
					System.out.println("존재하지 않는 부서번호입니다");
					break;
					}
				//부서번호확인 끝
				stmt.executeUpdate(ins);
				System.out.println("history정보 저장");
				stmt.executeUpdate("UPDATE EMPLOYEE SET rank= '"+rank+"' , DEPTNO ="+deptno+" WHERE EMPNO  = "+empno);
				System.out.println("인사정보 수정");
				con.commit();
				System.out.println("커밋완료");
			} catch (SQLException e) {
				System.out.println("SQL오류발생: " + e.getMessage());
				try {
					con.rollback();
				} catch (SQLException e1) {
					System.out.println("롤백오류:" + e1.getMessage());
				}
			}
			break;
		} // while끝

	}

	// 히스토리 삭제
	public void delHistory() {
		System.out.println("삭제할 직원의 사번을 입력하세요");
		String empno = sc.nextLine();
		System.out.println("삭제할 인사이동 정보가 생성된 날짜를 입력하세요('yyyy-mm-dd'형식)");
		String moveday = sc.nextLine();

		String del = "DELETE FROM history WHERE empno = " + empno + " AND moveday = TO_DATE('" + moveday
				+ "','yyyy-mm-dd')";
		try {
			con = DB.con();
			con.setAutoCommit(false);
			stmt = con.createStatement();
			stmt.executeUpdate(del);
			System.out.println("인사이동정보가 삭제되었습니다");
			con.commit();
		} catch (SQLException e) {
			System.out.println("SQL오류발생: " + e.getMessage());
			try {
				con.rollback();
			} catch (SQLException e1) {
				System.out.println("롤백오류:" + e1.getMessage());
			}
		}

	}
	
	
	/*
	 * EMPNO CHAR(10) NOT NULL, name VARCHAR2(50) NOT NULL, birth DATE, address
	 * VARCHAR2(50), educational VARCHAR2(50), deptno NUMBER, /* 부서번호 RANK
	 * VARCHAR2(50), /* 직급 PASS VARCHAR2(50) NOT NULL /* 비밀번호
	 */

	// 자동비밀번호 생성
	public String createpass() {
		char[] tempass = new char[8];
		for (int i = 0; i < tempass.length; i++) {
			int temletter = (int) (Math.random() * 74 + 49);
			if (temletter != 59)
				tempass[i] = (char) temletter;
			if (temletter == 59)
				i--;
		}
		String pass = new String(tempass);
		return pass;
	}

}
