package com.bstek.cola.security.service;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bstek.bdf3.security.orm.User;
import com.bstek.cola.orm.Message;

/** 
* 
* @author bob.yang
* @since 2017年12月15日
*
*/
public interface MessageService {
	
	Page<Message> loadSent(Pageable pageable, String searchKey);

	Page<Message> loadReceiver(Pageable pageable, String searchKey);

	void removeSend(String id);

	void sendMessage(Message message);

	List<User> loadUsers(String searchKey);

	void removeReceiver(String id);

	List<Message> loadCount();

	void isRead(String id);

	


}