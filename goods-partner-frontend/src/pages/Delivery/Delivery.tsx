import React, {useCallback, useEffect, useState} from 'react'
import {Box, Breadcrumbs, Chip, LinearProgress, Typography} from '@mui/material'
import DeliveryStatusChip from '../../components/DeliveryStatusChip/DeliveryStatusChip'
import {Link, Outlet, useParams} from 'react-router-dom'
import {setCurrentRouteIndex} from '../../features/currentDelivery/currentDeliverySlice'

import NavigateNextIcon from '@mui/icons-material/NavigateNext'
import Loading from '../../components/Loading/Loading'
import ErrorAlert from '../../components/ErrorAlert/ErrorAlert'
import {reformatDate} from '../../util/util'
import {useAppDispatch} from '../../hooks/redux-hooks'
import {
    useApproveDeliveryMutation,
    useCalculateDeliveryMutation,
    useGetDeliveryQuery,
    useResyncDeliveryMutation
} from '../../api/deliveries/deliveries.api'
import {DeliveryFormationStatus, DeliveryStatus} from '../../model/Delivery'
import {ConfirmationDialog} from "../../components/ConfirmationDialog/ConfirmationDialog";
import {DeliveryNav} from "./DeliveryNav/DeliveryNav";
import {ActionButtons} from "./ActionButtons/ActionButtons";


const Delivery = () => {
    const {deliveryId} = useParams()
    const dispatch = useAppDispatch()
    const {
        data: delivery,
        isLoading,
        error
    } = useGetDeliveryQuery(String(deliveryId))

    const [calculateDelivery] = useCalculateDeliveryMutation()
    const [approveDelivery] = useApproveDeliveryMutation()
    const [resyncDelivery] = useResyncDeliveryMutation()
    const calculateDeliveryHandler = useCallback(
        () => calculateDelivery(deliveryId!),
        [deliveryId]
    )

    const resyncDeliveryHandler = useCallback(
        () => resyncDelivery(deliveryId!),
        [deliveryId]
    )

    const [resyncDeliveryConfirmationDialogOpen, setResyncDeliveryConfirmationDialogOpen] = useState(false);
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

    const formationStatus = delivery.formationStatus;
    const calculated = formationStatus === DeliveryFormationStatus.CALCULATION_COMPLETED
    const loading = isLoadingBar(formationStatus);

    const isDeliveryError = formationStatus === DeliveryFormationStatus.ROUTE_CALCULATION_FAILED
        || formationStatus === DeliveryFormationStatus.ORDERS_LOADING_FAILED;

    const errorChip = <Chip sx={{ml: 1}} label={'Помилка'} color={'error'}/>;

    const isNotCompleted = delivery.status !== DeliveryStatus.COMPLETED;

    return (
        <section>
            {loading && loadingBar(formationStatus)}
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
                    {isDeliveryError && errorChip}
                </Box>
                {isNotCompleted &&
                    <ActionButtons openResyncDialog={() => setResyncDeliveryConfirmationDialogOpen(true)}
                                   calculateDeliveryHandler={calculateDeliveryHandler}
                                   formationStatus={formationStatus}
                                   deliveryStatus={delivery.status}
                                   openRecalculateDialog={() => setRecalculateConfirmationDialogOpen(true)}
                                   openApproveDialog={() => setApproveConfirmationDialogOpen(true)}/>
                }
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

                <ConfirmationDialog
                    title={"Синхронізація"}
                    text={"Ви впевнені, що бажаєте синхронізуватись з 1С? Весь прогрес по розрахунку буде втрачено."}
                    open={resyncDeliveryConfirmationDialogOpen}
                    setOpen={setResyncDeliveryConfirmationDialogOpen}
                    onAction={() => {
                        resyncDeliveryHandler();
                        setResyncDeliveryConfirmationDialogOpen(false);
                    }}
                />
            </Box>
        </section>
    )
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


function isLoadingBar(formationStatus: DeliveryFormationStatus) {
    return formationStatus === DeliveryFormationStatus.ORDERS_LOADING ||
        formationStatus === DeliveryFormationStatus.ROUTE_CALCULATION;
}

export default Delivery
