psql -U postgres -d inventory_view -c "create table if not exists inventory (product_id uuid not null, stock int not null, constraint pk_product_id primary key (product_id))"


