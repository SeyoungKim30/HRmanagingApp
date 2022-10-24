package vos;

public class BudgetVo {
//TYPE |초기예산  |예산결과  |초과비용 |사용률
	String year;
	String deptno;
	
	String sql1="SELECT B1.TYPE , 초기예산 , 예산결과 , 예산결과-초기예산 초과비용, NVL(ROUND(예산결과/초기예산*100),0)||'%' \"사용률\"\r\n"
			+ "FROM\r\n"
			+ "(SELECT \"TYPE\", SUM(BUDGET) 초기예산 FROM BUDGETPLAN WHERE 1=1";
	String sql2= " GROUP BY \"TYPE\") B1,\r\n"
			+ "(SELECT \"TYPE\", SUM(BUDGET) 예산결과 FROM BUDGETPLAN WHERE 1=1";
	String sql3	= " GROUP BY \"TYPE\") B2 WHERE B1.TYPE=B2.TYPE";
	
	int yearfilter=0;
	int deptfilter=0;
	
	public BudgetVo() { }


	public BudgetVo(String year, String deptno) {
		this.year = year;
		this.deptno = deptno;
	}


	public String getYear() {
		return year;
	}


	public void setYear(String year) {
		this.year = year;
		yearfilter++;
		sql1+=" And YEAR=to_date('"+year+"-01-01','yyyy-MM-DD')";
		sql2+=" AND to_CHAR(YEAR,'yyyy') LIKE " + year;
	}


	public String getDeptno() {
		return deptno;
	}


	public void setDeptno(String deptno) {
		this.deptno = deptno;
		deptfilter++;
		sql1+=" AND DEPTNO LIKE '%'||'"+deptno+"'||'%'  ";
		sql2+=" AND DEPTNO LIKE '%'||'"+deptno+"'||'%'  ";
	}
	
	public String getSql() {
		return sql1+sql2+sql3;
	}

	
}
