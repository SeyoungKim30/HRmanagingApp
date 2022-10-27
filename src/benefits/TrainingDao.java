package benefits;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import vos.Benefit;
import vos.DB;
import welcome.Welcome1;

public class TrainingDao {
	private Connection con;
	private Statement stmt;
	private ResultSet rs;
	Scanner sc = new Scanner(System.in);
	
	//입력
	public void insertTraining() {
		System.out.println("교육명을 입력하세요");
		String title=sc.nextLine();
		System.out.println("교육시행일을 입력하세요(yyyy-mm-dd 24:00)");
		String trainday=sc.nextLine();
		System.out.println("분류를 입력하세요");
		String edutype=sc.nextLine();
		System.out.println("최대 수강인원을 입력하세요");
		String maxcnt=sc.nextLine();
		System.out.println("강사를 입력하세요");
		String lecturer=sc.nextLine();

		try {
			con=DB.con();
			con.setAutoCommit(false);
			String sql="INSERT INTO training VALUES (origin.nextval||"+trainday.substring(2,6)+",'"+title
					+ "', to_date('"+trainday+"','yyyy-mm-dd HH24:MI'),'"+edutype+"',"+maxcnt+",0,'"+lecturer+"')";
			stmt=con.createStatement();
			System.out.print(stmt.executeUpdate(sql));
			con.commit();
			System.out.print("건 등록 완료\n");
     } catch (SQLException e) {
		System.out.println("SQL예외: "+e.getMessage());
		System.out.println("등록 실패");
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
	
	//교육리스트 검색
	public void searchTraining() {
		
		try {
			con=DB.con();
			String findsql="SELECT * FROM TRAINING WHERE trainday >= SYSDATE ";
			stmt=con.createStatement();
			rs=stmt.executeQuery(findsql);
			while(rs.next()) {
			System.out.println("교육번호\t교육명\t\t날짜      \t\t분류\t최대인원\t신청인원\t강사");
			System.out.println(rs.getString("LISTNUMBER")+"\t"+rs.getString("title")+"\t"+rs.getString("TRAINDAY")+"\t"+rs.getString("TYPE")+"\t"+rs.getString("MAXATTENDEE")+"\t"+rs.getString("CRTATTENDEE")+"\t"+rs.getString("LECTURER"));
			}
			
     } catch (SQLException e) {
		System.out.println("SQL예외: "+e.getMessage());
		}catch(Exception e) {
			System.out.println("일반예외:"+e.getMessage());
		}
		finally {
			DB.close(rs, stmt, con);
		}
	}
	//신청
	public void getTraining() {
		System.out.println("신청하려는 교육 번호를 입력하세요");
		String listnumber=sc.nextLine();
		String trainingsql="UPDATE TRAINING SET CRTATTENDEE = CRTATTENDEE + 1 WHERE LISTNUMBER = "+listnumber+" AND CRTATTENDEE < MAXATTENDEE ";
		String listsql="INSERT INTO ATTENDEELIST VALUES ("+listnumber+", "+Welcome1.user.getEmpno()+")";
		try {
			con=DB.con();
			con.setAutoCommit(false);
			stmt=con.createStatement();
			if(stmt.executeUpdate(trainingsql)+stmt.executeUpdate(listsql)==2) {
				System.out.print("교육 신청");
			}
			con.commit();
			System.out.print(" 완료\n");
     } catch (SQLException e) {
		System.out.println("SQL예외: "+e.getMessage());
		System.out.print("실패. 신청여부를 확인하세요 \t");
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
	
	//사람별 신청한 교육 리스트
	public void myTrain() {
		System.out.println("내가 신청한 교육");
		try {
			con=DB.con();
			String sql="SELECT * FROM training t, ATTENDEELIST a WHERE t.LISTNUMBER =a.LISTNUMBER  AND empno= "+Welcome1.user.getEmpno();
			stmt=con.createStatement();
			rs=stmt.executeQuery(sql);
			while(rs.next()) {
			System.out.println(rs.getString(1));
			}
			
     } catch (SQLException e) {
		System.out.println("SQL예외: "+e.getMessage());
		}catch(Exception e) {
			System.out.println("일반예외:"+e.getMessage());
		}
		finally {
			DB.close(rs, stmt, con);
		}
	
	}
	//교육별 신청한 사람들
	public void whoattend() {
		System.out.println("신청자를 확인할 교육 번호를 입력하세요");
		String listnumber=sc.nextLine();
		try {
			con=DB.con();
			String sql="SELECT a.empno, e.name, e.deptno, e.rank FROM training t,ATTENDEELIST a,employee e WHERE t.LISTNUMBER =a.LISTNUMBER AND e.EMPNO =a.EMPNO AND a.LISTNUMBER = "+listnumber;
			stmt=con.createStatement();
			rs=stmt.executeQuery(sql);
			while(rs.next()) {
			System.out.println(rs.getString(1));
			}
			
     } catch (SQLException e) {
		System.out.println("SQL예외: "+e.getMessage());
		}catch(Exception e) {
			System.out.println("일반예외:"+e.getMessage());
		}
		finally {
			DB.close(rs, stmt, con);
		}
	
	}
	
}
