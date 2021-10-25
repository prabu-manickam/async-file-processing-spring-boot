package com.babin.csvreconciler.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table
//        (indexes = {
//        @Index(name = "customerNumber_productReference", columnList = "customerNumber, productReference", unique = true)
//})
public class QwoRecord {
    @Id
    @GeneratedValue
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "CUSTOMER_NUMBER", nullable = false)
    private String customerNumber;

    @Column(name = "CUSTOMER_NAME")
    private String customerName;

    @Column(name = "PRODUCT_REFERENCE", nullable = false)
    private String productReference;

    @Column(name = "NRV")
    private double nrv;

}
