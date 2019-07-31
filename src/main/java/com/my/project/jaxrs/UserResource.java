package com.my.project.jaxrs;

import java.util.List;
import java.util.Objects;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * 根资源(发布到users路径上)
 */
@Path("/users") //@Path的value前后有没有/都不影响URI到资源的映射
@Produces(MediaType.APPLICATION_JSON) // 默认所有资源方法的Response的Content-Type都是application/json
public class UserResource {

	public static final String ABOUT_MESSAGE = "User Resources";
	public static final String ABOUT_MESSAGE_HTML = String.format("<html><body><h1>%s</h1></body></html>", ABOUT_MESSAGE);
	private static UserRepository repository = UserRepository.getInstance();

	/**
	 * 同一资源, 不同数据格式(xml/json)
	 * 
	 * 服务端一个资源方法可以响应多种不同的MediaType, 根据客户端HTTP Header中的Accept值来决定响应哪种类型
	 * 1. GET /users Accept: application/json, application/xml
	 *    则选择MediaType列表中的第一个做为响应类型(如下list()方法, 则为application/json)
	 * 2. GET /users Accept: application/json;q=0.9, application/xml
	 *    客户端两种MediaType都接受, 但quality factor不同(默认是1)
	 *    则选择quality factor值较大的一个做为响应类型(如下list()方法, 则为application/xml)
	 * 3. 服务端同样可设置quality factor, 功能与客户端类似, 如:
	 *    @Produces({"application/xml; qs=0.9", "application/json"})
	 */

	/**
	 * GET /users
	 * @return user list
	 */
	@GET
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public List<User> list() {
		return repository.list();
	}

	/**
	 * 同一资源, 不同表现形式(text/html)
	 * 
	 * 服务端也可以将不同的MediaType映射到多个不同的资源方法, 根据客户端HTTP Header中的Accept值来决定映射到哪一个资源方法
	 * 1. GET /users/about Accept: text/plain 映射到aboutText()方法
	 * 2. GET /users/about Accept: text/html 映射到aboutHtml()方法
	 * 3. GET /users/about Accept: text/plain;q=0.9, text/html 映射到aboutHtml()方法
	 * 4. GET /users/about Accept: text/* 随机调用aboutText()或aboutHtml()方法
	 * 5. 资源方法上的@Produces注解将覆盖资源类上的@Produces注解
	 */

	/**
     * GET /users/about
     * @return response Content-Type is text/plain
     */
    @GET
    @Path("/about")
    @Produces(MediaType.TEXT_PLAIN) //这里的MediaType将覆盖类上的@Produces注解
    public String aboutText() {
        return ABOUT_MESSAGE;
    }

	/**
     * GET /users/about
     * @return response Content-Type is text/html
     */
    @GET
    @Path("/about")
    @Produces(MediaType.TEXT_HTML) //这里的MediaType将覆盖类上的@Produces注解
    public String aboutHtml() {
    	return ABOUT_MESSAGE_HTML;
    }

	/**
	 * 使用@PathParam注解获取@Path路径中的参数
	 * 1. 使用{name}在@Path的路径中指定参数位置
	 * 2. 使用@PathParam将@Path中的参数映射到资源方法的参数上
	 * 3. 在@Path中指定参数时, 可以使用正则表达式来匹配URL, 如果URL与正则表达式不匹配将返回404
	 */

    /**
     * GET /users/{id:[1-9]+}
     * @param id id
     * @return user json string, if "id" not match the specific pattern, return 404 status code
     */
    @GET
    @Path("/{id:[1-9]+}")
    public User getById(@PathParam("id") Integer id) {
        return repository.getById(id);
    }

    /**
     * GET /users/{username:[a-zA-Z][a-zA-Z_0-9]*}
     * @param username username
     * @return user json string, if "username" not match the specific pattern, return 404 status code
     */
    @GET
    @Path("/{username:[a-zA-Z][a-zA-Z_0-9]*}")
    public User getByName(@PathParam("username") String username) {
    	return repository.getByName(username);
    }

    /**
     * 使用@POST注解响应客户端的post请求, 常用来创建资源
     */

    /**
     * POST /users
     * @param user user json string
     * @return status 204
     */
    @POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response save(User user) {
		repository.save(user);
		return Response.noContent().build();
	}

    /**
     * 使用@PUT注解响应客户端的put请求, 常用来创建或更新资源
     */

    /**
     * PUT /users
     * @return status 204
     */
    @PUT
    @Path("/{id:[1-9]+}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response update(@PathParam("id") Integer id, User user) {
    	if(Objects.equals(id, user.getId())) {
    		repository.saveOrUpdate(user);
    	}
		return Response.noContent().build();
	}

}
