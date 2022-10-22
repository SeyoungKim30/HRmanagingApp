package vos;

public class DayoffApply {
	int empno;
	String offuntil;
	int offduring;

	public DayoffApply() {
	}

	public DayoffApply(int empno, String offuntil, int offduring) {
		this.empno = empno;
		this.offuntil = offuntil;
		this.offduring = offduring;
	}

	public int getEmpno() {
		return empno;
	}

	public String getOffuntil() {
		String cut=offuntil.substring(0,10);
		return cut;
	}

	public int getOffduring() {
		return offduring;
	}

}
