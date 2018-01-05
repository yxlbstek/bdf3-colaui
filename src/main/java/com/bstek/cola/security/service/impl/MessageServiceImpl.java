package com.bstek.cola.security.service.impl;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.malagu.linq.JpaUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.bstek.bdf3.security.ContextUtils;
import com.bstek.bdf3.security.orm.User;
import com.bstek.cola.orm.Message;
import com.bstek.cola.orm.UserMessage;
import com.bstek.cola.security.service.MessageService;

/** 
* 
* @author bob.yang
* @since 2017年12月15日
*
*/
@Service("cola.messageService")
public class MessageServiceImpl implements MessageService {
	
	@Override
	public Page<Message> loadReceiver(Pageable pageable, String searchKey) {
		String username = ContextUtils.getLoginUsername();		
		Page<Message> page =  JpaUtil
				.linq(Message.class)
				.in(UserMessage.class)
					.select("messageId")
					.equal("receiverId", username)
				.end()
				.equal("type", "Message")
				.addIf(searchKey)
					.or()
						.like("title" , "%" + searchKey + "%")
						.like("sender" , "%" + searchKey + "%")
					.end()
				.endIf()
				.desc("createdAt")
				.paging(pageable);
		
		if (page.getSize() > 0) {
			for (Message message : page) {
				List<UserMessage> notReadMessages = JpaUtil
					.linq(UserMessage.class)
					.equal("messageId", message.getId())
					.equal("receiverId", username)
					.equal("read", false)
					.list();
				if (notReadMessages.size() > 0) {
					message.setRead(false);
				} else {
					message.setRead(true);
				}
			}
		}
		return page;
	}
	
	@Override
	public Page<Message> loadSent(Pageable pageable, String searchKey) {
		String username = ContextUtils.getLoginUsername();	
		return JpaUtil
				.linq(Message.class)
				.in(UserMessage.class)
					.select("messageId")
					.equal("senderId", username)
				.end()
				.equal("type", "Message")
				.addIf(searchKey)
					.or()
						.like("title" , "%" + searchKey + "%")
						.like("receiver" , "%" + searchKey + "%")
					.end()
				.endIf()
				.desc("createdAt")
				.paging(pageable);
	}

	@Override
	public void removeSend(String id) {
		JpaUtil
			.linu(UserMessage.class)
			.equal("messageId", id)
			.set("senderId", null)
			.update();
	}

	@Override
	public void removeReceiver(String id) {
		JpaUtil
			.linu(UserMessage.class)
			.equal("messageId", id)
			.equal("receiverId", ContextUtils.getLoginUsername())
			.set("receiverId", null)
			.update();
	}

	@Override
	public void sendMessage(Message message) {
		message.setId(UUID.randomUUID().toString());
		message.setCreatedAt(new Date());
		message.setType("Message");
		message.setSender(ContextUtils.getLoginUsername());
		JpaUtil.persist(message);
		String receiver = message.getReceiver();
		String[] usernames = receiver.split(";");
		for (int i = 0; i < usernames.length; i++) {
			UserMessage um = new UserMessage();
			um.setId(UUID.randomUUID().toString());
			um.setMessageId(message.getId());
			um.setSenderId(message.getSender());
			um.setRead(false);
			um.setReceiverId(usernames[i]);
			JpaUtil.persist(um);
		}
	}

	@Override
	public List<User> loadUsers(String searchKey) {
		return JpaUtil
			.linq(User.class)
			.notEqual("username", ContextUtils.getLoginUsername())
			.addIf(searchKey)
				.or()
					.like("username", "%" + searchKey + "%")
					.like("nickname", "%" + searchKey + "%")
				.end()
			.endIf()
			.list();
	}

	@Override
	public List<Message> loadCount() {
		String username = ContextUtils.getLoginUsername();
		List<Message> ms = new ArrayList<>();
		List<Message> messages = JpaUtil
				.linq(Message.class)
				.in(UserMessage.class)
					.select("messageId")
					.equal("receiverId", username)
					.equal("read", false)
				.end()
				.desc("createdAt")
				.list();
		if (messages.size() > 0) {
			for (Message message : messages) {
				message.setMessageCount(messages.size());
			}
			return messages;
		} else {
			Message message = new Message();
			message.setMessageCount(null);
			ms.add(message);
			return ms;
		}
		
	}

	@Override
	public void isRead(String id) {
		String username = ContextUtils.getLoginUsername();
		JpaUtil
			.linu(UserMessage.class)
			.equal("messageId", id)
			.equal("receiverId", username)
			.set("read", true)
			.update();	
	}



}
