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

db.query("SELECT 1", (err) => {
    if (err) console.error("Erro ao testar conexão:", err);
    else console.log("Conexão efetuada com sucesso");
});

// Rota para cadastrar novo usuário
app.post("/cadPost", (req, res) => {
    const { nome, email, senha } = req.body;

    if (!nome || !email || !senha) {
        return res.status(400).json({ message: "Todos os campos são obrigatórios." });
    }

    getUserByEmail(email, (err, user) => {
        if (err) {
            console.error("Erro ao verificar email:", err);
            return res.status(500).json({ message: "Erro no servidor." });
        }

        if (user) {
            return res.status(400).json({ message: "Email já cadastrado." });
        }

        const query = "INSERT INTO users2(nome, email, senha) VALUES (?, ?, ?)";
        db.query(query, [nome, email, senha], (err) => {
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

// Função para buscar usuário por email
const getUserByEmail = (email, callback) => {
    db.query("SELECT id, nome, email FROM users WHERE email = ?", [email], (err, results) => {
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
        `SELECT users.nome, users.email, ${tableName}.message FROM ${tableName}
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
const insertNewMessage = (socket, tipo, message, userId, userName, email) => {
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
                io.to(tipo).emit("newMessage", { nome: userName, message, email });
            }
        }
    );
};

// Gerenciando conexões socket
io.on("connection", (socket) => {
    console.log("Usuário conectado");

    socket.on("joinChat", ({ tipo, email }) => {
        const tableName = getTableName(tipo);
        if (!tableName) {
            console.error("Tipo de chat inválido:", tipo);
            socket.emit("error", "Tipo de chat inválido");
            return;
        }

        getUserByEmail(email, (err, user) => {
            if (err) {
                console.error("Erro ao buscar email:", err);
                socket.emit("emailInvalid");
            } else if (user) {
                socket.userId = user.id;
                socket.userName = user.nome;
                socket.email = user.email;
                socket.room = tipo;
                socket.join(tipo);
                socket.emit("emailValid", { name: user.nome, email: user.email });
                loadPreviousMessages(socket, tipo);
            } else {
                socket.emit("emailInvalid");
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
                socket.email
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