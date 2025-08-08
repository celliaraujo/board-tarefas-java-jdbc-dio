# 🗂️ Task Board em Java

Este projeto é um **board de tarefas** desenvolvido em Java, com foco em organização, boas práticas e estruturação de código. Ele simula o fluxo de cartões (cards) entre colunas de um quadro de tarefas, permitindo ações como movimentação, bloqueio, desbloqueio e cancelamento de cards.

---

## 🚀 Objetivo

Demonstrar todas as etapas do desenvolvimento de um sistema backend em Java, desde o planejamento até a implementação de funcionalidades, com foco em:

- Modelagem de dados
- Integração entre camadas
- Tratamento de exceções
- Controle transacional
- Boas práticas de programação orientada a objetos

---

## 🧠 Conceitos trabalhados

### ✅ Arquitetura em camadas

- **Camada de Serviço (`CardService`)**: Contém a lógica de negócio, validações e controle de fluxo.
- **Camada de Persistência (`CardDAO`, `BlockDAO`)**: Responsável por interações com o banco de dados.
- **Camada de DTOs (`CardDetailsDTO`, `BoardColumnInfoDTO`)**: Transporta dados entre camadas de forma segura e enxuta.
- **Camada de Entidades (`CardEntity`)**: Representa os dados persistidos no banco.

### 🔄 Controle de transações

- Uso explícito de `commit()` e `rollback()` para garantir integridade dos dados em operações críticas.

### ⚠️ Tratamento de exceções

- Exceções customizadas como `CardBlockedException`, `CardFinishedException` e `EntityNotFoundException` tornam os erros mais claros e específicos.

### 🧱 Modelagem de dados

- Utilização de **diagramas ER** para representar entidades e seus relacionamentos.
- Enum `BoardColumnKindEnum` para definir tipos de colunas (ex: INITIAL, FINAL, CANCEL).

### 🧼 Boas práticas

- Métodos coesos e com responsabilidade única
- Reutilização de lógica comum com métodos auxiliares
- Validações claras e mensagens de erro específicas
- Uso de `Optional` para evitar `null`

---

## 📦 Funcionalidades

- Criação de boards
- Exclusão de board
- Inserção de novos cards
- Movimentação de cards entre colunas
- Cancelamento de cards
- Bloqueio e desbloqueio de cards
- Validação de estado atual antes de qualquer operação

---

## 🛠️ Tecnologias utilizadas

- **Java 17+**
- **JDBC** para acesso ao banco
- **Lombok** para reduzir boilerplate
- **draw.io** para modelagem visual do banco


