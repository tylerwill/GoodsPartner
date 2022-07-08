import Paper from "@mui/material/Paper";
import TableContainer from "@mui/material/TableContainer";
import * as React from "react";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableRow from '@mui/material/TableRow';
import {TableCell} from "@mui/material";

const RouteDetails = (
    {
      date,
      routeId,
      distance,
      estimatedTime,
      finishTime,
      routeLink,
      spentTime,
      startTime,
      status,
      totalOrders,
      totalPoints,
      totalWeight,
      storeName,
      storeAddress
    }) => {
  return (
      <TableContainer component={Paper} sx={{marginTop: '10px'}}>
        <Table aria-label="spanning table">
          <TableBody>
            <TableRow>
              <TableCell align="left">
                Маршрут №{routeId} від {date}
              </TableCell>
              <TableCell align="left">
                Статус: {status}
              </TableCell>
              <TableCell align="left">
                <a href={routeLink} target="_blank">Показати на карті</a>
              </TableCell>
            </TableRow>
            <TableRow>
              <TableCell align="left">
                Кількість адрес: {totalPoints}
              </TableCell>
              <TableCell align="left">
                Кількість замовлень: {totalOrders}
              </TableCell>
              <TableCell align="left">
                Загальна маса: {totalWeight}
              </TableCell>
            </TableRow>
            <TableRow>
              <TableCell align="left">
                Склад: {storeName}
              </TableCell>
              <TableCell colSpan={2} align="left">
                Адреса складу: {storeAddress}
              </TableCell>
            </TableRow>
            <TableRow>
              <TableCell colSpan={3} align="left">
                Відстань: {distance} км
              </TableCell>
            </TableRow>
            <TableRow>
              <TableCell colSpan={3} align="left">
                Розрахунковий час виконання: {estimatedTime}
              </TableCell>
            </TableRow>
            <TableRow>
              <TableCell colSpan={3} align="left">
                Початок виконання: {startTime}
              </TableCell>
            </TableRow>
            <TableRow>
              <TableCell colSpan={3} align="left">
                Кінець виконання: {finishTime}
              </TableCell>
            </TableRow>
            <TableRow>
              <TableCell colSpan={3} align="left">
                Фактичний час виконання: {spentTime}
              </TableCell>
            </TableRow>
          </TableBody>
        </Table>
      </TableContainer>
  );
}

export default RouteDetails;