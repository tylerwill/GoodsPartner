import React from 'react'
import {Button, Card, CardContent} from '@mui/material'
import TableHead from '@mui/material/TableHead'
import TableRow from '@mui/material/TableRow'
import TableCell from '@mui/material/TableCell'
import TableSortLabel from '@mui/material/TableSortLabel'
import Box from '@mui/material/Box'
import {visuallyHidden} from '@mui/utils'
import Paper from '@mui/material/Paper'
import TableContainer from '@mui/material/TableContainer'
import Table from '@mui/material/Table'
import TableBody from '@mui/material/TableBody'
import TablePagination from '@mui/material/TablePagination'
import {useNavigate} from 'react-router-dom'
import DeliveryStatusChip from '../../../components/DeliveryStatusChip/DeliveryStatusChip'
import {reformatDate} from '../../../util/util'

const DeliveriesTable = ({deliveries, deleteDeliveryHandler}) => {
    // TODO: [UI] Remove shadow
    return (
        <Card variant='outlined'>
            <CardContent
                sx={{
                    minHeight: 'calc(100vh - 280px)',
                    display: 'flex'
                }}
            >
                {deliveries.length !== 0 ? (
                    <EnhancedTable deliveries={deliveries} deleteDeliveryHandler={deleteDeliveryHandler}/>
                ) : (
                    ''
                )}
            </CardContent>
        </Card>
    )
}

const headCells = [
    {
        id: 'deliveryStatus',
        label: 'Статус'
    },
    {
        id: 'deliveryDate',
        label: 'Дата'
    },
    {
        id: 'orderCount',
        label: 'Кількість замовлень'
    },
    {
        id: 'carCount',
        label: 'Кількість машин'
    },
    {
        id: 'action',
        label: ''
    }
]

function EnhancedTableHead(props) {
    const {order, orderBy, onRequestSort} = props
    const createSortHandler = property => event => {
        onRequestSort(event, property)
    }

    return (
        <TableHead>
            <TableRow>
                {headCells.map(headCell => (
                    <TableCell
                        key={headCell.id}
                        align='left'
                        sortDirection={orderBy === headCell.id ? order : false}
                    >
                        <TableSortLabel
                            active={orderBy === headCell.id}
                            direction={orderBy === headCell.id ? order : 'asc'}
                            onClick={createSortHandler(headCell.id)}
                        >
                            {headCell.label}
                            {orderBy === headCell.id ? (
                                <Box component='span' sx={visuallyHidden}>
                                    {order === 'desc' ? 'sorted descending' : 'sorted ascending'}
                                </Box>
                            ) : null}
                        </TableSortLabel>
                    </TableCell>
                ))}
            </TableRow>
        </TableHead>
    )
}

function EnhancedTable({deliveries, deleteDeliveryHandler}) {
    const navigate = useNavigate()
    const handleRowClick = deliveryId => {
        navigate(`/delivery/${deliveryId}`)
    }
    const [order, setOrder] = React.useState('asc')
    const [orderBy, setOrderBy] = React.useState('calories')
    const [page, setPage] = React.useState(0)
    const [rowsPerPage, setRowsPerPage] = React.useState(10)

    const handleRequestSort = (event, property) => {
        const isAsc = orderBy === property && order === 'asc'
        setOrder(isAsc ? 'desc' : 'asc')
        setOrderBy(property)
    }

    const handleChangePage = (event, newPage) => {
        setPage(newPage)
    }

    const handleChangeRowsPerPage = event => {
        setRowsPerPage(parseInt(event.target.value, 10))
        setPage(0)
    }

    // Avoid a layout jump when reaching the last page with empty rows.
    const emptyRows =
        page > 0 ? Math.max(0, (1 + page) * rowsPerPage - deliveries.length) : 0

    const handleTableEvent = (event, id) => {
        console.log("event", event);
        console.log("id", id);

    }

    return (
        <Box sx={{width: '100%'}}>
            <Paper sx={{width: '100%'}}>
                <TableContainer
                    sx={{minWidth: 750, minHeight: 'calc(100vh - 290px)'}}
                >
                    <Table aria-labelledby='tableTitle'>
                        <EnhancedTableHead
                            order={order}
                            orderBy={orderBy}
                            onRequestSort={handleRequestSort}
                        />
                        <TableBody>
                            {/* TODO: [UI Max] Fix sorting by status column */}
                            {stableSort(deliveries, getComparator(order, orderBy))
                                .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                                .map(delivery => {
                                    return (
                                        <TableRow
                                            hover
                                            key={delivery.id}
                                            onClick={() => handleRowClick(delivery.id)}
                                            sx={{cursor: 'pointer'}}
                                        >
                                            <TableCell component='th' scope='row' align='left'>
                                                <DeliveryStatusChip status={delivery.status}/>
                                            </TableCell>
                                            <TableCell align='left'>
                                                {reformatDate(delivery.deliveryDate)}
                                            </TableCell>
                                            <TableCell align='left'>
                                                {delivery.orderCount ?? '-'}
                                            </TableCell>
                                            <TableCell align='left'>
                                                {delivery.routeCount ?? '-'}
                                            </TableCell>
                                            <TableCell align='center'>
                                                <Button sx={{zIndex: 10000}} variant={'contained'} color="error"
                                                        onClick={(e) => {
                                                            e.stopPropagation();
                                                            deleteDeliveryHandler(delivery)
                                                        }}
                                                >
                                                    Видалити
                                                </Button>
                                            </TableCell>
                                        </TableRow>
                                    )
                                })}
                            {emptyRows > 0 && (
                                <TableRow
                                    style={{
                                        height: 53 * emptyRows
                                    }}
                                >
                                    <TableCell colSpan={6}/>
                                </TableRow>
                            )}
                        </TableBody>
                    </Table>
                </TableContainer>
                <TablePagination
                    rowsPerPageOptions={[10, 25, 50]}
                    component='div'
                    count={deliveries.length}
                    rowsPerPage={rowsPerPage}
                    page={page}
                    onPageChange={handleChangePage}
                    onRowsPerPageChange={handleChangeRowsPerPage}
                />
            </Paper>
        </Box>
    )
}

// TODO [UI]: Move comparators into util
function descendingComparator(a, b, orderBy) {
    if (b[orderBy] < a[orderBy]) {
        return -1
    }
    if (b[orderBy] > a[orderBy]) {
        return 1
    }
    return 0
}

function getComparator(order, orderBy) {
    return order === 'desc'
        ? (a, b) => descendingComparator(a, b, orderBy)
        : (a, b) => -descendingComparator(a, b, orderBy)
}

// This method is created for cross-browser compatibility, if you don't
// need to support IE11, you can use Array.prototype.sort() directly
function stableSort(array, comparator) {
    const stabilizedThis = array.map((el, index) => [el, index])
    stabilizedThis.sort((a, b) => {
        const order = comparator(a[0], b[0])
        if (order !== 0) {
            return order
        }
        return a[1] - b[1]
    })
    return stabilizedThis.map(el => el[0])
}

export default DeliveriesTable
