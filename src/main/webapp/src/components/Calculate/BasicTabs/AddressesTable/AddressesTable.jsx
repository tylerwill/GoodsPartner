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

function AddressesTable({date, clients}) {
  let isClientPrinted = false;
  let isAddressPrinted = false;
  let isWeightPrinted = false;
  return (<Card sx={{minWidth: 275}}>
        <CardContent>
          <Stack spacing={2}>
            {
              date
                  ? <>
                    <Typography variant="h5" gutterBottom component="div">
                      Список замовлень на {date}
                    </Typography>
                    <Box sx={{flexGrow: 1}}>
                      <TableContainer component={Paper}>
                        <Table aria-label="simple table">
                          <TableHead sx={{backgroundColor: 'rgba(0, 0, 0, 0.12);'}}>
                            <TableRow>
                              <TableCell sx={{padding: '7px'}}>Клієнт</TableCell>
                              <TableCell sx={{padding: '7px'}} align="center">Адреса</TableCell>
                              <TableCell sx={{padding: '7px'}} align="center">Замовлення</TableCell>
                              <TableCell sx={{padding: '7px'}} align="center">Маса, кг</TableCell>
                            </TableRow>
                          </TableHead>
                          <TableBody>

                            {
                              clients.map(client => (<>
                                {
                                  client.addresses.map(address => (<>
                                    {isWeightPrinted = false}
                                    {isClientPrinted = false}
                                    {isAddressPrinted = false}
                                    {
                                      address.orders.map(order => (<>
                                        <TableRow>
                                          {
                                            isClientPrinted === false &&
                                            <TableCell rowSpan={address.orders.length} sx={{padding: '7px'}} component="th"
                                                       scope="row">
                                              {client.clientName}
                                            </TableCell>
                                          }
                                          {
                                            isWeightPrinted === false &&
                                            <TableCell rowSpan={address.orders.length} sx={{padding: '7px'}}
                                                       align="center">{address.address}</TableCell>
                                          }
                                          <TableCell sx={{padding: '7px'}} align="center">{order.orderNumber}</TableCell>

                                          {
                                            isWeightPrinted === false &&
                                            <TableCell rowSpan={address.orders.length} sx={{padding: '7px'}}
                                                       align="center">{address.addressTotalWeight}</TableCell>
                                          }
                                          {isWeightPrinted = true}
                                          {isClientPrinted = true}
                                          {isAddressPrinted = true}
                                        </TableRow>
                                      </>))
                                    }
                                  </>))
                                }
                              </>))
                            }
                          </TableBody>
                        </Table>
                      </TableContainer>
                    </Box>
                  </>
                  : <Typography variant="h5" gutterBottom component="div">
                    Оберіть дату та натисніть "РОЗРАХУВАТИ"
                  </Typography>
            }
          </Stack>
        </CardContent>
      </Card>
  );
}

export default AddressesTable;