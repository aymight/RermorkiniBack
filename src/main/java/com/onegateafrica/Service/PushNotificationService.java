package com.onegateafrica.Service;

import io.github.jav.exposerversdk.PushClientException;

public interface PushNotificationService {
	void ajouterPushNotification(String recipient , String title ,String message) throws PushClientException, InterruptedException;
}
