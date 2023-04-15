import React, {useCallback} from 'react'
import {Box, Button, Typography} from '@mui/material'
import {ArrowForward} from '@mui/icons-material'
// @ts-ignore
import DeliveriesTable from './DeliveriesTable/DeliveriesTable'
import Loading from '../../components/Loading/Loading'
import ErrorAlert from '../../components/ErrorAlert/ErrorAlert'
// @ts-ignore
import NewDeliveryDialog from './NewDeliveryDialog/NewDeliveryDialog'
import useAuth from '../../auth/AuthProvider'
import {
    useAddDeliveryMutation,
    useDeleteDeliveryMutation,
    useGetDeliveriesQuery
} from '../../api/deliveries/deliveries.api'
import {useActions, useAppSelector} from "../../hooks/redux-hooks";
import {ConfirmationDialog} from "../../components/ConfirmationDialog/ConfirmationDialog";
import Delivery from "../../model/Delivery";

const Deliveries = () => {
    const {data: deliveries, error, isLoading} = useGetDeliveriesQuery()
    const [addDelivery] = useAddDeliveryMutation()
    const [deleteDelivery] = useDeleteDeliveryMutation();

    const deleteDeliveryDialogOpen = useAppSelector(state => state.currentDelivery.deleteDeliveryDialogOpen);
    const deliveryToDelete = useAppSelector(state => state.currentDelivery.deliveryToDelete);
    const {setDeleteDeliveryDialogOpen, setDeliveryToDelete} = useActions();


    const [openNewDeliveryDialog, setOpenNewDeliveryDialog] =
        React.useState(false)

    const addNewDeliveryHandler = (date: string) =>
        addDelivery({deliveryDate: date})

    const deleteDeliveryAction = useCallback(() => {
		// TODO: Should always be true
        if (deliveryToDelete) {
            deleteDelivery(deliveryToDelete.id);
            setDeleteDeliveryDialogOpen(false);
        }
    }, [deleteDelivery, setDeleteDeliveryDialogOpen]);

    const deleteDeliveryHandler = useCallback((delivery: Delivery) => {
        setDeliveryToDelete(delivery);
        setDeleteDeliveryDialogOpen(true);
    }, [setDeliveryToDelete, setDeleteDeliveryDialogOpen]);

    // @ts-ignore
    const {user} = useAuth()
    const isDriver = user.role === 'DRIVER'

    if (isLoading) {
        return <Loading/>
    }

    return (
        <>
            <Box
                sx={{
                    mt: 2,
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'center'
                }}
            >
                <Typography variant='h6' component='h2'>
                    Доставки
                </Typography>
                {!isDriver && (
                    <Button
                        onClick={() => setOpenNewDeliveryDialog(true)}
                        variant='contained'
                    >
                        Створити нову доставку <ArrowForward/>
                    </Button>
                )}
            </Box>

            <Box sx={{mt: 2}}>
                {!isLoading && <DeliveriesTable deliveries={deliveries} deleteDeliveryHandler={deleteDeliveryHandler}/>}
            </Box>

            <NewDeliveryDialog
                open={openNewDeliveryDialog}
                setOpen={setOpenNewDeliveryDialog}
                onCreate={addNewDeliveryHandler}
            />

            {/* FIXME: Temporary removed*/}
            {/*<ConfirmationDialog*/}
            {/*    title={"Видалити доставку"}*/}
            {/*    text={"Ви впевнені, що бажаєте видалити доставку? Цю дію не можна буде відмінити."}*/}
            {/*    open={deleteDeliveryDialogOpen}*/}
            {/*    setOpen={setDeleteDeliveryDialogOpen}*/}
            {/*    onAction={deleteDeliveryAction}*/}
            {/*/>*/}

            {error && <ErrorAlert error={error}/>}
        </>
    )
}

export default Deliveries
