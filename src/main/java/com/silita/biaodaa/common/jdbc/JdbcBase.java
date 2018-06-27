package com.silita.biaodaa.common.jdbc;

import com.silita.biaodaa.model.Mo;
import com.silita.biaodaa.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DaoSupport;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.SQLExceptionTranslator;

import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class JdbcBase extends DaoSupport {

	private JdbcTemplate jdbcTemplate;
	private static Object index_lock=new Object();
	private static Object sequence_lock=new Object();
//	@Autowired
	private HttpSession session;  
	/**
	 * Set the JDBC DataSource to be used by this DAO.
	 */
	@Autowired
	public void setDataSource(DataSource dataSource) {
		if (this.jdbcTemplate == null || dataSource != this.jdbcTemplate.getDataSource()) {
			this.jdbcTemplate = createJdbcTemplate(dataSource);
			initTemplateConfig();
		}
	}

	public User user(){
		return (User) session.getAttribute(User.USER_KEY);
	}
	
	public Page queryForPage(String sql, List<String> params, int pageNumber,
			int pageSize, Class<?> cls) {
		Page page = new Page();
		String count="SELECT COUNT(1) num FROM (" + sql + ") r";
		int total = this.getJdbcTemplate().queryForObject(
				count,
				params.toArray(new Object[params.size()]),Integer.class);
		page.setTotal(total);
		page.setTotalPages(((int) total / pageSize) + 1);
		page.setPageNumber(pageNumber);
		page.setPageSize(pageSize);
		
		sql += " LIMIT " + (pageNumber-1) * pageSize+ ","+pageSize+" ";
		page.setRows(this.getJdbcTemplate().query(sql,
				params.toArray(new Object[params.size()]),
				MapperFactory.newInstance(cls)));

		return page;
	}
	/**
	 * 使用注意:第一个单词必须椒大写的SELECT
	 * @param sql
	 * @param statisticsSql
	 * @param params
	 * @param pageNumber
	 * @param pageSize
	 * @param cls
	 * @return
	 */
	public Page queryForPage(String sql,String statisticsSql, List<String> params, int pageNumber,
			int pageSize, Class<?> cls) {
		Page page = new Page();
		statisticsSql="SELECT COUNT(1) total,"+statisticsSql+" FROM (" + sql + ") r";
		//statisticsSql=sql.replaceFirst("SELECT", "SELECT count(1) total,"+statisticsSql+",");
		Map<String,Object> totalResult = this.getJdbcTemplate().queryForMap(
				statisticsSql,
				params.toArray(new Object[params.size()]));
		page.setStatistics(totalResult);
		
		Integer total=Integer.valueOf(String.valueOf(totalResult.get("total")));
		page.setTotal(total);
		page.setTotalPages((total / pageSize) + 1);
		page.setPageNumber(pageNumber);
		page.setPageSize(pageSize);
		
		sql += " LIMIT " + (pageNumber-1) * pageSize+ ","+pageSize+" ";
		page.setRows(this.getJdbcTemplate().query(sql,
				params.toArray(new Object[params.size()]),
				MapperFactory.newInstance(cls)));

		return page;
	}
	/**
	 * 使用注意:第一个单词必须椒大写的SELECT
	 * @param sql
	 * @param totalSql
	 * @param params
	 * @param pageNumber
	 * @param pageSize
	 * @param cls
	 * @return
	 */
	public Page queryForPageAndTotal(String sql,String totalSql, List<String> params, int pageNumber,
			int pageSize, Class<?> cls) {
		Page page = new Page();
		//statisticsSql="SELECT COUNT(1) total,"+statisticsSql+" FROM (" + sql + ") r";
		//statisticsSql=sql.replaceFirst("SELECT", "SELECT count(1) total,"+statisticsSql+",");
		Map<String,Object> totalResult = this.getJdbcTemplate().queryForMap(
				totalSql,
				params.toArray(new Object[params.size()]));
		page.setStatistics(totalResult);
		
		Integer total=Integer.valueOf(String.valueOf(totalResult.get("total")));
		page.setTotal(total);
		page.setTotalPages((total / pageSize) + 1);
		page.setPageNumber(pageNumber);
		page.setPageSize(pageSize);
		
		sql += " LIMIT " + (pageNumber-1) * pageSize+ ","+pageSize+" ";
		page.setRows(this.getJdbcTemplate().query(sql,
				params.toArray(new Object[params.size()]),
				MapperFactory.newInstance(cls)));

		return page;
	}
	/**
	 * 使用注意:第一个单词必须椒大写的SELECT
	 * @param sql
	 * @param statisticsSql
	 * @param params
	 * @param pageNumber
	 * @param pageSize
	 * @param cls
	 * @return
	 */
	public Page queryForPage(String sql,String statisticsSql,  Object[] params, int pageNumber,
			int pageSize, Class<?> cls) {
		Page page = new Page();
		statisticsSql="SELECT COUNT(1) total,"+statisticsSql+" FROM (" + sql + ") r";
		//statisticsSql=sql.replaceFirst("SELECT", "SELECT count(1) total,"+statisticsSql+",");
		Map<String,Object> totalResult = this.getJdbcTemplate().queryForMap(
				statisticsSql,
				params);
		page.setStatistics(totalResult);
		
		Integer total=Integer.valueOf(String.valueOf(totalResult.get("total")));
		page.setTotal(total);
		page.setTotalPages((total / pageSize) + 1);
		page.setPageNumber(pageNumber);
		page.setPageSize(pageSize);
		
		sql += " LIMIT " + (pageNumber-1) * pageSize+ ","+pageSize+" ";
		page.setRows(this.getJdbcTemplate().query(sql,
				params,
				MapperFactory.newInstance(cls)));

		return page;
	}
	public Page queryForPage(String sql, Object[] params, int pageNumber,
			int pageSize, Class<?> cls) {
		Page page = new Page();
		String count="SELECT COUNT(1) num FROM (" + sql + ") r";
		int total = this.getJdbcTemplate().queryForObject(
				count,
				params,Integer.class);
		page.setTotal(total);
		page.setTotalPages(((int) total / pageSize) + 1);
		page.setPageNumber(pageNumber);
		page.setPageSize(pageSize);
		
		sql += " LIMIT " + (pageNumber-1) * pageSize + ","+pageSize+" ";
		page.setRows(this.getJdbcTemplate().query(sql,
				params,
				MapperFactory.newInstance(cls)));
		
		return page;
	}

	
	public Page queryForPage(String sql,String statisticsSql, List<String> params, Object obj) {
		return queryForPage(sql,statisticsSql,params,((Mo)obj).getPageNumber(),((Mo)obj).getPageSize(),obj.getClass());
	}
	public Page queryForPage(String sql, List<String> params, Object obj) {
		return queryForPage(sql,params,((Mo)obj).getPageNumber(),((Mo)obj).getPageSize(),obj.getClass());
	}
	
	public Page queryForPage(String sql,String statisticsSql, Object[] params, Object obj) {
		return queryForPage(sql,statisticsSql,params,((Mo)obj).getPageNumber(),((Mo)obj).getPageSize(),obj.getClass());
	}
	
	public Page queryForPage(String sql,Object[] params, Object obj) {
		return queryForPage(sql,params,((Page)obj).getPageNumber(),((Page)obj).getPageSize(),obj.getClass());
	}
	
	public Page queryForPage(String sql,int currentPage,
			int pageSize, Class<?> cls) {
		return queryForPage(sql,currentPage,pageSize,cls);
	}
	
	public <T> List<T> querys(String sql,Object[] args,Class<T> cls){
		return  this.getJdbcTemplate().query(sql, args, MapperFactory.newInstance(cls));
	}
	public <T> List<T> querys(String sql,List<String> params,Class<T> cls){
		return  this.getJdbcTemplate().query(sql,  params.toArray(new Object[params.size()]), MapperFactory.newInstance(cls));
	}
	public <T> T query(String sql,Object[] args,Class<T> cls){
	    List<T> list=this.querys(sql, args, cls);
	    if(list.size()>0){
	    	return this.querys(sql, args, cls).get(0);
	    }else{
	    	return null;
	    }
	}
	public Long getTableNextId(String clolumnName, String table) {
		synchronized(index_lock){
			List<Map<String, Object>> ls = this.getJdbcTemplate().queryForList(
					"SHOW TABLE STATUS WHERE NAME= ?", new Object[] { table });
			Long index = null ;
			if (ls.size() == 1) {
				Map m = ls.get(0);
				index = Long.valueOf((m.get("Auto_increment").toString()));
			}
			return index;
		}
	}
	
    /**
     * 精确到月
     * @param str
     * @return
     */
	public String queryNextWillNo(String str) {
		String sql = "SELECT willnextval(?)";
		return this.getJdbcTemplate().queryForObject(sql, new Object[] { str },
				String.class);
	}
    /**
     * 精确到月
     * @param str
     * @return
     */
	public String queryNextNo(String str) {
		synchronized(sequence_lock){
			String sql = "SELECT nextval(?)";
			return this.getJdbcTemplate().queryForObject(sql, new Object[] { str },
					String.class);
		}
	}
	/**
	 * 精确到日
	 * @param str
	 * @return
	 */
	public String queryNextWillNoDay(String str) {

		return this.willnextvalDay(str);
	}

	private String currval(String seqName){
		String sql=" SELECT CONCAT(pre_string,SUBSTRING( DATE_FORMAT(CURDATE(), '%Y%m%d%H'),3,4),LPAD(current_value,digit,'0')) "+
        " FROM sequence  WHERE NAME = '"+seqName+"'";
		return this.getJdbcTemplate().queryForObject(sql, String.class);
	}
	private String willnextval(String seqName){
		String sql=" SELECT CONCAT(pre_string,SUBSTRING( DATE_FORMAT(CURDATE(), '%Y%m%d%H'),3,4),LPAD(current_value+1,digit,'0')) "+
		" FROM sequence  WHERE NAME = '"+seqName+"'";
		return this.getJdbcTemplate().queryForObject(sql, String.class);
	}

	private String nextval(String seqName){
		String sql=" UPDATE sequence SET current_value = current_value+increment WHERE NAME = '"+seqName+"'";
		this.getJdbcTemplate().update(sql);
		return currval(seqName);
	}
	private String nextvalDay(String seqName){
		String sql=" UPDATE sequence SET current_value = current_value+increment WHERE NAME = '"+seqName+"'";
		this.getJdbcTemplate().update(sql);
		return currvalDay(seqName);
	}

	private String setval(String seqName,int value){
		String sql=" UPDATE sequence SET current_value = '"+value+"' WHERE NAME = '"+seqName+"';";
		this.getJdbcTemplate().update(sql);
		return currval(seqName);
	}

	private String currvalDay(String seqName){
		String sql=" SELECT CONCAT(pre_string,SUBSTRING( DATE_FORMAT(CURDATE(), '%Y%m%d%H'),3,6),LPAD(current_value,digit,'0')) "+
        " FROM sequence  WHERE NAME = '"+seqName+"'";
		return this.getJdbcTemplate().queryForObject(sql, String.class);
	}

	private String willnextvalDay(String seqName){
		String sql=" SELECT CONCAT(pre_string,SUBSTRING( DATE_FORMAT(CURDATE(), '%Y%m%d%H'),3,6),LPAD(current_value+1,digit,'0')) "+
		" FROM sequence  WHERE NAME = '"+seqName+"'";
		return this.getJdbcTemplate().queryForObject(sql, String.class);
	}

	public String getDate(){
		SimpleDateFormat fomat=new SimpleDateFormat("yyyy-MM-dd");
		return fomat.format(new Date());
	}

	public String getDatetime(){
		SimpleDateFormat fomat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return fomat.format(new Date());
	}

	/**
	 * Create a JdbcTemplate for the given DataSource.
	 * Only invoked if populating the DAO with a DataSource reference!
	 * <p>Can be overridden in subclasses to provide a JdbcTemplate instance
	 * with different configuration, or a custom JdbcTemplate subclass.
	 * @param dataSource the JDBC DataSource to create a JdbcTemplate for
	 * @return the new JdbcTemplate instance
	 * @see #setDataSource
	 */
	protected JdbcTemplate createJdbcTemplate(DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}

	/**
	 * Return the JDBC DataSource used by this DAO.
	 */
	public final DataSource getDataSource() {
		return (this.jdbcTemplate != null ? this.jdbcTemplate.getDataSource() : null);
	}

	/**
	 * Set the JdbcTemplate for this DAO explicitly,
	 * as an alternative to specifying a DataSource.
	 */
	public final void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
		initTemplateConfig();
	}

	/**
	 * Return the JdbcTemplate for this DAO,
	 * pre-initialized with the DataSource or set explicitly.
	 */
	public final JdbcTemplate getJdbcTemplate() {
	  return this.jdbcTemplate;
	}

	/**
	 * Initialize the template-based configuration of this DAO.
	 * Called after a new JdbcTemplate has been set, either directly
	 * or through a DataSource.
	 * <p>This implementation is empty. Subclasses may override this
	 * to configure further objects based on the JdbcTemplate.
	 * @see #getJdbcTemplate()
	 */
	protected void initTemplateConfig() {
	}

	@Override
	protected void checkDaoConfig() {
		if (this.jdbcTemplate == null) {
			throw new IllegalArgumentException("'dataSource' or 'jdbcTemplate' is required");
		}
	}


	/**
	 * Return the SQLExceptionTranslator of this DAO's JdbcTemplate,
	 * for translating SQLExceptions in custom JDBC access code.
	 * @see org.springframework.jdbc.core.JdbcTemplate#getExceptionTranslator()
	 */
	protected final SQLExceptionTranslator getExceptionTranslator() {
		return getJdbcTemplate().getExceptionTranslator();
	}

	/**
	 * Get a JDBC Connection, either from the current transaction or a new one.
	 * @return the JDBC Connection
	 * @throws CannotGetJdbcConnectionException if the attempt to get a Connection failed
	 * @see org.springframework.jdbc.datasource.DataSourceUtils#getConnection(DataSource)
	 */
	protected final Connection getConnection() throws CannotGetJdbcConnectionException {
		return DataSourceUtils.getConnection(getDataSource());
	}

	/**
	 * Close the given JDBC Connection, created via this DAO's DataSource,
	 * if it isn't bound to the thread.
	 * @param con Connection to close
	 * @see org.springframework.jdbc.datasource.DataSourceUtils#releaseConnection
	 */
	protected final void releaseConnection(Connection con) {
		DataSourceUtils.releaseConnection(con, getDataSource());
	}
	
	protected BigDecimal getRMB(BigDecimal charge,String type) {
		if(null==charge||null==type||"".equals(charge)||"".equals(type)){
			return null;
		}
		String sql="SELECT * FROM exchange e,(SELECT MAX(DATETIME) dk FROM exchange) k WHERE e.datetime=k.dk";
		Map<String,Object> m=this.getJdbcTemplate().queryForMap(sql);
		BigDecimal  src=new BigDecimal(String.valueOf(m.get("rmb")));
		BigDecimal  target=new BigDecimal(String.valueOf(m.get(type.toLowerCase())));
		return target.divide(src,4, BigDecimal.ROUND_HALF_UP).multiply(charge).setScale(4, BigDecimal.ROUND_HALF_UP); 
	}
	
	protected boolean vertify(String str) {
		return null==str||"".equals(str)?false:true;
	}
	protected boolean vertify(Integer str) {
		return null==str?false:true;
	}
	
	protected boolean vertify(Long str) {
		return null==str?false:true;
	
	}
	protected boolean vertify(BigDecimal str) {
		if(null==str){
			return false;
		}
		BigDecimal mid=str.setScale(4, BigDecimal.ROUND_HALF_UP);
		return 0==mid.doubleValue()?false:true;
	}
	
	protected boolean vertify(Date str) {
		return null==str?false:true;
	}
	public int queryForInt(String var1, Object[] var2){
		return this.getJdbcTemplate().queryForObject(var1,var2,Integer.class);
	}
	public int queryForInt(String var1){
		return this.getJdbcTemplate().queryForObject(var1,new Object[]{},Integer.class);
	}
}
