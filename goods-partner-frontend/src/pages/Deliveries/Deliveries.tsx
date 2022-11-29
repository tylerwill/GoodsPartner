import React from "react";
import {Box, Button, Typography} from "@mui/material";
import {ArrowForward} from "@mui/icons-material";
import DeliveriesTable from "./DeliveriesTable/DeliveriesTable";
import Loading from "../../components/Loading/Loading";
import ErrorAlert from "../../components/ErrorAlert/ErrorAlert";
import NewDeliveryDialog from "./NewDeliveryDialog/NewDeliveryDialog";
import useAuth from "../../auth/AuthProvider";
import {useAddDeliveryMutation, useGetDeliveriesQuery} from '../../api/deliveries/deliveries.api'

const Deliveries = () => {
    const {data: deliveries, error, isLoading} = useGetDeliveriesQuery();
    const [addDelivery] = useAddDeliveryMutation();
    const [openNewDeliveryDialog, setOpenNewDeliveryDialog] = React.useState(false);

    const addNewDeliveryHandler = (date: string) => addDelivery({deliveryDate: date});

    // @ts-ignore
    const {user} = useAuth();
    const isDriver = user.role === 'DRIVER';

    if (isLoading) {
        return <Loading/>
    }

    return <section>
        <Box sx={{mt: 2, display: 'flex', justifyContent: 'space-between', alignItems: 'center'}}>
            <Typography variant="h6" component="h2">
                Доставки
            </Typography>
            {!isDriver && <Button onClick={() => setOpenNewDeliveryDialog(true)} variant="contained">Створити нову
                доставку <ArrowForward/></Button>}
        </Box>

        <Box sx={{mt: 2}}>
            {!isLoading && <DeliveriesTable deliveries={deliveries}/>}
        </Box>

        <NewDeliveryDialog open={openNewDeliveryDialog} setOpen={setOpenNewDeliveryDialog}
                           onCreate={addNewDeliveryHandler}/>

        {error && <ErrorAlert error={error}/>}
    </section>
}


export default Deliveries;
