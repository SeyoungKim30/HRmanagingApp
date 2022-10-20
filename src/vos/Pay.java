package vos;
/*
SELECT empno,to_char(payday,'YYYY'),to_char(payday,'mm'),salary FROM pay WHERE empno =2022101001 ;
SELECT empno,to_char(payday,'YYYY'),to_char(payday,'mm'),amount FROM extrapay WHERE empno =2022101001 AND payday IS NOT NULL;
 * 
 * */
public class Pay {
	int empno;
	String month;
	String year;
	int amount;
	String cate;
	
	public Pay() {	}

	public Pay(int empno, String month, String year, int amount, String cate) {
		this.empno = empno;
		this.month = month;
		this.year = year;
		this.amount = amount;
		this.cate = cate;
	}
	
	public int getEmpno() {
		return empno;
	}

	public String getMonth() {
		return month;
	}

	public String getYear() {
		return year;
	}

	public int getAmount() {
		return amount;
	}

	public String getCate() {
		return cate;
	}


	
	
}
