create table users (
  id bigint, 
  username varchar(255) not null, 
  password varchar(255) not null,
  money bigint not null,
  PRIMARY KEY (id),
  unique (username)
);
create table resources (
  id bigint, 
  name varchar(255) not null,
  PRIMARY KEY (id),
  unique (name)
);
create table stock (
  user_id bigint not null,
  resource_id bigint not null,
  amount bigint not null,
  unique index(user_id, resource_id)
);
