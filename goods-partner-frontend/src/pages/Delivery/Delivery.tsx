import React, {useCallback, useEffect} from 'react'
import {Box, Breadcrumbs, Button, LinearProgress, Tooltip, Typography} from '@mui/material'
import DeliveryStatusChip from '../../components/DeliveryStatusChip/DeliveryStatusChip'
import {Link, useParams} from 'react-router-dom'
import {setCurrentRouteIndex, setTabIndex} from '../../features/currentDelivery/currentDeliverySlice'

import ShoppingCartIcon from '@mui/icons-material/ShoppingCart'
import RouteIcon from '@mui/icons-material/Route'

import DoneIcon from '@mui/icons-material/Done'
import NavigateNextIcon from '@mui/icons-material/NavigateNext'
import {ArrowForward} from '@mui/icons-material'
import Loading from '../../components/Loading/Loading'
import ErrorAlert from '../../components/ErrorAlert/ErrorAlert'
import BasicTabs from '../../hoc/BasicTabs/BasicTabs'
import Orders from './Orders/Orders'
import History from './History/History'
import Routes from './Routes/Routes'
import {reformatDate} from '../../util/util'
import useAuth from '../../auth/AuthProvider'
import {useAppDispatch, useAppSelector} from '../../hooks/redux-hooks'
import {
    useApproveDeliveryMutation,
    useCalculateDeliveryMutation,
    useGetDeliveryQuery
} from '../../api/deliveries/deliveries.api'
import {DeliveryFormationStatus, DeliveryStatus} from '../../model/Delivery'
import {UserRole} from '../../model/User'
import InventoryIcon from '@mui/icons-material/Inventory'

import FactCheckIcon from '@mui/icons-material/FactCheck'
import CarLoad from './CarLoad/CarLoad'
import Shipping from './Shipping/Shipping'

