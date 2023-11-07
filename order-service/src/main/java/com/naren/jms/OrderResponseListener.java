package com.naren.jms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.naren.model.OrderRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class OrderResponseListener {

    @Autowired
    private JmsTemplate jmsTemplate;
    @JmsListener(destination = "ORDER.RESPONSE")
    public void receive(Message message) throws JMSException, JsonProcessingException {
        TextMessage textMessage = (TextMessage) message;
        log.info("### 4 ### Order Service received message response : {} with correlation id: {}",
                textMessage.getText(), textMessage.getJMSCorrelationID());
        if(textMessage.getText().contains("ok")){
            Map<Integer,OrderRequest> orderRequestMAp=new HashMap<>();
            orderRequestMAp.put(1, OrderRequest.builder().message("this is a messege").identifier("this is fro retry").build());
            ObjectMapper objectMapper=new ObjectMapper();
            jmsTemplate.convertAndSend("ORDER.REQUEST.RETRY",objectMapper.writeValueAsString(orderRequestMAp));
        }

        // do some business logic here, like updating the order in the database
    }
}