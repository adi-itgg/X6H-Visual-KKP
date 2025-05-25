create table if not exists m_curriculum
(
    id       int auto_increment
        primary key,
    name     varchar(20) not null,
    duration time        not null
)
    engine = InnoDB;

create table if not exists m_invoice
(
    id     int auto_increment
        primary key,
    name   varchar(20)     null,
    amount bigint unsigned null
)
    engine = InnoDB;

create table if not exists m_parent
(
    id           bigint unsigned auto_increment
        primary key,
    nik          varchar(18)                         not null,
    name         varchar(30)                         not null,
    job          varchar(30)                         null,
    phone_number varchar(20)                         not null,
    address      varchar(300)                        not null,
    created_at   timestamp default CURRENT_TIMESTAMP not null,
    updated_at   timestamp default CURRENT_TIMESTAMP not null,
    constraint id
        unique (id),
    constraint m_parent_uk
        unique (nik)
)
    engine = InnoDB;

create table if not exists m_role
(
    id         bigint unsigned auto_increment
        primary key,
    name       varchar(30)                         null,
    created_at timestamp default CURRENT_TIMESTAMP not null
)
    engine = InnoDB;

create table if not exists m_student
(
    id          bigint unsigned auto_increment
        primary key,
    name        varchar(30)                         not null,
    gender      char                                not null comment 'L or P',
    birth_place varchar(15)                         not null comment 'tempat lahir',
    birth_date  date                                not null comment 'tanggal lahir',
    address     varchar(200)                        not null comment 'alamat',
    parent_id   bigint unsigned                     null comment 'id orang tua/wali',
    created_at  timestamp default CURRENT_TIMESTAMP not null,
    updated_at  timestamp default CURRENT_TIMESTAMP not null,
    constraint id
        unique (id),
    constraint m_student_m_parent_id_fk
        foreign key (parent_id) references m_parent (id)
)
    engine = InnoDB;

create table if not exists m_teacher
(
    id           bigint unsigned auto_increment
        primary key,
    nip          varchar(18)                         not null,
    name         varchar(30)                         not null,
    gender       char                                not null,
    education    varchar(30)                         null,
    phone_number varchar(20)                         null,
    address      varchar(300)                        not null,
    created_at   timestamp default CURRENT_TIMESTAMP not null,
    updated_at   timestamp default CURRENT_TIMESTAMP not null,
    constraint id
        unique (id),
    constraint m_teacher_uk
        unique (nip)
)
    engine = InnoDB;

create table if not exists m_class
(
    id           int auto_increment
        primary key,
    name         varchar(20)                         not null,
    teacher_id   bigint unsigned                     not null,
    max_capacity int                                 not null,
    schedule     time                                not null,
    duration     time                                not null,
    invoice_id   int                                 null comment 'Harga kelas tambahan',
    created_at   timestamp default CURRENT_TIMESTAMP not null,
    updated_at   timestamp default CURRENT_TIMESTAMP not null,
    constraint id
        unique (id),
    constraint m_class_m_invoice_id_fk
        foreign key (invoice_id) references m_invoice (id),
    constraint m_class_m_teacher_id_fk
        foreign key (teacher_id) references m_teacher (id)
)
    engine = InnoDB;

create table if not exists m_user
(
    id                     bigint unsigned auto_increment
        primary key,
    name                   varchar(30)                         null,
    email                  varchar(50)                         null,
    password               varchar(300)                        null,
    created_at             timestamp default CURRENT_TIMESTAMP null,
    updated_at             timestamp default CURRENT_TIMESTAMP null,
    forgot_pass_otp        varchar(200)                        null,
    forgot_pass_expired_at timestamp                           null
)
    engine = InnoDB;

create table if not exists t_bill_student
(
    id           bigint unsigned auto_increment
        primary key,
    student_id   bigint unsigned                     not null,
    parent_id    bigint unsigned                     not null,
    total_amount bigint unsigned                     null,
    created_at   timestamp default CURRENT_TIMESTAMP not null,
    constraint id
        unique (id),
    constraint t_bill_student_m_parent_id_fk
        foreign key (parent_id) references m_parent (id),
    constraint t_bill_student_m_student_id_fk
        foreign key (student_id) references m_student (id)
)
    engine = InnoDB;

create table if not exists t_bill_student_detail
(
    id              bigint unsigned auto_increment
        primary key,
    bill_student_id bigint unsigned          not null,
    invoice_id      int                      not null,
    name            varchar(20)              not null,
    amount          bigint unsigned          not null,
    created_date    date default (curdate()) not null,
    constraint id
        unique (id),
    constraint t_bill_student_detail_m_invoice_id_fk
        foreign key (invoice_id) references m_invoice (id),
    constraint t_bill_student_detail_t_bill_id_fk
        foreign key (bill_student_id) references t_bill_student (id)
)
    engine = InnoDB;

create table if not exists t_student
(
    id            bigint unsigned auto_increment
        primary key,
    student_id    bigint unsigned                     not null,
    curriculum_id int                                 not null,
    class_id      int                                 not null,
    enter_year    int                                 null comment 'tahun masuk',
    created_at    timestamp default CURRENT_TIMESTAMP not null,
    updated_at    timestamp default CURRENT_TIMESTAMP not null,
    constraint id
        unique (id),
    constraint t_student_m_class_id_fk
        foreign key (class_id) references m_class (id),
    constraint t_student_m_curriculum_id_fk
        foreign key (curriculum_id) references m_curriculum (id),
    constraint t_student_m_student_id_fk
        foreign key (student_id) references m_student (id)
)
    engine = InnoDB;

