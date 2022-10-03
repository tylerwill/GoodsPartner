import React from "react";
import {Backdrop, Box, CircularProgress} from "@mui/material";
import OrdersContent from "./OrdersContent/OrdersContent";

const Orders = ({ordersPreview, loading, updatePreviewOrderAddress, currentDeliveryOrders}) => {

    const hasLinkedOrders = currentDeliveryOrders && currentDeliveryOrders.length !== 0;
    const showBackDrop = loading || (!ordersPreview && !hasLinkedOrders);

    const ordersToShow = hasLinkedOrders ? currentDeliveryOrders : ordersPreview?.orders;
    // TODO: [UI] Check how many times render calls
    return <Box sx={{padding: '0 24px'}}>
            {showBackDrop ? notLoadedYet(showBackDrop)
                : <OrdersContent orders={ordersToShow} updatePreviewOrderAddress={updatePreviewOrderAddress}/>}
    </Box>
}

function notLoadedYet(showBackDrop) {
    return (<>
        <Backdrop
            sx={{color: '#fff', zIndex: (theme) => theme.zIndex.drawer + 1}}
            open={showBackDrop}
        >
            <CircularProgress color="inherit"/>
        </Backdrop>
    </>)
}

export default Orders;
