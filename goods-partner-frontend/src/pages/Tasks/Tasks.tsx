import {Box, Button, ListItemIcon, ListItemText, Menu, MenuItem, Typography} from '@mui/material'
import Table from '@mui/material/Table'
import TableBody from '@mui/material/TableBody'
import TableCell from '@mui/material/TableCell'
import TableContainer from '@mui/material/TableContainer'
import TableHead from '@mui/material/TableHead'
import TableRow from '@mui/material/TableRow'
import Paper from '@mui/material/Paper'
import ErrorAlert from '../../components/ErrorAlert/ErrorAlert'
import Loading from '../../components/Loading/Loading'
import React, {useState} from "react";
import TablePagination from "@mui/material/TablePagination";
import {MapPointStatus} from "../../model/MapPointStatus";
import {
    useAddTaskMutation,
    useDeleteTaskMutation,
    useGetTasksQuery,
    useUpdateTaskMutation
} from "../../api/tasks/tasks.api";
import {useGetCarsQuery} from "../../api/cars/cars.api";
import {AddTaskDialog} from "./AddTaskDialog/AddTaskDialog";
import {Task} from "../../model/Task";
import {Car} from "../../model/Car";
import IconButton from "@mui/material/IconButton";
import MoreVertIcon from "@mui/icons-material/MoreVert";
import EditOutlinedIcon from "@mui/icons-material/EditOutlined";
import DeleteForeverOutlinedIcon from "@mui/icons-material/DeleteForeverOutlined";

export const Tasks = () => {
    const {data: tasks} = useGetTasksQuery();
    const [addTask] = useAddTaskMutation();
    const [updateTask] = useUpdateTaskMutation();
    const [deleteTask] = useDeleteTaskMutation();

    const {data: cars, error, isLoading} = useGetCarsQuery();

    const [page, setPage] = useState(0)
    const [rowsPerPage, setRowsPerPage] = useState(25)

    const [isAddTaskDialogOpen, setIsAddTaskDialogOpen] = useState(false);
    const [isEditTaskDialogOpen, setIsEditTaskDialogOpen] = useState(false)



    const defaultNewTaskState = {
        description: '',
        car: undefined,
        executionDate: '',
        mapPoint: {
            longitude: 0,
            latitude: 0,
            address: '',
            status: MapPointStatus.UNKNOWN
        },
        id: 0
    } as Task;


    const [taskToAdd, setTaskToAdd] = useState(defaultNewTaskState);
    const [editedTask, setEditedTask] = useState(defaultNewTaskState)

    const handleChangePage = (event: any, newPage: number) => {
        setPage(newPage)
    }

    const handleChangeRowsPerPage = (event: React.BaseSyntheticEvent) => {
        setRowsPerPage(parseInt(event.target.value, 10))
        setPage(0)
    }

    const addNewTaskActionHandler = (task: Task) => {
        console.log("task", task);
        addTask(task);
    }

    if (isLoading || !tasks) {
        return <Loading/>
    }


    // Avoid a layout jump when reaching the last page with empty rows.
    const emptyRows =
        Math.max(0, (1 + page) * rowsPerPage - tasks.length);

    return (
        <section>
            <Box sx={{display: 'flex', justifyContent: 'space-between'}}>
                <Typography variant='h6' component='h2'>
                    Завдання
                </Typography>

                <Button onClick={() => setIsAddTaskDialogOpen(true)} variant='contained'>
                    Додати завдання
                </Button>
            </Box>
            <Box mt={2}>
                <Paper variant={'outlined'}>
                    <TableContainer>
                        <Table sx={{minWidth: 650}} aria-label='simple table'>
                            <TableHead sx={{fontWeight: 'bold'}}>
                                <TableRow>
                                    <TableCell>Машина</TableCell>
                                    <TableCell>Дата</TableCell>
                                    <TableCell>Адреса</TableCell>
                                    <TableCell>Опис завдання</TableCell>
                                    <TableCell/>
                                </TableRow>
                            </TableHead>

                            <TableBody>
                                {/*TODO: [Tolik] Fix key*/}
                                {tasks
                                    .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                                    .map(task => (
                                        <TableRow
                                            key={'task' + task.id}>
                                            <TableCell>{task.car!.name} {task.car!.licencePlate}</TableCell>
                                            <TableCell>{task.executionDate}</TableCell>
                                            <TableCell>{task.mapPoint.address}</TableCell>
                                            <TableCell> {task.description}</TableCell>
                                            <TableCell>
                                                <BasicMenu
                                                    task={task}
                                                    setEditedTask={setEditedTask}
                                                    deleteTask={deleteTask}
                                                    openEditDialog={setIsEditTaskDialogOpen}
                                                />
                                            </TableCell>
                                        </TableRow>
                                    ))}
                                {emptyRows > 0 && (
                                    <TableRow
                                        style={{
                                            height: 53 * emptyRows
                                        }}
                                    >
                                        <TableCell colSpan={4}/>
                                    </TableRow>
                                )}
                            </TableBody>
                        </Table>
                    </TableContainer>
                    <TablePagination
                        rowsPerPageOptions={[25, 50, 100]}
                        component='div'
                        count={tasks.length}
                        rowsPerPage={rowsPerPage}
                        page={page}
                        onPageChange={handleChangePage}
                        onRowsPerPageChange={handleChangeRowsPerPage}
                    />
                </Paper>
            </Box>
            {error && <ErrorAlert error={error}/>}
            {isAddTaskDialogOpen && <AddTaskDialog
                title={"Додати задачу"}
                closeDialog={() => setIsAddTaskDialogOpen(false)}
                open={isAddTaskDialogOpen}
                actionHandler={addNewTaskActionHandler}
                task={taskToAdd}
                setTask={setTaskToAdd}
                cars={cars as Car[]}/>}

            {isEditTaskDialogOpen && (
                <AddTaskDialog
                    title={"Редагувати задачу"}
                    closeDialog={() => setIsEditTaskDialogOpen(false)}
                    open={isEditTaskDialogOpen}
                    actionHandler={updateTask}
                    task={editedTask}
                    setTask={setEditedTask}
                    cars={cars as Car[]}/>
            )}

        </section>
    )
}


