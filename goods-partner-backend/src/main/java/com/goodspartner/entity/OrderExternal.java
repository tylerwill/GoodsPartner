package com.goodspartner.entity;


import com.goodspartner.dto.ProductDto;
import com.vladmihalcea.hibernate.type.json.JsonType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
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

    @Column(name = "ref_key")
    private String refKey;

    @Column(name = "comment")
    private String comment;

    @Column(name = "created_date")
    private LocalDate createdDate;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "address", referencedColumnName = "orderAddress"),
            @JoinColumn(name = "client_Name", referencedColumnName = "clientName"),
    })
    private AddressExternal addressExternal;

    @Column(name = "manager")
    private String managerFullName;

    @Column(name = "order_weight")
    private double orderWeight;

    @Column(name = "valid_address")
    private boolean validAddress;

    @Type(type = "json")
    @Column(columnDefinition = "jsonb")
    private List<ProductDto> products;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_id", referencedColumnName = "id")
    private Delivery delivery;

    @ManyToOne
    @JoinColumn(name = "car_load_id", referencedColumnName = "id")
    private CarLoad carLoad;
}

