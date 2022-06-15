import Container from "@mui/material/Container";
import Box from "@mui/material/Box";
import Grid from "@mui/material/Grid";
import DateChooserCard from "./DateChooserCard/DateChooserCard";
import BasicTabs from "./BasicTabs/BasicTabs";
import React from "react";

const Calculate = ({date, orders, clients, stores, getCalculatedDataByDate}) => {
  return (
      <div className="App">
        <Container>
          <Box sx={{flexGrow: 1}}>
            <Grid container spacing={15}>
              <Grid item xs={2}>
                <DateChooserCard
                    getCalculatedDataByDate={getCalculatedDataByDate}
                />
              </Grid>
              <Grid item xs={10}>
                <BasicTabs
                    date={date}
                    orders={orders}
                    clients={clients}
                    stores={stores}
                />
              </Grid>
            </Grid>
          </Box>
        </Container>
      </div>
  );
}

export default Calculate;