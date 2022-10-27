package welcome;

import java.util.Scanner;

import benefits.BenefitDao;
import benefits.TrainingDao;
import hrs.A_newEmp;
import hrs.Appre;
import hrs.Deptinfo;
import hrs.Retiring;
import payment.ExtraDao;
import payment.Paystub;
import payment.Saldao;
import schedule.DayoffDao;
import schedule.SCDdao;

public class Userrun {
	Scanner sc = new Scanner(System.in);
	int choose=0;
	
	
	public Userrun() {
	while(true) {	
		System.out.println("1.사원정보  2.부서정보  3.근무일정  4.급여  5.복지,교육 6.인사평가  99.종료");
		choose=sc.nextInt();	
		if(choose==99) break;
		switch(choose) {
			case 1: userHrs(); break;
			case 2: userDept(); break;
			case 3: userSchedule(); break;
			case 4: userPayment(); break;
			case 5: userBenefits(); break;
			case 6: userAppraisal(); break;
			default: System.out.println("잘못된 번호가 입력되었습니다.");
			}
		}
	}
	
	public void userHrs() {		//1 사원정보, 퇴사신청
		A_newEmp ne= new A_newEmp();
		Retiring rt=new Retiring();
		System.out.println("1.내 정보 수정  2.퇴사신청");
		choose=sc.nextInt();
		switch(choose) {
		case 1: ne.myInfo(); break;
		case 2: rt.applyretire(); break;
		default : break;
		}
	}
	public void userBenefits() {	//5 복지교육
		TrainingDao tr=new TrainingDao();
		BenefitDao bn = new BenefitDao();
		
		System.out.println("1.교육신청  2.교육 검색  3.내가 신청한 교육");
		System.out.println("4.복지신청  5.복지 검색  6.내가 신청한 복지\n0.다른메뉴");
		choose=sc.nextInt();
		switch(choose) {
		case 1: tr.getTraining(); break;
		case 2: tr.searchTraining(); break;
		case 3: tr.myTrain(); break;
		case 4: bn.getBene(); break;
		case 5 : bn.searchBene(); break;
		case 6 : bn.myBene(); break;
		default : break;
		}
	}
	
	public void userAppraisal() {	//6 인사평가
		Appre ap = new Appre();
		System.out.println("1.인사평가  2.나의 평가결과 조회  3.연도별 평가결과 조회");
		System.out.println("0.다른메뉴");
		choose=sc.nextInt();
		switch(choose) {
		case 1: ap.insertAns(); break;
		case 2: ap.myAppre(); break;
		case 3: ap.myApprePast(); break;
		default : break;
		}
	}
	
	public void userDept() {		//2 부서검색
		Deptinfo dp=new Deptinfo();
		dp.searchDept();
	}

	public void userPayment() {		//4. 급여
		Saldao sal=new Saldao();
		ExtraDao ext=new ExtraDao();
		Paystub stub=new Paystub();
		System.out.println("1.계좌정보 변경  2.수당신청  3.소득 조회  0.다른메뉴");
		choose=sc.nextInt();
		switch(choose) {
		case 1:sal.updateBankac(); break;
		case 2: ext.insertExtra(); break;
		case 3: stub.myMonthtotal(); break;
		default : break;
		}
		
	}
	public void userSchedule() {	//3. 근무일정
		SCDdao sche=new SCDdao();
		DayoffDao doff= new DayoffDao();
		System.out.println("1.출근스케줄 확인  2.연차신청  0.다른메뉴");
		choose=sc.nextInt();
		switch(choose) {
		case 1: sche.showMyScd(); break;
		case 2: doff.useDayoff(); break;
		default : break;
		}
	}


}
