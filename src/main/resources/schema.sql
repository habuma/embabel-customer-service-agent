create table Order_Details (
    id identity primary key,
    order_number varchar(10),
    sku varchar(10),
    shipment_status varchar(10),
    refund_eligible boolean,
    resend_eligible boolean
);
