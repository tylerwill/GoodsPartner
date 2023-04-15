import {Box, Button, Tooltip} from "@mui/material";
import React, {FC} from "react";
import DoneIcon from "@mui/icons-material/Done";
import {DeliveryFormationStatus, DeliveryStatus} from "../../../model/Delivery";


interface ActionButtonsProps {
    formationStatus: DeliveryFormationStatus
    openResyncDialog: () => void
    calculateDeliveryHandler: () => void
    openRecalculateDialog: () => void
    openApproveDialog: () => void
    deliveryStatus: DeliveryStatus
    isAdmin:boolean
}

export const ActionButtons: FC<ActionButtonsProps> = ({
                                                          formationStatus,
                                                          deliveryStatus,
                                                          openApproveDialog,
                                                          openRecalculateDialog,
                                                          openResyncDialog,
                                                          calculateDeliveryHandler,
                                                          isAdmin
                                                      }) => {
    const calculationEnabled = isCalculationEnabled(formationStatus);
    const recalculationEnabled = calculationEnabled
        && (deliveryStatus === DeliveryStatus.DRAFT || (deliveryStatus === DeliveryStatus.APPROVED && isAdmin));

    console.log("en", recalculationEnabled)
    console.log("isAdmin", isAdmin)

    const syncEnabled = deliveryStatus === DeliveryStatus.DRAFT
        || (deliveryStatus === DeliveryStatus.APPROVED && isAdmin);

    const firstCalculationButtonVisible = formationStatus === DeliveryFormationStatus.READY_FOR_CALCULATION;

    const recalculationButtonVisible = isRecalculationButtonVisible(formationStatus);

    const isApproveEnabled = deliveryStatus === DeliveryStatus.DRAFT
        && formationStatus == DeliveryFormationStatus.CALCULATION_COMPLETED;

    return <Box sx={{display: "flex", justifyContent: "flex-end"}}>
        <Tooltip
            title="Оновити замовлення"
            placement="top"
            arrow>
                                <span>
                                    <Button sx={{mr: 2}} color={"warning"} variant="outlined"
                                            onClick={openResyncDialog} disabled={!syncEnabled}>
                                        Синхронізувати
                                    </Button>
                                </span>
        </Tooltip>

        {firstCalculationButtonVisible && (
            <CalculateButton
                title={"Розрахувати маршрут"}
                enabled={calculationEnabled}
                onClick={calculateDeliveryHandler}
            />
        )}

        {recalculationButtonVisible && (
            <CalculateButton
                title={"Перерахувати маршрут"}
                enabled={recalculationEnabled}
                onClick={openRecalculateDialog}

            />
        )}

        <ApproveButton
            enabled={isApproveEnabled}
            onClick={openApproveDialog}
        />

    </Box>;
}

interface ActionButtonProps {
    enabled: boolean
    onClick: () => any
    title?: string
}

function ApproveButton({enabled, onClick}: ActionButtonProps) {
    return (
        <Button
            variant='contained'
            color={'success'}
            onClick={onClick}
            disabled={!enabled}
        >
            <DoneIcon sx={{mr: 1, width: '0.7em', height: '0.7em'}}/> Затвердити
        </Button>
    )
}

function CalculateButton({enabled, onClick, title}: ActionButtonProps) {
    return (
        <Tooltip
            title='Для розрахунку маршруту відредагуйте адреси, що потребують уточнення'
            placement='top'
            arrow
        >
			<span>
				<Button sx={{mr: 2}} variant='contained' disabled={!enabled} onClick={onClick}>
					{title}
				</Button>
			</span>
        </Tooltip>
    )
}

function isCalculationEnabled(formationStatus: DeliveryFormationStatus) {
    return formationStatus === DeliveryFormationStatus.READY_FOR_CALCULATION
        || formationStatus === DeliveryFormationStatus.ROUTE_CALCULATION_FAILED
        || formationStatus === DeliveryFormationStatus.CALCULATION_COMPLETED;
}

function isRecalculationButtonVisible(formationStatus: DeliveryFormationStatus) {
    return formationStatus === DeliveryFormationStatus.CALCULATION_COMPLETED
        || formationStatus === DeliveryFormationStatus.ROUTE_CALCULATION_FAILED;
}