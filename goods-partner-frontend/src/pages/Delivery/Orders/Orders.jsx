import React, {useCallback} from "react";
import {Box} from "@mui/material";
import OrdersTable from "./OrdersContent/OrdersTable";
import {useDispatch, useSelector} from "react-redux";
import {updateAddressForOrder, updateOrder} from "../../../features/currentDelivery/currentDeliverySlice";
import BasicTabs from "../../../hoc/BasicTabs/BasicTabs";
import ChooseAddressDialog from "./OrdersContent/ChooseAddressDialog/ChooseAddressDialog";

const Orders = () => {
    const {orders} = useSelector(state => state.currentDelivery.delivery);
    const dispatch = useDispatch();
    const updateOrderHandler = useCallback((updatedOrder) => {
        dispatch(updateOrder(updatedOrder));
    }, []);

    const updateOrderAddressHandler = useCallback((updatedAddressInfo) => {
        dispatch(updateAddressForOrder(updatedAddressInfo));
    }, []);

    const [orderAddressDialogOpen, setOrderAddressDialogOpen] = React.useState(false);
    const [editedOrder, setEditedOrder] = React.useState(null);

    const invalidOrders = orders
        .filter(order => order.mapPoint.status === "UNKNOWN");


    const tabLabels = [
        {name: `Всі замовлення (${orders.length})`, enabled: true},
        {name: `потребують уточнення (${invalidOrders.length})`, enabled: true}
    ];
    return <Box sx={{padding: '0 24px'}}>
        <BasicTabs labels={tabLabels}>
            <OrdersTable orders={orders} keyPrefix={"all"}
                         setEditedOrder={setEditedOrder} updateOrder={updateOrderHandler}
                         setOrderAddressDialogOpen={updateOrderAddressHandler}/>

            <OrdersTable orders={invalidOrders} keyPrefix={"invalid"}
                         setEditedOrder={setEditedOrder} updateOrder={updateOrderHandler}
                         setOrderAddressDialogOpen={setOrderAddressDialogOpen}/>
        </BasicTabs>

        {
            orderAddressDialogOpen && <ChooseAddressDialog open={orderAddressDialogOpen}
                                                           handleClose={() => setOrderAddressDialogOpen(false)}
                                                           order={editedOrder}
                                                           updateAddressForOrder={updateOrderAddressHandler}/>
        }
    </Box>
}

export default Orders;