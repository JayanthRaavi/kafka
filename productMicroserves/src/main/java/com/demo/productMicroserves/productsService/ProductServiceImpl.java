package com.demo.productMicroserves.productsService;

import com.demo.core.ProductCreatedEvent;
import com.demo.productMicroserves.rest.CreateProductRestModel;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class ProductServiceImpl implements ProductService{

    private final Logger LOGGER= LoggerFactory.getLogger(ProductServiceImpl.class);

    //kafka template is used to publish event to kafka topic
    KafkaTemplate<String,ProductCreatedEvent> kafkaTemplate;

    public ProductServiceImpl(KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public String createProduct(CreateProductRestModel product) throws Exception{
        String productId= UUID.randomUUID().toString();

        ProductCreatedEvent productCreatedEvent= new ProductCreatedEvent(productId,product.getTittle()
        ,product.getPrice(),product.getQuantity());
/*
        // Asyncronous method of sending events
       CompletableFuture<SendResult<String,ProductCreatedEvent>> future =
               kafkaTemplate.send("product-created-events-topic",productId,productCreatedEvent);

       future.whenComplete((result,exception) ->{
           if (exception!= null)
               LOGGER.error("****** Failed to sent Message : "+exception.getMessage());
           else
               LOGGER.info("****** Message send successfully : "+result.getRecordMetadata());

       });

 */
        LOGGER.info(" ********* before publishing a product create event");

        ProducerRecord<String,ProductCreatedEvent>record=new ProducerRecord<>(
                "product-created-event-topic",
                productId,
                productCreatedEvent);
        record.headers().add("messageId",UUID.randomUUID().toString().getBytes());

        SendResult<String, ProductCreatedEvent> result =
                kafkaTemplate.send(record).get();

        LOGGER.info("Partition : "+ result.getRecordMetadata().partition());
        LOGGER.info("Topic : "+ result.getRecordMetadata().topic());
        LOGGER.info("Offset : "+ result.getRecordMetadata().offset());

       LOGGER.info("****** Returning product Id ");
        return productId;
    }
}
