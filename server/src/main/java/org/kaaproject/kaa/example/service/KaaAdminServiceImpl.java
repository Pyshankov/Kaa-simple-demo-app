package org.kaaproject.kaa.example.service;


import org.kaaproject.kaa.common.avro.GenericAvroConverter;
import org.kaaproject.kaa.common.dto.NotificationDto;
import org.kaaproject.kaa.example.Utils.ParsingUtils;
import org.kaaproject.kaa.example.mobile.notification.Data;
import org.kaaproject.kaa.server.common.admin.AdminClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import java.io.*;
import java.nio.ByteBuffer;


@Service
public class KaaAdminServiceImpl implements  KaaAdminService{

  final static Logger LOGGER = LoggerFactory.getLogger(KaaAdminServiceImpl.class);

    @Autowired
    private AdminClient adminClient;

    @Autowired
    private ApplicationContext context;

    private Algorithm hashService;


    public void sendNotification(String json) throws IOException  {

        LOGGER.info("receive json : "+json);

        NotificationDto dto = (NotificationDto) context.getBean("notificationDto");

        //parsing json
        Data data = ParsingUtils.parseData(json);

        //lookup bean with hash function
        hashService = (Algorithm) context.getBean(data.getHashFunction().toUpperCase());

        //performing hash operation and set result to data object
        data.setData(ByteBuffer.wrap(hashService.calculate(data.getData().array())));

        GenericAvroConverter<Data> notificationConverter = new GenericAvroConverter<>(data.getSchema());

        //send notification
        try {
            adminClient.sendUnicastNotificationSimplified(dto, data.getEndpointKeyHash(),notificationConverter.encodeToJson(data));
            LOGGER.info("json sent :"+notificationConverter.encodeToJson(data));

        } catch (Exception e){
            LOGGER.error("could't sent notification to endpoint : "+e);
        }

    }



}
