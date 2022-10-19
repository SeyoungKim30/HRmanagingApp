package vos;

public class Retirement {
	private String empno;
	private String name;
	private String deptno;
	private String rank;
	private String retireyear;
	private String reason;
	private String state;
	String searchline = "SELECT e.empno,name,deptno,RANK, retireday,reason, state FROM retirement r, EMPLOYEE e  WHERE r.EMPNO =e.EMPNO ";
	int howmanyquestion=0;
	int noEmpno=0;
	int noName=0;
	int noDeptno=0;
	int noRank=0;
	int noRetireyear=0;
	int noState=0;
	
	public Retirement() {}

	public Retirement(String empno, String name, String deptno, String rank, String retireyear, String reason,
			String state) {
		this.empno = empno;
		this.name = name;
		this.deptno = deptno;
		this.rank = rank;
		this.retireyear = retireyear;
		this.reason = reason;
		this.state = state;
	}

	public String getEmpno() {
		return empno;
	}
	
	public void printAll() {
		System.out.println("━━━━━━━━━━━━━━━━━");
		System.out.println("사원번호:\\t"+empno);
		System.out.println("이름:\\t"+name);
		System.out.println("부서번호:\\t"+deptno);
		System.out.println("직급:\\t"+rank);
		System.out.println("퇴직일:\\t"+retireyear);
		System.out.println("사유:\\t"+reason);
		System.out.println("처리상태:\\t"+state);
		
	}
	
	
	public void setEmpno(String empno) {
		this.empno = empno;
		searchline+="AND e.empno like '%'||?||'%' ";
		noEmpno=++howmanyquestion;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		searchline+="AND name like '%'||?||'%' ";
		noName=++howmanyquestion;
	}

	public String getDeptno() {
		return deptno;
	}

	public void setDeptno(String deptno) {
		this.deptno = deptno;
		searchline+="AND deptno like '%'||?||'%' ";
		noDeptno=++howmanyquestion;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
		searchline+="AND e.rank like '%'||?||'%' ";
		noRank=++howmanyquestion;
	}

	public String getRetireyear() {
		return retireyear;
	}

	public void setRetireyear(String retireyear) {
		this.retireyear = retireyear;
		searchline+="AND to_char(retireday,'yyyy') like '%'||?||'%' ";
		noRetireyear=++howmanyquestion;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
		searchline+="AND state like '%'||?||'%' ";
		noState=++howmanyquestion;
	}
	public String getSearchline() {
		return searchline;
	}

	public int getHowmanyquestion() {
		return howmanyquestion;
	}

	public void setHowmanyquestion(int howmanyquestion) {
		this.howmanyquestion = howmanyquestion;
	}

	public int getNoEmpno() {
		return noEmpno;
	}

	public void setNoEmpno(int noEmpno) {
		this.noEmpno = noEmpno;
	}

	public int getNoName() {
		return noName;
	}

	public void setNoName(int noName) {
		this.noName = noName;
	}

	public int getNoDeptno() {
		return noDeptno;
	}

	public void setNoDeptno(int noDeptno) {
		this.noDeptno = noDeptno;
	}

	public int getNoRank() {
		return noRank;
	}

	public void setNoRank(int noRank) {
		this.noRank = noRank;
	}

	public int getNoRetireyear() {
		return noRetireyear;
	}

	public void setNoRetireyear(int noRetireyear) {
		this.noRetireyear = noRetireyear;
	}

	public int getNoState() {
		return noState;
	}

	public void setNoState(int noState) {
		this.noState = noState;
	}


	
}
