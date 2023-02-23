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
import DownloadIcon from "@mui/icons-material/Download";

const OrdersTable = ({orders, keyPrefix, updateOrder, isExcluded, basic}) => {
    const [page, setPage] = useState(0)
    const [rowsPerPage, setRowsPerPage] = useState(25)

    const [collapseAll, setCollapseAll] = useState(false)
    const [expandAll, setExpandAll] = useState(false)

    const collapseAllHandler = useCallback(() => {
        setCollapseAll(true)
        setExpandAll(false)
    }, [])

    const expandAllHandler = useCallback(() => {
        setExpandAll(true)
        setCollapseAll(false)
    }, [])

    const reset = useCallback(() => {
        setExpandAll(false)
        setCollapseAll(false)
    }, [])

    const handleChangePage = (event, newPage) => {
        setPage(newPage)
    }

    const handleChangeRowsPerPage = event => {
        setRowsPerPage(parseInt(event.target.value, 10))
        setPage(0)
    }


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
                >
                    <DownloadIcon sx={{mr: 1}}/> документи
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
                                                        rowsPerPage={rowsPerPage}
                                                        collapseAll={collapseAll}
                                                        expandAll={expandAll}
                                                        reset={reset}/>
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
