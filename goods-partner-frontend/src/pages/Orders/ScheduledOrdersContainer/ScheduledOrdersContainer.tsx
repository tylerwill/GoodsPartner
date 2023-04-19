import {useGetScheduledQuery} from "../../../api/orders/orders.api";
import OrdersTableWithReschedule from "../OrdersTableWithReschedule/OrdersTableWithReschedule";
import Loading from "../../../components/Loading/Loading";
import React from "react";

export const ScheduledOrdersContainer = () => {
    const {data: orders, isLoading} = useGetScheduledQuery();
    if (isLoading) {
        return <Loading/>
    }
    return <OrdersTableWithReschedule orders={orders} hasExcluded={false}/>
}