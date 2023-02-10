package com.goodspartner.entity;

import com.goodspartner.dto.MapPoint;
import com.goodspartner.dto.Product;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
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
@TypeDef(name = "JSONB", typeClass = JsonBinaryType.class)
public class OrderExternal {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "orders_external_id_sequence")
    @SequenceGenerator(name = "orders_external_id_sequence", sequenceName = "orders_external_id_sequence")
    private Long id;

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

    @Column(name = "frozen")
    private boolean frozen;

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

    @Column(name = "exclude_reason")
    private String excludeReason;

    @Column(name = "dropped")
    private boolean dropped;

    @Column(name = "client_name")
    private String clientName;

    @Column(name = "address")
    private String address;

    @Type(type = "JSONB")
    @Column(columnDefinition = "jsonb")
    private MapPoint mapPoint;

    @Type(type = "JSONB")
    @Column(columnDefinition = "jsonb", updatable = false) // We dont update Products on our side !!
    private List<Product> products;

    @Column(name = "order_weight")
    private double orderWeight;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_id", referencedColumnName = "id")
    private Delivery delivery;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_load_id", referencedColumnName = "id")
    private CarLoad carLoad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_point_id", referencedColumnName = "id")
    private RoutePoint routePoint;
}

