-- БАЗА ДАННЫХ ДЛЯ УЧЁТА ЗАЯВОК НА РЕМОНТ

-- Таблица ролей
CREATE TABLE IF NOT EXISTS roles (
                                     id BIGSERIAL PRIMARY KEY,
                                     name VARCHAR(50) UNIQUE NOT NULL
    );

-- Таблица пользователей (для входа в систему)
CREATE TABLE IF NOT EXISTS users (
                                     id BIGSERIAL PRIMARY KEY,
                                     username VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- Связь пользователей и ролей
CREATE TABLE IF NOT EXISTS user_roles (
                                          user_id BIGINT NOT NULL,
                                          role_id BIGINT NOT NULL,
                                          PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
    );

-- Таблица клиентов
CREATE TABLE IF NOT EXISTS clients (
                                       id BIGSERIAL PRIMARY KEY,
                                       full_name VARCHAR(255) NOT NULL,
    phone VARCHAR(20) UNIQUE NOT NULL,
    email VARCHAR(255),
    address TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- Таблица устройств
CREATE TABLE IF NOT EXISTS devices (
                                       id BIGSERIAL PRIMARY KEY,
                                       client_id BIGINT NOT NULL,
                                       device_type VARCHAR(30) NOT NULL,
    brand VARCHAR(100) NOT NULL,
    model VARCHAR(100) NOT NULL,
    serial_number VARCHAR(100),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE CASCADE
    );

-- Таблица техников
CREATE TABLE IF NOT EXISTS technicians (
                                           id BIGSERIAL PRIMARY KEY,
                                           full_name VARCHAR(255) NOT NULL,
    phone VARCHAR(20) UNIQUE NOT NULL,
    email VARCHAR(255),
    specialization VARCHAR(100),
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- Таблица заявок на ремонт
CREATE TABLE IF NOT EXISTS repair_requests (
                                               id BIGSERIAL PRIMARY KEY,
                                               client_id BIGINT NOT NULL,
                                               device_id BIGINT NOT NULL,
                                               technician_id BIGINT,
                                               problem_description TEXT NOT NULL,
                                               status VARCHAR(20) NOT NULL DEFAULT 'NEW',
    estimated_cost DECIMAL(10, 2),
    final_cost DECIMAL(10, 2),
    completed_at TIMESTAMP,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE RESTRICT,
    FOREIGN KEY (device_id) REFERENCES devices(id) ON DELETE RESTRICT,
    FOREIGN KEY (technician_id) REFERENCES technicians(id) ON DELETE SET NULL
    );

-- Индексы для ускорения
CREATE INDEX IF NOT EXISTS idx_requests_status ON repair_requests(status);
CREATE INDEX IF NOT EXISTS idx_requests_client ON repair_requests(client_id);
CREATE INDEX IF NOT EXISTS idx_requests_technician ON repair_requests(technician_id);
CREATE INDEX IF NOT EXISTS idx_requests_dates ON repair_requests(created_at, completed_at);
