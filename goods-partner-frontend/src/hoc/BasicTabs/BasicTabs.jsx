import React from 'react';
import Box from "@mui/material/Box";
import AppBar from "@mui/material/AppBar";
import Tabs from "@mui/material/Tabs";
import Tab from "@mui/material/Tab";

function TabPanel(props) {
    const {children, value, index, ...other} = props;

    return (
        <Box
            sx={{padding: '24px 0 24px'}}
            role="tabpanel"
            hidden={value !== index}
            id={`simple-tabpanel-${index}`}
            aria-labelledby={`simple-tab-${index}`}
            {...other}
        >
            {value === index && (
                <Box >
                    {children}
                </Box>
            )}
        </Box>
    );
}

function a11yProps(index) {
    return {
        id: `simple-tab-${index}`,
        'aria-controls': `simple-tabpanel-${index}`,
    };
}

export default function BasicTabs({children, labels, fullWidth}) {
    const [tabIndex, setTabIndex] = React.useState(0);

    const handleChange = (event, newValue) => {
        setTabIndex(newValue);
    };
    return (
        <Box sx={{bgcolor: 'background.paper'}}>
            {createTabsPanel(tabIndex, handleChange, labels, fullWidth)}
            {
                children.map((element, index) => {
                    return (<TabPanel key={"tabPanel" + labels[index].name} value={tabIndex} index={index}>
                        {element}
                    </TabPanel>)
                })
            }
        </Box>
    );
}

function createTabsPanel(value, handleChange, labels, fullWidth) {
    const tabs = (<Tabs value={value} onChange={handleChange} variant={fullWidth ? "fullWidth" : "standard"}
                        textColor={ fullWidth ? "inherit" : "primary"}>
        {
            labels.map((label, index) => <Tab sx={{borderBottom: 1, borderColor: 'divider'}}
                                              key={"tab" + label.name}
                                              iconPosition="start"
                                              label={<>{label.icon} {label.name}</>} disabled={!label.enabled} {...a11yProps(index)}/>)
        }
    </Tabs>);

    return fullWidth ? <AppBar sx={fullWidth && {padding: '0 250px'}} position={"static"}>{tabs}</AppBar> : <> {tabs} </>;
}
