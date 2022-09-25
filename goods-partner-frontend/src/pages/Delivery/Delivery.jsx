import React, {useEffect} from "react";
import {Box, Breadcrumbs, Button,  Typography} from "@mui/material";
import {ArrowForward} from "@mui/icons-material";
import {Link, useParams} from "react-router-dom";
import NavigateNextIcon from '@mui/icons-material/NavigateNext';
import OrdersContainer from "../Orders/OrdersContainer";

const Delivery = ({currentDelivery, loadDelivery}) => {
    let {id} = useParams();
    console.log("currentDelivery", currentDelivery);
    useEffect(() => {
        if (currentDelivery.id !== id) {
            loadDelivery(id);
        }
    }, []);

    return <section>
        <Box sx={{mt: 2, display: 'flex', justifyContent: 'space-between', alignItems: 'center'}}>
            <Typography variant="h6" component="h2">
                {/*TODO: [UI Max] Format date to be same as in design*/}
                Доставка на {currentDelivery.deliveryDate}
            </Typography>
            <Button variant="contained" disabled>Розрахувати Маршрут <ArrowForward/></Button>
        </Box>

        <Breadcrumbs
            sx={{mt:1}}
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