package com.kafka.reactive.dto;


import com.kafka.reactive.avro.Pzdt006Avro;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public interface Pzdt006DTOMapper {

    public static Pzdt006DTO convertTo(Pzdt006Avro source) {
        Pzdt006DTO target = new Pzdt006DTO();

        target.setTransactionCode(source.getCDTRANRECB());
        target.setQrcode(source.getCDQRCDRECB());
        target.setPersonType(source.getTPPESSRECD());
        target.setDocumentNumber(source.getNRCPFCNPJRECD());
        target.setName(source.getNMRECD());
        target.setBranchCode(source.getCDAGENRECD());
        target.setAccountCode(source.getCDCNTARECD());
        target.setAccountType(source.getTPCNTARECD());
        target.setPixKey(source.getCDENDECADRBACEN());
        target.setStartPaymentMethod(source.getCDFORMINICPGTO());
        target.setQrcodeDuoDate(LocalDate.parse(source.getDTVENCQRCD(), DateTimeFormatter.ISO_DATE));
        target.setDhSendToBacen(source.getDHENVIRECBBACEN());
        target.setOriginalValueQrcode(new BigDecimal(source.getVLORIGQRCD()));
        target.setPaymentValue(new BigDecimal(source.getVLPGTOINTT()));
        target.setPersonTypeOriginalPayer(source.getTPPESSPGADORIG());
        target.setDocumentNumberOriginalPayer(source.getNRCPFCNPJORIG());
        target.setPersonTypePayer(source.getTPPESSPGAD());
        target.setDocumentNumberPayer(source.getNRCPFCNPJPGAD());
        target.setNamePayer(source.getNMPGAD());
        target.setComplementInformationQrcode(source.getTXINFOCOMPQRCD());
        target.setOperationSituation(source.getCDSITUOPER());
        target.setCurrencyCode(source.getCDMOEDPGTOINTT());

        return target;
    }
}
