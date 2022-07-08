import Box from '@mui/material/Box';
import {Card, CardContent, Stack, Typography} from "@mui/material";

import * as React from 'react';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';
import OrdersHeader from "../../OrdersList/OrdersTable/OrdersHeader/OrdersHeader";
import OrdersBody from "../../OrdersList/OrdersTable/OrdersBody/OrdersBody";
import RouteDetails from "./RouteDetails/RouteDetails";
import RouteBody from "./RouteBody/RouteBody";

const RouteTable = ({date, route}) => {
  return (<>
        <RouteDetails
            routeId={route.routeId}
            status={route.status}
            totalWeight={route.totalWeight}
            totalPoints={route.totalPoints}
            totalOrders={route.totalOrders}
            distance={route.distance}
            estimatedTime={route.estimatedTime}
            startTime={route.startTime}
            finishTime={route.finishTime}
            spentTime={route.spentTime}
            routeLink={route.routeLink}
            date={date}
            storeName={route.storeName}
            storeAddress={route.storeAddress}
        />
        <RouteBody
            clients={route.clients}
            storeName={route.storeName}
            storeAddress={route.storeAddress}
        />
      </>
  );
}

export default RouteTable;