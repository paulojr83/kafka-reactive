package com.kafka.reactive.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@ToString
@Slf4j
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Pzdt006DTO {

    @JsonProperty("CD_TRAN_RECB")
    private String transactionCode;

    @JsonProperty("CD_QRCD_RECB")
    private String qrcode;

    @JsonProperty("TP_PESS_RECD")
    private String personType;

    @JsonProperty("NR_CPF_CNPJ_RECD")
    private long documentNumber;

    @JsonProperty("NM_RECD")
    private String name;

    @JsonProperty("CD_AGEN_RECD")
    private int branchCode;

    @JsonProperty("CD_CNTA_RECD")
    private String accountCode;

    @JsonProperty("TP_CNTA_RECD")
    private String accountType;

    @JsonProperty("CD_ENDE_CADR_BACEN")
    private String pixKey;

    @JsonProperty("CD_FORM_INIC_PGTO")
    private String startPaymentMethod;

    @JsonProperty("DT_VENC_QRCD")
    private LocalDate qrcodeDuoDate;

    @JsonProperty("DH_ENVI_RECB_BACEN")
    private Instant dhSendToBacen;

    @JsonProperty("VL_ORIG_QRCD")
    private BigDecimal originalValueQrcode;

    @JsonProperty("VL_PGTO_INTT")
    private BigDecimal paymentValue;

    @JsonProperty("TP_PESS_PGAD_ORIG")
    private String personTypeOriginalPayer;

    @JsonProperty("NR_CPF_CNPJ_ORIG")
    private long documentNumberOriginalPayer;

    @JsonProperty("TP_PESS_PGAD")
    private String personTypePayer;

    @JsonProperty("NR_CPF_CNPJ_PGAD")
    private long documentNumberPayer;

    @JsonProperty("NM_PGAD")
    private String namePayer;

    @JsonProperty("TX_INFO_COMP_QRCD")
    private String complementInformationQrcode;

    @JsonProperty("CD_SITU_OPER")
    private Integer operationSituation;

    @JsonProperty("CD_MOED_PGTO_INTT")
    private String currencyCode;

    public void setDhSendToBacen(String dhSendToBacen) {
        var dhAux = dhSendToBacen.substring(0, 26);
        this.dhSendToBacen =
                LocalDateTime.parse(dhAux, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS"))
                        .atZone(ZoneId.systemDefault())
                        .toInstant();
    }
}