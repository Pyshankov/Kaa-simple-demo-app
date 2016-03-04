package org.kaaproject.kaa.example;



import org.apache.commons.codec.digest.DigestUtils;
import org.kaaproject.kaa.client.DesktopKaaPlatformContext;
import org.kaaproject.kaa.client.Kaa;
import org.kaaproject.kaa.client.KaaClient;
import org.kaaproject.kaa.client.KaaClientPlatformContext;
import org.kaaproject.kaa.common.dto.NotificationDto;
import org.kaaproject.kaa.example.service.Algorithm;
import org.kaaproject.kaa.server.common.admin.AdminClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import java.util.Date;

@SpringBootApplication
public class DemoApplication {

	@Value("${kaa.host}")
	private String kaaHost;

	@Value("${kaa.port}")
	private int kaaPort;

	@Value("${kaa.notification.schema.id}")
	private String schemaId;

	@Value("${kaa.notification.topic.id}")
	private String topicId;

	@Value("${kaa.app.id}")
	private  String appId;

	@Value("${kaa.appender.user}")
	private String user;

	@Value("${kaa.appender.password}")
	private String password;

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	public AdminClient kaaClient(){
		AdminClient adminClient = new AdminClient(kaaHost,kaaPort);
		adminClient.login(user,password);
		return adminClient;
	}

	@Bean
	public KaaClientPlatformContext getDesktopKaaPlatformContext(){
		return new DesktopKaaPlatformContext();
	}

	@Bean
	public KaaClient coreJavaClient(KaaClientPlatformContext context){
		return Kaa.newClient(context);
	}

	@Bean
	@Scope("prototype")
	public NotificationDto notificationDto(){
		Date date = new Date(System.currentTimeMillis()+86400);

		NotificationDto notificationDto = new NotificationDto();
		notificationDto.setApplicationId(appId);
		notificationDto.setExpiredAt(date);
		notificationDto.setSchemaId(schemaId);
		notificationDto.setTopicId(topicId);
		return notificationDto;
	}

	@Bean(name = "SHA1")
	public Algorithm sha1Service(){
		return DigestUtils::sha1;
	}

	@Bean(name = "MD5")
	public Algorithm md5Service() {
		return DigestUtils::md5;
	}

}
