import React, {useEffect} from "react";
import {Box, Breadcrumbs, Button, Tooltip, Typography} from "@mui/material";
import {ArrowForward} from "@mui/icons-material";
import {Link, useParams} from "react-router-dom";
import NavigateNextIcon from '@mui/icons-material/NavigateNext';
import OrdersContainer from "./Orders/OrdersContainer";
import BasicTabs from "../../hoc/BasicTabs/BasicTabs";
import ShippingContainer from "./Shipping/ShippingContainer";
import DoneIcon from '@mui/icons-material/Done';
import Routes from "./Routes/Routes";

const Delivery = ({
                      currentDelivery, loadDelivery,
                      linkOrdersToDeliveryAndCalculate,
                      ordersPreview, approve,
                      updateRoutePoint, updateRoute
                  }) => {

    let {id} = useParams();

    const calculated = currentDelivery?.orders?.length > 0;

    const routesForCurrentDelivery = currentDelivery.routes;

    useEffect(() => {
        if (currentDelivery.id !== id) {
            loadDelivery(id);
        }
    }, [currentDelivery.id]);

    const hasInvalidOrders = ordersPreview?.orders
        .some(order => order.mapPoint.status === "UNKNOWN");

    const tabLabels = ['Замовлення', 'Маршрути', 'Завантаження'];
    debugger;
    return <section>
        <Box sx={{mt: 2, display: 'flex', justifyContent: 'space-between', alignItems: 'center'}}>
            <Typography variant="h6" component="h2">
                {/*TODO: [UI Max] Format date to be same as in design*/}
                Доставка на {currentDelivery.deliveryDate}
            </Typography>

            {calculated ?
                <Button variant="contained" color={"success"}
                        onClick={() => approve(currentDelivery.id)}
                        disabled={currentDelivery.status !== 'DRAFT'}>
                    <DoneIcon sx={{mr: 1, width: '0.7em', height: '0.7em'}}/> Затвердити
                </Button>

                :
                <Tooltip title="Для розрахунку маршруту відредагуйте адреси, що потребують уточнення" placement="top"
                         arrow>
                <span>
                    <Button variant="contained"
                            disabled={hasInvalidOrders}
                            onClick={linkOrdersToDeliveryAndCalculate}>Розрахувати Маршрут <ArrowForward/></Button>
                </span>
                </Tooltip>
            }


        </Box>


        {/*TODO: [UI Max] Move to separate component */}
        {/*TODO: [UI Max] If delivery exist we should place delivery date instead of 'new delivery' */}
        <Breadcrumbs
            sx={{mt: 1}}
            separator={<NavigateNextIcon fontSize="small"/>}
            aria-label="breadcrumb">
            <Link color="inherit" to={"/deliveries"}>
                Доставки
            </Link>
            <Typography color="text.primary">Нова доставка</Typography>

        </Breadcrumbs>


        <Box sx={{marginTop: '16px'}}>
            {/*TODO: [UI] Add icons */}
            {/*TODO: [UI Max] disable some labels if route is not in calculated status */}
            <BasicTabs labels={tabLabels} fullWidth={true}>
                <OrdersContainer/>
                <Routes
                    deliveryDate={currentDelivery.deliveryDate}
                    routes={routesForCurrentDelivery}
                    updateRoutePoint={updateRoutePoint}
                    updateRoute={updateRoute}
                />
                <ShippingContainer/>
            </BasicTabs>

        </Box>


    </section>
}

export default Delivery;