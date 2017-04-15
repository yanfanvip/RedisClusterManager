package com.newegg.redis.model;

import java.io.Serializable;

public class M_Slot implements Serializable{
	private static final long serialVersionUID = -3130817906319917413L;
	
	private Integer start;
	private Integer end;
	
	public Integer getStart() {
		return start;
	}
	public void setStart(Integer start) {
		this.start = start;
	}
	public Integer getEnd() {
		return end;
	}
	public void setEnd(Integer end) {
		this.end = end;
	}
	@Override
	public String toString() {
		return "{start : " + start + " , end : " + end + " }";
	}
}
