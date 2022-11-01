import React, {useCallback} from "react";
import Box from "@mui/material/Box";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import Table from "@mui/material/Table";
import TableCell from "@mui/material/TableCell";
import TableRow from "@mui/material/TableRow";
import Paper from "@mui/material/Paper";
import TableBody from "@mui/material/TableBody";
import IconButton from "@mui/material/IconButton";
import KeyboardArrowUpIcon from "@mui/icons-material/KeyboardArrowUp";
import KeyboardArrowDownIcon from "@mui/icons-material/KeyboardArrowDown";
import {Button, Collapse} from "@mui/material";
import UnfoldMoreIcon from "@mui/icons-material/UnfoldMore";
import UnfoldLessIcon from "@mui/icons-material/UnfoldLess";
import TablePagination from "@mui/material/TablePagination";


const Shipping = ({productsShipping}) => {
    const [page, setPage] = React.useState(0);
    const [rowsPerPage, setRowsPerPage] = React.useState(10);

    const [collapseAll, setCollapseAll] = React.useState(false);
    const [expandAll, setExpandAll] = React.useState(false);

    const collapseAllHandler = useCallback(() => {
        setCollapseAll(true);
        setExpandAll(false);
    }, []);

    const expandAllHandler = useCallback(() => {
        setExpandAll(true);
        setCollapseAll(false);
    }, []);

    const reset = useCallback(() => {
        setExpandAll(false);
        setCollapseAll(false);
    }, []);

    const handleChangePage = (event, newPage) => {
        setPage(newPage);
    };

    const handleChangeRowsPerPage = (event) => {
        setRowsPerPage(parseInt(event.target.value, 10));
        setPage(0);
    };

    // Avoid a layout jump when reaching the last page with empty rows.
    const emptyRows =
        page > 0 ? Math.max(0, (1 + page) * rowsPerPage - productsShipping.length) : 0;

    return <Box sx={{paddingLeft:'24px', paddingRight:'24px'}}>
        <Box sx={{display: "flex", justifyContent: 'flex-end', mb: 2}}>
            <Button onClick={expandAllHandler}>
                <UnfoldMoreIcon sx={{mr: 1, transform: 'rotate(45deg)', width: '0.75em'}}/> розгорнути всі
            </Button>
            <Button onClick={collapseAllHandler}>
                <UnfoldLessIcon sx={{mr: 1, transform: 'rotate(45deg)', width: '0.75em'}}/> Згорнути всі
            </Button>
        </Box>
        <Paper variant={"outlined"}>
            <TableContainer style={{
                borderTop: '1px solid rgba(0, 0, 0, 0.1)'
            }}>
                <Table aria-label="collapsible table">
                    <TableHead>
                        <TableRow>
                            <TableCell/>
                            <TableCell>Товар</TableCell>
                            <TableCell>Загальна кількість</TableCell>
                            <TableCell>Загальна вага</TableCell>

                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {productsShipping.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                            .map((shipping, index) => {
                                return (<Row key={"shipping" + index} shipping={shipping}
                                             expandAll={expandAll} collapseAll={collapseAll} reset={reset}/>)
                            })}
                        {emptyRows > 0 && (
                            <TableRow
                                style={{
                                    height: 53 * emptyRows,
                                }}
                            >
                                <TableCell colSpan={4}/>
                            </TableRow>
                        )}
                    </TableBody>
                </Table>
            </TableContainer>
            <TablePagination
                rowsPerPageOptions={[10, 25, 50]}
                component="div"
                count={productsShipping.length}
                rowsPerPage={rowsPerPage}
                page={page}
                onPageChange={handleChangePage}
                onRowsPerPageChange={handleChangeRowsPerPage}
            />
        </Paper>
    </Box>;
}

export default Shipping;


const Row = ({shipping, collapseAll, expandAll, reset}) => {
    const [carLoadTableOpen, setCarLoadTableOpen] = React.useState(false);
    const isTableOpened = expandAll || (carLoadTableOpen && !collapseAll);

    return (
        <>
            <TableRow sx={{'& > *': {borderBottom: 'unset'}}}>
                <TableCell>
                    <IconButton
                        size="small"
                        onClick={() => {setCarLoadTableOpen(!carLoadTableOpen); reset()}}
                    >
                        {carLoadTableOpen ? <KeyboardArrowUpIcon/> : <KeyboardArrowDownIcon/>}
                    </IconButton>
                </TableCell>
                <TableCell component="th" scope="row">
                    {shipping.article}
                </TableCell>
                <TableCell>{shipping.totalAmount} </TableCell>
                <TableCell>{shipping.totalWeight} кг</TableCell>
            </TableRow>
            <TableRow>
                <TableCell style={{paddingBottom: 0, paddingTop: 0}} colSpan={5}>
                    <Collapse in={isTableOpened} timeout="auto" unmountOnExit>
                        <Box sx={{margin: 1}}>
                            <TableContainer component={Paper}>
                                <Table sx={{minWidth: 650}} size="small">
                                    <TableHead>
                                        <TableRow>
                                            <TableCell>Замовлення</TableCell>
                                            <TableCell>Машина</TableCell>
                                            <TableCell>Кількість</TableCell>
                                            <TableCell>Вага</TableCell>
                                            <TableCell>Загальна вага</TableCell>
                                        </TableRow>
                                    </TableHead>
                                    <TableBody>
                                        {shipping.productLoadDtos.map((load) => (
                                            <TableRow
                                                key={"shipping" + load.orderNumber}
                                                sx={{'&:last-child td, &:last-child th': {border: 0}}}
                                            >
                                                <TableCell component="th" scope="row">
                                                    {load.orderNumber}
                                                </TableCell>
                                                <TableCell> {load.car}</TableCell>
                                                <TableCell> {load.amount}</TableCell>
                                                <TableCell> {load.weight} кг</TableCell>
                                                <TableCell> {load.totalWeight} кг</TableCell>
                                            </TableRow>
                                        ))}
                                    </TableBody>
                                </Table>
                            </TableContainer>
                        </Box>
                    </Collapse>
                </TableCell>
            </TableRow>
        </>
    );
}