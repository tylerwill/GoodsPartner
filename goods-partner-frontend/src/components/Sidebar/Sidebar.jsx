import * as React from 'react'
import Drawer from '@mui/material/Drawer'
import Toolbar from '@mui/material/Toolbar'
import List from '@mui/material/List'
import ListItem from '@mui/material/ListItem'
import Divider from '@mui/material/Divider'
import { Link } from 'react-router-dom'
import LocalShippingIcon from '@mui/icons-material/LocalShipping'
import ListItemButton from '@mui/material/ListItemButton'
import ListItemIcon from '@mui/material/ListItemIcon'
import ListItemText from '@mui/material/ListItemText'
import { ContentPasteSharp, Inventory2Sharp } from '@mui/icons-material'
import SupervisedUserCircle from '@mui/icons-material/SupervisedUserCircle'
import LogoutIcon from '@mui/icons-material/Logout'
import GrandeDolceLogo from '../Logo/GrandeDolceLogo'
import useAuth from '../../auth/AuthProvider'
import ShoppingCartIcon from '@mui/icons-material/ShoppingCart'
import BusinessIcon from '@mui/icons-material/Business';

const ListButton = (icon, name, paddingLeft) => {
	return (
		<>
			<ListItemButton sx={{ pl: paddingLeft }}>
				<ListItemIcon>{icon}</ListItemIcon>
				<ListItemText primary={name} />
			</ListItemButton>
			<Divider />
		</>
	)
}
const drawerWidth = 256

const Sidebar = ({ open }) => {
	const { user } = useAuth()

	return (
		<div className='sidebar'>
			<Drawer
				sx={{
					width: drawerWidth,
					flexShrink: 0,
					'& .MuiDrawer-paper': {
						width: drawerWidth,
						boxSizing: 'border-box'
					}
				}}
				variant='persistent'
				anchor='left'
				open={open}
			>
				<Toolbar />

				{/*TODO: [Nastya] Link in sidebar should be highlighted based on current URI*/}
				<List sx={{ overflow: 'auto', paddingTop: 0 }}>
					<ListItem sx={{ padding: ' 16px 24px' }}>
						<GrandeDolceLogo />
					</ListItem>
					<Divider />

					<ListItem disablePadding component={Link} to={'/deliveries'}>
						{ListButton(<Inventory2Sharp />, 'Доставки')}
					</ListItem>

					{user.role !== 'DRIVER' && (
						<>
							<ListItem disablePadding component={Link} to={'/orders'}>
								{ListButton(<ShoppingCartIcon />, 'Замовлення')}
							</ListItem>

							<ListItem disablePadding component={Link} to={'/cars'}>
								{ListButton(<LocalShippingIcon />, 'Автомобілі')}
							</ListItem>

							<ListItem disablePadding component={Link} to={'/users'}>
								{ListButton(<SupervisedUserCircle />, 'Користувачі')}
							</ListItem>

							<ListItem disablePadding component={Link} to={'/clients/addresses'}>
								{ListButton(<BusinessIcon />, 'Адреси клієнтів')}
							</ListItem>

							<ListItem disablePadding component={Link} to={'/reports'}>
								{ListButton(<ContentPasteSharp />, 'Звітність')}
							</ListItem>


						</>
					)}
					<Divider />
				</List>

				<List style={{ marginTop: `auto` }}>
					<ListItem disablePadding>
						{ListButton(<LogoutIcon />, 'Вийти')}
					</ListItem>
				</List>
			</Drawer>
		</div>
	)
}

export default Sidebar
