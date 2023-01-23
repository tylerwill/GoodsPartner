import {FC, useState, MouseEvent} from 'react'
import IconButton from '@mui/material/IconButton'
import MoreVertIcon from '@mui/icons-material/MoreVert'
import {ListItemIcon, ListItemText, Menu, MenuItem} from '@mui/material'
import EditOutlinedIcon from '@mui/icons-material/EditOutlined'
import DeleteForeverOutlinedIcon from '@mui/icons-material/DeleteForeverOutlined'
import {User} from '../../model/User'
import {ConfirmationDialog} from "../ConfirmationDialog/ConfirmationDialog";

interface Props {
    user: User
    deleteUser: Function
    setEditedUser: Function
    openEditDialog: Function
}

const ActionMenu: FC<Props> = ({
                                   user,
                                   deleteUser,
                                   setEditedUser,
                                   openEditDialog
                               }) => {
    const [anchorEl, setAnchorEl] = useState<HTMLButtonElement | null>()
    const open = Boolean(anchorEl)

    const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);

    const handleClick = (event: MouseEvent<HTMLButtonElement>) => {
        setAnchorEl(event.currentTarget)
    }
    const handleClose = () => {
        setAnchorEl(null)
    }
    const handleDelete = () => {
        deleteUser(user.id)
    }

    const handleEdit = () => {
        setEditedUser(user)
        openEditDialog(true)
        handleClose()
    }

    return (
        <div>
            <IconButton
                aria-label='more'
                id='long-button'
                aria-controls={open ? 'long-menu' : undefined}
                aria-expanded={open ? 'true' : undefined}
                aria-haspopup='true'
                onClick={handleClick}
            >
                <MoreVertIcon/>
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
                        <EditOutlinedIcon/>
                    </ListItemIcon>
                    <ListItemText>Редагувати</ListItemText>
                </MenuItem>
                <MenuItem onClick={handleClose}>
                    <ListItemIcon>
                        <DeleteForeverOutlinedIcon sx={{color: '#D32F2F'}}/>
                    </ListItemIcon>
                    <ListItemText sx={{color: '#D32F2F'}} onClick={()=> setDeleteDialogOpen(true)}>
                        Видалити
                    </ListItemText>
                </MenuItem>
            </Menu>
            <ConfirmationDialog
                title={"Видалити користувача"}
                text={"Ви впевнені, що бажаєте видалити користувача? Цю дію не можна буде відмінити."}
                open={deleteDialogOpen}
                setOpen={setDeleteDialogOpen}
                onAction={handleDelete}
            />
        </div>
    )
}

export default ActionMenu
