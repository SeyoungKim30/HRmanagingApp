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

public class BenefitDao {

	private Connection con;
	private Statement stmt;
	private PreparedStatement pstmt;
	private ResultSet rs;
	Scanner sc = new Scanner(System.in);
	
	//입력
	public void insertBene() {
		System.out.println("복지명을 입력하세요");
		String title=sc.nextLine();
		System.out.println("직급 조건을 입력하세요(조건이 없으면 X 입력)");
		String condi1=sc.nextLine();
		System.out.println("부서 조건을 입력하세요(조건이 없으면 X 입력)");
		String condi2=sc.nextLine();
		System.out.println("마감일을 입력하세요(yyyymmdd)");
		String reportDay=sc.nextLine();
		System.out.println("시행비용은 얼마인가요");
		String cost=sc.nextLine();
		if(condi1.equals("X")) condi1="'||NULL||'";
		if(condi2.equals("X")) condi2="'||NULL||'";

		try {
			con=DB.con();
			con.setAutoCommit(false);
			String sql="INSERT INTO benefit VALUES (origin.nextval||"+reportDay+"+20,' "
					+ title+"', '"+condi1+"' ,'"+condi2+"' , to_date('"+reportDay+"','yyyymmdd'), "+cost+")";
			stmt=con.createStatement();
			stmt.executeUpdate(sql);
			con.commit();
     } catch (SQLException e) {
		System.out.println("SQL예외: "+e.getMessage());
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
	
	//복지리스트 검색
	public void searchBene() {
		List<Benefit> blist=new ArrayList<Benefit>();
		try {
			con=DB.con();
			String findsql="SELECT LISTNUMBER, title,condi1,nvl(condi2,'조건없음'),reportday,cost FROM benefit ";
			stmt=con.createStatement();
			rs=stmt.executeQuery(findsql);
			while(rs.next()) {
			Benefit bb= new Benefit(rs.getString("LISTNUMBER"),rs.getString("title"),rs.getString(3),rs.getString(4),rs.getString("reportday"),rs.getString("cost"));
			blist.add(bb);
			}
			
     } catch (SQLException e) {
		System.out.println("SQL예외: "+e.getMessage());
		}catch(Exception e) {
			System.out.println("일반예외:"+e.getMessage());
		}
		finally {
			DB.close(rs, stmt, con);
		}
		for(Benefit b:blist) {
			b.printAll();
		}
	}
	//신청
	public void getBene() {
		Benefit bb;
		String getsql="";
		try {
			con=DB.con();
			con.setAutoCommit(false);
			System.out.println("신청하려는 복지 번호를 입력하세요");
			String listnumber=sc.nextLine();
			String findsql="SELECT title,nvl(condi1,'X'),nvl(condi2,'X'),reportday,cost FROM benefit where listnumber = "+listnumber;
			stmt=con.createStatement();
			rs=stmt.executeQuery(findsql);
			while(rs.next()) {
			bb= new Benefit(rs.getString("title"),rs.getString(2),rs.getString(3),rs.getString("reportday"),rs.getString("cost"));
			getsql="INSERT INTO ATTENDEELIST \r\n"
					+ "	(SELECT "+listnumber+" , "+Welcome1.user.getEmpno()+" FROM employee \r\n"
					+ "	WHERE EMPNO ="+Welcome1.user.getEmpno()+" AND \"RANK\" LIKE '%'||'"+bb.getCondi1()+"'||'%' AND deptno LIKE '%'||'"+bb.getCondi2()+"'||'%')";
			}
			
			System.out.print(stmt.executeUpdate(getsql)+"신청");
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
	
	//사람별 신청한 복지 리스트
	public void myBene() {
		try {
			con=DB.con();
			String sql="SELECT title FROM benefit b , ATTENDEELIST a WHERE a.LISTNUMBER =b.listnumber AND a.empno = "+Welcome1.user.getEmpno();
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
	//복지별 신청한 사람들
	public void whoattend() {
		System.out.println("신청자를 확인할 복지 등록번호를 입력하세요");
		String listnumber=sc.nextLine();
		try {
			con=DB.con();
			String sql="SELECT name FROM benefit b , ATTENDEELIST a,employee e \r\n"
					+ "WHERE a.LISTNUMBER =b.listnumber AND e.EMPNO =a.EMPNO  AND a.listnumber= "+listnumber;
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
