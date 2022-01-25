package com.zhangtao.blog.pojo;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "tb_article")
public class ArticleNoContent {

	@Id
	@Column(name = "id")
	private String id;
	@Column(name = "title")
	private String title;
	@Column(name = "user_id")
	private String userId;
	@Column(name = "category_id")
	private String categoryId;
	@Column(name = "type")
	private String type;
	@Column(name = "cover")
	private String cover;
	@Column(name = "state")
	private String state;
	@Column(name = "summary")
	private String summary;
	@Column(name = "labels")
	private String label;
	@Column(name = "view_count")
	private long viewCount;
	@Column(name = "create_time")
	private Date createTime;
	@Column(name = "update_time")
	private Date updateTime;

	@Transient
	private List<String> labels = new ArrayList<>();

	public String getLabel() {
		//打散
		if (label != null) {
			if (!this.label.contains("-")) {
				this.labels.add(label);
			}else{
				String[] split = label.split("-");
				List<String> strings = Arrays.asList(split);
				this.labels.addAll(strings);
			}
		}
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setLabels(List<String> labels) {
		this.labels = labels;
	}
	public List<String>  getLabels() {
		return this.labels;
	}

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

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
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


	public void setLabels(String label) {
		this.label = label;
	}

	public long getViewCount() {
		return viewCount;
	}

	public void setViewCount(long viewCount) {
		this.viewCount = viewCount;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

}
