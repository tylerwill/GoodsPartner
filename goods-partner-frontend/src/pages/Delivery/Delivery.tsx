import React, {useCallback, useEffect, useState} from 'react'
import {Box, Breadcrumbs, Button, LinearProgress, Tooltip, Typography} from '@mui/material'
import DeliveryStatusChip from '../../components/DeliveryStatusChip/DeliveryStatusChip'
import {Link, Outlet, useParams} from 'react-router-dom'
import {setCurrentRouteIndex} from '../../features/currentDelivery/currentDeliverySlice'

import DoneIcon from '@mui/icons-material/Done'
import NavigateNextIcon from '@mui/icons-material/NavigateNext'
import Loading from '../../components/Loading/Loading'
import ErrorAlert from '../../components/ErrorAlert/ErrorAlert'
import {reformatDate} from '../../util/util'
import useAuth from '../../auth/AuthProvider'
import {useAppDispatch} from '../../hooks/redux-hooks'
import {
    useApproveDeliveryMutation,
    useCalculateDeliveryMutation,
    useGetDeliveryQuery
} from '../../api/deliveries/deliveries.api'
import {DeliveryFormationStatus, DeliveryStatus} from '../../model/Delivery'
import {UserRole} from '../../model/User'
import {ConfirmationDialog} from "../../components/ConfirmationDialog/ConfirmationDialog";
import {DeliveryNav} from "./DeliveryNav/DeliveryNav";

const Delivery = () => {
    const {deliveryId} = useParams()
    const dispatch = useAppDispatch()
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

    const [recalculateConfirmationDialogOpen, setRecalculateConfirmationDialogOpen] = useState(false);
    const [approveConfirmationDialogOpen, setApproveConfirmationDialogOpen] = useState(false);

    const approveDeliveryHandler = useCallback(() => {
        approveDelivery(String(deliveryId))
    }, [deliveryId])

    useEffect(() => {
        dispatch(setCurrentRouteIndex(0))
    }, [dispatch, deliveryId])

    if (isLoading || !delivery) {
        return <Loading/>
    }

    const isDriver = user.role === UserRole.DRIVER

    const calculationFailed = delivery.formationStatus === DeliveryFormationStatus.ROUTE_CALCULATION_FAILED;

    const calculationEnabled =
        (delivery.formationStatus === DeliveryFormationStatus.READY_FOR_CALCULATION
            || delivery.formationStatus === DeliveryFormationStatus.ROUTE_CALCULATION_FAILED
            || delivery.formationStatus === DeliveryFormationStatus.CALCULATION_COMPLETED)
        && !isDriver;

    let recalculationButtonVisible = delivery.formationStatus === DeliveryFormationStatus.CALCULATION_COMPLETED
        && !isDriver && delivery.status === DeliveryStatus.DRAFT;

    recalculationButtonVisible = recalculationButtonVisible || calculationFailed;

    const firstCalculationButtonVisible = delivery.formationStatus === DeliveryFormationStatus.READY_FOR_CALCULATION && !isDriver;

    const isPreApprove =
        delivery.formationStatus ===
        DeliveryFormationStatus.CALCULATION_COMPLETED && !isDriver

    const isApproveEnabled = delivery.status === DeliveryStatus.DRAFT;

    const calculated =
        delivery?.formationStatus === DeliveryFormationStatus.CALCULATION_COMPLETED


    const isLoadingBar =
        delivery &&
        (delivery.formationStatus === DeliveryFormationStatus.ORDERS_LOADING ||
            delivery.formationStatus === DeliveryFormationStatus.ROUTE_CALCULATION)

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
                    {/*TODO: Move to ActionButtons component*/}
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
                                onClick={() => setRecalculateConfirmationDialogOpen(true)}

                            />
                        )}
                        {isPreApprove && (
                            <ApproveButton
                                enabled={isApproveEnabled}
                                onClick={() => setApproveConfirmationDialogOpen(true)}
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
                    <DeliveryNav calculated={calculated}/>

                    <Box sx={{padding: '24px 0 24px', backgroundColor: '#fff'}}>
                        <Outlet/>
                    </Box>

                    <ConfirmationDialog
                        title={"Затвердити доставку"}
                        text={"Ви впевнені, що бажаєте затвердити доставку? Цю дію не можна буде відмінити."}
                        open={approveConfirmationDialogOpen}
                        setOpen={setApproveConfirmationDialogOpen}
                        onAction={() => {
                            approveDeliveryHandler();
                            setApproveConfirmationDialogOpen(false);
                        }}
                    />

                    <ConfirmationDialog
                        title={"Перерахувати доставку"}
                        text={"Ви впевнені, що бажаєте перерахувати доставку? Цю дію не можна буде відмінити."}
                        open={recalculateConfirmationDialogOpen}
                        setOpen={setRecalculateConfirmationDialogOpen}
                        onAction={() => {
                            calculateDeliveryHandler();
                            setRecalculateConfirmationDialogOpen(false);
                        }}
                    />
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
				<Button sx={{mr: 2}} variant='contained' disabled={!enabled} onClick={onClick}>
					{title}
				</Button>
			</span>
        </Tooltip>
    )
}

export default Delivery
