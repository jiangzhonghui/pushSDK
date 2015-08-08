package com.duowan.xgame.mobile.rest.service;

import java.util.logging.Logger;

import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spring.scope.RequestContextFilter;

import javax.json.stream.JsonGenerator;

import com.duowan.xgame.mobile.rest.util.CrossDomainFilter;
import com.duowan.xgame.mobile.rest.util.LoggingResponseFilter;


/**
 * 
 * Registers the components to be used by the JAX-RS application  
 * 
 * @author ama
 *
 */
public class GameOnlineRestApplication extends ResourceConfig {

    /**
	* Register JAX-RS application components.
	*/	
	public GameOnlineRestApplication(){
		packages("com.duowan.xgame.mobile.rest.service");
		register(RequestContextFilter.class);
		register(JacksonFeature.class);
		registerInstances(new LoggingFilter(Logger.getLogger("GameOnlineRestApplication"), true));
		register(LoggingResponseFilter.class);
		register(CrossDomainFilter.class);
		register(TsPushServer.class);
        //register(McpeLauncherService.class);
        //register(AccountService.class);
        //register(TimelineService.class);
        property(JsonGenerator.PRETTY_PRINTING, true);
	}
}
