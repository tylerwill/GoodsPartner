package com.goodspartner.entity;


import com.goodspartner.dto.ProductDto;
import com.vladmihalcea.hibernate.type.json.JsonType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "grandedolce_orders")
@TypeDef(name = "json", typeClass = JsonType.class)
public class OrderExternal {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "orders_external_id_sequence")
    @SequenceGenerator(name = "orders_external_id_sequence", sequenceName = "orders_external_id_sequence")
    private int id;

    @Column(name = "order_number")
    private String orderNumber;

    @Column(name = "created_date")
    private LocalDate createdDate;

    // TODO check if we could map to external address
    @Column(name = "client_name")
    private String clientName;

    @Column(name = "address")
    private String address;

    @Column(name = "manager")
    private String managerFullName;

    @Column(name = "order_weight")
    private double orderWeight;

    @Column(name = "valid_address")
    private boolean validAddress;

    @Type(type = "json")
    @Column(columnDefinition = "jsonb")
    private List<ProductDto> products;
}
