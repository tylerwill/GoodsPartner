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
import {Collapse} from "@mui/material";


const Shipping = ({productsShipping}) => {

    return <Box sx={{paddingLeft:'24px', paddingRight:'24px'}}>
        <TableContainer component={Paper}  style={{
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
                    {productsShipping.map((shipping) => {
                        return (<Row key={shipping.id} shipping={shipping}/>)
                    })}
                </TableBody>
            </Table>
        </TableContainer>
    </Box>;
}

export default Shipping;


const Row = ({shipping}) => {
    const [carLoadTableOpen, setCarLoadTableOpen] = React.useState(false);
    return (
        <>
            <TableRow sx={{'& > *': {borderBottom: 'unset'}}}>
                <TableCell>
                    <IconButton
                        size="small"
                        onClick={() => setCarLoadTableOpen(!carLoadTableOpen)}
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
                    <Collapse in={carLoadTableOpen} timeout="auto" unmountOnExit>
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
