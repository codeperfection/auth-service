CREATE TABLE auth_user
(
    id UUID,

    email VARCHAR(256) NOT NULL,
    password VARCHAR(256) NOT NULL,
    name VARCHAR(256) NOT NULL,

    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,

    PRIMARY KEY (id),
    UNIQUE (email)
);

CREATE TABLE role
(
    id UUID,

    name VARCHAR(256) NOT NULL,

    PRIMARY KEY (id),
    UNIQUE (name)
);

INSERT INTO role
VALUES ('6ff18911-8db8-4803-b8e8-7b36b0540753', 'ROLE_USER');

CREATE TABLE user_role
(
    user_id UUID,
    role_id UUID,

    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES auth_user (id),
    FOREIGN KEY (role_id) REFERENCES role (id)
);

CREATE INDEX idx_user_role_user_id ON user_role (user_id);

-- Spring's JdbcRegisteredClientRepository requires this exact table, see
-- classpath:org/springframework/security/oauth2/server/authorization/client/oauth2-registered-client-schema.sql
CREATE TABLE oauth2_registered_client
(
    id VARCHAR(100) NOT NULL,
    client_id VARCHAR(100) NOT NULL,
    client_id_issued_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    client_secret VARCHAR(200) DEFAULT NULL,
    client_secret_expires_at timestamp DEFAULT NULL,
    client_name VARCHAR(200) NOT NULL,
    client_authentication_methods VARCHAR(1000) NOT NULL,
    authorization_grant_types VARCHAR(1000) NOT NULL,
    redirect_uris VARCHAR(1000) DEFAULT NULL,
    post_logout_redirect_uris VARCHAR(1000) DEFAULT NULL,
    scopes VARCHAR(1000) NOT NULL,
    client_settings VARCHAR(2000) NOT NULL,
    token_settings VARCHAR(2000) NOT NULL,
    PRIMARY KEY (id)
);
