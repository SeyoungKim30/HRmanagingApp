package vos;

public class HrplanVo {
//DNAME|RANKER|CNT|사람수|달성률 |상태|
	String dname;
	String ranker;
	String plancnt;
	String realcnt;
	String reaching;
	String state;
	String deptno;
	int filterindex=0;
	int deptfilter=0;
	int rankfilter=0;
	String search1="SELECT dname, h.ranker , h.cnt, d.사람수 , ROUND(nvl(사람수/cnt*100,0))||'%' 달성률 , \r\n"
			+ "	CASE WHEN nvl(사람수/cnt*100,0)<90 THEN '미달'\r\n"
			+ "		WHEN nvl(사람수/cnt*100,0) <110 THEN '적정'\r\n"
			+ "		ELSE '초과' END AS 상태\r\n"
			+ "from hrplan h , (SELECT d.deptno \"부서번호별\", COUNT(*) \"사람수\"  FROM EMPLOYEE e , DEPARTMENT d \r\n"
			+ "				WHERE e.DEPTNO =d.deptno ";

	String search2=	 "			GROUP BY d.DEPTNO) d,\r\n"
			+ "	department dp\r\n"
			+ "WHERE h.deptno = d.부서번호별 AND dp.deptno=h.deptno AND TO_CHAR(YEAR,'yyyy') = TO_CHAR(SYSDATE,'yyyy')";
	
	
	public HrplanVo() {}
	
	public HrplanVo(String dname, String ranker, String plancnt, String realcnt, String reaching, String state, String searchSql) {
		this.dname = dname;
		this.ranker = ranker;
		this.plancnt = plancnt;
		this.realcnt = realcnt;
		this.reaching = reaching;
		this.state = state;
		this.search1 = searchSql;
	}

	public String getDname() {
		return dname;
	}

	public void setDname(String dname) {
		this.dname = dname;
	}

	public String getRanker() {
		return ranker;
	}

	public void setRanker(String ranker) {
		this.ranker = ranker;
		search1+=" AND \"RANK\" LIKE '%'||'"+ranker+"'||'%' ";
		search2+=" AND  h.ranker LIKE '%'||'"+ranker+"'||'%' ";	
		filterindex++;
		rankfilter=filterindex;
	}

	public String getDeptno() {
		return deptno;
	}

	public void setDeptno(String deptno) {
		this.deptno = deptno;
		search1+=" AND e.deptno LIKE '%'||"+deptno+"||'%' ";
		search2+=" AND h.deptno LIKE '%'||"+deptno+"||'%' ";
		filterindex++;
		deptfilter=filterindex;
	}

	public String getPlancnt() {
		return plancnt;
	}

	public void setPlancnt(String plancnt) {
		this.plancnt = plancnt;
	}

	public String getRealcnt() {
		return realcnt;
	}

	public void setRealcnt(String realcnt) {
		this.realcnt = realcnt;
	}

	public String getReaching() {
		return reaching;
	}

	public void setReaching(String reaching) {
		this.reaching = reaching;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getSearch1() {
		return search1;
	}

	public String getSearch2() {
		return search2;
	}

	public int getDeptfilter() {
		return deptfilter;
	}

	public int getRankfilter() {
		return rankfilter;
	}

	public int getFilterindex() {
		return filterindex;
	}
	


	
	
	
}