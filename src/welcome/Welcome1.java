package welcome;

import java.util.Scanner;

import schedule.WorkTableDAO;
import vos.Employee;

public class Welcome1 {
	public static Employee user;

	public static void main(String[] args) {
	WorkTableDAO wtd= new WorkTableDAO();
	Login lg= new Login();
	if(user.getAccess())System.out.println("관리자권한이 확인되었습니다.");
	wtd.timeon();	//로그인하고 출근기록
	Scanner sc = new Scanner(System.in);
	
	if(!user.getAccess()) {
		Userrun ur=new Userrun();
		}
	if(user.getAccess()) {
		Adminrun ar=new Adminrun();
		}

	
	wtd.timeoff();//퇴근
	}

}

//인사평가, 예산계획
//2022101001
//12345