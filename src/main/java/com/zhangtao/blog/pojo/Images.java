package com.zhangtao.blog.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table ( name ="tb_images" )
public class Images {

  	@Id
	@Column(name = "id")
	private String id;
  	@Column(name = "user_id" )
	private String user_id;
  	@Column(name = "url" )
	private String url;
  	@Column(name = "path" )
	private String path;
  	@Column(name = "content_type" )
	private String content_type;
  	@Column(name = "name" )
	private String name;
  	@Column(name = "state" )
	private String state;
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


	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}


	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}


	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}


	public String getContent_type() {
		return content_type;
	}

	public void setContent_type(String content_type) {
		this.content_type = content_type;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
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
