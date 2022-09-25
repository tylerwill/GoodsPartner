import React, {useEffect} from "react";
import {Box, Button, Typography} from "@mui/material";
import {ArrowForward} from "@mui/icons-material";
import DeliveriesTable from "./DeliveriesTable/DeliveriesTable";

const Deliveries = ({deliveries, getDeliveries}) => {
    // TODO: [UI] check amount for back calls
    useEffect(()=> {
        getDeliveries();
    }, [])

    return <section>
        <Box sx={{mt: 2, display: 'flex', justifyContent: 'space-between', alignItems: 'center'}}>
            <Typography variant="h6" component="h2">
                Доставки
            </Typography>
            <Button variant="contained">Сформувати нову доставку <ArrowForward/></Button>
        </Box>

        <Box sx={{mt: 2}}>
            <DeliveriesTable deliveries={deliveries}/>
        </Box>
    </section>
}

export default Deliveries;
