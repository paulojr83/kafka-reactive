package com.kafka.reactive.producer;

import com.kafka.reactive.avro.Pzdt006Avro;
import com.kafka.reactive.dto.Pzdt006DTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@Slf4j
@RequestMapping("/api/kafka")
@RestController
public class KafkaProducer {

    private final KafkaTemplate<String, Pzdt006Avro> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, Pzdt006Avro> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping("/send")
    public void send(@RequestBody Pzdt006DTO input) {
        Pzdt006Avro pzdt006Avro = Pzdt006Avro.newBuilder()
                .setCDTRANRECB(input.getTransactionCode())
                .setCDQRCDRECB(input.getQrcode())
                .setTPPESSRECD(input.getPersonType())
                .setNRCPFCNPJRECD(input.getDocumentNumber())
                .setNMRECD(input.getName())
                .setCDAGENRECD(input.getBranchCode())
                .setCDCNTARECD(input.getAccountCode())
                .setTPCNTARECD(input.getAccountType())
                .setCDENDECADRBACEN(input.getPixKey())
                .setCDFORMINICPGTO(input.getStartPaymentMethod())
                .setDTVENCQRCD(input.getQrcodeDuoDate().toString())
                .setDHENVIRECBBACEN(input.getDhSendToBacen().toString())
                .setVLORIGQRCD(input.getOriginalValueQrcode().toString())
                .setVLPGTOINTT(input.getPaymentValue().toString())
                .setTPPESSPGADORIG(input.getPersonTypeOriginalPayer())
                .setNRCPFCNPJORIG(input.getDocumentNumberOriginalPayer())
                .setTPPESSPGAD(input.getPersonTypePayer())
                .setNRCPFCNPJPGAD(input.getDocumentNumberPayer())
                .setNMPGAD(input.getNamePayer())
                .setTXINFOCOMPQRCD(input.getComplementInformationQrcode())
                .setCDSITUOPER(input.getOperationSituation())
                .setCDMOEDPGTOINTT(input.getCurrencyCode())
                .build();

        this.kafkaTemplate.send("Pzdt006DTO", UUID.randomUUID().toString(), pzdt006Avro);
    }
}
