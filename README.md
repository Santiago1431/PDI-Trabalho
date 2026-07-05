# Rastreamento e Decodificação de Código de Cadeia 8-Direcional (Freeman)

Este projeto foi desenvolvido como atividade prática da disciplina de **Processamento Digital de Imagens (PDI)**. Ele implementa o algoritmo de rastreamento de contornos utilizando a vizinhança-8 de Freeman (Pavlidis T-8) e a compactação de dados em código de cadeia representado em formato hexadecimal, além da operação inversa (decodificação) para reconstrução da imagem.

**Autor**: Eurico Santiago Climaco Rodrigues

---

## 📁 Estrutura do Projeto

O código-fonte está modularizado em três classes Java principais:

1. **`ChainCode.java`**:
   * Contém as classes internas, definições dos deslocamentos da vizinhança-8 e a lógica dos algoritmos de rastreamento de contorno (`rastrearContorno`) e decodificação (`decodificarContorno`).
   * Ponto de entrada (`main`) que disponibiliza um menu interativo e aceita argumentos de linha de comando.
2. **`PBMReader.java`**:
   * Responsável pela leitura e escrita de imagens no formato **PBM** (`P1` - Portable Bitmap em texto plano), lidando de forma robusta com linhas de comentário iniciadas em `#`.
3. **`CTNFile.java`**:
   * Responsável pela persistência e leitura de arquivos `.ctn` (Código de Cadeia compactado em hexadecimal).

---

## 🛠️ Como Compilar o Projeto

Você pode compilar o projeto utilizando o **Makefile** incluso na pasta raiz ou diretamente via comandos Java.

### Via Makefile:
```bash
make compile
```

### Via Prompt/Terminal Comum:
```bash
javac PBMReader.java CTNFile.java ChainCode.java
```

---

## 🚀 Como Executar o Programa

O programa possui duas interfaces de execução (Menu Interativo ou Linha de Comando Direta) e manipula automaticamente a inserção das extensões de arquivos se não forem fornecidas.

### Modo 1: Menu Interativo (Sem parâmetros)
Execute o comando principal do Java. O programa abrirá um menu no console perguntando se você deseja codificar ou decodificar e os caminhos dos respectivos arquivos:
```bash
java ChainCode
```

### Modo 2: Linha de Comando Direta
Útil para automatização de testes e scripts. É necessário passar a flag de operação, o arquivo de entrada e o de saída:

* **Codificação (Imagem PBM ➔ Arquivo CTN)**:
  ```bash
  java ChainCode -c entrada.pbm saida.ctn
  ```
  *(O programa lê a imagem PBM, rastreia a borda, compacta as direções em Hexadecimal e gera o arquivo de metadados CTN).*

* **Decodificação (Arquivo CTN ➔ Imagem PBM)**:
  ```bash
  java ChainCode -d entrada.ctn saida.pbm
  ```
  *(O programa lê a cadeia hexadecimal do arquivo CTN, reconstrói a matriz binária do contorno a partir das direções de Freeman e a grava em uma nova imagem PBM).*



## 📝 Formato dos Arquivos

### Formato CTN (.ctn)
Os arquivos gerados na codificação e lidos na decodificação seguem estritamente a seguinte estrutura de 4 linhas:
```text
<altura> <largura>
<linha_inicial> <coluna_inicial>
<número_de_direções>
<cadeia_hexadecimal>
```
*Exemplo:*
```text
400 400
55 91
273
E400000000001C01C01C703F...
```