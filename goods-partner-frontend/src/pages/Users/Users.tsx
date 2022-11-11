import React, {useEffect, useState} from "react";
import {Box, Button, Typography} from "@mui/material";
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';

import CheckIcon from '@mui/icons-material/Check';
import CloseIcon from '@mui/icons-material/Close';
import UserFormDialog from "./UserFormDialog/UserFormDialog";
import {addUser, deleteUser, fetchUsers, updateUser} from "../../features/users/usersSlice";
import Loading from "../../components/Loading/Loading";
import ErrorAlert from "../../components/ErrorAlert/ErrorAlert";
import {useAppDispatch, useAppSelector} from "../../hooks/redux-hooks";
import {User, UserRole} from "../../model/User";
import ActionMenu from "../../components/ActionMenu/ActionMenu";

const Users = () => {
    const {users, loading, error} = useAppSelector(state => state.users);
    const dispatch = useAppDispatch();

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

    const addUserHandler = (user: User) => {
        dispatch(addUser(user));
        setNewUser(defaultNewUserState);
    }

    const updateUserHandler = (user: User) => {
        dispatch(updateUser(user));
        setNewUser(defaultNewUserState);
    }

    const deleteUserHandler = (id: number) => {
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
                                <TableCell>{user.role}</TableCell>
                                <TableCell align="center">{user.enabled ? <CheckIcon/> : <CloseIcon/>}</TableCell>
                                <TableCell align="center"><ActionMenu user={user}
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

// function toRoleString(role: UserRole) {
//     switch (role) {
//         case 'ADMIN': {
//             return 'Адміністратор'
//         }
//         case 'LOGIST': {
//             return 'Логіст'
//         }
//         case 'DRIVER': {
//             return 'Водій'
//         }
//     }
// }


export default Users;
