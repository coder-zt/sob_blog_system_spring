package com.zhangtao.blog.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table ( name ="tb_refreshtoken" )
public class RefreshToken {

  	@Id
	@Column(name = "id")
	private String id;
  	@Column(name = "refresh_token" )
	private String refresh_token;
  	@Column(name = "user_id" )
	private String user_id;
  	@Column(name = "mobile_token_key" )
	private String mobile_token_key;
  	@Column(name = "token_key" )
	private String token_key;
  	@Column(name = "create_time" )
	private java.sql.Timestamp create_time;
  	@Column(name = "update_time" )
	private java.sql.Timestamp update_time;


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	public String getRefresh_token() {
		return refresh_token;
	}

	public void setRefresh_token(String refresh_token) {
		this.refresh_token = refresh_token;
	}


	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}


	public String getMobile_token_key() {
		return mobile_token_key;
	}

	public void setMobile_token_key(String mobile_token_key) {
		this.mobile_token_key = mobile_token_key;
	}


	public String getToken_key() {
		return token_key;
	}

	public void setToken_key(String token_key) {
		this.token_key = token_key;
	}


	public java.sql.Timestamp getCreate_time() {
		return create_time;
	}

	public void setCreate_time(java.sql.Timestamp create_time) {
		this.create_time = create_time;
	}


	public java.sql.Timestamp getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(java.sql.Timestamp update_time) {
		this.update_time = update_time;
	}

}
