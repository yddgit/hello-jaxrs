package com.my.project.jaxrs;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;

/**
 * WebService启动类
 */
public class Main {

	/** 服务监听地址 */
    public static final String BASE_URI = "http://localhost:8080/api/";

    /**
     * 启动HTTP Server, 发布Application中定义的资源
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {
    	// 扫描com.my.project.jaxrs包下的resource和provider
        final ResourceConfig rc = new ResourceConfig().packages("com.my.project.jaxrs");
        // 创建HTTP Server实例
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    /**
     * 启动WebService服务
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        final HttpServer server = startServer();
        System.out.println(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));
        System.in.read();
        server.shutdownNow();
    }

}
