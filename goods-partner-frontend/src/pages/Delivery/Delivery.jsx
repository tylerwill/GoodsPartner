import React, {useEffect} from "react";
import {Box, Breadcrumbs, Button, Tooltip, Typography} from "@mui/material";
import {ArrowForward} from "@mui/icons-material";
import {Link, useParams} from "react-router-dom";
import NavigateNextIcon from '@mui/icons-material/NavigateNext';
import OrdersContainer from "../Orders/OrdersContainer";

const Delivery = ({currentDelivery, loadDelivery, linkOrdersToDeliveryAndCalculate, ordersPreview}) => {
    let {id} = useParams();

    useEffect(() => {
        if (currentDelivery.id !== id) {
            loadDelivery(id);
        }
    }, [currentDelivery.id]);

    const hasInvalidOrders = ordersPreview?.orders
        .some(order => order.mapPoint.status === "UNKNOWN");

    return <section>
        <Box sx={{mt: 2, display: 'flex', justifyContent: 'space-between', alignItems: 'center'}}>
            <Typography variant="h6" component="h2">
                {/*TODO: [UI Max] Format date to be same as in design*/}
                Доставка на {currentDelivery.deliveryDate}
            </Typography>
            <Tooltip title="Для розрахунку маршруту відредагуйте адреси, що потребують уточнення" placement="top" arrow>
                <span>
                    <Button variant="contained"
                            disabled={hasInvalidOrders}
                            onClick={linkOrdersToDeliveryAndCalculate}>Розрахувати Маршрут <ArrowForward/></Button>
                </span>
            </Tooltip>
        </Box>


        {/*TODO: [UI Max] Move to separate component */}
        <Breadcrumbs
            sx={{mt: 1}}
            separator={<NavigateNextIcon fontSize="small"/>}
            aria-label="breadcrumb">
            <Link color="inherit" to={"/deliveries"}>
                Доставки
            </Link>
            <Typography color="text.primary">Нова доставка</Typography>

        </Breadcrumbs>

        <Box sx={{mt: 2}}>
            <OrdersContainer/>
        </Box>


    </section>
}

export default Delivery;