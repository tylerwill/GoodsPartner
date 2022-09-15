package com.goodspartner.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "deliveries")
public class Delivery {

    @Id
    @Column(name = "id",
            updatable = false)
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;

    @Column(name = "delivery_date")
    private LocalDate deliveryDate;

    @OneToMany(mappedBy = "delivery")
    private List<Route> routes;

    @OneToMany(mappedBy = "delivery")
    private List<OrderExternal> orders;

    @OneToMany(mappedBy = "delivery")
    private List<CarLoad> carLoads;

    @Enumerated(value = EnumType.STRING)
    @Column(length = 8)
    private DeliveryStatus status;

}
