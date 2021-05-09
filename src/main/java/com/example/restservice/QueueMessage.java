package com.example.restservice;

import java.io.Serializable;
import java.util.UUID;


public class QueueMessage implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3113891606510214022L;
	private UUID uuid = UUID.randomUUID();
	private String message;
	
	public QueueMessage(String message) {
		this.message = message;
	}

	public UUID getUuid() {
		return uuid;
	}

	public String getMessage() {
		return message;
	}
}
