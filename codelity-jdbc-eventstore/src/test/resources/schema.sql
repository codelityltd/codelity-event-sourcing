ALTER TABLE IF EXISTS event_delivery DROP CONSTRAINT fk_event_delivery_event_id;

DROP TABLE IF EXISTS event_delivery;
CREATE TABLE event_delivery (
    delivery_id int8 NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    stream_id varchar(50) NOT NULL,
    delivery_order int4 NOT NULL,
    event_id int8 NOT NULL,
    status int4 NOT NULL,
    retry_count int4 NOT NULL,
    picked_up_by uuid NULL,
    picked_up_time timestamp NULL,
    handler_code varchar(50) NULL,
    CONSTRAINT pk_event_delivery_id PRIMARY KEY (delivery_id)
);

DROP TABLE IF EXISTS event;
CREATE TABLE  IF NOT EXISTS event (
      event_id int8 NOT NULL GENERATED BY DEFAULT AS IDENTITY,
      stream_id varchar(50) NOT NULL,
      position int4 NOT NULL,
      name varchar(50) NOT NULL,
      metadata varchar(250) NOT NULL,
      payload text NOT NULL,
      date_created timestamp NOT NULL,
      CONSTRAINT pk_event_id PRIMARY KEY (event_id)
);

ALTER TABLE event_delivery ADD CONSTRAINT fk_event_delivery_event_id
    FOREIGN KEY (event_id) REFERENCES event(event_id);


