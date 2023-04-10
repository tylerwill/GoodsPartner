import React, {useCallback} from 'react'
import {Box} from '@mui/material'
// @ts-ignore
import OrdersTable from './OrdersContent/OrdersTable'
import {setOrderTabIndex} from '../../../features/currentDelivery/currentDeliverySlice'
// @ts-ignore
import BasicTabs from '../../../hoc/BasicTabs/BasicTabs'
import {useParams} from 'react-router-dom'
import {useGetOrdersForDeliveryQuery, useUpdateOrderMutation} from '../../../api/delivery-orders/delivery-orders.api'
import {useAppDispatch, useAppSelector} from '../../../hooks/redux-hooks'
import Order from '../../../model/Order'
import ChooseAddressDialog from './OrdersContent/ChooseAddressDialog/ChooseAddressDialog'
import Loading from '../../../components/Loading/Loading'
import ExcludeOrderDialog from './OrdersContent/ExcludeOrderDialog/ExcludeOrderDialog'
import DeliveryTypeDialog from './OrdersContent/DeliveryTypeDialog/DeliveryTypeDialog'
import {MapPointStatus} from "../../../model/MapPoint";
import {DeliveryType} from "../../../model/Delivery";

export const Orders = () => {
    const {deliveryId} = useParams()

    const {
        data: orders
    } = useGetOrdersForDeliveryQuery(String(deliveryId))

    const isOrderAddressDialogOpen = useAppSelector(
        state => state.deliveryOrders.isOrderAddressDialogOpen
    )
    const isExcludeOrderDialogOpen = useAppSelector(
        state => state.deliveryOrders.isExcludeOrderDialogOpen
    )
    const isDeliveryTypeDialogOpen = useAppSelector(
        state => state.deliveryOrders.isDeliveryTypeDialogOpen
    )

    const [updateOrder] = useUpdateOrderMutation()
    const {orderTabIndex} = useAppSelector(state => state.currentDelivery)
    const dispatch = useAppDispatch()

    const setOrdersTabHandler = useCallback(
        (index: number) => dispatch(setOrderTabIndex(index)),
        [dispatch]
    )

    const updateOrderHandler = useCallback(
        (updatedOrder: Order) => updateOrder(updatedOrder),
        [dispatch]
    )

    if (!orders) {
        return <Loading/>
    }

    const {tabs, tabLabels} = getTabsAndOrders(orders);

    return (
        <Box sx={{padding: '0 24px'}}>
            <BasicTabs
                labels={tabLabels}
                setTabIndex={setOrdersTabHandler}
                tabIndex={orderTabIndex}
            >

                {
                    tabs.map(e => <OrdersTable
                        basic={e.basic}
                        isExcluded={e.isExcluded}
                        orders={e.orders}
                        keyPrefix={e.keyPrefix}
                        key={"OrderTab" + e.keyPrefix}
                        updateOrder={updateOrderHandler}
                    />)
                }

            </BasicTabs>

            {isOrderAddressDialogOpen && <ChooseAddressDialog/>}

            {isExcludeOrderDialogOpen && <ExcludeOrderDialog/>}

            {isDeliveryTypeDialogOpen && <DeliveryTypeDialog/>}
        </Box>
    )
}

interface OrdersTabsAndLabels {
    tabLabels: any[]
    tabs: any[]
}

const getTabsAndOrders = (orders: Order[]): OrdersTabsAndLabels => {
    // TODO: Maybe use memo to not recalculate
    const ordersWithInvalidAddress = orders
        .filter(order => order.deliveryType === DeliveryType.REGULAR)
        .filter(order => order.mapPoint.status === MapPointStatus.UNKNOWN)
        .filter(order => !order.excluded)

    const excludedOrders = orders.filter(order => order.excluded)

    const grandeDolceOrders = orders
        .filter(order => order.deliveryType === DeliveryType.REGULAR)
        .filter(order => !order.excluded)
        .filter(order => !order.dropped);

    const postalOrders = orders
        .filter(order => order.deliveryType === DeliveryType.POSTAL)
        .filter(order => !order.excluded)
        .filter(order => !order.dropped);

    const selfOrders = orders
        .filter(order => order.deliveryType === DeliveryType.SELF_SERVICE)
        .filter(order => !order.excluded)
        .filter(order => !order.dropped);

    const prePackOrders = orders
        .filter(order => order.deliveryType === DeliveryType.PRE_PACKING)
        .filter(order => !order.excluded)
        .filter(order => !order.dropped);

    const droppedOrders = orders.filter(order => order.dropped);

    const tabLabels = [];

    const tabs = [];

    if (grandeDolceOrders.length !== 0) {
        tabLabels.push({name: `Grande Dolce (${grandeDolceOrders.length})`, enabled: true});
        tabs.push({
            orders: grandeDolceOrders,
            keyPrefix: 'grandeDolce',
            basic: true
        });
    }

    if (postalOrders.length !== 0) {
        tabLabels.push({name: `Пошта (${postalOrders.length})`, enabled: true});
        tabs.push({
            orders: postalOrders,
            keyPrefix: 'postalOrders'
        });
    }

    if (selfOrders.length !== 0) {
        tabLabels.push({name: `Самовивіз (${selfOrders.length})`, enabled: true});
        tabs.push({
            orders: selfOrders,
            keyPrefix: 'selfOrders'
        });
    }

    if (prePackOrders.length !== 0) {
        tabLabels.push({name: `Фасовка (${prePackOrders.length})`, enabled: true});
        tabs.push({
            orders: prePackOrders,
            keyPrefix: 'prePackOrders'
        });
    }


    if (excludedOrders.length !== 0) {
        tabLabels.push({name: `Вилучені (${excludedOrders.length})`, enabled: true});
        tabs.push({
            orders: excludedOrders,
            keyPrefix: 'excluded',
            isExcluded: true,
            basic: true
        });
    }

    if (droppedOrders.length !== 0) {
        tabLabels.push({name: `Нерозраховані (${droppedOrders.length})`, enabled: true});
        tabs.push({
            orders: droppedOrders,
            keyPrefix: 'dropped',
            basic: true
        });
    }

    if (ordersWithInvalidAddress.length !== 0) {
        tabLabels.push({name: `Потребують уточнення (${ordersWithInvalidAddress.length})`, enabled: true});
        tabs.push({
            orders: ordersWithInvalidAddress,
            keyPrefix: 'invalid',
            basic: true
        });
    }

    return {
        tabs,
        tabLabels
    }
}