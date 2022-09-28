import React from "react";
import {Backdrop, Box, Card, CircularProgress} from "@mui/material";
import OrdersContent from "./OrdersContent/OrdersContent";

const Orders = ({ordersPreview, loading, updatePreviewOrderAddress, currentDeliveryOrders}) => {

    const hasLinkedOrders = currentDeliveryOrders.length!==0;
    const showBackDrop = loading || (!ordersPreview && !hasLinkedOrders);

    const ordersToShow = hasLinkedOrders ? currentDeliveryOrders : ordersPreview?.orders;
    return <section>
        <Card variant="outlined">
            <Box>
                {showBackDrop ? notLoadedYet(showBackDrop)
                    : <OrdersContent orders={ordersToShow} updatePreviewOrderAddress={updatePreviewOrderAddress}/>}
            </Box>
        </Card>
    </section>
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
