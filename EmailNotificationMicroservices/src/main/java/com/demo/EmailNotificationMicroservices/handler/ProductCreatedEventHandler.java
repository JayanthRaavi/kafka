package com.demo.EmailNotificationMicroservices.handler;

import com.demo.EmailNotificationMicroservices.error.NotRetryableException;
import com.demo.EmailNotificationMicroservices.error.RetryableException;
import com.demo.EmailNotificationMicroservices.io.ProcessedEventEntity;
import com.demo.EmailNotificationMicroservices.io.ProcessedEventRepository;
import com.demo.core.ProductCreatedEvent;
import org.apache.kafka.common.header.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Component
@KafkaListener(topics="product-created-event-topic")
public class ProductCreatedEventHandler {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    RestTemplate restTemplate;

    ProcessedEventRepository processedEventRepository;

    public ProductCreatedEventHandler(RestTemplate restTemplate,ProcessedEventRepository processedEventRepository) {
        this.restTemplate = restTemplate;
        this.processedEventRepository=processedEventRepository;
    }

    @Transactional
    @KafkaHandler
    public void handle(@Payload ProductCreatedEvent productCreatedEvent,
    @Header("messageId")String messageId,
                       @Header(KafkaHeaders.RECEIVED_KEY)String messageKey) {

        LOGGER.info("Received a new event : "+productCreatedEvent.getTittle() + " with product id: "+
                productCreatedEvent.getProductId());

        ProcessedEventEntity existedRecord= processedEventRepository.findByMessageId(messageId);

        if (existedRecord!=null){
            LOGGER.info("found duplicate message id: {} "+existedRecord.getMessageId());
            return;
        }

        String requestUrl = "http://localhost:8085/response/200";

        try {
            ResponseEntity<String> response = restTemplate.exchange(requestUrl, HttpMethod.GET, null, String.class);

            if (response.getStatusCode().value() == HttpStatus.OK.value()) {
                LOGGER.info("Received response from a remote service: " + response.getBody());
            }
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            throw new RetryableException(ex);
        } catch(HttpServerErrorException ex) {
            LOGGER.error(ex.getMessage());
            throw new NotRetryableException(ex);
        } catch(Exception ex) {
            LOGGER.error(ex.getMessage());
            throw new NotRetryableException(ex);
        }
        try {
            processedEventRepository.save(new ProcessedEventEntity(messageId, productCreatedEvent.getProductId()));
        }catch (DataIntegrityViolationException ex){
            throw new NotRetryableException(ex);
        }
    }


}
