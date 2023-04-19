import {useGetSkippedQuery} from "../../../api/orders/orders.api";
import OrdersTableWithReschedule from "../OrdersTableWithReschedule/OrdersTableWithReschedule";
import Loading from "../../../components/Loading/Loading";
import React from "react";

export const SkippedOrdersContainer = () => {
    const {data: orders, isLoading} = useGetSkippedQuery();
    if (isLoading) {
        return <Loading/>
    }
    return <OrdersTableWithReschedule orders={orders} hasExcluded={true}/>
}