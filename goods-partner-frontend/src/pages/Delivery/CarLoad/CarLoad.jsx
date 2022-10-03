import React from "react";
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


const CarLoad = ({carLoads}) => {
    console.log("carLoads", carLoads);

    return <Box>
        <TableContainer component={Paper}>
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
                    {carLoads.map((carLoad, index) => {
                        return (<Row carLoad={carLoad} key={'carLoadsPrefix' + index}/>)
                    })}
                </TableBody>
            </Table>
        </TableContainer>
    </Box>;
}

export default CarLoad;


const Row = ({carLoad, setOrderAddressDialogOpen, setEditedOrder}) => {
    const [carLoadTableOpen, setCarLoadTableOpen] = React.useState(false);
    const totalWeight = carLoad.orders.reduce((acc, order) => acc + order.orderWeight, 0);
    return (
        <>
            <TableRow sx={{'& > *': {borderBottom: 'unset'}}}>
                <TableCell>
                    <IconButton
                        aria-label="expand row"
                        size="small"
                        onClick={() => setCarLoadTableOpen(!carLoadTableOpen)}
                    >
                        {carLoadTableOpen ? <KeyboardArrowUpIcon/> : <KeyboardArrowDownIcon/>}
                    </IconButton>
                </TableCell>
                <TableCell component="th" scope="row">
                    {carLoad.orderNumber}
                </TableCell>
                <TableCell>{carLoad.orders.length} </TableCell>
                <TableCell>totalWeight кг</TableCell>
            </TableRow>
            {/*<TableRow>*/}
            {/*    <TableCell style={{paddingBottom: 0, paddingTop: 0}} colSpan={7}>*/}
            {/*        <Collapse in={orderTableOpen} timeout="auto" unmountOnExit>*/}
            {/*            <Box sx={{margin: 2}}>*/}
            {/*                <TableContainer component={Paper}>*/}
            {/*                    <Table sx={{minWidth: 650}} size="small">*/}
            {/*                        <TableHead>*/}
            {/*                            <TableRow>*/}
            {/*                                <TableCell sx={{width: '500px'}}>Артикул</TableCell>*/}
            {/*                                <TableCell>Кількість</TableCell>*/}
            {/*                                <TableCell>Вага</TableCell>*/}
            {/*                                <TableCell>Загальна вага</TableCell>*/}
            {/*                            </TableRow>*/}
            {/*                        </TableHead>*/}
            {/*                        <TableBody>*/}
            {/*                            {order.products.map((product) => (*/}
            {/*                                <TableRow*/}
            {/*                                    key={keyPrefix + product.productName}*/}
            {/*                                    sx={{'&:last-child td, &:last-child th': {border: 0}}}*/}
            {/*                                >*/}
            {/*                                    <TableCell component="th" scope="row">*/}
            {/*                                        {product.productName}*/}
            {/*                                    </TableCell>*/}
            {/*                                    <TableCell> {product.amount}</TableCell>*/}
            {/*                                    <TableCell> {product.unitWeight} кг</TableCell>*/}
            {/*                                    <TableCell> {product.totalProductWeight} кг</TableCell>*/}
            {/*                                </TableRow>*/}
            {/*                            ))}*/}
            {/*                        </TableBody>*/}
            {/*                    </Table>*/}
            {/*                </TableContainer>*/}
            {/*            </Box>*/}
            {/*        </Collapse>*/}
            {/*    </TableCell>*/}
            {/*</TableRow>*/}
        </>
    );
}