const Delivery = () => {
    const {deliveryId} = useParams()
    const dispatch = useAppDispatch()
    const {tabIndex} = useAppSelector(state => state.currentDelivery)
    const {
        data: delivery,
        isLoading,
        error
    } = useGetDeliveryQuery(String(deliveryId))

    // @ts-ignore
    const {user} = useAuth()
    const [calculateDelivery] = useCalculateDeliveryMutation()
    const [approveDelivery] = useApproveDeliveryMutation()
    const calculateDeliveryHandler = useCallback(
        () => calculateDelivery(deliveryId!),
        [deliveryId]
    )

    const approveDeliveryHandler = useCallback(() => {
        approveDelivery(String(deliveryId))
    }, [deliveryId])

    useEffect(() => {
        dispatch(setTabIndex(0))
        dispatch(setCurrentRouteIndex(0))
    }, [dispatch, deliveryId])

    if (isLoading || !delivery) {
        return <Loading/>
    }

    const isDriver = user.role === UserRole.DRIVER

    const calculationEnabled =
        ( delivery.formationStatus === DeliveryFormationStatus.READY_FOR_CALCULATION
        || delivery.formationStatus === DeliveryFormationStatus.CALCULATION_COMPLETED )
        && !isDriver;

    const recalculationButtonVisible = delivery.formationStatus === DeliveryFormationStatus.CALCULATION_COMPLETED
        && !isDriver && delivery.status !== DeliveryStatus.APPROVED;
    const firstCalculationButtonVisible = delivery.formationStatus === DeliveryFormationStatus.READY_FOR_CALCULATION && !isDriver;

    const isPreApprove =
        delivery.formationStatus ===
        DeliveryFormationStatus.CALCULATION_COMPLETED && !isDriver
    const isApproveEnabled = delivery.status === DeliveryStatus.DRAFT

    const calculated =
        delivery?.formationStatus === DeliveryFormationStatus.CALCULATION_COMPLETED

    const tabLabels = [
        {
            name: 'Замовлення',
            enabled: true,
            icon: <ShoppingCartIcon sx={{mr: 1}}/>
        },
        {
            name: 'Маршрути',
            enabled: calculated,
            icon: <RouteIcon sx={{mr: 1}}/>
        },
        {
            name: 'Завантаження',
            enabled: calculated,
            icon: <InventoryIcon sx={{mr: 1}}/>
        },
        {name: 'Історія', enabled: true, icon: <FactCheckIcon sx={{mr: 1}}/>}
    ]

    const isLoadingBar =
        delivery &&
        (delivery.formationStatus === DeliveryFormationStatus.ORDERS_LOADING ||
            delivery.formationStatus === DeliveryFormationStatus.ROUTE_CALCULATION)

    const setTabIndexHandler = (index: number) => dispatch(setTabIndex(index))

    if (delivery) {
        return (
            <section>
                {isLoadingBar && loadingBar(delivery.formationStatus)}
                {error && <ErrorAlert error={error}/>}
                <Box
                    sx={{
                        mt: 2,
                        display: 'flex',
                        justifyContent: 'space-between',
                        alignItems: 'center'
                    }}
                >
                    <Box sx={{display: 'flex'}}>
                        <Typography sx={{mr: 2}} variant='h6' component='h2'>
                            Доставка на {reformatDate(delivery.deliveryDate)}
                        </Typography>
                        <DeliveryStatusChip status={delivery.status}/>
                    </Box>
                    <Box sx={{display: 'flex', justifyContent: 'flex-end'}}>
                        {firstCalculationButtonVisible && (
                            <CalculateButton
                                title={"Розрахувати маршрут"}
                                enabled={calculationEnabled}
                                onClick={calculateDeliveryHandler}
                            />
                        )}

                        {recalculationButtonVisible && (
                            <CalculateButton
                                title={"Перерахувати маршрут"}
                                enabled={calculationEnabled}
                                onClick={calculateDeliveryHandler}
                            />
                        )}
                        {isPreApprove && (
                            <ApproveButton
                                enabled={isApproveEnabled}
                                onClick={approveDeliveryHandler}
                            />
                        )}
                    </Box>

                </Box>

                {/*/!*TODO: [UI Max] Move to separate component *!/*/}
                {/*/!*TODO: [UI Max] If delivery exist we should place delivery date instead of 'new delivery' *!/*/}
                <Box sx={{mt: 1}}>
                    <Breadcrumbs
                        separator={<NavigateNextIcon fontSize='small'/>}
                        aria-label='breadcrumb'
                    >
                        <Link color='inherit' to={'/deliveries'}>
                            Доставки
                        </Link>
                        <Typography color='text.primary'>Нова доставка</Typography>
                    </Breadcrumbs>
                </Box>

                <Box sx={{marginTop: '16px'}}>
                    <BasicTabs
                        labels={tabLabels}
                        fullWidth={true}
                        tabIndex={tabIndex}
                        setTabIndex={setTabIndexHandler}
                    >
                        <Orders/>
                        <Routes/>
                        {user.role === 'DRIVER' ? <CarLoad/> : <Shipping/>}
                        <History/>
                    </BasicTabs>
                </Box>
            </section>
        )
    }
}

function loadingBar(formationStatus: DeliveryFormationStatus) {
    let message = 'Розрахування маршрутів'
    if (formationStatus === DeliveryFormationStatus.ORDERS_LOADING) {
        message = 'Завантаження замовлень'
    }

    return (
        <Box
            sx={{
                mt: 2,
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center'
            }}
        >
            <Box>{message}</Box>{' '}
            <Box sx={{width: '80%'}}>
                <LinearProgress/>
            </Box>
        </Box>
    )
}

interface ActionButtonProps {
    enabled: boolean
    onClick: () => any
    title?: string
}

function ApproveButton({enabled, onClick}: ActionButtonProps) {
    return (
        <Button
            variant='contained'
            color={'success'}
            onClick={onClick}
            disabled={!enabled}
        >
            <DoneIcon sx={{mr: 1, width: '0.7em', height: '0.7em'}}/> Затвердити
        </Button>
    )
}

function CalculateButton({enabled, onClick, title}: ActionButtonProps) {
    return (
        <Tooltip
            title='Для розрахунку маршруту відредагуйте адреси, що потребують уточнення'
            placement='top'
            arrow
        >
			<span>
				<Button sx={{mr:2}} variant='contained' disabled={!enabled} onClick={onClick}>
					{title}
				</Button>
			</span>
        </Tooltip>
    )
}

export default Delivery
