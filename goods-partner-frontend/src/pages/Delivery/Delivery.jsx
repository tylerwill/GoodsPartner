import React, {useEffect} from "react";
import {Backdrop, Box, Breadcrumbs, Button, CircularProgress, Tooltip, Typography} from "@mui/material";
import {ArrowForward} from "@mui/icons-material";
import {Link, useParams} from "react-router-dom";
import NavigateNextIcon from '@mui/icons-material/NavigateNext';
import OrdersContainer from "./Orders/OrdersContainer";
import BasicTabs from "../../hoc/BasicTabs/BasicTabs";
import ShippingContainer from "./Shipping/ShippingContainer";
import DoneIcon from '@mui/icons-material/Done';
import Routes from "./Routes/Routes";
import HistoryContainer from "./History/HistoryContainer";
import DeliveryStatusChip from "../../components/DeliveryStatusChip/DeliveryStatusChip";
import {getOrderById} from "../../reducers/deliveries-reducer";

const Delivery = ({
                      currentDelivery, loadDelivery,
                      linkOrdersToDeliveryAndCalculate,
                      ordersPreview, approve,
                      updateRoutePoint, updateRoute,
                      getOrderById, deliveriesLoading
                  }) => {

    let {id} = useParams();
    const calculated = currentDelivery?.routes?.length > 0;
    const routesForCurrentDelivery = currentDelivery.routes;

    useEffect(() => {
        if (currentDelivery.id !== id) {
            loadDelivery(id);
        }
    }, [currentDelivery.id]);

    if (deliveriesLoading) {
        // TODO: [UI Max] This component using in different places. Should be moved to hoc
        return <Backdrop sx={{color: '#fff', zIndex: (theme) => theme.zIndex.drawer + 1}}
                         open={deliveriesLoading}
        >
            <CircularProgress color="inherit"/>
        </Backdrop>
    }

    const hasInvalidOrders = ordersPreview?.orders
        .some(order => order.mapPoint.status === "UNKNOWN");

    const tabLabels = [
        {name: 'Замовлення', enabled: true},
        {name: 'Маршрути', enabled: calculated},
        {name: 'Завантаження', enabled: calculated},
        {name: 'Історія', enabled: true}];

    return <section>
        <Box sx={{mt: 2, display: 'flex', justifyContent: 'space-between', alignItems: 'center'}}>
            <Box sx={{display: 'flex'}}>
                <Typography
                    sx={{mr: 2}}
                    variant="h6" component="h2">
                    {/*TODO: [UI Max] Format date to be same as in design*/}
                    Доставка на {currentDelivery.deliveryDate}
                </Typography>
                <DeliveryStatusChip status={currentDelivery.status}/>
            </Box>

            {calculated ?
                <Button variant="contained" color={"success"}
                        onClick={() => approve(currentDelivery.id)}
                        disabled={currentDelivery.status !== 'DRAFT'}>
                    <DoneIcon sx={{mr: 1, width: '0.7em', height: '0.7em'}}/> Затвердити
                </Button>

                :
                <Tooltip title="Для розрахунку маршруту відредагуйте адреси, що потребують уточнення"
                         placement="top"
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
                    getOrderById={getOrderById}
                />
                <ShippingContainer/>
                <HistoryContainer/>
            </BasicTabs>

        </Box>


    </section>
}

export default Delivery;