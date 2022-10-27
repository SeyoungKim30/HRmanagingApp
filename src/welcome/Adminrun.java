package welcome;

import java.util.Scanner;

import benefits.BenefitDao;
import benefits.TrainingDao;
import hrs.A_newEmp;
import hrs.Appre;
import hrs.Deptinfo;
import hrs.Retiring;
import operplan.Analizing;
import operplan.HrplanDao;
import payment.ExtraDao;
import payment.Paystub;
import payment.Saldao;
import schedule.DayoffDao;
import schedule.SCDdao;
import vos.BudgetVo;
import vos.HrplanVo;
import vos.Retirement;

public class Adminrun {
	Scanner sc = new Scanner(System.in);
	int choose=0;
	
	public Adminrun() {
		while(true) {
		System.out.println("━─━─━─━─━─━─━─━─━─━─━─━─━─━─━─━─━─━─━─━─━─━─━─━─━─━─━─━─━─━─━─━─");
		System.out.println("1.인사관리  2.부서관리  3.근무일정 관리  4.급여관리  5.복지,교육  6.인사평가");
		System.out.println("7.계획/분석  8.퇴사관리 99. 종료");
		choose=sc.nextInt();	
		if(choose==99) break;
		switch(choose) {
		case 1:	adminHrs(); break;
		case 2: adminDept();break;
		case 3: adminSchedule(); break;
		case 4: adminPayment(); break;
		case 5: adminBene(); break;
		case 6: adminAppraisal(); break;
		case 7: adminOperation(); break;
		case 8: adminRetiring(); break;
		default: System.out.println("잘못된 번호가 입력되었습니다.");
		}
		}
	}

	public void adminBene() {		//5번 복지 교육
		TrainingDao tr=new TrainingDao();
		BenefitDao bn = new BenefitDao();
		System.out.println("1.교육신청  2.교육 검색  3.내가 신청한 교육");
		System.out.println("4.복지신청  5.복지 검색  6.내가 신청한 복지");
		System.out.println("11. 교육 입력  12.교육별 신청자 확인");
		System.out.println("13. 복지 입력 14.복지별 신청자 확인\n0.다른메뉴");
		choose=sc.nextInt();
		switch(choose) {
		case 1: tr.getTraining(); break;
		case 2: tr.searchTraining(); break;
		case 3: tr.myTrain(); break;
		case 4: bn.getBene(); break;
		case 5 : bn.searchBene(); break;
		case 6 : bn.myBene(); break;
		case 11: tr.insertTraining(); break;
		case 12: tr.whoattend(); break;
		case 13: bn.insertBene(); break;
		case 14: bn.whoattend(); break;
		default : break;
		}
	}
	
