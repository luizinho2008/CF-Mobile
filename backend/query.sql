CREATE DATABASE projeto2024;
USE projeto2024;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(50),
    cpf VARCHAR(11),
    idade DATE
);

CREATE TABLE msgfisicas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    message TEXT,
    user_id INT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE msgintelectuais (
    id INT AUTO_INCREMENT PRIMARY KEY,
    message TEXT,
    user_id INT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE msgsensoriais (
    id INT AUTO_INCREMENT PRIMARY KEY,
    message TEXT,
    user_id INT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE msgneurodivergentes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    message TEXT,
    user_id INT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);