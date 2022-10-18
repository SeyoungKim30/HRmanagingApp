package hrs;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import vos.DB;
import vos.Department;

//부서정보 검색, 새 부서 생성, 부서 정보 수정

public class Deptinfo {
	private Connection con;
	private Statement stmt;
	private ResultSet rs;
	Scanner sc = new Scanner(System.in);
	
//새 부서 등록
	public void insertDept() {
		System.out.println("새로 등록할 부서 번호를 입력하세요 (중복 불가)");
		String deptno = sc.nextLine();
		System.out.println("새로 등록할 부서명을 입력하세요");
		String dname = sc.nextLine();
		String insertsql="INSERT INTO DEPARTMENT VALUES ("+deptno+",'"+dname+"')";
		while(true) {
		try {
			con=DB.con();
			con.setAutoCommit(false);
			stmt=con.createStatement();
			rs=stmt.executeQuery("select * from department where deptno ="+deptno);
			if(rs.next()) {
				System.out.println("중복된 부서 번호입니다");
				break;
				}
			stmt.executeUpdate(insertsql);
			con.commit();
			System.out.println("부서정보가 등록되었습니다");
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
	
	
//부서 삭제
	public void delDept() {
			System.out.println("삭제할 부서 번호를 입력하세요 ");
			String deptno = sc.nextLine();
			String delsql="delete from department where deptno="+deptno;
			while(true) {
			try {
				con=DB.con();
				con.setAutoCommit(false);
				stmt=con.createStatement();
				rs=stmt.executeQuery("select * from department where deptno ="+deptno);
				if(!rs.next()) {
					System.out.println("존재하지 않는 부서 번호입니다");
					break;
					}
				stmt.executeUpdate(delsql);
				con.commit();
				System.out.println("부서정보가 삭제되었습니다");
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

//부서번호 또는 부서명으로 검색
	public void searchDept() {
		List<Department> dlist = new ArrayList<Department>();
		System.out.println("검색할 부서번호 또는 부서명을 입력해주세요");
		System.out.println("부서번호:");
		String deptno = sc.nextLine();
		System.out.println("부서명:");
		String dname = sc.nextLine();
		String sql="select de.deptno,de.dname, 카운트 from (SELECT d.DEPTNO 부서번호 ,count(empno) 카운트 \r\n"
				+ "FROM employee e , DEPARTMENT d  WHERE d.DEPTNO (+)= e.DEPTNO GROUP BY d.DEPTNO) subde , department de\r\n"
				+ "where subde.부서번호 (+)= de.deptno\r\n"
				+ "AND (de.deptno like '%'||'"+deptno+"'||'%'\r\n"
				+ "and de.dname LIKE '%'||'"+dname+"'||'%')";
		try {
			con=DB.con();
			stmt=con.createStatement();
			rs=stmt.executeQuery(sql);
			
			while(rs.next()) {
				dlist.add(new Department(rs.getInt("deptno"), rs.getString("dname"),rs.getInt("카운트")));
			}
		} catch (SQLException e) {
			System.out.println("sql예외 발생 : "+e.getMessage());
		}
		for(Department dd:dlist) {
			dd.printAll();
		}
		
	}

//부서명 변경
	public void updateDname() {
		System.out.println("이름을 변경할 부서번호를 입력하세요");
		String deptno = sc.nextLine();
		System.out.println("변경할 이름을 입력하세요");
		String dname = sc.nextLine();
		String sqlupdate="UPDATE department SET dname = '"+dname+"' WHERE deptno = "+deptno;
		try {
			con=DB.con();
			con.setAutoCommit(false);
			stmt=con.createStatement();
			stmt.execute(sqlupdate);
			con.commit();
			System.out.println("부서명이 변경되었습니다.");
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
