const express = require("express");
const http = require("http");
const { Server } = require("socket.io");
const mysql = require("mysql2");
const cors = require("cors");
const crypto = require("crypto");

const app = express();
const server = http.createServer(app);
const io = new Server(server, { cors: { origin: "*", methods: ["GET","POST"] } });

app.use(cors());
app.use(express.json());

// Banco de dados
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

db.query("SELECT 1", (err) => { if (err) console.error(err); else console.log("Conexão efetuada com sucesso"); });

// Função para buscar usuário
const getUserByEmail = (email, callback) => {
    db.query("SELECT id, nome, email, senha FROM users2 WHERE email = ?", [email], (err, results) => {
        if (err) return callback(err, null);
        return callback(null, results.length>0 ? results[0] : null);
    });
};

// Cadastro
app.post("/cadPost", (req,res) => {
    const { nome, email, senha } = req.body;
    if (!nome || !email || !senha) return res.status(400).json({ message: "Todos os campos são obrigatórios." });

    getUserByEmail(email, (err,user) => {
        if (err) return res.status(500).json({ message: "Erro no servidor." });
        if (user) return res.status(400).json({ message: "Email já cadastrado." });

        const senhaHash = crypto.createHash("sha256").update(senha).digest("hex");

        db.query("INSERT INTO users2(nome,email,senha) VALUES (?,?,?)", [nome,email,senhaHash], (err) => {
            if (err) return res.status(500).json({ message: "Erro ao cadastrar o usuário." });
            return res.status(200).json({ message: "Cadastro realizado com sucesso!" });
        });
    });
});

// Login
app.post("/login", (req,res) => {
    const { email, senha } = req.body;
    if (!email || !senha) return res.status(400).json({ message: "Email e senha são obrigatórios." });

    getUserByEmail(email, (err,user) => {
        if (err) return res.status(500).json({ message: "Erro no servidor." });
        if (!user) return res.status(401).json({ message: "Email não cadastrado." });

        const senhaHash = crypto.createHash("sha256").update(senha).digest("hex");

        if (user.senha === senhaHash) {
            return res.status(200).json({ message: "Login realizado com sucesso!", user: { id:user.id, nome:user.nome, email:user.email } });
        } else {
            return res.status(401).json({ message: "Senha incorreta." });
        }
    });
});

// Chat (opcional)
const getTableName = (tipo) => ({ fisicas:"msgfisicas", intelectuais:"msgintelectuais", emocionais:"msgemocionais", sensoriais:"msgsensoriais", neurodivergentes:"msgneurodivergentes" }[tipo] || null);

const loadPreviousMessages = (socket,tipo) => {
    const tableName = getTableName(tipo);
    if (!tableName) return;
    db.query(`SELECT users.nome, users.email, ${tableName}.message FROM ${tableName} INNER JOIN users2 AS users ON ${tableName}.user_id = users.id ORDER BY ${tableName}.id ASC`,
        (err,results) => { if (!err) socket.emit("previousMessages",results); });
};

const insertNewMessage = (socket,tipo,message,userId,userName,email) => {
    const tableName = getTableName(tipo);
    if (!tableName) return;
    db.query(`INSERT INTO ${tableName} (user_id,message) VALUES (?,?)`, [userId,message], (err) => {
        if (!err) io.to(tipo).emit("newMessage",{ nome:userName, message, email });
    });
};

io.on("connection", (socket) => {
    console.log("Usuário conectado");

    socket.on("joinChat", ({tipo,email}) => {
        const tableName = getTableName(tipo);
        if (!tableName) return socket.emit("error","Tipo de chat inválido");

        getUserByEmail(email,(err,user) => {
            if (user) {
                socket.userId = user.id;
                socket.userName = user.nome;
                socket.email = user.email;
                socket.room = tipo;
                socket.join(tipo);
                socket.emit("emailValid",{ name:user.nome, email:user.email });
                loadPreviousMessages(socket,tipo);
            } else socket.emit("emailInvalid");
        });
    });

    socket.on("sendMessage",(data) => {
        if (socket.userId && socket.room) insertNewMessage(socket,socket.room,data.message,socket.userId,socket.userName,socket.email);
    });

    socket.on("disconnect",() => console.log("Usuário desconectado"));
});

server.listen(5000,()=>console.log("Servidor rodando na porta 5000"));