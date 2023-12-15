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

-- Spring's JdbcOAuth2AuthorizationService requires this table, see
-- classpath:org/springframework/security/oauth2/server/authorization/oauth2-authorization-schema.sql
CREATE TABLE oauth2_authorization
(
    id VARCHAR(100) NOT NULL,
    registered_client_id VARCHAR(100) NOT NULL,
    principal_name VARCHAR(200) NOT NULL,
    authorization_grant_type VARCHAR(100) NOT NULL,
    authorized_scopes VARCHAR(1000) DEFAULT NULL,
    attributes VARCHAR DEFAULT NULL,
    state VARCHAR(500) DEFAULT NULL,
    authorization_code_value VARCHAR DEFAULT NULL,
    authorization_code_issued_at TIMESTAMP DEFAULT NULL,
    authorization_code_expires_at TIMESTAMP DEFAULT NULL,
    authorization_code_metadata VARCHAR DEFAULT NULL,
    access_token_value VARCHAR DEFAULT NULL,
    access_token_issued_at TIMESTAMP DEFAULT NULL,
    access_token_expires_at TIMESTAMP DEFAULT NULL,
    access_token_metadata VARCHAR DEFAULT NULL,
    access_token_type VARCHAR(100)  DEFAULT NULL,
    access_token_scopes VARCHAR(1000) DEFAULT NULL,
    oidc_id_token_value VARCHAR DEFAULT NULL,
    oidc_id_token_issued_at TIMESTAMP DEFAULT NULL,
    oidc_id_token_expires_at TIMESTAMP DEFAULT NULL,
    oidc_id_token_metadata VARCHAR DEFAULT NULL,
    refresh_token_value VARCHAR DEFAULT NULL,
    refresh_token_issued_at TIMESTAMP DEFAULT NULL,
    refresh_token_expires_at TIMESTAMP DEFAULT NULL,
    refresh_token_metadata VARCHAR DEFAULT NULL,
    user_code_value VARCHAR DEFAULT NULL,
    user_code_issued_at TIMESTAMP DEFAULT NULL,
    user_code_expires_at TIMESTAMP DEFAULT NULL,
    user_code_metadata VARCHAR DEFAULT NULL,
    device_code_value VARCHAR DEFAULT NULL,
    device_code_issued_at TIMESTAMP DEFAULT NULL,
    device_code_expires_at TIMESTAMP DEFAULT NULL,
    device_code_metadata VARCHAR DEFAULT NULL,
    PRIMARY KEY (id)
);
