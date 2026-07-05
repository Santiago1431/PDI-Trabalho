# Configurações do Compilador Java
JC = javac
JVM = java
MAIN = ChainCode
SOURCES = ChainCode.java PBMReader.java CTNFile.java

# Alvo principal: compilar tudo
all: compile

# Compila os arquivos fonte Java
compile:
	$(JC) $(SOURCES)

# Compila e executa o programa no modo interativo
run: compile
	$(JVM) $(MAIN)

# Limpa os arquivos compilados .class (detecta Windows ou Unix)
clean:
ifeq ($(OS),Windows_NT)
	@if exist *.class del /q /f *.class
else
	rm -f *.class
endif
