package com.duowan.xgame.mobile.rest.util;

import java.io.IOException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;


public class CrossDomainFilter  implements ContainerResponseFilter {

	public void filter(ContainerRequestContext requestContext,
			ContainerResponseContext responseContext) throws IOException {
		//responseContext.getHeaders().add("X-Powered-By", "Jersey :-)");
		responseContext.getHeaders().add("Access-Control-Allow-Origin", "*");
		//responseContext.getHeaders().add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
		//responseContext.getHeaders().add("Access-Control-Allow-Credentials", "true");
		//responseContext.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
		//responseContext.getHeaders().add("Access-Control-Max-Age", "1209600");
	}
}