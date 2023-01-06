import React, {useCallback} from "react";
import {Box} from "@mui/material";
import OrdersTable from "./OrdersContent/OrdersTable";
import {setOrderTabIndex} from "../../../features/currentDelivery/currentDeliverySlice";
import BasicTabs from "../../../hoc/BasicTabs/BasicTabs";
import {useParams} from "react-router-dom";
import {useGetOrdersForDeliveryQuery, useUpdateOrderMutation} from "../../../api/delivery-orders/delivery-orders.api";
import {useAppDispatch, useAppSelector} from "../../../hooks/redux-hooks";
import {MapPointStatus} from "../../../model/MapPointStatus";
import Order from "../../../model/Order";
import ChooseAddressDialog from "./OrdersContent/ChooseAddressDialog/ChooseAddressDialog";
import {DeliveryType} from "../../../model/DeliveryType";
import Loading from "../../../components/Loading/Loading";
import ExcludeOrderDialog from "./OrdersContent/ExcludeOrderDialog/ExcludeOrderDialog";
import DeliveryTypeDialog from "./OrdersContent/DeliveryTypeDialog/DeliveryTypeDialog";


const Orders = () => {
    const {deliveryId} = useParams();

    const {data: orders, isLoading, error} = useGetOrdersForDeliveryQuery(String(deliveryId));

    const isOrderAddressDialogOpen = useAppSelector(state => state.deliveryOrders.isOrderAddressDialogOpen);
    const isExcludeOrderDialogOpen = useAppSelector(state => state.deliveryOrders.isExcludeOrderDialogOpen);
    const isDeliveryTypeDialogOpen = useAppSelector(state => state.deliveryOrders.isDeliveryTypeDialogOpen);

    const [updateOrder] = useUpdateOrderMutation();
    const {orderTabIndex} = useAppSelector(state => state.currentDelivery);
    const dispatch = useAppDispatch();

    const setOrdersTabHandler = useCallback((index: number) => dispatch(setOrderTabIndex(index)), [dispatch]);

    const updateOrderHandler = useCallback((updatedOrder: Order) => updateOrder(updatedOrder), [dispatch]);

    if (!orders) {
        return <Loading/>;
    }

    const invalidOrders = orders
        .filter(order => order.deliveryType === DeliveryType.REGULAR)
        .filter(order => order.mapPoint.status === MapPointStatus.UNKNOWN);

    const excludedOrders = orders
        .filter(order => order.excluded);


    const tabLabels = [
        {name: `Всі замовлення (${orders.length})`, enabled: true},
        {name: `Потребують уточнення (${invalidOrders.length})`, enabled: true},
        {name: `Вилучені (${excludedOrders.length})`, enabled: true}
    ];

    return <Box sx={{padding: '0 24px'}}>
        <BasicTabs labels={tabLabels} setTabIndex={setOrdersTabHandler} tabIndex={orderTabIndex}>
            <OrdersTable orders={orders} keyPrefix={"all"}
                         updateOrder={updateOrderHandler}
            />

            <OrdersTable orders={invalidOrders} keyPrefix={"invalid"}
                         updateOrder={updateOrderHandler}/>

            <OrdersTable orders={excludedOrders} keyPrefix={"excluded"}
                         updateOrder={updateOrderHandler}/>
        </BasicTabs>

        {
            isOrderAddressDialogOpen && <ChooseAddressDialog/>
        }

        {
            isExcludeOrderDialogOpen && <ExcludeOrderDialog/>
        }

        {
            isDeliveryTypeDialogOpen && <DeliveryTypeDialog/>
        }
    </Box>
}

export default Orders;