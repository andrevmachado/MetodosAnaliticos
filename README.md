# Métodos Analíticos

Projeto em Java para simulação de filas e redes de atendimento.

## Arquivo principal

A aplicação começa em `src/src/Main.java`, no método `Main.main`.

Por padrão, o `Main` carrega o arquivo:

```java
loader.load("src/src/test.yml");
```

## Como executar na IDE

1. Abra o projeto na sua IDE preferida (IntelliJ, por exemplo).
2. A pasta `src/src` é o diretório de código-fonte.
3. Configure a classe de execução como `Main`.
4. Garanta que o diretório de trabalho esteja na raiz do projeto:
   `E:\Apps\High Jump\MetodosAnaliticos\MetodosAnaliticos`
5. Execute a aplicação normalmente pela IDE.

## Como executar pela linha de comando

Abra o PowerShell na raiz do projeto e execute:

```powershell
javac -d out src\src\*.java
java -cp out Main
```

### O que esses comandos fazem

- `javac -d out src\src\*.java` compila todos os arquivos `.java` da pasta `src/src` e salva as classes compiladas em `out`.
- `java -cp out Main` executa a classe `Main` usando a pasta `out` como classpath.

## Como alterar o arquivo de fila/configuração

Se você quiser trocar o arquivo YAML carregado pela aplicação, edite o `Main.java` e altere esta linha:

```java
loader.load("src/src/test.yml");
```

Por exemplo, se quiser usar `model.yml`, que está na raiz do projeto, troque para:

```java
loader.load("model.yml");
```

