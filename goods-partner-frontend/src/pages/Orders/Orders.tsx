import React, {useEffect} from "react";
import {Box, Typography} from "@mui/material";
import BasicTabs from "../../hoc/BasicTabs/BasicTabs";
import {useAppDispatch, useAppSelector} from "../../hooks/redux-hooks";
import DeliveredOrders from "./DiliveredOrders/DiliveredOrders";
import SkippedOrders from "./SkippedOrders/SkippedOrders";
import {fetchCompletedOrders, fetchSkippedOrders, setTabIndex} from "../../features/orders/ordersSlice";


const Orders = () => {
    const dispatch = useAppDispatch();
    const {tabIndex, completedOrders} = useAppSelector(state => state.orders);

    useEffect(() => {
        dispatch(fetchCompletedOrders());
        dispatch(fetchSkippedOrders());
    }, []);


    const tabLabels = [
        {name: 'Доставленні', enabled: true},
        {name: 'Не доставленні', enabled: true}
    ];

    const styles = {p: 4};

    return <section>
        <Box sx={{mt: 2, display: 'flex', justifyContent: 'space-between', alignItems: 'center'}}>
            <Box sx={{display: 'flex'}}>
                <Typography
                    sx={{mr: 2}}
                    variant="h6" component="h2">
                    Замовлення
                </Typography>
            </Box>

        </Box>

        <Box sx={{marginTop: '16px'}}>
            <BasicTabs labels={tabLabels} tabIndex={tabIndex}
                       setTabIndex={(index: number) => dispatch(setTabIndex(index))} fullWidth={false} styles={styles}>
                <DeliveredOrders completedOrders={completedOrders}/>
                <SkippedOrders/>
            </BasicTabs>

        </Box>

    </section>
}


export default Orders;