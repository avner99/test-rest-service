package com.example.restservice;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

@RestController
public class Controller {

	@Autowired
	private ApplicationContext context;

	@PostMapping(path = "/greeting")

	public @ResponseBody Person testExcercise(@RequestBody Person person1) {
		// Validate Request
		if (person1.getId() == null || person1.getName() == null)
		{
	        throw  new ResponseStatusException(HttpStatus.BAD_REQUEST);

		}
		
		// parse message to xml
		XmlMapper xmlMapper = new XmlMapper();
		String xmlPerson = "";
		try {
			xmlPerson = xmlMapper.writeValueAsString(person1);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
	        throw  new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);

		}
		// add uuid to person
		QueueMessage qMessage = new QueueMessage(xmlPerson);
		UUID uuid = qMessage.getUuid();
		// to jms
		JmsTemplate jmsTemplate = context.getBean(JmsTemplate.class);
		jmsTemplate.convertAndSend("pQueue", qMessage);
		// from jms
		QueueMessage inMessage = null;
		while (true) {
			Object inMessageObject = jmsTemplate.receiveAndConvert("pQueue");
			if (inMessageObject instanceof QueueMessage) {
				inMessage = (QueueMessage) inMessageObject;
				if (uuid.equals(inMessage.getUuid())) {
					break;
				}
				jmsTemplate.convertAndSend("pQueue", inMessage);
			}
		}
		
	
	
	// to json
	Person person2 = null;
		
	try {
	String personXml = inMessage.getMessage();
	
		person2 = xmlMapper.readValue(personXml, Person.class);
	}catch(
	JsonProcessingException e)
	{
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

	return person2;
	}
}
