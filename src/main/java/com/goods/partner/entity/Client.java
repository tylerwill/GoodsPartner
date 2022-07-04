package com.goods.partner.entity;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "clients")
@NamedEntityGraph(name = "client-with-addresses",
        attributeNodes = {
                @NamedAttributeNode("addresses")
        })
public class Client {

    @Id
    private int id;

    private String name;

    @OneToMany(
            mappedBy = "client"
    )
    private List<Address> addresses;
}
