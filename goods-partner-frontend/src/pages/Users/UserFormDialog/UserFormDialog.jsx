import React from 'react'
import Box from '@mui/material/Box'
import Modal from '@mui/material/Modal'
import Grid from '@mui/material/Grid'
import {
	Button,
	Checkbox,
	FormControl,
	FormControlLabel,
	FormGroup,
	MenuItem,
	Select,
	TextField,
	Typography
} from '@mui/material'

const style = {
	position: 'absolute',
	top: '50%',
	left: '50%',
	transform: 'translate(-50%, -50%)',
	width: 510,
	bgcolor: 'background.paper',
	border: '2px solid #000',
	boxShadow: 24,
	padding: '16px 24px'
}

export default function UserFormDialog({
	title,
	closeDialog,
	open,
	actionHandler,
	user,
	setUser
}) {
	function handleEnabled(e) {
		setUser({ ...user, enabled: e.target.checked })
	}

	const saveHandler = () => {
		actionHandler(user)
		closeDialog()
	}

	return (
		<div>
			<Modal
				open={open}
				onClose={closeDialog}
				aria-labelledby='modal-modal-title'
				aria-describedby='modal-modal-description'
			>
				<Box sx={style}>
					<Grid container spacing={2}>
						<Grid item xs={12}>
							<Typography variant='h6' component='h3'>
								{title}
							</Typography>
						</Grid>
						<Grid item xs={6}>
							<TextField
								id='outlined-basic'
								value={user.userName}
								required
								onChange={e => setUser({ ...user, userName: e.target.value })}
								label="Ім'я"
								variant='outlined'
							/>
						</Grid>
						<Grid item xs={6}>
							<TextField
								id='outlined-basic'
								value={user.email}
								type={'email'}
								onChange={e => setUser({ ...user, email: e.target.value })}
								required
								label='Пошта'
								variant='outlined'
							/>
						</Grid>

						<Grid item xs={6}>
							<FormControl>
								<Select
									value={user.role}
									onChange={e => setUser({ ...user, role: e.target.value })}
									autoWidth
									MenuProps={{ MenuListProps: { disablePadding: true } }}
								>
									<MenuItem value={'ADMIN'}>Адміністратор</MenuItem>
									<MenuItem value={'DRIVER'}>Водій</MenuItem>
									<MenuItem value={'LOGIST'}>Логіст</MenuItem>
								</Select>
							</FormControl>
						</Grid>

						<Grid item xs={12} sx={{ mt: 2 }}>
							<FormGroup>
								<FormControlLabel
									control={<Checkbox />}
									checked={user.enabled}
									onChange={handleEnabled}
									label='Активний'
								/>
							</FormGroup>
						</Grid>

						<Grid item xs={12} sx={{ mt: 2 }}>
							<Box sx={{ display: 'flex', justifyContent: 'flex-end' }}>
								<Button sx={{ mr: 2 }} variant='outlined' onClick={closeDialog}>
									Скасувати
								</Button>
								<Button variant='contained' onClick={saveHandler}>
									{' '}
									Зберегти{' '}
								</Button>
							</Box>
						</Grid>
					</Grid>
				</Box>
			</Modal>
		</div>
	)
}
