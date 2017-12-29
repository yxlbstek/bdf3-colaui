package com.bstek.cola.orm;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/** 
* 
* @author bob.yang
* @since 2017年12月29日
*
*/

@Entity
@Table(name = "BDF3_USER_MESSAGE")
public class UserMessage implements Serializable {

	private static final long serialVersionUID = -1096415888868770205L;

	@Id
	@Column(name = "ID_", length = 64)
	private String id;
	
	@Column(name = "USERNAME_", length = 64)
	private String username;
	
	@Column(name = "MESSAGE_ID_", length = 64)
	private String messageId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	
}