	public void adminHrs() {	//1번
		A_newEmp ne= new A_newEmp();
		Retiring rt=new Retiring();
		System.out.println("1.내 정보 수정  2.퇴사신청");
		System.out.println("11. 사원등록  12.사원검색  13. 사원정보 수정  14. 사원정보 삭제");
		System.out.println("15. 인사이동 등록 16.인사이동 내역 삭제\n0.다른메뉴");
		choose=sc.nextInt();
		switch(choose) {
		case 1: ne.myInfo(); break;
		case 2: rt.applyretire(); break;
		case 11: ne.registerEmp();break;
		case 12: ne.searchEmp(); break;
		case 13: ne.updateEmp(); break;
		case 14: ne.deleteEmp(); break;
		case 15: ne.insertHistory(); break;
		case 16: ne.delHistory(); break;
		default : break;
		}
	}
	public void adminAppraisal() {		//6번 인사평가
		Appre ap = new Appre();
		System.out.println("1.인사평가  2.나의 평가결과 조회  3.연도별 평가결과 조회");
		System.out.println("11.인사평가 문항 등록  12.개인별 평가결과 조회  13.조건별 평가결과 조회");
		System.out.println("0.다른메뉴");
		choose=sc.nextInt();
		switch(choose) {
		case 1: ap.insertAns(); break;
		case 2: ap.myAppre(); break;
		case 3: ap.myApprePast(); break;
		case 11: ap.insertQues(); break;
		case 12: ap.byQuebyEmp(); break;
		case 13: ap.byDept(); break;
		default : break;
		}
	}
	public void adminDept() {		//2번 부서관리
		Deptinfo dp=new Deptinfo();
		System.out.println("1.부서검색  11.신규 부서 등록  12.부서 삭제  13.부서명 변경 0.다른메뉴");
		choose=sc.nextInt();
		switch(choose) {
		case 1: dp.searchDept(); break;
		case 11: dp.insertDept(); break;
		case 12: dp.delDept(); break;
		case 13: dp.updateDname(); break;
		default : break;
		}
	}
	public void adminOperation() {		//7번 계획분석
		Analizing az=new Analizing();
		HrplanDao hp = new HrplanDao();
		System.out.println("11.조건별 인사평가 통계  12.조건별 퇴사분석  13.평균 퇴사분석");
		System.out.println("14.인사계획 입력  15.조건별 인사현황  16.부서별 인사현황");
		System.out.println("17.예산계획 입력  18.항목별 예산현황  19.부서별 예산현황  0.이전메뉴");
		choose=sc.nextInt();
		switch(choose) {
		case 11:az.showpointbyfilter(); 	break;
		case 12:az.retirementstudy1();	 break;
		case 13:az.retirementstudy2();	break;
		case 14: hp.inserthrplan();		break;
		case 15: hp.hrplanByRank(new HrplanVo()); break;
		case 16: hp.hrplanByDept(); break;
		case 17: hp.insertBudget(); break;
		case 18: hp.budgetByAccount(new BudgetVo()); break;
		case 19: hp.budgetByDept(); break;
		default : break;
		}
	}
	public void adminPayment() {		//4 급여관리
		Saldao sal=new Saldao();
		ExtraDao ext=new ExtraDao();
		Paystub stub=new Paystub();
		System.out.println("1.계좌정보 변경  2.수당신청  3.내 소득 조회 ");
		System.out.println("11.급여지급(전직원)  12.급여지급(부서별)  13.급여지급(개인별)");
		System.out.println("14.수당입력  15.수당신청 승인  16.수당 지급");
		System.out.println("17.개인별 소득조회  0.다른메뉴");
		choose=sc.nextInt();
		switch(choose) {
		case 1:sal.updateBankac(); break;
		case 2: ext.insertExtra(); break;
		case 3: stub.myMonthtotal(); break;
		case 11: sal.insertPayAll(); break;
		case 12: sal.insertPayDept(); break;
		case 13: sal.insertManual(); break;
		case 14: ext.insertExtraAdmin(); break;
		case 15: ext.stateConfirm();  break;
		case 16: ext.statePaid(); break;
		case 17 : stub.empMonthtotal(); break;
		default : break;
		}
		
	}
	public void adminSchedule() {	//3. 근무일정
		SCDdao sche=new SCDdao();
		DayoffDao doff= new DayoffDao();
		System.out.println("1.출근스케줄 확인  2.연차신청");
		System.out.println("11.출근스케줄 입력(부서별)  12.출근스케줄 입력(개인별)  13.개인별 근무기록  14.부서별 근무기록");
		System.out.println("15. 일자별 근무자(부서별)  16.일자별 근무자(전체)  17. 초과근무시간");
		System.out.println("18.연차초기화,수당지급  19.연차신청 승인  0.다른메뉴");
		choose=sc.nextInt();
		switch(choose) {
		case 1: sche.showMyScd(); break;
		case 2: doff.useDayoff(); break;
		case 11: sche.setScdbyDept(); break;
		case 12: sche.setScdbyEmp(); break;
		case 13: sche.showScdEmpMonth(); break;
		case 14: sche.showScdDeptMonth(); break;
		case 15: sche.showScdDeptDay();  break;
		case 16: sche.showScdDay(); break;
		case 17: sche.showOvertime(); break;
		case 18: doff.resetDayoff(); break;
		case 19: doff.newapply();  break;
		default : break;
		}
	}

	public void adminRetiring() {		//8번 퇴사관리
		Retiring rt=new Retiring();
		System.out.println("11.퇴사정보 입력  12.퇴사신청 조회  13.퇴사신청 상태변경  14.자동 퇴사처리  0.이전메뉴");
		choose=sc.nextInt();
		switch(choose) {
		case 11: rt.insertretire(); break;
		case 12: rt.selectApply(new Retirement()); break;
		case 13: rt.changeState(); break;
		case 14: rt.changeStateAuto(); break;
		default : break;
		}
	}

}
