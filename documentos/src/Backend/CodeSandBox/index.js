var express = require("express");
var app = express();
var bodyParser = require("body-parser");
var port = process.env.PORT || 3000;
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

//Banco de Dados
var sqlite3 = require("sqlite3").verbose();
var DBPATH = "appBD.db";
var db = new sqlite3.Database(DBPATH);

//Buscar todos os usuários
app.get("/buscarUsuarios", function (req, res) {
  db.all(`SELECT * FROM usuarios`, [], (err, rows) => {
    if (err) {
      res.send(err);
    }
    res.send(rows);
  });
});

//Buscar todas as tarefas
app.get("/buscarTarefas", function (req, res) {
  db.all(`SELECT * FROM tarefas`, [], (err, rows) => {
    if (err) {
      res.send(err);
    }
    res.send(rows);
  });
});

app.get("/", function (req, res) {
  res.send("Servidor funcionando!");
});

// Rota para obter todas as tarefas com base no id do usuário
app.post("/obterTarefas", function (req, res) {
  var id_usuario = req.body.id_usuario;
  db.all(
    "SELECT * FROM tarefas WHERE id_usuario = ?",
    [id_usuario],
    function (err, rows) {
      if (err) {
        res.status(500).send("Erro ao obter as tarefas");
      } else {
        if (rows.length > 0) {
          // Tarefas encontradas, enviar as tarefas como resposta
          res.status(200).json(rows);
        } else {
          // Nenhuma tarefa encontrada
          res.status(404).send("Nenhuma tarefa encontrada");
        }
      }
    },
  );
});

// Rota para buscar usuário por ID
app.get("/dadosUsuario/:id", function (req, res) {
  var id_usuario = req.params.id;
  db.all(
    "SELECT * FROM usuarios WHERE id = ?",
    [id_usuario],
    function (err, rows) {
      if (err) {
        res.status(500).send("Erro ao obter usuário");
      } else {
        if (rows.length > 0) {
          res.status(200).json(rows);
        } else {
          res.status(404).send("Nenhum usuário encontrada");
        }
      }
    },
  );
});

// Rota para obter todas as tarefas com base no id do usuário e status ativo
app.get("/obterTarefasAtivas/:id_usuario", function (req, res) {
  var id_usuario = req.params.id_usuario;
  db.all(
    "SELECT * FROM tarefas WHERE id_usuario = ? AND status = 1 ORDER BY data_hora ASC;",
    [id_usuario],
    function (err, rows) {
      if (err) {
        res.status(500).send("Erro ao obter as tarefas");
      } else {
        if (rows.length > 0) {
          // Tarefas encontradas, enviar as tarefas como resposta
          res.status(200).json(rows);
        } else {
          // Nenhuma tarefa encontrada
          res.status(404).send("Nenhuma tarefa encontrada");
        }
      }
    },
  );
});

// Rota para obter todas as tarefas com base no id do usuário e status finalizada
app.get("/obterTarefasFinalizadas/:id_usuario", function (req, res) {
  var id_usuario = req.params.id_usuario;
  db.all(
    "SELECT * FROM tarefas WHERE id_usuario = ? AND status = 0 ORDER BY data_hora ASC;",
    [id_usuario],
    function (err, rows) {
      if (err) {
        res.status(500).send("Erro ao obter as tarefas");
      } else {
        if (rows.length > 0) {
          // Tarefas encontradas, enviar as tarefas como resposta
          res.status(200).json(rows);
        } else {
          // Nenhuma tarefa encontrada
          res.status(404).send("Nenhuma tarefa encontrada");
        }
      }
    },
  );
});

//Cadastrar um usuário (utilizando criptografia)
const crypto = require("crypto");
app.post("/cadastrarUsuario", function (req, res) {
  var nome = req.body.nome;
  var email = req.body.email;
  var senha = req.body.senha;
  var humor = req.body.humor;

  // Criptografia da senha usando SHA-256
  var senhaCriptografada = crypto
    .createHash("sha256")
    .update(senha)
    .digest("hex");

  db.get(
    "SELECT * FROM usuarios WHERE email = ?",
    [email],
    function (err, row) {
      if (err) {
        res.status(500).send("Erro ao verificar email");
      } else {
        if (row) {
          res.status(400).send("Este email já está registrado");
        } else {
          var sql =
            "INSERT INTO usuarios (nome, email, senha, humor) VALUES (?, ?, ?, ?)";
          db.run(sql, [nome, email, senhaCriptografada, humor], function (err) {
            if (err) {
              res.status(500).send("Erro ao criar usuário");
            } else {
              res.status(201).send("Usuário registrado com sucesso");
            }
          });
        }
      }
    },
  );
});

//Cadastrar tarefa
app.post("/cadastrarTarefa", function (req, res) {
  var id_usuario = req.body.id_usuario;
  var nome_tarefa = req.body.nome_tarefa;
  var data_hora = req.body.data_hora;
  var status = req.body.status;
  var sql =
    "INSERT INTO tarefas (id_usuario, nome_tarefa, data_hora, status) VALUES (?, ?, ?, ?)";
  db.run(sql, [id_usuario, nome_tarefa, data_hora, status], function (err) {
    if (err) {
      res.status(500).send("Erro ao criar tarefa");
    } else {
      res.status(201).send("Tarefa registrada com sucesso");
    }
  });
});

