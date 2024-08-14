package com.kafka.reactive.consumer;

import com.kafka.reactive.avro.Pzdt006Avro;
import com.kafka.reactive.dto.Pzdt006DTO;
import com.kafka.reactive.dto.Pzdt006DTOMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaConsumer {

    @KafkaListener(topics = "Pzdt006DTO")
    public void receive(@Payload GenericRecord payload) {
        Pzdt006DTO pzdt006DTO = Pzdt006DTOMapper.convertTo((Pzdt006Avro) payload);
        log.info("Name: {}\n Document Number Payer: {}", pzdt006DTO.getName(), pzdt006DTO.getDocumentNumberPayer());
    }

}
