package com.my.project.jaxrs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class UserResourceTest {

    private HttpServer server;
    private WebTarget target;
    private User admin;
    private User bob;
    private GenericType<List<User>> genericType = new GenericType<List<User>>() {};

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        // 启动服务
        server = Main.startServer();
        // 创建客户端
        Client c = ClientBuilder.newClient();
        target = c.target(Main.BASE_URI);
        // 初始化数据
        admin = new User(1, "admin", "123456");
        bob = new User(2, "Bob", "111222");
    }

    @After
    public void tearDown() throws Exception {
        server.shutdownNow();
    }

    /**
     * GET /users
     * Accept: application/json
     * return user list
     */
    @Test
    public void testListJson() {
    	List<User> list = target.path("users").request(MediaType.APPLICATION_JSON).get(genericType);
    	assertTrue(list.contains(admin));
    }

    /**
     * GET /users
     * Accept: application/xml
     * return user list
     */
    @Test
    public void testListXml() {
    	List<User> list = target.path("users").request(MediaType.APPLICATION_XML).get(genericType);
    	assertTrue(list.contains(admin));
    }

    /**
     * GET /users
     * Accept: application/xml, application/json
     * return user list
     */
    @Test
    public void testListAuto() {
    	List<User> list = target.path("users").request(MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON).get(genericType);
    	assertTrue(list.contains(admin));
    }

    /**
     * GET /users
     * Accept: application/json;q=0.9, application/xml
     * return user list
     */
    @Test
    public void testList() {
    	List<User> list = target.path("users").request().accept("application/json;q=0.9, application/xml").get(genericType);
    	assertTrue(list.contains(admin));
    }

    /**
     * GET /users/about
     * Accept: text/plain
     * return "User Resources"
     */
    @Test
    public void testAboutText() {
        String responseMsg = target.path("users/about").request(MediaType.TEXT_PLAIN).get(String.class);
        System.out.println(responseMsg);
        assertEquals(UserResource.ABOUT_MESSAGE, responseMsg);
    }

    /**
     * GET /users/about
     * Accept: text/html
     * return "<html><body><h1>User Resources</h1></body></html>"
     */
    @Test
    public void testAboutHtml() {
    	String responseMsg = target.path("users/about").request(MediaType.TEXT_HTML).get(String.class);
    	System.out.println(responseMsg);
    	assertEquals(UserResource.ABOUT_MESSAGE_HTML, responseMsg);
    }

    /**
     * GET /users/about
     * Accept: text/*
     * return 服务端将随机响应text或html内容
     */
    @Test
    public void testAboutAuto() {
    	String responseMsg = target.path("users/about").request()
    			.accept("text/*").get(String.class);
    	System.out.println(responseMsg);
    	assertTrue(responseMsg.contains(UserResource.ABOUT_MESSAGE));
    }

    /**
     * GET /users/about
     * Accept: text/plain;q=0.9, text/html
     * return 服务端根据accept的值会优先响应html内容
     */
    @Test
    public void testAbout() {
    	String responseMsg = target.path("users/about").request()
    			.accept("text/plain;q=0.9, text/html").get(String.class);
    	System.out.println(responseMsg);
    	assertEquals(UserResource.ABOUT_MESSAGE_HTML, responseMsg);
    }

    /**
     * GET /users/{id}
     * return user json string
     */
    @Test
    public void testGetById() {
    	User response = target.path("users/1").request().get(User.class);
    	assertEquals(admin, response);
    }

    /**
     * GET /users/{id:[1-9]+}
     * return 404 status code
     */
    @Test
    public void testGetByIdFail() {
    	expectedException.expect(NotFoundException.class);
    	expectedException.expectMessage("HTTP 404 Not Found");
    	target.path("users/0").request().get(User.class);
    }

    /**
     * GET /users/{username}
     * return user json string
     */
    @Test
    public void testGetByName() {
    	User response = target.path("users/admin").request().get(User.class);
    	assertEquals(admin, response);
    }

    /**
     * GET /users/{username:[a-zA-Z][a-zA-Z_0-9]*}
     * return 404 status code
     */
    @Test
    public void testGetByNameFail() {
    	expectedException.expect(NotFoundException.class);
    	expectedException.expectMessage("HTTP 404 Not Found");
		target.path("users/_admin").request().get(User.class);
    }

    /**
     * POST /users
     * Content-Type: application/json
     * return 204 No Content
     */
    @Test
    public void testSave() {
    	target.path("users").request().post(Entity.json(bob));
    	User added = target.path("users/Bob").request().get(User.class);
    	assertEquals(added, bob);
    }

    /**
     * PUT /users/{id:[1-9]+}
     * Content-Type: application/json
     * return 204 No Content
     */
    @Test
    public void testUpdate() {
    	assertEquals("111222", bob.getPassword());
    	target.path("users").request().post(Entity.json(bob));
    	bob.setPassword("654321");
    	target.path("users/2").request().put(Entity.json(bob));
    	User updated = target.path("users/2").request().get(User.class);
    	assertEquals(updated.getPassword(), "654321");
    	assertEquals(updated, bob);
    }

}
