package com.goodspartner.repository;

import com.goodspartner.entity.AddressExternal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressExternalRepository extends JpaRepository<AddressExternal, AddressExternal.OrderAddressId> {

}