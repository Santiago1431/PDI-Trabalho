/**
 * Rastreamento e Decodificação de Código de Cadeia 8-Direcional (Freeman)
 * Eurico Santiago Climaco Rodrigues
 * PDI - Processamento Digital de Imagens
 */


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ChainCode {

    // 1. Representação de um ponto bidimensional (i = linha, j = coluna)
    public static class Ponto {
        public int y; // linha (eixo y, aumenta para baixo)
        public int x; // coluna (eixo x, aumenta para a direita)

        public Ponto(int y, int x) {
            this.y = y;
            this.x = x;
        }

        @Override
        public String toString() {
            return "(" + y + ", " + x + ")";
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Ponto outro = (Ponto) obj;
            return this.y == outro.y && this.x == outro.x;
        }
    }

    // 2. Convenção Freeman 8-direcional (deslocamento para cada direção de 0 a 7)
    public static final Ponto[] Direcoes = {
        new Ponto(0, 1),   // 0: Leste (Direita)
        new Ponto(-1, 1),  // 1: Nordeste (Cima-Direita)
        new Ponto(-1, 0),  // 2: Norte (Cima)
        new Ponto(-1, -1), // 3: Noroeste (Cima-Esquerda)
        new Ponto(0, -1),  // 4: Oeste (Esquerda)
        new Ponto(1, -1),  // 5: Sudoeste (Baixo-Esquerda)
        new Ponto(1, 0),   // 6: Sul (Baixo)
        new Ponto(1, 1)    // 7: Sudeste (Baixo-Direita)
    };


    /**
     * Converte uma String hexadecimal de código de cadeia em um array de direções (0 a 7).
     * Exemplo: "F70" -> [7, 5, 6, 0]
     */
    public static int[] decodificarHexParaDirecoes(String hex) {
        StringBuilder bits = new StringBuilder();

        // 1. Converte cada caractere hexadecimal em 4 bits
        for (int i = 0; i < hex.length(); i++) {
            char c = hex.charAt(i);
            int valor = Character.digit(c, 16);
            if (valor == -1) {
                System.err.println("Erro: Caractere hexadecimal inválido: " + c);
                continue;
            }

            // Converte para binário e garante que tenha 4 caracteres (ex: 7 -> "0111")
            String binario = Integer.toBinaryString(valor);
            while (binario.length() < 4) {
                binario = "0" + binario;
            }
            bits.append(binario);
        }

        // 2. Agrupa os bits de 3 em 3 para extrair as direções
        int totalBits = bits.length();
        int qtdDirecoes = totalBits / 3;
        int[] direcoes = new int[qtdDirecoes];

        for (int i = 0; i < qtdDirecoes; i++) {
            String bloco3Bits = bits.substring(i * 3, (i + 1) * 3);
            direcoes[i] = Integer.parseInt(bloco3Bits, 2); // Converte binário para inteiro 
        }

        return direcoes;
    }

    //Reconstrói um contorno a partir do ponto inicial e do código de cadeia (como array de inteiros).
    public static int[][] decodificarContorno(Ponto inicio, int[] codigoCadeia, int altura, int largura) {
        int[][] grade = new int[altura][largura];

        // Ponto atual
        int atualY = inicio.y;
        int atualX = inicio.x;

        // Pinta o ponto inicial na grade (se estiver dentro dos limites)
        if (atualY >= 0 && atualY < altura && atualX >= 0 && atualX < largura) {
            grade[atualY][atualX] = 255;
        }

        // Percorre cada movimento do código de cadeia
        for (int i = 0; i < codigoCadeia.length; i++) {
            int dir = codigoCadeia[i];

            // Verifica se a direção é válida (0 a 7)
            if (dir < 0 || dir > 7) {
                System.err.println("Aviso: Direção inválida na posição " + i + ": " + dir);
                continue;
            }

            // Soma o deslocamento correspondente
            Ponto deslocamento = Direcoes[dir];
            atualY += deslocamento.y;
            atualX += deslocamento.x;

            // Pinta a nova coordenada (se dentro dos limites)
            if (atualY >= 0 && atualY < altura && atualX >= 0 && atualX < largura) {
                grade[atualY][atualX] = 255;
            } else {
                System.err.println("Aviso: Contorno saiu dos limites na posição (" + atualY + ", " + atualX + ")");
            }
        }

        return grade;
    }

    // Sobrecarga que aceita o código de cadeia como String hexadecimal diretamente.
    public static int[][] decodificarContorno(Ponto inicio, String codigoCadeiaHex, int altura, int largura) {
        int[] direcoes = decodificarHexParaDirecoes(codigoCadeiaHex);
        return decodificarContorno(inicio, direcoes, altura, largura);
    }




    //Localizando 1° ponto
    public static Ponto pontoInicial(int[][] grade){
        int altura = grade.length;
        int largura = grade[0].length;
        
        for (int y = 0; y < altura; y++) {
            for (int x = 0; x < largura; x++) {
                if (grade[y][x] > 0) {
                    return new Ponto(y, x); // Corrigido para (linha, coluna)
                }
            }
        }      
        return null; 
    }


    //Auxiliar para encontrar qual direção (0 a 7) representa o movimento de um ponto para outro.
    public static int obterDirecao(Ponto de, Ponto para) {
        int dy = para.y - de.y;
        int dx = para.x - de.x;
        for (int d = 0; d < 8; d++) {
            if (Direcoes[d].y == dy && Direcoes[d].x == dx) {
                return d;
            }
        }
        return -1; // Não são vizinhos
    }

    //Retorna o k-ésimo vizinho de b a partir de c no sentido horário.
    public static Ponto vizinho8(Ponto b, Ponto c, int k) {
        int dirBC = obterDirecao(b, c);
        int dirK = (dirBC - k + 8) % 8; // Sentido horário (decrementa o índice)
        return new Ponto(b.y + Direcoes[dirK].y, b.x + Direcoes[dirK].x);
    }

    //Retorna o ponto de fundo anterior a nk na varredura horária de b.
    public static Ponto vizinhoAnterior(Ponto b, Ponto nk) {
        int dirNK = obterDirecao(b, nk);
        int dirAnt = (dirNK + 1) % 8; // Sentido anti-horário (+1)
        return new Ponto(b.y + Direcoes[dirAnt].y, b.x + Direcoes[dirAnt].x);
    }

    /**
     * Algoritmo RastreamentoContorno(I, C)
     * Entrada: imagem binária I (grade)
     * Saída: lista de pontos do contorno C
     */
    public static List<Ponto> rastrearContorno(int[][] grade) {
        Ponto b0 = pontoInicial(grade);
        if (b0 == null) {
            return new ArrayList<>();
        }

        List<Ponto> C = new ArrayList<>();
        int altura = grade.length;
        int largura = grade[0].length;

        // Definir vizinho inicial (oeste)
        Ponto c0 = new Ponto(b0.y, b0.x - 1);

        // Encontrar b1
        Ponto b = b0;
        Ponto c = c0;
        Ponto b1 = null;
        Ponto c1 = null;

        for (int k = 0; k < 8; k++) {
            Ponto nk = vizinho8(b, c, k);
            if (nk.y >= 0 && nk.y < altura && nk.x >= 0 && nk.x < largura) {
                if (grade[nk.y][nk.x] > 0) {
                    b1 = nk;
                    c1 = vizinhoAnterior(b, nk);
                    break;
                }
            }
        }

        if (b1 == null) {
            C.add(b0);
            return C;
        }

        // Inicialização
        b = b1;
        c = c1;
        C.add(b0);
        C.add(b1);

        Ponto prox = b1;
        Ponto ant = null;

        // Passos 3–5 — Rastreamento
        while (true) {
            boolean achouVizinho = false;
            for (int k = 0; k < 8; k++) {
                Ponto nk = vizinho8(b, c, k);
                if (nk.y >= 0 && nk.y < altura && nk.x >= 0 && nk.x < largura) {
                    if (grade[nk.y][nk.x] > 0) {
                        prox = nk;
                        ant = vizinhoAnterior(b, nk);
                        achouVizinho = true;
                        break;
                    }
                }
            }

            if (!achouVizinho) {
                break; // Evita loop infinito se não encontrar vizinhos válidos
            }

            // Condição de parada: se o ponto atual for b0 e o próximo for b1
            if (b.equals(b0) && prox.equals(b1)) {
                break;
            }

            b = prox;
            c = ant;
            C.add(b);
        }

        return C;
    }

    // Converte uma lista de pontos de contorno para a lista de direções de Freeman.
    public static List<Integer> converterContornoParaDirecoes(List<Ponto> contorno) {
        List<Integer> direcoes = new ArrayList<>();
        if (contorno.size() < 2) {
            return direcoes;
        }
        
        for (int i = 0; i < contorno.size() - 1; i++) {
            int dir = obterDirecao(contorno.get(i), contorno.get(i + 1));
            if (dir != -1) {
                direcoes.add(dir);
            }
        }
        return direcoes;
    }

    /**
     * Converte uma lista de direções de código de cadeia (0 a 7) em uma String hexadecimal compacta.
     * Exemplo: [7, 5, 6, 0] -> "F70"
     */
    public static String codificarDirecoesParaHex(List<Integer> direcoes) {
        StringBuilder bits = new StringBuilder();

        // 1. Converte cada direção em binário de 3 bits
        for (int dir : direcoes) {
            String bin = Integer.toBinaryString(dir);
            // Garante que tenha exatamente 3 bits (ex: 2 -> "010")
            while (bin.length() < 3) {
                bin = "0" + bin;
            }
            bits.append(bin);
        }

        // 2. Preenchimento (padding) com zeros à direita até ser múltiplo de 4
        while (bits.length() % 4 != 0) {
            bits.append("0");
        }

        // 3. Agrupa de 4 em 4 bits e converte para hexadecimal
        StringBuilder hex = new StringBuilder();
        for (int i = 0; i < bits.length(); i += 4) {
            String bloco4Bits = bits.substring(i, i + 4);
            int valor = Integer.parseInt(bloco4Bits, 2);
            hex.append(Integer.toHexString(valor).toUpperCase());
        }

        return hex.toString();
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            // Modo linha de comando
            processarLinhaDeComando(args);
        } else {
            // Modo interativo (menu)
            Scanner leitor = new Scanner(System.in);
            try {
                System.out.println("=============================================================");
                System.out.println("            CÓDIGO DE CADEIA 8-DIRECIONAL (FREEMAN)");
                System.out.println("=============================================================");
                System.out.println("Escolha uma opção:");
                System.out.println("  [1] Codificar (Imagem PBM -> Arquivo CTN)");
                System.out.println("  [2] Decodificar (Arquivo CTN -> Imagem PBM)");
                System.out.print("Opção: ");
                int opcao = leitor.nextInt();
                leitor.nextLine(); // Limpa o buffer de quebra de linha

                if (opcao == 1) {
                    System.out.print("Digite o caminho do arquivo de imagem de entrada (.pbm): ");
                    String caminhoEntrada = leitor.nextLine().trim();
                    System.out.print("Digite o caminho do arquivo de saída (.ctn): ");
                    String caminhoSaida = leitor.nextLine().trim();
                    
                    executarCodificacao(caminhoEntrada, caminhoSaida);
                } else if (opcao == 2) {
                    System.out.print("Digite o caminho do arquivo de código de cadeia de entrada (.ctn): ");
                    String caminhoEntrada = leitor.nextLine().trim();
                    System.out.print("Digite o caminho do arquivo de imagem de saída (.pbm): ");
                    String caminhoSaida = leitor.nextLine().trim();
                    
                    executarDecodificacao(caminhoEntrada, caminhoSaida);
                } else {
                    System.out.println("[ERRO] Opção inválida.");
                }
            } catch (Exception e) {
                System.err.println("\n[ERRO] Ocorreu uma falha no processamento: " + e.getMessage());
            } finally {
                leitor.close();
            }
        }
    }

    private static void processarLinhaDeComando(String[] args) {
        if (args.length < 3) {
            exibirInstrucoesUso();
            return;
        }

        String modo = args[0];
        String entrada = args[1];
        String saida = args[2];

        try {
            if ("-c".equalsIgnoreCase(modo)) {
                executarCodificacao(entrada, saida);
            } else if ("-d".equalsIgnoreCase(modo)) {
                executarDecodificacao(entrada, saida);
            } else {
                System.out.println("[ERRO] Modo desconhecido: " + modo);
                exibirInstrucoesUso();
            }
        } catch (Exception e) {
            System.err.println("[ERRO] Falha ao processar comando: " + e.getMessage());
        }
    }

    private static void exibirInstrucoesUso() {
        System.out.println("Uso:");
        System.out.println("  Codificação: java ChainCode -c <entrada.pbm> <saida.ctn>");
        System.out.println("  Decodificação: java ChainCode -d <entrada.ctn> <saida.pbm>");
    }

    private static void executarCodificacao(String caminhoPBM, String caminhoCTN) throws IOException {
        caminhoPBM = garantirExtensao(caminhoPBM, ".pbm");
        caminhoCTN = garantirExtensao(caminhoCTN, ".ctn");
        System.out.println("Lendo imagem PBM...");
        PBMReader.ImagemPBM imagem = PBMReader.ler(caminhoPBM);
        
        System.out.println("Rastreando contorno...");
        List<Ponto> contorno = rastrearContorno(imagem.grade);
        
        if (contorno.isEmpty()) {
            System.out.println("Aviso: Nenhum contorno de objeto encontrado na imagem.");
            return;
        }
        
        System.out.println("Ponto inicial encontrado: " + contorno.get(0));
        System.out.println("Quantidade de pontos de contorno: " + contorno.size());
        
        System.out.println("Gerando lista de direções...");
        List<Integer> direcoes = converterContornoParaDirecoes(contorno);
        
        System.out.println("Compactando para Hexadecimal...");
        String cadeiaHex = codificarDirecoesParaHex(direcoes);
        System.out.println("Cadeia Hexadecimal gerada: " + cadeiaHex);
        
        System.out.println("Salvando arquivo CTN...");
        CTNFile.salvar(caminhoCTN, imagem.altura, imagem.largura, contorno.get(0), direcoes.size(), cadeiaHex);
        System.out.println("Codificação concluída com sucesso!");
    }

    private static void executarDecodificacao(String caminhoCTN, String caminhoPBM) throws IOException {
        caminhoCTN = garantirExtensao(caminhoCTN, ".ctn");
        caminhoPBM = garantirExtensao(caminhoPBM, ".pbm");
        System.out.println("Lendo arquivo CTN...");
        CTNFile.DadosCTN dados = CTNFile.ler(caminhoCTN);
        
        System.out.println("Decodificando cadeia...");
        int[] direcoes = decodificarHexParaDirecoes(dados.cadeiaHex);
        
        // Se houver bits de padding, truncamos para a quantidade registrada nDirecoes
        int[] direcoesValidas = new int[dados.nDirecoes];
        System.arraycopy(direcoes, 0, direcoesValidas, 0, Math.min(direcoes.length, dados.nDirecoes));
        
        System.out.println("Reconstruindo contorno na grade...");
        int[][] grade = decodificarContorno(dados.pontoInicial, direcoesValidas, dados.altura, dados.largura);
        
        System.out.println("Salvando imagem PBM...");
        PBMReader.salvar(grade, caminhoPBM);

        
        System.out.println("Decodificação concluída com sucesso!");
    }

    private static String garantirExtensao(String nomeArquivo, String extensao) {
        if (nomeArquivo == null || nomeArquivo.isEmpty()) {
            return nomeArquivo;
        }
        if (!nomeArquivo.toLowerCase().endsWith(extensao.toLowerCase())) {
            return nomeArquivo + extensao;
        }
        return nomeArquivo;
    }
}
