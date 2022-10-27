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
		String filter1="INSERT INTO ATTENDEELIST "
				+ "(SELECT ? ,empno FROM employee WHERE empno= ? ";
		
		String condi="";
		System.out.println("복지명을 입력하세요");
		String title=sc.nextLine();
		System.out.println("마감일을 입력하세요(yyyymmdd)");
		String reportDay=sc.nextLine();
		System.out.println("시행비용은 얼마인가요");
		String cost=sc.nextLine();
		
		String setcondition ="Y";
		while(true) {
		System.out.println("조건을 입력할까요(Y/N)");
		setcondition =sc.nextLine();	
		if(setcondition.equals("N")) break;
			
		System.out.println("번호를 입력하세요");
		System.out.println("1.부서조건  2.직급 조건  3. 나이 조건");
		setcondition=sc.nextLine();
		switch(setcondition) {
			case "1" : System.out.println("부서번호를 입력하세요");
				String deptset=sc.nextLine();
				condi+= "and (deptno = "+deptset;
				String additionaldeptno="0";
				while(!additionaldeptno.equals("N")){
				System.out.println("부서를 추가로 입력하려면 부서번호, 완료하려면 N을 입력하세요");
					additionaldeptno=sc.nextLine();
					if(additionaldeptno.equals("N")) break;
					condi+=" or deptno = "+additionaldeptno;
				}
				condi+= " )";
				continue;
				
			case "2" :System.out.println("직급을 입력하세요");
			String ranking=sc.nextLine();
			condi+= " and (RANK LIKE '%'||'"+ranking+"'||'%' ";
			String additionalranking="0";
			while(!additionalranking.equals("N")){
			System.out.println("추가로 입력하려면 직급명, 완료하려면 N을 입력하세요");
			additionalranking=sc.nextLine();
				if(additionalranking.equals("N")) break;
				condi+=" or RANK LIKE '%'||'"+additionalranking+"'||'%'";
			}
			condi+= " ) ";
			break;
				
			case "3" :
				System.out.println("나이를 입력하세요(최소, 최대)");
				String age1=sc.nextLine();
				String age2=sc.nextLine();
				condi+=" and (sysdate - birth)/365 >= "+age1+" AND (sysdate - birth)/365 <= "+age2;
				break;
			default :
				System.out.println("조건이 입력되지 않았습니다");
			}
		}
		
		System.out.println("입력할 조건: "+filter1+condi+")");
		try {
			con=DB.con();
			con.setAutoCommit(false);
			String sql="INSERT INTO benefit VALUES (origin.nextval||"+reportDay+"+20,' "
					+ title+"', q'[ "+filter1+condi+")]' , to_date('"+reportDay+"','yyyymmdd'), "+cost+")";
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
			String findsql="SELECT LISTNUMBER, title,condi,reportday,cost FROM benefit ";
			stmt=con.createStatement();
			rs=stmt.executeQuery(findsql);
			while(rs.next()) {
			Benefit bb= new Benefit(rs.getString("LISTNUMBER"),rs.getString("title"),rs.getString(3),rs.getString("reportday"),rs.getString("cost"));
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
		String insertsql="";
		try {
			con=DB.con();
			con.setAutoCommit(false);
			System.out.println("신청하려는 복지 번호를 입력하세요");
			String listnumber=sc.nextLine();
			String findsql="SELECT listnumber , CONDI FROM benefit WHERE LISTNUMBER = "+listnumber;
			stmt=con.createStatement();
			rs=stmt.executeQuery(findsql);
			while(rs.next()) {
				bb=new Benefit(rs.getString(1),rs.getString(2));
				insertsql=bb.getCondi();
			}
			pstmt=con.prepareStatement(insertsql);
			pstmt.setString(1,listnumber);
			pstmt.setInt(2,Welcome1.user.getEmpno());
			System.out.println(insertsql);
			System.out.print(pstmt.executeUpdate()+"건 신청");
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
	
	//내가 신청한 복지 리스트
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
