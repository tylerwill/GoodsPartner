package com.goodspartner.entity;

import com.goodspartner.dto.Product;
import com.vladmihalcea.hibernate.type.json.JsonType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "grandedolce_orders")
@TypeDef(name = "json", typeClass = JsonType.class)
public class OrderExternal {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "orders_external_id_sequence")
    @SequenceGenerator(name = "orders_external_id_sequence", sequenceName = "orders_external_id_sequence")
    private Integer id;

    @Column(name = "ref_key")
    private String refKey;

    @Column(name = "order_number")
    private String orderNumber;

    @Column(name = "shipping_date")
    private LocalDate shippingDate;

    @Column(name = "comment")
    private String comment;

    @Column(name = "manager")
    private String managerFullName;

    @Column(name = "is_frozen")
    private boolean isFrozen;

    @Column(name = "delivery_start")
    private LocalTime deliveryStart;

    @Column(name = "delivery_finish")
    private LocalTime deliveryFinish;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "delivery_type")
    private DeliveryType deliveryType;

    @Column(name = "reschedule_date")
    private LocalDate rescheduleDate;

    @Column(name = "excluded")
    private boolean excluded;

    @Column(name = "dropped")
    private boolean dropped;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumns({
            @JoinColumn(name = "client_name", referencedColumnName = "client_name"),
            @JoinColumn(name = "address", referencedColumnName = "order_address")
    })
    private AddressExternal addressExternal;

    @Type(type = "json")
    @Column(columnDefinition = "jsonb")
    private List<Product> products;

    @Column(name = "order_weight")
    private double orderWeight;

    @ManyToOne
    @JoinColumn(name = "delivery_id", referencedColumnName = "id")
    private Delivery delivery;

    @ManyToOne
    @JoinColumn(name = "car_load_id", referencedColumnName = "id")
    private CarLoad carLoad;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "route_point_id", referencedColumnName = "id")
    private RoutePoint routePoint;
}

