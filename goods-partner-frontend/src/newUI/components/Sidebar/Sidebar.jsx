import * as React from 'react';
import Drawer from "@mui/material/Drawer";
import Toolbar from "@mui/material/Toolbar";
import List from "@mui/material/List";
import ListItem from "@mui/material/ListItem";
import Divider from "@mui/material/Divider";
import {Link} from "react-router-dom";
import LocalShippingIcon from "@mui/icons-material/LocalShipping";
import ListItemButton from "@mui/material/ListItemButton";
import ListItemIcon from "@mui/material/ListItemIcon";
import InventoryIcon from "@mui/icons-material/Inventory";
import ListItemText from "@mui/material/ListItemText";
import {Download, ExpandLess, ExpandMore} from "@mui/icons-material";
import {Collapse} from "@mui/material";
import ShoppingCartIcon from "@mui/icons-material/ShoppingCart";
import RouteIcon from "@mui/icons-material/Route";
import LogoutIcon from "@mui/icons-material/Logout";

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

const ListButtonWithLink = (icon, name, paddingLeft, component, to) => {
    return (<>
        <ListItemButton component={component} to={to} sx={{pl: paddingLeft}}>
            <ListItemIcon>
                {icon}
            </ListItemIcon>
            <ListItemText primary={name}/>
        </ListItemButton>
        <Divider/>
    </>);
}
const drawerWidth = 256;

const Sidebar = () => {

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
        <div className="sidebar">
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

                {/*TODO: [Nastya] Link in sidebar should be highlighted based on current URI*/}
                <List sx={{overflow: 'auto', paddingTop: 0}}>

                    <ListItem sx={{padding: ' 16px 24px'}}>
                        {/*TODO: [Nastya] Move into 'processed company' logo component*/}
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
                            {ListButtonWithLink(<ShoppingCartIcon/>, "Замовлення", 4, Link, "/new/orders")}
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
        </div>
    )
}

export default Sidebar;