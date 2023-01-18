import React from 'react'
import IconButton from '@mui/material/IconButton'
import MoreVertIcon from '@mui/icons-material/MoreVert'
import { ListItemIcon, ListItemText, Menu, MenuItem } from '@mui/material'
import EditOutlinedIcon from '@mui/icons-material/EditOutlined'
import DeleteForeverOutlinedIcon from '@mui/icons-material/DeleteForeverOutlined'
import { User } from '../../model/User'

interface Props {
	user: User
	deleteUser: Function
	setEditedUser: Function
	openEditDialog: Function
}

const ActionMenu: React.FC<Props> = ({
	user,
	deleteUser,
	setEditedUser,
	openEditDialog
}) => {
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
		if (window.confirm(`Are you sure wanted to delete user: ${user} ?`)) {
			deleteUser(user.id)
		}
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

export default ActionMenu
