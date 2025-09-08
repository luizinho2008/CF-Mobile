const express = require("express");
const http = require("http");
const { Server } = require("socket.io");
const mysql = require("mysql2");
const cors = require("cors");
const crypto = require("crypto");

const app = express();
const server = http.createServer(app);
const io = new Server(server, { cors: { origin: "*", methods: ["GET", "POST"] } });

app.use(cors());
app.use(express.json());

const path = require("path");
const { SessionsClient } = require("@google-cloud/dialogflow");

// instancia cliente do Dialogflow
require('dotenv').config(); // no topo do arquivo

const dialogflowClient = new SessionsClient({
  keyFilename: process.env.GOOGLE_APPLICATION_CREDENTIALS
});

// Conexão com o banco de dados
const db = mysql.createPool({
    host: "rok3ly.h.filess.io",
    user: "newConexaoFamiliar_generally",
    password: "532522f8adee4ed2b3b4cf66481d4994a8afda6c",
    database: "newConexaoFamiliar_generally",
    port: "3307",
    waitForConnections: true,
    connectionLimit: 50,
    queueLimit: 0
});

db.query("SELECT 1", (err) => {
    if (err) console.error(err);
    else console.log("Conexão efetuada com sucesso");
});

// Função para buscar usuário por email
const getUserByEmail = (email, callback) => {
    db.query("SELECT id, nome, email, senha FROM users2 WHERE email = ?", [email], (err, results) => {
        if (err) return callback(err, null);
        return callback(null, results.length > 0 ? results[0] : null);
    });
};

// Cadastro de usuário
app.post("/cadPost", (req, res) => {
    const { nome, email, senha } = req.body;
    if (!nome || !email || !senha) return res.status(400).json({ message: "Todos os campos são obrigatórios." });

    getUserByEmail(email, (err, user) => {
        if (err) return res.status(500).json({ message: "Erro no servidor." });
        if (user) return res.status(400).json({ message: "Email já cadastrado." });

        const senhaHash = crypto.createHash("sha256").update(senha).digest("hex");

        db.query("INSERT INTO users2(nome,email,senha) VALUES (?,?,?)", [nome, email, senhaHash], (err) => {
            if (err) return res.status(500).json({ message: "Erro ao cadastrar o usuário." });
            return res.status(200).json({ message: "Cadastro realizado com sucesso!" });
        });
    });
});

app.post("/sendMessage1", async (req, res) => {
  try {
    const { message, sessionId } = req.body;

    if (!message) {
      return res.status(400).json({ error: "Mensagem obrigatória" });
    }

    const sessionPath = dialogflowClient.projectAgentSessionPath(
      "autismobot-durv", // seu project_id
      sessionId || "123456"
    );

    const request = {
      session: sessionPath,
      queryInput: {
        text: {
          text: message,
          languageCode: "pt-BR",
        },
      },
    };

    const responses = await dialogflowClient.detectIntent(request);
    const result = responses[0].queryResult;

    res.json({
      reply: result.fulfillmentText || "[sem resposta do bot]",
    });
  } catch (err) {
    console.error("Erro no Dialogflow:", err);
    res.status(500).json({ error: err.message });
  }
});

// Login
app.post("/login", (req, res) => {
    const { email, senha } = req.body;
    if (!email || !senha) return res.status(400).json({ message: "Email e senha são obrigatórios." });

    getUserByEmail(email, (err, user) => {
        if (err) return res.status(500).json({ message: "Erro no servidor." });
        if (!user) return res.status(401).json({ message: "Email não cadastrado." });

        const senhaHash = crypto.createHash("sha256").update(senha).digest("hex");

        if (user.senha === senhaHash) {
            return res.status(200).json({ message: "Login realizado com sucesso!", user: { id: user.id, nome: user.nome, email: user.email } });
        } else {
            return res.status(401).json({ message: "Senha incorreta." });
        }
    });
});

// Funções auxiliares para chat
const getTableName = (tipo) => ({
    fisicas: "msgfisicas",
    intelectuais: "msgintelectuais",
    emocionais: "msgemocionais",
    sensoriais: "msgsensoriais",
    neurodivergentes: "msgneurodivergentes"
}[tipo] || null);

const loadPreviousMessages = (socket, tipo) => {
    const tableName = getTableName(tipo);
    if (!tableName) return;
    db.query(
        `SELECT users.nome, users.email, ${tableName}.message FROM ${tableName} INNER JOIN users2 AS users ON ${tableName}.user_id = users.id ORDER BY ${tableName}.id ASC`,
        (err, results) => {
            if (!err) socket.emit("previousMessages", results);
        }
    );
};

const insertNewMessage = (socket, tipo, message, userId, userName, email) => {
    const tableName = getTableName(tipo);
    if (!tableName) return;
    db.query(
        `INSERT INTO ${tableName} (user_id, message) VALUES (?, ?)`,
        [userId, message],
        (err) => {
            if (err) {
                console.error("Erro ao inserir mensagem:", err);
                socket.emit("error", "Erro ao enviar a mensagem.");
                return;
            }
            io.to(tipo).emit("newMessage", {
                nome: userName,
                message,
                email
            });
        }
    );
};

// Socket.IO
io.on("connection", (socket) => {
  console.log("Usuário conectado");

  socket.on("joinChat", ({ email, tipo }) => {
    if (!email || !tipo) return socket.emit("error", "Email e tipo são obrigatórios");
    
    getUserByEmail(email, (err, user) => {
      if (err) return socket.emit("error", "Erro no servidor");
      if (!user) return socket.emit("emailInvalid");

      const tableName = getTableName(tipo);
      if (!tableName) return socket.emit("error", "Tipo inválido");

      socket.userId = user.id;
      socket.userName = user.nome;
      socket.email = user.email;
      socket.room = tipo;

      socket.join(tipo);
      socket.emit("emailValid", { name: user.nome, email: user.email });

      // Envia mensagens antigas
      db.query(
        `SELECT u.nome, u.email, m.message FROM ${tableName} m INNER JOIN users2 u ON m.user_id = u.id ORDER BY m.id ASC`,
        (err, results) => {
          if (!err) socket.emit("previousMessages", results);
        }
      );
    });
  });

  socket.on("sendMessage", (data) => {
    if (!socket.userId || !socket.room) return socket.emit("error", "Usuário não autenticado");

    if (!data || !data.message) return;

    const tableName = getTableName(socket.room);
    if (!tableName) return;

    db.query(
      `INSERT INTO ${tableName} (user_id, message) VALUES (?, ?)`,
      [socket.userId, data.message],
      (err) => {
        if (err) return socket.emit("error", "Erro ao enviar mensagem");

        io.to(socket.room).emit("newMessage", {
          nome: socket.userName,
          email: socket.email,
          message: data.message
        });
      }
    );
  });

  socket.on("disconnect", () => {
    console.log("Usuário desconectado");
  });
});

server.listen(5000, () => console.log("Servidor rodando na porta 5000"));