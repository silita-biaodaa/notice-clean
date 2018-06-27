package com.silita.biaodaa.model;

import java.io.Serializable;

public class Mo implements Serializable{

	private static final long serialVersionUID = 1L;
	protected String updater,creater;
	public static Integer PAGE_NUMBER=1;
	public static Integer PAGE_SIZE=20;
	protected Long id;
	protected Integer pageNumber=PAGE_NUMBER;// 当前第几页
	protected Integer pageSize = PAGE_SIZE;// 每页的数据行数，默认20条，系统参数
	protected Integer totalPages=0;
	protected Integer total=0;

	public String getUpdater() {
		return updater;
	}
	public void setUpdater(String updater) {
		this.updater = updater;
	}
	public String getCreater() {
		return creater;
	}
	public void setCreater(String creater) {
		this.creater = creater;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getPageSize() {
		return pageSize==null?PAGE_SIZE:pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}


	public Integer getPageNumber() {
		return pageNumber==null?1:pageNumber;
	}
	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}
	public Integer getTotalPages() {
		return totalPages;
	}
	public void setTotalPages(Integer totalPages) {
		this.totalPages = totalPages;
	}
	public Integer getTotal() {
		return total;
	}
	public void setTotal(Integer total) {
		this.total = total;
	}
	
}
