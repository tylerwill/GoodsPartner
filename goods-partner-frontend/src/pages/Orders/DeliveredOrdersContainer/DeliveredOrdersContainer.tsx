import {useGetCompletedQuery} from "../../../api/orders/orders.api";
import {OrdersBasicTable} from "../OrdersBasicTable/OrdersBasicTable";
import Loading from "../../../components/Loading/Loading";
import React from "react";

export const DeliveredOrdersContainer = () => {
    const {data: orders, isLoading} = useGetCompletedQuery();
    if (isLoading) {
        return <Loading/>
    }
    return <OrdersBasicTable orders={orders}/>
}