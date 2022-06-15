import {Typography} from "@mui/material";
import Grid from "@mui/material/Grid";
import React from "react";

const OrdersHeader = ({client, address, manager}) => {
  return (<Grid container spacing={2}>
        <Grid item xs={4}>
          <Typography variant="body2" gutterBottom>
            Клієнт:
          </Typography>
          <Typography variant="body2" gutterBottom>
            {client}
          </Typography>
        </Grid>
        <Grid item xs={4}>
          <Typography variant="body2" gutterBottom>
            Адреса:
          </Typography>
          <Typography variant="body2" gutterBottom>
            {address}
          </Typography>
        </Grid>
        <Grid item xs={4}>
          <Typography variant="body2" gutterBottom>
            Менеджер:
          </Typography>

          <Typography variant="body2" gutterBottom>
            {manager}
          </Typography>
        </Grid>
      </Grid>
  );
}

export default OrdersHeader;