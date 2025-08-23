const express = require("express");
const http = require("http");
const { Server } = require("socket.io");
const mysql = require("mysql2");
const cors = require("cors");

const app = express();
const server = http.createServer(app);
const io = new Server(server, {
    cors: {
        origin: "*",
        methods: ["GET", "POST"]
    }
});

app.use(cors());
app.use(express.json());

// Configuração do banco de dados
const db = mysql.createPool({
    host: "j4b2qn.h.filess.io",
    user: "conexaofamiliar_managedpot",
    password: "502ad781f3d8a2969d7f7b8c26675bd0862b8934",
    database: "conexaofamiliar_managedpot",
    port: "3307",
    waitForConnections: true,
    connectionLimit: 50,
    queueLimit: 0
});

db.query("SELECT 1", (err, results) => {
    if (err) console.error("Erro ao testar conexão:", err);
    else console.log("Conexão efetuada com sucesso");
});

// Rota para cadastrar novo usuário
app.post("/cadPost", (req, res) => {
    const { nome, cpf, idade } = req.body;

    if (!nome || !cpf || !idade) {
        return res.status(400).json({ message: "Todos os campos são obrigatórios." });
    }

    getUserByCpf(cpf, (err, user) => {
        if (err) {
            console.error("Erro ao verificar CPF:", err);
            return res.status(500).json({ message: "Erro no servidor." });
        }

        if (user) {
            return res.status(400).json({ message: "Já existe um usuário com esse CPF." });
        }

        const query = "INSERT INTO users (nome, cpf, idade) VALUES (?, ?, ?)";
        db.query(query, [nome, cpf, idade], (err, result) => {
            if (err) {
                console.error("Erro ao inserir usuário:", err);
                return res.status(500).json({ message: "Erro ao cadastrar o usuário." });
            }

            return res.status(200).json({ message: "Cadastro realizado com sucesso!" });
        });
    });
});

// Função que retorna o nome da tabela baseado no tipo do chat
const getTableName = (tipo) => {
    const chatTables = {
        fisicas: "msgfisicas",
        intelectuais: "msgintelectuais",
        emocionais: "msgemocionais",
        sensoriais: "msgsensoriais",
        neurodivergentes: "msgneurodivergentes"
    };
    return chatTables[tipo] || null;
};

// Função para buscar usuário por CPF
const getUserByCpf = (cpf, callback) => {
    db.query("SELECT id, nome, cpf FROM users WHERE cpf = ?", [cpf], (err, results) => {
        if (err) return callback(err, null);
        return callback(null, results.length > 0 ? results[0] : null);
    });
};

// Função para carregar mensagens anteriores
const loadPreviousMessages = (socket, tipo) => {
    const tableName = getTableName(tipo);
    if (!tableName) {
        console.error("Tipo de chat inválido:", tipo);
        return;
    }

    db.query(
        `SELECT users.nome, users.cpf, ${tableName}.message FROM ${tableName} 
         INNER JOIN users ON ${tableName}.user_id = users.id 
         ORDER BY ${tableName}.id ASC`,
        (err, results) => {
            if (err) {
                console.error("Erro ao carregar mensagens:", err);
            } else {
                socket.emit("previousMessages", results);
            }
        }
    );
};

// Função para inserir nova mensagem
const insertNewMessage = (socket, tipo, message, userId, userName, cpf) => {
    const tableName = getTableName(tipo);
    if (!tableName) {
        console.error("Tipo de chat inválido:", tipo);
        return;
    }

    db.query(
        `INSERT INTO ${tableName} (user_id, message) VALUES (?, ?)`,
        [userId, message],
        (err) => {
            if (err) {
                console.error("Erro ao inserir mensagem:", err);
            } else {
                // Corrigido: enviar 'nome' ao invés de 'name'
                io.to(tipo).emit("newMessage", { nome: userName, message, cpf });
            }
        }
    );
};

// Gerenciando conexões socket
io.on("connection", (socket) => {
    console.log("Usuário conectado");

    socket.on("joinChat", ({ tipo, cpf }) => {
        const tableName = getTableName(tipo);
        if (!tableName) {
            console.error("Tipo de chat inválido:", tipo);
            socket.emit("error", "Tipo de chat inválido");
            return;
        }

        getUserByCpf(cpf, (err, user) => {
            if (err) {
                console.error("Erro ao buscar CPF:", err);
                socket.emit("cpfInvalid");
            } else if (user) {
                socket.userId = user.id;
                socket.userName = user.nome;
                socket.cpf = user.cpf;
                socket.room = tipo;
                socket.join(tipo);
                socket.emit("cpfValid", { name: user.nome, cpf: user.cpf });
                loadPreviousMessages(socket, tipo);
            } else {
                socket.emit("cpfInvalid");
            }
        });
    });

    socket.on("sendMessage", (data) => {
        if (socket.userId && socket.room) {
            insertNewMessage(
                socket,
                socket.room,
                data.message,
                socket.userId,
                socket.userName,
                socket.cpf
            );
        }
    });

    socket.on("disconnect", () => {
        console.log("Usuário desconectado");
    });
});

server.listen(5000, () => {
    console.log("Servidor rodando na porta 5000");
});