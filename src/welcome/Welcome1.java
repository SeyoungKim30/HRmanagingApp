package welcome;

import hrs.A_newEmp;
import hrs.Appre;
import hrs.Retiring;
import payment.Saldao;
import vos.Employee;

public class Welcome1 {
	public static Employee user;
	
	public static void main(String[] args) {
		Login lg=new Login();
	//	lg.login();
	//	System.out.println("관리자 권한: "+user.getAccess());
		Appre ap=new Appre();
		A_newEmp emp=new A_newEmp();
//		emp.registerEmp();
//		ap.insertAns();
//		ap.myAppre();
//		ap.myApprePast();
//		ap.byQuebyEmp();
//		ap.byDept();
		Retiring rt=new Retiring();
	//	rt.applyretire();
	//	rt.insertretire();
	///	rt.selectApply(new Retirement());
	//	rt.changeState();
		Saldao sa=new Saldao();
		sa.insertPayDept();
		
		
	}

}
