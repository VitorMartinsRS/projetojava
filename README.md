# Projeto Java

Este é um projeto Java simples com uma classe principal em `main.java`.

## Como executar

```bash
cd /Users/.../projetojava
javac main.java
java Main
```

O programa solicita o seu nome e imprime uma mensagem de boas-vindas.

## Container

O projeto inclui um Dockerfile multiestágio para Java 25. Para construir a imagem para `linux/amd64`:

```bash
docker buildx build --platform=linux/amd64 -t xadrez:1.0.0 .
```

O aplicativo usa JavaFX e precisa de um display gráfico no ambiente de execução para abrir a interface.
# projetojava
