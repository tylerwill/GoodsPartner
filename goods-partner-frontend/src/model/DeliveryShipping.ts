export interface DeliveryShipping {
    article: string;
    totalAmount: number;
    totalWeight: number;
    productLoadDtos: ProductLoadDto[];
}

export interface ProductLoadDto {
    orderNumber: string;
    car: string;
    amount: number;
    weight: number;
    totalWeight: number;
}
