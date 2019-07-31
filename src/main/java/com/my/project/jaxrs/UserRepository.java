package com.my.project.jaxrs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 用户数据操作类
 * @author yang
 */
public class UserRepository {

	private static final AtomicInteger NEXT_ID = new AtomicInteger(1);

	private static List<User> users = new ArrayList<User>();

	private static UserRepository instance = null;
	private UserRepository() {
		User admin = new User(NEXT_ID.getAndIncrement(), "admin", "123456");
		users.add(admin);
	}
	/**
	 * 获取用户数据操作类实例
	 * @return 用户数据操作类实例
	 */
	public static UserRepository getInstance() {
		if(instance == null) {
			instance = new UserRepository();
		}
		return instance;
	}

	/**
	 * 检查用户是否存在
	 * @param username 用户名
	 * @return 用户存在时返回true, 否则返回false
	 */
	public boolean exist(String username) {
		boolean exist = false;
		if(username != null) {
			for(User u : users) {
				if(Objects.equals(u.getUsername(), username)) {
					exist = true;
				}
			}
		}
		return exist;
	}

	/**
	 * 根据ID获取用户
	 * @param id ID
	 * @return 如果对应的用户存在则返回用户对象, 否则返回null
	 */
	public User getById(Integer id) {
		User user = null;
		if(id != null) {
			for(User u : users) {
				if(Objects.equals(u.getId(), id)) {
					user = u;
					break;
				}
			}
		}
		return user;
	}

	/**
	 * 根据用户名获取用户
	 * @param username 用户名
	 * @return 如果对应的用户存在则返回用户对象, 否则返回null
	 */
	public User getByName(String username) {
		User user = null;
		if(username != null) {
			for(User u : users) {
				if(Objects.equals(u.getUsername(), username)) {
					user = u;
					break;
				}
			}
		}
		return user;
	}

	/**
	 * 添加用户
	 * @param user 用户
	 */
	public void save(User user) {
		if(!exist(user.getUsername()) && user.getUsername() != null) {
			user.setId(NEXT_ID.getAndIncrement());
			users.add(user);
		}
	}

	/**
	 * 更新用户
	 * @param user 用户
	 */
	public void update(User user) {
		User u1 = getById(user.getId());
		User u2 = getByName(user.getUsername());
		if(u1 != null && Objects.equals(u1, u2)) {
			u1.setPassword(user.getPassword());
		}
	}

	/**
	 * 添加或更新用户
	 * @param user 用户
	 */
	public void saveOrUpdate(User user) {
		User u1 = getById(user.getId());
		User u2 = getByName(user.getUsername());
		if(u1 == null && u2 == null && user.getUsername() != null) {
			user.setId(NEXT_ID.getAndIncrement());
			users.add(user);
		} else if(u1 != null && Objects.equals(u1, u2)) {
			u1.setPassword(user.getPassword());
		}
	}

	/**
	 * 删除用户
	 * @param id ID
	 */
	public void deleteById(Integer id) {
		Iterator<User> it = users.iterator();
		while(it.hasNext()) {
			User u = it.next();
			if(Objects.equals(u.getId(), id)) {
				it.remove();
			}
		}
	}

	/**
	 * 删除用户
	 * @param username 用户名
	 */
	public void deleteByName(String username) {
		Iterator<User> it = users.iterator();
		while(it.hasNext()) {
			User u = it.next();
			if(Objects.equals(u.getUsername(), username)) {
				it.remove();
			}
		}
	}

	/**
	 * 获取所有用户
	 * @return 用户列表
	 */
	public List<User> list() {
		return users;
	}

	/**
	 * 用户登录
	 * @param username 用户名
	 * @param password 密码
	 * @return 登录成功返回用户信息，否则返回<code>null</code>
	 */
	public User login(String username, String password) {
		User user = null;
		for(User u : users) {
			if(Objects.equals(u.getUsername(), username) && Objects.equals(u.getPassword(), password)) {
				user = u;
				break;
			}
		}
		return user;
	}

}
