import Container from "@mui/material/Container";
import Box from "@mui/material/Box";
import Grid from "@mui/material/Grid";
// import DateChooserCard from "./DateChooserCard/DateChooserCard";
// import BasicTabs from "./BasicTabs/BasicTabs";
import React from "react";
import CarTable from "./CarTable/CarTable";
import DateChooserCard from "../Calculate/DateChooserCard/DateChooserCard";


const Calculate = ({orders, routes, stores, getCalculatedDataByDate, routeAddresses}) => {

    return (
        <div className="App">
            <Container>
                <Box sx={{flexGrow: 1}}>
                    <Grid container spacing={3} direction="column">
                        <Grid item xs={2}>
                            <DateChooserCard
                                getCalculatedDataByDate={getCalculatedDataByDate}
                            />
                        </Grid>
                        <Grid item xs={12}>
                            <CarTable/>
                        </Grid>
                    </Grid>
                </Box>
            </Container>
        </div>
    );
}

export default Calculate;


