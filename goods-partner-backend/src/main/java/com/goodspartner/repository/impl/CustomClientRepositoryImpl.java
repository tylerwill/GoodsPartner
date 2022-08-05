package com.goodspartner.repository.impl;

import com.goodspartner.entity.Address;
import com.goodspartner.entity.Client;
import com.goodspartner.entity.Order;
import com.goodspartner.repository.CustomClientRepository;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.time.LocalDate;
import java.util.List;

public class CustomClientRepositoryImpl implements CustomClientRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Client> findClientsByOrderDate(LocalDate date) {
        EntityGraph<Client> entityGraph = (EntityGraph<Client>) entityManager.getEntityGraph("client-with-addresses");

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Client> criteriaQuery = builder.createQuery(Client.class);

        Root<Client> clientRoot = criteriaQuery.from(Client.class);
        Join<Client, Address> addressJoin = clientRoot.join("addresses");
        Join<Address, Order> orderJoin = addressJoin.join("orders");

        Predicate shippingDate = builder.equal(orderJoin.get("shippingDate"), date);

        return entityManager.createQuery(criteriaQuery
                        .select(clientRoot)
                        .where(shippingDate))
                .setHint("javax.persistence.fetchgraph", entityGraph)
                .getResultList();
    }
}
