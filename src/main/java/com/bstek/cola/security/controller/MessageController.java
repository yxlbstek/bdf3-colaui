package com.bstek.cola.security.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bstek.bdf3.security.orm.User;
import com.bstek.cola.log.annotation.Log;
import com.bstek.cola.orm.Message;
import com.bstek.cola.security.service.MessageService;

/** 
* 
* @author bob.yang
* @since 2017年12月15日
*
*/

@RestController("cola.messageController")
@Transactional(readOnly = true)
@RequestMapping("/api")
public class MessageController {

	@Autowired
	private MessageService messageService;
	
	@RequestMapping(path = "/message/loadUsers", method = RequestMethod.GET)
	public List<User> loadUsers(@RequestParam(name = "searchKey", required = false) String searchKey) {
		return messageService.loadUsers(searchKey);
	}	
	
	@RequestMapping(path = "/message/loadCount", method = RequestMethod.GET)
	public Long loadCount() {
		return messageService.loadCount();
	}

	@RequestMapping(path = "/message/loadSent", method = RequestMethod.GET)
	public Page<Message> loadSent(Pageable pageable, @RequestParam(name = "searchKey", required = false) String searchKey) {
		return messageService.loadSent(pageable, searchKey);
	}
	
	@RequestMapping(path = "/message/loadReceiver", method = RequestMethod.GET)
	public Page<Message> loadReceiver(Pageable pageable, @RequestParam(name = "searchKey", required = false) String searchKey) {
		return messageService.loadReceiver(pageable, searchKey);
	}

	@RequestMapping(path = "/message/removeSend", method = RequestMethod.POST)
	@Log(module = "消息管理", category = "系统日志", operation = "删除已发送消息", source = "/message")
	@Transactional
	public void removeSend(@RequestParam String id) {
		messageService.removeSend(id);
	}
	
	@RequestMapping(path = "/message/isRead", method = RequestMethod.POST)
	@Transactional
	public void isRead(@RequestParam String id) {
		messageService.isRead(id);
	}

	@RequestMapping(path = "/message/removeReceiver", method = RequestMethod.POST)
	@Log(module = "消息管理", category = "系统日志", operation = "删除收件箱消息", source = "/message")
	@Transactional
	public void removeReceiver(@RequestParam String id) {
		messageService.removeReceiver(id);
	}
	
	@RequestMapping(path = "/message/sendMessage", method = RequestMethod.POST)
	@Log(module = "消息管理", category = "系统日志", operation = "发送消息", source = "/message")
	@Transactional
	public void sendMessage(@RequestBody Message message) throws Exception {
		messageService.sendMessage(message);
	}

}
