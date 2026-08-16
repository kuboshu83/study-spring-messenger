CREATE TABLE recipients (
    id uuid primary key DEFAULT gen_random_uuid(),
    name text not null,
    email text unique not null,
    locked boolean not null DEFAULT false,
    created_at timestamp(0) with time zone not null DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE groups (
    id uuid primary key DEFAULT gen_random_uuid(),
    name text unique not null,
    locked boolean not null DEFAULT false,
    description text not null,
    created_at timestamp(0) with time zone not null DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE recipients_groups (
    recipient_id uuid not null references recipients(id) ON DELETE CASCADE,
    group_id uuid not null references groups(id) ON DELETE CASCADE,
    created_at timestamp(0) with time zone not null DEFAULT CURRENT_TIMESTAMP,
    primary key (recipient_id, group_id)
);

CREATE TABLE applications (
    id uuid primary key DEFAULT gen_random_uuid(),
    name text unique not null,
    locked boolean not null DEFAULT false,
    description text not null,
    created_at timestamp(0) with time zone not null DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE applications_groups (
    application_id uuid not null references applications(id) ON DELETE CASCADE,
    group_id uuid not null references groups(id) ON DELETE CASCADE,
    created_at timestamp(0) with time zone not null DEFAULT CURRENT_TIMESTAMP,
    primary key (application_id, group_id)
);