import React, {useCallback} from "react";
import {Box} from "@mui/material";
import OrdersTable from "./OrdersContent/OrdersTable";
import {
    setOrderTabIndex,
    updateAddressForOrder,
    updateOrder
} from "../../../features/currentDelivery/currentDeliverySlice";
import BasicTabs from "../../../hoc/BasicTabs/BasicTabs";
import {useParams} from "react-router-dom";
import {useGetOrdersForDeliveryQuery} from "../../../api/orders/orders.api";
import {useAppDispatch, useAppSelector} from "../../../hooks/redux-hooks";
import {MapPointStatus} from "../../../model/MapPointStatus";
import Order from "../../../model/Order";


const Orders = () => {
    const {deliveryId} = useParams();

    const {data: orders, isLoading, error} = useGetOrdersForDeliveryQuery(String(deliveryId));

    const {orderTabIndex} = useAppSelector(state => state.currentDelivery);
    const dispatch = useAppDispatch();

    const setOrdersTabHandler = useCallback((index: number) => dispatch(setOrderTabIndex(index)), []);

    const updateOrderHandler = useCallback((updatedOrder: Order) => {
        dispatch(updateOrder(updatedOrder));
    }, []);

    const updateOrderAddressHandler = useCallback((updatedAddressInfo: any) => {
        dispatch(updateAddressForOrder(updatedAddressInfo));
    }, []);

    const [orderAddressDialogOpen, setOrderAddressDialogOpen] = React.useState(false);
    const [editedOrder, setEditedOrder] = React.useState(null);

    if (!orders) {
        return <div>No orders</div>
    }

    const invalidOrders = orders ? orders
        .filter(order => order.mapPoint.status === MapPointStatus.UNKNOWN) : [];


    const tabLabels = [
        {name: `Всі замовлення (${orders.length})`, enabled: true},
        {name: `потребують уточнення (${invalidOrders.length})`, enabled: true}
    ];
    return <Box sx={{padding: '0 24px'}}>
        <BasicTabs labels={tabLabels} setTabIndex={setOrdersTabHandler} tabIndex={orderTabIndex}>
            <OrdersTable orders={orders} keyPrefix={"all"}
                         setEditedOrder={setEditedOrder}
                         updateOrder={updateOrderHandler}
                         setOrderAddressDialogOpen={updateOrderAddressHandler}
            />

            <OrdersTable orders={invalidOrders} keyPrefix={"invalid"}
                         setEditedOrder={setEditedOrder}
                         updateOrder={updateOrderHandler}
                         setOrderAddressDialogOpen={setOrderAddressDialogOpen}/>
        </BasicTabs>

        {/*{*/}
        {/*    orderAddressDialogOpen && <ChooseAddressDialog open={orderAddressDialogOpen}*/}
        {/*                                                   handleClose={() => setOrderAddressDialogOpen(false)}*/}
        {/*                                                   order={editedOrder}*/}
        {/*                                                   updateAddressForOrder={updateOrderAddressHandler}/>*/}
        {/*}*/}
    </Box>
}

export default Orders;