package com.bstek.cola.orm;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.bstek.cola.excel.annotation.ColumnDesc;

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
	@ColumnDesc(label = "ID")
	private String id;
	
	@Column(name = "SENDER_ID_", length = 64)
	@ColumnDesc(label = "发送人ID")
	private String senderId;
	
	@Column(name = "RECEIVER_ID_", length = 64)
	@ColumnDesc(label = "接收人ID")
	private String receiverId;
	
	@Column(name = "MESSAGE_ID_", length = 64)
	@ColumnDesc(label = "消息ID")
	private String messageId;
	
	@Column(name = "READ_")
	@ColumnDesc(label = "是否已读")
	private boolean read;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSenderId() {
		return senderId;
	}

	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}

	public String getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(String receiverId) {
		this.receiverId = receiverId;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	
}
