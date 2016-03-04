package org.kaaproject.kaa.example.service;


import java.io.IOException;

public interface KaaAdminService {
     void sendNotification(String json) throws IOException;
}