interface BasicMenuProps {
    task: Task
    deleteTask: (id: number) => void
    setEditedTask: (car: Task) => void
    openEditDialog: (open: boolean) => void
}

function BasicMenu({
                       task,
                       deleteTask,
                       setEditedTask,
                       openEditDialog
                   }: BasicMenuProps) {
    const [anchorEl, setAnchorEl] = React.useState<HTMLButtonElement | null>()
    const open = Boolean(anchorEl)
    const handleClick = (event: React.MouseEvent<HTMLButtonElement>) => {
        setAnchorEl(event.currentTarget)
    }
    const handleClose = () => {
        setAnchorEl(null)
    }
    const handleDelete = () => {
        // TODO:replace confirm dialog
        if (window.confirm(`Are you sure wanted to delete task: ${task.description} ?`)) {
            deleteTask(task.id)
        }
    }

    const handleEdit = () => {
        setEditedTask(task)
        openEditDialog(true)
        handleClose()
    }

    return (
        <div>
            <IconButton
                aria-controls={open ? 'long-menu' : undefined}
                aria-expanded={open ? 'true' : undefined}
                aria-haspopup='true'
                onClick={handleClick}
            >
                <MoreVertIcon />
            </IconButton>
            <Menu
                id='basic-menu'
                anchorEl={anchorEl}
                open={open}
                onClose={handleClose}
                MenuListProps={{
                    'aria-labelledby': 'basic-button'
                }}
            >
                <MenuItem onClick={handleEdit}>
                    <ListItemIcon>
                        <EditOutlinedIcon />
                    </ListItemIcon>
                    <ListItemText>Редагувати</ListItemText>
                </MenuItem>
                <MenuItem onClick={handleClose}>
                    <ListItemIcon>
                        <DeleteForeverOutlinedIcon sx={{ color: '#D32F2F' }} />
                    </ListItemIcon>
                    <ListItemText sx={{ color: '#D32F2F' }} onClick={handleDelete}>
                        Видалити
                    </ListItemText>
                </MenuItem>
            </Menu>
        </div>
    )
}