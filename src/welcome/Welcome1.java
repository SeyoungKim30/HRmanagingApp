package welcome;

import java.text.SimpleDateFormat;
import java.util.Date;

import hrs.A_newEmp;
import hrs.Appre;
import vos.Employee;

public class Welcome1 {
	public static Employee user;
	
	public static void main(String[] args) {
		Login lg=new Login();
		lg.login();
		System.out.println("관리자 권한: "+user.getAccess());
		Appre ap=new Appre();
		A_newEmp emp=new A_newEmp();
//		emp.registerEmp();
		ap.insertAns();
		
	}

}
