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
import {ContentPasteSharp, Download, ExpandLess, ExpandMore, Inventory2Sharp} from "@mui/icons-material";
import {Collapse} from "@mui/material";
import ShoppingCartIcon from "@mui/icons-material/ShoppingCart";
import RouteIcon from "@mui/icons-material/Route";
import LogoutIcon from "@mui/icons-material/Logout";
import GrandeDolceLogo from "../Logo/GrandeDolceLogo";

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
const drawerWidth = 256;

const Sidebar = ({open}) => {
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
                        <GrandeDolceLogo/>
                    </ListItem>
                    <Divider/>

                    <ListItem disablePadding component={Link} to={"/deliveries"}>
                        {ListButton(<Inventory2Sharp/>, 'Доставки')}
                    </ListItem>
                    <ListItem disablePadding component={Link} to={"/cars"}>
                        {ListButton(<LocalShippingIcon/>, 'Автомобілі')}
                    </ListItem>
                    <ListItem disablePadding component={Link} to={"/reports"}>
                        {ListButton(<ContentPasteSharp/>, 'Звітність')}
                    </ListItem>
                    <Divider/>

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