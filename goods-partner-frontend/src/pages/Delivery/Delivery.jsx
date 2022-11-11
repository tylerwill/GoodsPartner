import React, {useEffect} from "react";
import {Box, Breadcrumbs, Button, LinearProgress, Tooltip, Typography} from "@mui/material";
import DeliveryStatusChip from "../../components/DeliveryStatusChip/DeliveryStatusChip";
import {Link, useParams} from "react-router-dom";
import {useDispatch, useSelector} from "react-redux";
import {
    approveDelivery,
    calculateDelivery,
    fetchDelivery,
    fetchDeliveryForDriver, setCurrentRouteIndex, setTabIndex
} from "../../features/currentDelivery/currentDeliverySlice";

import ShoppingCartIcon from '@mui/icons-material/ShoppingCart';
import RouteIcon from '@mui/icons-material/Route';
import InventoryIcon from '@mui/icons-material/Inventory';
import HistorySharpIcon from '@mui/icons-material/HistorySharp';

import DoneIcon from '@mui/icons-material/Done';
import NavigateNextIcon from '@mui/icons-material/NavigateNext';
import {ArrowForward} from "@mui/icons-material";
import Loading from "../../components/Loading/Loading";
import ErrorAlert from "../../components/ErrorAlert/ErrorAlert";
import BasicTabs from "../../hoc/BasicTabs/BasicTabs";
import Orders from "./Orders/Orders";
import Routes from "./Routes/Routes";
import History from "./History/History";
import Shipping from "./Shipping/Shipping";
import {reformatDate} from "../../util/util";
import useAuth from "../../auth/AuthProvider";
import CarLoad from "./CarLoad/CarLoad";


const Delivery = () => {
    const {id} = useParams();
    const dispatch = useDispatch();
    const {delivery, loading, error, tabIndex} = useSelector(state => state.currentDelivery);
    const {user} = useAuth();

    const calculateHandler = () => dispatch(calculateDelivery(delivery));
    const approveHandler = () => dispatch(approveDelivery(id));

    useEffect(() => {
        if (!delivery || delivery.id !== id) {
            if (user.role === 'DRIVER') {
                dispatch(fetchDeliveryForDriver(id));
            } else {
                dispatch(fetchDelivery(id));
            }
            dispatch(setTabIndex(0));
            dispatch(setCurrentRouteIndex(0));
        }

    }, [delivery, dispatch, id]);

    if (loading) {
        return <Loading/>
    }
    const isDriver = user.role === 'DRIVER';

    const hasInvalidOrders = delivery.orders.some(order => order.mapPoint.status === "UNKNOWN");

    const isPreCalculationStatus = delivery.formationStatus === 'ORDERS_LOADED' && !isDriver;
    const isPreApprove = delivery.formationStatus === 'COMPLETED' && !isDriver;

    const isApproveEnabled = delivery.status !== 'APPROVED';

    const calculated = delivery.formationStatus === 'COMPLETED';

    const tabLabels = [
        {name: 'Замовлення', enabled: true, icon: <ShoppingCartIcon sx={{mr: 1}}/>},
        {name: 'Маршрути', enabled: calculated, icon: <RouteIcon sx={{mr: 1}}/>},
        {name: 'Завантаження', enabled: calculated, icon: <InventoryIcon sx={{mr: 1}}/>},
        {name: 'Історія', enabled: true, icon: <HistorySharpIcon sx={{mr: 1}}/>}
    ];

    const isLoadingBar = delivery.formationStatus === 'ORDERS_LOADING' || delivery.formationStatus === 'ROUTE_CALCULATION';

    const setTabIndexHandler = (index) => {
        dispatch(setTabIndex(index));
    }

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

            {isPreCalculationStatus && <CalculateButton enabled={!hasInvalidOrders} calculate={calculateHandler}/>}
            {isPreApprove && <ApproveButton enabled={isApproveEnabled} approve={approveHandler}/>}

        </Box>


        {/*/!*TODO: [UI Max] Move to separate component *!/*/}
        {/*/!*TODO: [UI Max] If delivery exist we should place delivery date instead of 'new delivery' *!/*/}
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
            <BasicTabs labels={tabLabels} fullWidth={true} tabIndex={tabIndex} setTabIndex={setTabIndexHandler}>
                <Orders/>
                <Routes/>
                {user.role === 'DRIVER' ? <CarLoad/> : <Shipping productsShipping={delivery.productsShipping}/>}
                <History/>
            </BasicTabs>

        </Box>

    </section>
}

function loadingBar(formationStatus) {
    let message = 'Розрахування маршрутів'
    if (formationStatus === 'ORDERS_LOADING') {
        message = 'Завантаження замовлень';
    }

    return <Box sx={{mt: 2, display: 'flex', justifyContent: 'space-between', alignItems: 'center'}}>
        <Box>{message}</Box> <Box sx={{width: '80%'}}><LinearProgress/></Box></Box>
}

function ApproveButton({enabled, approve}) {
    return <Button variant="contained" color={"success"}
                   onClick={approve}
                   disabled={!enabled}>
        <DoneIcon sx={{mr: 1, width: "0.7em", height: "0.7em"}}/> Затвердити
    </Button>;
}

function CalculateButton({enabled, calculate}) {
    return <Tooltip title="Для розрахунку маршруту відредагуйте адреси, що потребують уточнення"
                    placement="top"
                    arrow>
                    <span>
                        <Button variant="contained"
                                disabled={!enabled}
                                onClick={calculate}
                        >Розрахувати Маршрут <ArrowForward/></Button>
                    </span>
    </Tooltip>;
}


export default Delivery;