import * as React from 'react';
import {styled} from '@mui/material/styles';
import Box from '@mui/material/Box';
import Drawer from '@mui/material/Drawer';
import AppBar from '@mui/material/AppBar';
import Toolbar from '@mui/material/Toolbar';
import List from '@mui/material/List';
import Typography from '@mui/material/Typography';
import Divider from '@mui/material/Divider';
import IconButton from '@mui/material/IconButton';
import MenuIcon from '@mui/icons-material/Menu';
import CloseIcon from '@mui/icons-material/Close';
import ListItem from '@mui/material/ListItem';
import ListItemButton from '@mui/material/ListItemButton';
import ListItemIcon from '@mui/material/ListItemIcon';
import ListItemText from '@mui/material/ListItemText';
import {Download, ExpandLess, ExpandMore} from "@mui/icons-material";
import {Collapse, Container} from "@mui/material";

import LocalShippingIcon from '@mui/icons-material/LocalShipping';
import InventoryIcon from '@mui/icons-material/Inventory';
import ShoppingCartIcon from '@mui/icons-material/ShoppingCart';
import RouteIcon from '@mui/icons-material/Route';
import LogoutIcon from '@mui/icons-material/Logout';
import {Link} from "react-router-dom";

const drawerWidth = 256;

const Main = styled('main', {shouldForwardProp: (prop) => prop !== 'open'})(
    ({theme, open}) => ({
        flexGrow: 1,
        marginLeft: `-${drawerWidth}px`,
        padding: theme.spacing(3),
        transition: theme.transitions.create('margin', {
            easing: theme.transitions.easing.sharp,
            duration: theme.transitions.duration.leavingScreen,
        }),

        ...(open && {
            transition: theme.transitions.create('margin', {
                easing: theme.transitions.easing.easeOut,
                duration: theme.transitions.duration.enteringScreen,
            }),
        }),
    }),
);

const ListButton = (icon, name, paddingLeft) => {
    return (<>
        <ListItemButton sx={{pl: paddingLeft}}>
            <ListItemIcon>
                {icon}
            </ListItemIcon>
            <ListItemText primary={name}/>
        </ListItemButton>
        <Divider/>
    </>);
}


export default function Layout(props) {
    const [open, setOpen] = React.useState(true);
    const [subMenuOpen, setSubMenuOpen] = React.useState(true);

    const handleDrawerOpen = () => {
        setOpen(true);
    };

    const handleDrawerClose = () => {
        setOpen(false);
    };


    const changeCollapsedSubmenu = () => {
        setSubMenuOpen(!subMenuOpen);
    }

    return (
        <Box sx={{display: 'flex'}}>
            <AppBar position="fixed" open={open} sx={
                {
                    zIndex: (theme) => theme.zIndex.drawer + 1,
                    backgroundColor: '#536DFE',
                    padding: '0 8px'
                }}>
                <Toolbar>
                    <IconButton
                        color="inherit"
                        aria-label="open drawer"
                        onClick={handleDrawerOpen}
                        edge="start"
                        sx={{mr: 3, ...(open && {display: 'none'})}}
                    >
                        <MenuIcon/>
                    </IconButton>

                    <IconButton
                        color="inherit"
                        aria-label="close drawer"
                        onClick={handleDrawerClose}
                        edge="start"
                        sx={{mr: 3, ...(!open && {display: 'none'})}}
                    >
                        <CloseIcon/>
                    </IconButton>

                    <Typography sx={{textTransform: 'uppercase'}} noWrap component="div">
                        Goods partner
                    </Typography>
                </Toolbar>
            </AppBar>

            <Drawer sx={{
                width: drawerWidth,
                flexShrink: 0,
                '& .MuiDrawer-paper': {
                    width: drawerWidth,
                    boxSizing: 'border-box',
                },
            }}
                    variant="persistent"
                    anchor="left"
                    open={open}
            >
                <Toolbar/>

                <List sx={{overflow: 'auto', paddingTop: 0}}>

                    {/*TODO: [Tolik] Move into company logo component*/}
                    <ListItem sx={{padding: ' 16px 24px'}}>
                        <img src="/logo.jpg" alt={"Grande Dolce logo"} width='180px' height='30px'/>
                    </ListItem>
                    <Divider/>


                    <ListItem disablePadding component={Link} to={"/new/cars"}>
                        {ListButton(<LocalShippingIcon/>, 'Автомобілі')}
                    </ListItem>
                    <Divider/>

                    <ListItemButton onClick={changeCollapsedSubmenu}>
                        <ListItemIcon>
                            <InventoryIcon/>
                        </ListItemIcon>
                        <ListItemText primary={'Доставка'}/>
                        {subMenuOpen ? <ExpandLess/> : <ExpandMore/>}
                    </ListItemButton>
                    <Divider/>

                    <Collapse in={subMenuOpen} timeout="auto" unmountOnExit>
                        <List component="div" disablePadding>
                            {ListButton(<ShoppingCartIcon/>, "Замовлення", 4)}
                            {ListButton(<RouteIcon/>, "Маршрути", 4)}
                            {ListButton(<Download/>, "Завантаження", 4)}

                        </List>
                    </Collapse>
                </List>


                <List style={{marginTop: `auto`}}>
                    <ListItem disablePadding>
                        {ListButton(<LogoutIcon/>, 'Вийти')}
                    </ListItem>
                </List>
            </Drawer>


            <Main open={open}>
                <Toolbar/>
                <Container disableGutters>
                    {props.children}
                </Container>
            </Main>
        </Box>
    );
}
