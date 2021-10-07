package com.zhangtao.blog.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table ( name ="tb_article" )
public class Article {

  	@Id
	@Column(name = "id")
	private String id;
  	@Column(name = "title" )
	private String title;
  	@Column(name = "user_id" )
	private String user_id;
  	@Column(name = "category_id" )
	private String category_id;
  	@Column(name = "content" )
	private String content;
  	@Column(name = "type" )
	private String type;
  	@Column(name = "cover" )
	private String cover;
  	@Column(name = "state" )
	private String state;
  	@Column(name = "summary" )
	private String summary;
  	@Column(name = "labels" )
	private String labels;
  	@Column(name = "view_count" )
	private long view_count;
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


	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}


	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}


	public String getCategory_id() {
		return category_id;
	}

	public void setCategory_id(String category_id) {
		this.category_id = category_id;
	}


	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}


	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}


	public String getCover() {
		return cover;
	}

	public void setCover(String cover) {
		this.cover = cover;
	}


	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}


	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}


	public String getLabels() {
		return labels;
	}

	public void setLabels(String labels) {
		this.labels = labels;
	}


	public long getView_count() {
		return view_count;
	}

	public void setView_count(long view_count) {
		this.view_count = view_count;
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
