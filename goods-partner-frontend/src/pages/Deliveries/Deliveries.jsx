import React, {useEffect} from "react";
import {Box, Button, Typography} from "@mui/material";
import {ArrowForward} from "@mui/icons-material";
import DeliveriesTable from "./DeliveriesTable/DeliveriesTable";
import {useDispatch, useSelector} from "react-redux";
import {createDelivery, fetchDeliveries} from "../../features/deliveries/deliveriesSlice";
import Loading from "../../components/Loading/Loading";
import ErrorAlert from "../../components/ErrorAlert/ErrorAlert";
import NewDeliveryDialog from "./NewDeliveryDialog/NewDeliveryDialog";

const Deliveries = () => {
    const [openNewDeliveryDialog, setOpenNewDeliveryDialog] = React.useState(false);

    const {deliveries, loading, error} = useSelector(state => state.deliveriesList);
    const dispatch = useDispatch();

    useEffect(() => {
        dispatch(fetchDeliveries());
    }, [dispatch])

    if (loading) {
        return <Loading/>
    }

    return <section>
        <Box sx={{mt: 2, display: 'flex', justifyContent: 'space-between', alignItems: 'center'}}>
            <Typography variant="h6" component="h2">
                Доставки
            </Typography>
            <Button onClick={() => setOpenNewDeliveryDialog(true)} variant="contained">Створити нову доставку <ArrowForward/></Button>
        </Box>

        <Box sx={{mt: 2}}>
            <DeliveriesTable deliveries={deliveries}/>
        </Box>

        <NewDeliveryDialog open={openNewDeliveryDialog} setOpen={setOpenNewDeliveryDialog}
                           onCreate={(date) => dispatch(createDelivery(date))}/>

        {error && <ErrorAlert error={error}/>}
    </section>
}


export default Deliveries;
