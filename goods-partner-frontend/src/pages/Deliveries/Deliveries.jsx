import React, {useEffect, useState} from "react";
import {
    Box,
    Button,
    Dialog,
    DialogActions,
    DialogContent,
    DialogContentText,
    DialogTitle,
    TextField,
    Typography
} from "@mui/material";
import {ArrowForward} from "@mui/icons-material";
import DeliveriesTable from "./DeliveriesTable/DeliveriesTable";

const Deliveries = ({deliveries, loadDeliveries, createDelivery}) => {
    const [openNewDeliveryDialog, setOpenNewDeliveryDialog] = React.useState(false);

    // TODO: [UI] check amount for back calls
    useEffect(() => {
        loadDeliveries();
    }, [])

    return <section>
        <Box sx={{mt: 2, display: 'flex', justifyContent: 'space-between', alignItems: 'center'}}>
            <Typography variant="h6" component="h2">
                Доставки
            </Typography>
            <Button onClick={() => setOpenNewDeliveryDialog(true)} variant="contained">Сформувати нову
                доставку <ArrowForward/></Button>
        </Box>

        <Box sx={{mt: 2}}>
            <DeliveriesTable deliveries={deliveries}/>
        </Box>

        <NewDeliveryDialog open={openNewDeliveryDialog} setOpen={setOpenNewDeliveryDialog}
                           onCreate={(date) => createDelivery(date)}/>
    </section>
}

function NewDeliveryDialog({open, setOpen, onCreate}) {
    const handleClose = () => {
        setOpen(false);
    };

    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    const defaultDate = tomorrow.toJSON().slice(0, 10).replace(/-/g, '-');
    const [calculationDate, setCalculationDate] = useState(defaultDate);

    const handleCreate = () => {
        onCreate(calculationDate);
        setOpen(false);
    };

    return (
        <Dialog
            open={open}
            onClose={handleClose}
            aria-labelledby="alert-dialog-title"
            aria-describedby="alert-dialog-description"
        >
            <DialogTitle id="alert-dialog-title">
                {"Сформувати доставку"}
            </DialogTitle>
            <DialogContent sx={{mt: 2}}>
                <DialogContentText id="alert-dialog-description">
                    Оберіть дату, на яку бажаєте сформувати доставку.
                </DialogContentText>
                <TextField
                    sx={{minWidth: '100%', mt: 2}}
                    id="date"
                    type="date"
                    defaultValue={calculationDate}
                    // TODO: Что это????
                    InputLabelProps={{
                        shrink: true,
                    }}
                    onChange={(e) => setCalculationDate(e.target.value)}/>
            </DialogContent>
            <DialogActions sx={{pb: '16px', pr: '24px'}}>
                <Button variant='outlined' onClick={handleClose}>Скасувати</Button>
                <Button variant='contained' onClick={handleCreate} autoFocus>
                    Сформувати
                </Button>
            </DialogActions>
        </Dialog>
    );
}

export default Deliveries;
