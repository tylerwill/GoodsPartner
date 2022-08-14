import OrdersBody from "./OrdersBody/OrdersBody";
import React from "react";
import {Typography} from "@mui/material";
import OrdersHeader from "./OrdersHeader/OrdersHeader";

const OrdersTable = ({order}) => {
    return (<>
            <Typography variant="h6" gutterBottom component="div">
                Замовлення {order.orderNumber} від {order.createdDate}
            </Typography>
            <OrdersHeader
                client={order.clientName}
                address={order.address}
                manager={order.managerFullName}
            />
            <OrdersBody
                products={order.products}
            />
        </>
    );
}

export default OrdersTable;