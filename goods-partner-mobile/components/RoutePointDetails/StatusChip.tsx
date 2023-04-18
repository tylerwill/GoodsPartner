import {RoutePointStatus} from "../../model/RoutePoint";
import {FC} from "react";
import {Chip, ChipProps} from "@rneui/themed";
import tw from "twrnc";

interface StatusChipProps {
    routePointStatus: RoutePointStatus
}

export const StatusChip: FC<StatusChipProps> = ({routePointStatus}) => {
    let title;
    let borderColor = 'border-blue-600';
    let textColor = 'text-blue-500';
    let buttonType = 'outline' as 'solid' | 'outline';

    if (routePointStatus == RoutePointStatus.INPROGRESS) {
        title = 'В Роботі';
        buttonType = 'solid';
        textColor = 'text-white'
    } else if (routePointStatus == RoutePointStatus.PENDING) {
        title = 'В очікуванні';
    } else if (routePointStatus == RoutePointStatus.SKIPPED) {
        title = 'Пропущено';
        borderColor = 'border-yellow-600';
        textColor = 'text-yellow-500';
    } else if (routePointStatus == RoutePointStatus.DONE) {
        title = 'Завершено';
        borderColor = 'border-green-600';
        textColor = 'text-green-500'
    }

    return <Chip buttonStyle={tw`${borderColor}`}
                 titleStyle={tw`${textColor}`}
                 title={title}
                 type={buttonType}
    />;
}