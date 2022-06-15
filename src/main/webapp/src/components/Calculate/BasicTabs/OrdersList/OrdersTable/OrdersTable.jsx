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
            client={order.orderData.clientName}
            address={order.orderData.address}
            manager={order.orderData.managerFullName}
        />
        <OrdersBody
            products={order.orderData.products}
        />
      </>
  );
}

export default OrdersTable;