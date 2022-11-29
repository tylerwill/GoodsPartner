import React, {useCallback, useEffect} from "react";
import {Box, Breadcrumbs, Button, LinearProgress, Tooltip, Typography} from "@mui/material";
import DeliveryStatusChip from "../../components/DeliveryStatusChip/DeliveryStatusChip";
import {Link, useParams} from "react-router-dom";
import {setCurrentRouteIndex, setTabIndex} from "../../features/currentDelivery/currentDeliverySlice";

import ShoppingCartIcon from '@mui/icons-material/ShoppingCart';
import RouteIcon from '@mui/icons-material/Route';

import DoneIcon from '@mui/icons-material/Done';
import NavigateNextIcon from '@mui/icons-material/NavigateNext';
import {ArrowForward} from "@mui/icons-material";
import Loading from "../../components/Loading/Loading";
import ErrorAlert from "../../components/ErrorAlert/ErrorAlert";
import BasicTabs from "../../hoc/BasicTabs/BasicTabs";
import Orders from "./Orders/Orders";
import Routes from "./Routes/Routes";
import {reformatDate} from "../../util/util";
import useAuth from "../../auth/AuthProvider";
import {useAppDispatch, useAppSelector} from "../../hooks/redux-hooks";
import {useCalculateDeliveryMutation, useGetDeliveryQuery} from '../../api/deliveries/deliveries.api'
import {DeliveryFormationStatus} from "../../model/Delivery";
import {MapPointStatus} from "../../model/MapPointStatus";
import {UserRole} from "../../model/User";
import {useGetOrdersForDeliveryQuery} from "../../api/orders/orders.api";


const Delivery = () => {
    const {deliveryId} = useParams();
    const dispatch = useAppDispatch();
    const {tabIndex} = useAppSelector(state => state.currentDelivery);
    const {data: orders} = useGetOrdersForDeliveryQuery(String(deliveryId));
    const {data: delivery, isLoading, error} = useGetDeliveryQuery(String(deliveryId));
    // @ts-ignore

    const {user} = useAuth();
    const [calculateDelivery] = useCalculateDeliveryMutation();
    const calculateDeliveryHandler = useCallback(() => calculateDelivery(deliveryId!), [deliveryId]);

    // const approveHandler = () => dispatch(approveDelivery(id));

    useEffect(() => {
        dispatch(setTabIndex(0));
        dispatch(setCurrentRouteIndex(0));

    }, [dispatch, deliveryId]);
    if (isLoading || !delivery) {
        return <Loading/>

    }

    const isDriver = user.role === UserRole.DRIVER;
    //
    debugger;
    const isPreCalculationStatus = delivery.formationStatus === DeliveryFormationStatus.ORDERS_LOADED && !isDriver;
    // const isPreApprove = delivery.formationStatus === 'COMPLETED' && !isDriver;
    //
    // const isApproveEnabled = delivery.status !== 'APPROVED';
    //
    const calculated = delivery?.formationStatus === DeliveryFormationStatus.COMPLETED;

    const hasInvalidOrders = orders?.some(order => order.mapPoint.status === MapPointStatus.UNKNOWN);


    const tabLabels = [
        {name: 'Замовлення', enabled: true, icon: <ShoppingCartIcon sx={{mr: 1}}/>},
        {name: 'Маршрути', enabled: calculated, icon: <RouteIcon sx={{mr: 1}}/>},
        // {name: 'Завантаження', enabled: calculated, icon: <InventoryIcon sx={{mr: 1}}/>},
        // {name: 'Історія', enabled: true, icon: <HistorySharpIcon sx={{mr: 1}}/>}
    ];

    const isLoadingBar = delivery && (delivery.formationStatus === DeliveryFormationStatus.ORDERS_LOADING
        || delivery.formationStatus === DeliveryFormationStatus.ROUTE_CALCULATION);

    const setTabIndexHandler = (index: number) => dispatch(setTabIndex(index));

    if (delivery) {
        return <section>
            {isLoadingBar && loadingBar(delivery.formationStatus)}
            {error && <ErrorAlert error={error}/>}
            <Box sx={{mt: 2, display: 'flex', justifyContent: 'space-between', alignItems: 'center'}}>
                <Box sx={{display: 'flex'}}>
                    <Typography
                        sx={{mr: 2}}
                        variant="h6" component="h2">
                        Доставка на {reformatDate(delivery.deliveryDate)}
                    </Typography>
                    <DeliveryStatusChip status={delivery.status}/>
                </Box>

                {isPreCalculationStatus && <CalculateButton enabled={!hasInvalidOrders} onClick={calculateDeliveryHandler}/>}
                {/*{isPreApprove && <ApproveButton enabled={isApproveEnabled} approve={approveHandler}/>}*/}

            </Box>


            {/*/!*TODO: [UI Max] Move to separate component *!/*/}
            {/*/!*TODO: [UI Max] If delivery exist we should place delivery date instead of 'new delivery' *!/*/}
            <Box sx={{mt: 1}}>
                <Breadcrumbs
                    separator={<NavigateNextIcon fontSize="small"/>}
                    aria-label="breadcrumb">
                    <Link color="inherit" to={"/deliveries"}>
                        Доставки
                    </Link>
                    <Typography color="text.primary">Нова доставка</Typography>
                </Breadcrumbs>
            </Box>

            <Box sx={{marginTop: '16px'}}>
                <BasicTabs labels={tabLabels} fullWidth={true} tabIndex={tabIndex} setTabIndex={setTabIndexHandler}>
                    <Orders/>
                    <Routes/>
                    {/*{user.role === 'DRIVER' ? <CarLoad/> : <Shipping productsShipping={delivery.productsShipping}/>}*/}
                    {/*<History/>*/}
                </BasicTabs>

            </Box>

        </section>
    }
}

function loadingBar(formationStatus: DeliveryFormationStatus) {
    let message = 'Розрахування маршрутів'
    if (formationStatus === DeliveryFormationStatus.ORDERS_LOADING) {
        message = 'Завантаження замовлень';
    }

    return <Box sx={{mt: 2, display: 'flex', justifyContent: 'space-between', alignItems: 'center'}}>
        <Box>{message}</Box> <Box sx={{width: '80%'}}><LinearProgress/></Box></Box>
}

interface ActionButtonProps {
    enabled: boolean,
    onClick: () => any
}

function ApproveButton({enabled, onClick}: ActionButtonProps) {
    return <Button variant="contained" color={"success"}
                   onClick={onClick}
                   disabled={!enabled}>
        <DoneIcon sx={{mr: 1, width: "0.7em", height: "0.7em"}}/> Затвердити
    </Button>;
}

function CalculateButton({enabled, onClick}: ActionButtonProps) {
    return <Tooltip title="Для розрахунку маршруту відредагуйте адреси, що потребують уточнення"
                    placement="top"
                    arrow>
                    <span>
                        <Button variant="contained"
                                disabled={!enabled}
                                onClick={onClick}
                        >Розрахувати Маршрут <ArrowForward/></Button>
                    </span>
    </Tooltip>;
}


export default Delivery;