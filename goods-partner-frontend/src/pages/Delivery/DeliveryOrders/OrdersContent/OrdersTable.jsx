import React, {useCallback, useState} from 'react'
import {Button} from '@mui/material'
import TableContainer from '@mui/material/TableContainer'
import Paper from '@mui/material/Paper'
import Box from '@mui/material/Box'
import TablePagination from '@mui/material/TablePagination'

import UnfoldMoreIcon from '@mui/icons-material/UnfoldMore'
import UnfoldLessIcon from '@mui/icons-material/UnfoldLess'
import {DeliveryOrdersBasicTable} from "./DeliveryOrdersBasicTable/DeliveryOrdersBasicTable";
import {DeliveryOrdersShortTable} from "./DeliveryOrdersShortTable/DeliveryOrdersShortTable";
import TextSnippetIcon from '@mui/icons-material/TextSnippet';
import UnarchiveIcon from '@mui/icons-material/Unarchive';
import {useParams} from "react-router-dom";
import {apiUrl} from "../../../../util/util";
import {useCollapseExpand} from "../../../../hooks/useCollapseExpand";
import {useTablePaging} from "../../../../hooks/useTablePaging";

const OrdersTable = ({orders, keyPrefix, updateOrder, isExcluded, basic}) => {
    const {deliveryId} = useParams()

    const [page, rowsPerPage, handleChangePage, handleChangeRowsPerPage] = useTablePaging();
    const [collapseAll, expandAll, collapseAllHandler, expandAllHandler, reset] = useCollapseExpand();

    return (
        <Box>
            {basic &&
                <Box sx={{display: 'flex', mb: 2}}>
                    <Button onClick={expandAllHandler}>
                        <UnfoldMoreIcon
                            sx={{mr: 1, transform: 'rotate(45deg)', width: '0.75em'}}
                        />{' '}
                        розгорнути всі
                    </Button>
                    <Button onClick={collapseAllHandler}>
                        <UnfoldLessIcon
                            sx={{mr: 1, transform: 'rotate(45deg)', width: '0.75em'}}
                        />{' '}
                        Згорнути всі
                    </Button>
                </Box>
            }

            {!basic && <Box display={'flex'} sx={{mb: 2}}>
                <Button
                    variant='outlined'
                    size={"small"}
                    target={"_blank"}
                    href={`${apiUrl}/documents?deliveryId=${deliveryId}&deliveryType=${orders[0].deliveryType}`}
                >
                    <TextSnippetIcon sx={{mr: 1}}/> документи
                </Button>

                <Button
                    sx={{ml:2}}
                    size={"small"}
                    href={`${apiUrl}/reports/shipment?deliveryId=${deliveryId}&deliveryType=${orders[0].deliveryType}`}
                    target={"_blank"}
                    variant='outlined'
                >
                    <UnarchiveIcon sx={{mr: 1}}/> відвантаження
                </Button>
            </Box>
            }
            <Paper variant={'outlined'}>
                <TableContainer
                    style={{
                        borderTop: '1px solid rgba(0, 0, 0, 0.1)'
                    }}
                >
                    {
                        basic ? <DeliveryOrdersBasicTable orders={orders}
                                                          isExcluded={isExcluded}
                                                          keyPrefix={keyPrefix}
                                                          page={page}
                                                          rowsPerPage={rowsPerPage}
                                                          collapseAll={collapseAll}
                                                          expandAll={expandAll}
                                                          reset={reset}
                                                          updateOrder={updateOrder}/>
                            : <DeliveryOrdersShortTable orders={orders}
                                                        keyPrefix={keyPrefix}
                                                        page={page}
                                                        rowsPerPage={rowsPerPage}/>
                    }

                </TableContainer>
                <TablePagination
                    rowsPerPageOptions={[25, 50, 100]}
                    component='div'
                    count={orders.length}
                    rowsPerPage={rowsPerPage}
                    page={page}
                    onPageChange={handleChangePage}
                    onRowsPerPageChange={handleChangeRowsPerPage}
                />
            </Paper>
        </Box>
    )
}

export default OrdersTable
