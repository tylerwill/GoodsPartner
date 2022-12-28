import React from "react";
import IconButton from "@mui/material/IconButton";
import MoreVertIcon from "@mui/icons-material/MoreVert";
import {ListItemText, Menu, MenuItem} from "@mui/material";
import Order from "../../../../../model/Order";

interface Props {
    changeAddress: () => void,
    exclude: () => void
}

// : React.FC<Props>
// {user, deleteUser, setEditedUser, openEditDialog}
const OrderActionMenu: React.FC<Props> = ({changeAddress, exclude}) => {
    const [anchorEl, setAnchorEl] = React.useState<HTMLButtonElement | null>();
    const open = Boolean(anchorEl);
    const handleClick = React.useCallback((event: React.MouseEvent<HTMLButtonElement>) => {
        setAnchorEl(event.currentTarget);
    }, []);

    const handleClose = React.useCallback(() => {
        setAnchorEl(null);
    }, []);

    const handleChangeAddress = React.useCallback(() => {
        changeAddress();
        handleClose()
    }, [changeAddress]);

    const handleExcludeOrder = React.useCallback(() => {
        exclude();
        handleClose();
    }, [exclude]);

    return (
        <div>
            <IconButton
                aria-label="more"
                aria-controls={open ? 'long-menu' : undefined}
                aria-expanded={open ? 'true' : undefined}
                aria-haspopup="true"
                onClick={handleClick}
            >
                <MoreVertIcon/>
            </IconButton>
            <Menu
                id="basic-menu"
                anchorEl={anchorEl}
                open={open}
                onClose={handleClose}
                MenuListProps={{
                    'aria-labelledby': 'basic-button',
                }}
            >
                <MenuItem onClick={handleChangeAddress}>
                    <ListItemText>Редагувати адресу</ListItemText>
                </MenuItem>
                <MenuItem>
                    <ListItemText>Змінити тип доставки</ListItemText>
                </MenuItem>
                <MenuItem onClick={handleExcludeOrder}>
                    <ListItemText>Вилучити</ListItemText>
                </MenuItem>
            </Menu>
        </div>
    );
}

export default OrderActionMenu;