//Método de Login (utilizando criptografia)
app.post("/login", (req, res) => {
  const { email, senha } = req.body;

  // Criptografia da senha recebida na requisição
  const senhaCriptografada = crypto
    .createHash("sha256")
    .update(senha)
    .digest("hex");

  db.get(
    "SELECT * FROM usuarios WHERE email = ? AND senha = ?",
    [email, senhaCriptografada],
    (err, row) => {
      if (err) {
        res.status(500).json({ error: "Erro ao autenticar usuário" });
      } else {
        if (row) {
          // Usuário encontrado, enviar resposta de sucesso com dados do usuário
          res.status(200).json({
            message: "Login bem-sucedido",
            user: {
              id: row.id,
              nome: row.nome,
              email: row.email,
              humor: row.humor,
            },
          });
        } else {
          // Usuário não encontrado ou senha incorreta
          res.status(401).json({ message: "Usuário ou senha incorretos" });
        }
      }
    },
  );
});

//Deletar usuário
app.delete("/deletarUsuario", function (req, res) {
  var email = req.body.email;
  var senha = req.body.senha;

  // Verificar se o email e a senha foram fornecidos
  if (!email || !senha) {
    return res
      .status(400)
      .send("Email e senha são obrigatórios para deletar a conta.");
  }
  db.get(
    "SELECT * FROM usuarios WHERE email = ? AND senha = ?",
    [email, senha],
    function (err, row) {
      if (err) {
        return res.status(500).send("Erro ao consultar o banco de dados.");
      } else {
        if (!row) {
          // Usuário não encontrado ou credenciais incorretas
          return res.status(401).send("Email ou senha incorretos.");
        } else {
          // Deletar o usuário do banco de dados
          var sql = "DELETE FROM usuarios WHERE email = ?";
          db.run(sql, [email], function (err) {
            if (err) {
              return res.status(500).send("Erro ao deletar a conta.");
            } else {
              return res.status(200).send("Conta deletada com sucesso.");
            }
          });
        }
      }
    },
  );
});

//Deletar tarefa
app.delete("/deletarTarefa", function (req, res) {
  var id = req.body.id;

  db.get("SELECT * FROM tarefas WHERE id = ?", [id], function (err, row) {
    if (err) {
      return res.status(500).send("Erro ao consultar o banco de dados.");
    } else {
      if (!row) {
        // Usuário não encontrado ou credenciais incorretas
        return res.status(401).send("ID incorreto.");
      } else {
        // Deletar o usuário do banco de dados
        var sql = "DELETE FROM tarefas WHERE id = ?";
        db.run(sql, [id], function (err) {
          if (err) {
            return res.status(500).send("Erro ao deletar a tarefa.");
          } else {
            return res.status(200).send("Tarefa deletada com sucesso.");
          }
        });
      }
    }
  });
});

// //Atualizar tarefa pelo nome através da URL
// app.post("/atualizarTarefa/:nome_tarefa", (req, res) => {
//   var nome_tarefa = req.body.nome_tarefa;
//   const status = req.body.status;

//   db.run(
//     "UPDATE tarefas SET status = ? WHERE nome_tarefa = ?",
//     [status, id],
//     function (err) {
//       if (err) {
//         res.status(500).send("Erro ao atualizar a tarefa");
//       } else if (this.changes === 0) {
//         res.status(404).send("Tarefa não encontrada");
//       } else {
//         res.status(200).send("Tarefa atualizada com sucesso");
//       }
//     },
//   );
// });

app.get("/quantidadeTarefas/:id", function (req, res) {
  var id_usuario = req.params.id;
  // Consulta para obter a quantidade total de tarefas do usuário atual
  db.get(
    "SELECT COUNT(*) AS total FROM tarefas WHERE id_usuario = ?",
    [id_usuario],
    function (err, rowTotal) {
      if (err) {
        res.status(500).send("Erro ao obter a quantidade total de tarefas");
      } else {
        // Enviar a quantidade total como resposta
        res.status(200).json({
          total: rowTotal.total,
        });
      }
    },
  );
});

// Rota para buscar o ID do usuário através do email
app.post("/buscarId", (req, res) => {
  const email = req.body.email;

  db.get("SELECT id FROM usuarios WHERE email = ?", [email], (err, row) => {
    if (err) {
      res.status(500).send("Erro ao buscar o usuário");
    } else if (row) {
      res.status(200).json({ id: row.id });
    } else {
      res.status(404).send("Usuário não encontrado");
    }
  });
});

//Atualizar usuario pelo id através da URL
app.post("/atualizarUsuario/:id/:humor", (req, res) => {
  var id = req.params.id;
  const humor = req.params.humor;

  db.run(
    "UPDATE usuarios SET humor = ? WHERE id = ?",
    [humor, id],
    function (err) {
      if (err) {
        res.status(500).send("Erro ao atualizar o usuário");
      } else if (this.changes === 0) {
        res.status(404).send("Usuário não encontrado");
      } else {
        res.status(200).send("Usuário atualizado com sucesso");
      }
    },
  );
});

//Atualizar status da tarefa pelo id através da URL
app.post("/atualizarStatus/:id/:status", (req, res) => {
  var id = req.params.id;
  const status = req.params.status;

  db.run(
    "UPDATE tarefas SET status = ? WHERE id = ?",
    [status, id],
    function (err) {
      if (err) {
        res.status(500).send("Erro ao atualizar a tarefa");
      } else if (this.changes === 0) {
        res.status(404).send("Tarefa não encontrada");
      } else {
        res.status(200).send("Tarefa atualizada com sucesso");
      }
    },
  );
});

// Rota para buscar o humor do usuário através do id
app.post("/buscarHumor", (req, res) => {
  const id = req.body.id;

  db.get("SELECT humor FROM usuarios WHERE id = ?", [id], (err, row) => {
    if (err) {
      res.status(500).send("Erro ao buscar o usuário");
    } else if (row) {
      res.status(200).json({ humor: row.humor });
    } else {
      res.status(404).send("Usuário não encontrado");
    }
  });
});

app.listen(port);
