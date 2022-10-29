import React, {useEffect, useState} from "react";
import {Box, Button, ListItemIcon, ListItemText, Menu, MenuItem, Typography} from "@mui/material";
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';

import CheckIcon from '@mui/icons-material/Check';
import CloseIcon from '@mui/icons-material/Close';
import IconButton from "@mui/material/IconButton";
import MoreVertIcon from '@mui/icons-material/MoreVert';
import EditOutlinedIcon from '@mui/icons-material/EditOutlined';
import DeleteForeverOutlinedIcon from '@mui/icons-material/DeleteForeverOutlined';
import UserFormDialog from "./UserFormDialog/UserFormDialog";
import {useDispatch, useSelector} from "react-redux";
import {fetchUsers, addUser, updateUser, deleteUser} from "../../features/users/usersSlice";
import Loading from "../../components/Loading/Loading";
import ErrorAlert from "../../components/ErrorAlert/ErrorAlert";

const Users = () => {
    const {users, loading, error} = useSelector(state => state.users);
    const dispatch = useDispatch();

    const defaultNewUserState = {
        id: null,
        userName: '',
        email: '',
        role: 'DRIVER',
        enabled: true
    };

    const [isAddUserDialogOpen, setIsAddUserDialogOpen] = useState(false);
    const [isEditUserDialogOpen, setIsEditUserDialogOpen] = useState(false);
    const [editedUser, setEditedUser] = useState({});
    const [newUser, setNewUser] = useState(defaultNewUserState);

    const addUserHandler = (user) => {
        dispatch(addUser(user));
        setNewUser(defaultNewUserState);
    }

    const updateUserHandler = (user) => {
        dispatch(updateUser(user));
        setNewUser(defaultNewUserState);
    }

    const deleteUserHandler = (id) => {
        dispatch(deleteUser(id));
    }

    useEffect(() => {
        dispatch(fetchUsers());
    }, [dispatch]);

    if (loading) {
        return <Loading/>
    }

    return <section>
        <Box sx={{display: 'flex', justifyContent: 'space-between'}}>
            <Typography variant="h6" component="h2">
                Користувачі
            </Typography>

            <Button onClick={() => setIsAddUserDialogOpen(true)} variant="contained">Додати користувача</Button>
        </Box>
        <Box mt={2}>
            <TableContainer component={Paper}>
                <Table sx={{minWidth: 650}} aria-label="simple table">
                    <TableHead sx={{fontWeight: 'bold'}}>
                        <TableRow>
                            <TableCell>Ім'я</TableCell>
                            <TableCell align="left">Пошта</TableCell>
                            <TableCell align="left">Роль</TableCell>
                            <TableCell align="center">Активний</TableCell>
                            <TableCell/>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {/*// TODO: [Tolik] Fix key*/}
                        {users.map((user) => (
                            <TableRow key={"tableUserId " + user.id}>
                                <TableCell>{user.userName}</TableCell>
                                <TableCell>{user.email}</TableCell>
                                <TableCell>{toRoleString(user.role)}</TableCell>
                                <TableCell align="center">{user.enabled ? <CheckIcon/> : <CloseIcon/>}</TableCell>
                                <TableCell align="center"><BasicMenu user={user}
                                                      setEditedUser={setEditedUser}
                                                      deleteUser={deleteUserHandler}
                                                      openEditDialog={setIsEditUserDialogOpen}
                                /></TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
        </Box>
        {/*Dialog for adding new user*/}
        <UserFormDialog closeDialog={() => setIsAddUserDialogOpen(false)} open={isAddUserDialogOpen}
                        title={"Додати користувача"}
                        user={newUser}
                        setUser={setNewUser}
                        actionHandler={addUserHandler}/>

        {/*/!*Dialog for editing existing user*!/*/}
        <UserFormDialog closeDialog={() => setIsEditUserDialogOpen(false)} open={isEditUserDialogOpen}
                        title={"Редагувати користувача"}
                        actionHandler={updateUserHandler}
                        user={editedUser}
                        setUser={setEditedUser}
        />
        {error && <ErrorAlert error={error}/>}
    </section>
}

function toRoleString(role) {
    switch (role) {
        case 'ADMIN': {
            return 'Адміністратор'
        }
        case 'LOGIST': {
            return 'Логіст'
        }
        case 'DRIVER': {
            return 'Водій'
        }
    }
}

function BasicMenu({user, deleteUser, setEditedUser, openEditDialog}) {
    const [anchorEl, setAnchorEl] = React.useState(null);
    const open = Boolean(anchorEl);
    const handleClick = (event) => {
        setAnchorEl(event.currentTarget);
    };
    const handleClose = () => {
        setAnchorEl(null);
    };
    const handleDelete = () => {
        // TODO:replace confirm dialog
        if (window.confirm(`Are you sure wanted to delete user: ${user} ?`)) {
            deleteUser(user.id);
        }
    }

    const handleEdit = () => {
        setEditedUser(user);
        openEditDialog(true);
        handleClose();
    }

    return (
        <div>
            <IconButton
                aria-label="more"
                id="long-button"
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
                <MenuItem onClick={handleEdit}>
                    <ListItemIcon>
                        <EditOutlinedIcon/>
                    </ListItemIcon>
                    <ListItemText>Редагувати</ListItemText>
                </MenuItem>
                <MenuItem onClick={handleClose}>
                    <ListItemIcon>
                        <DeleteForeverOutlinedIcon sx={{color: '#D32F2F'}}/>
                    </ListItemIcon>
                    <ListItemText sx={{color: '#D32F2F'}} onClick={handleDelete}>Видалити</ListItemText>
                </MenuItem>
            </Menu>
        </div>
    );
}

export default Users;
