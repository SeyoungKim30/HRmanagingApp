package schedule;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Scanner;

import vos.DB;
import welcome.Welcome1;

//로그인시 실행하고 시스템에서 시간 받아서 입력하기
public class WorkTableDAO {
	private Connection con;
	private Statement stmt;
	private ResultSet rs;

	Scanner sc = new Scanner(System.in);
	
	//로그인할때 insert
	public void timeon() {
	
		try {
			con=DB.con();
			con.setAutoCommit(false);
			stmt=con.createStatement();
			String sql="INSERT INTO WORKTABLE (empno, TIMEON)VALUES ("+Welcome1.user.getEmpno()+", sysdate)";
			stmt.executeUpdate(sql);
			con.commit();

     } catch (SQLException e) {
		System.out.println("SQL예외: "+e.getMessage());
		System.out.println("출근정보 기록에 실패했습니다");
		try {
			con.rollback();
		} catch (SQLException e1) {
			System.out.println("롤백예외:" +e1.getMessage());
		}
		}catch(Exception e) {
			System.out.println("일반예외:"+e.getMessage());
		}
		finally {
			DB.close(rs, stmt, con);
		}
	}
	
	//로그아웃 update
	public void timeoff() {

	String sql="UPDATE worktable SET TIMEOFF = sysdate\r\n"
				+ "WHERE EMPNO LIKE "+Welcome1.user.getEmpno()+" AND TIMEON < sysdate\r\n"
				+ "AND TIMEON+1 > sysdate AND TIMEOFF is NULL";
	try {
			con=DB.con();
			con.setAutoCommit(false);
			stmt=con.createStatement();
			System.out.println(stmt.executeUpdate(sql)+"로그아웃");
			con.commit();
     } catch (SQLException e) {
		System.out.println("SQL예외: "+e.getMessage());
		System.out.println("퇴근정보 기록에 실패했습니다");
		try {
			con.rollback();
		} catch (SQLException e1) {
			System.out.println("롤백예외:" +e1.getMessage());
		}
		}catch(Exception e) {
			System.out.println("일반예외:"+e.getMessage());
		}
		finally {
			DB.close(rs, stmt, con);
		}
	}
	
	//출근부 조회
	
}
