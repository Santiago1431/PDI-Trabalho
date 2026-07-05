/**
 * Rastreamento e Decodificação de Código de Cadeia 8-Direcional (Freeman)
 * Eurico Santiago Climaco Rodrigues
 * PDI - Processamento Digital de Imagens
 */

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * Classe utilitária para leitura e escrita de imagens no formato PBM (P1 - Portable Bitmap).
 */
public class PBMReader {

    /**
     * Classe para representar os dados de uma imagem PBM.
     */
    public static class ImagemPBM {
        public int largura;
        public int altura;
        public int[][] grade;

        public ImagemPBM(int largura, int altura, int[][] grade) {
            this.largura = largura;
            this.altura = altura;
            this.grade = grade;
        }
    }

    /**
     * Lê um arquivo PBM (formato P1 - texto plano) e retorna um objeto ImagemPBM.
     * Ignora comentários iniciados com o caractere '#'
     */
    public static ImagemPBM ler(String caminhoArquivo) throws IOException {
        File arquivo = new File(caminhoArquivo);
        if (!arquivo.exists()) {
            throw new IOException("Arquivo PBM não encontrado: " + caminhoArquivo);
        }

        Scanner scanner = new Scanner(arquivo);

        // 1. Cabeçalho "P1"
        String cabecalho = obterProximoToken(scanner);
        if (cabecalho == null || !cabecalho.equalsIgnoreCase("P1")) {
            scanner.close();
            throw new IOException("Formato de arquivo inválido. Deve começar com 'P1'.");
        }

        // 2. Dimensões
        String tokenLargura = obterProximoToken(scanner);
        String tokenAltura = obterProximoToken(scanner);
        if (tokenLargura == null || tokenAltura == null) {
            scanner.close();
            throw new IOException("Largura ou altura ausentes no arquivo PBM.");
        }

        int largura = Integer.parseInt(tokenLargura);
        int altura = Integer.parseInt(tokenAltura);

        if (largura <= 0 || altura <= 0) {
            scanner.close();
            throw new IOException("Dimensões inválidas: largura=" + largura + ", altura=" + altura);
        }

        // 3. Pixels
        int[][] grade = new int[altura][largura];
        for (int y = 0; y < altura; y++) {
            for (int x = 0; x < largura; x++) {
                String tokenPixel = obterProximoToken(scanner);
                if (tokenPixel == null) {
                    scanner.close();
                    throw new IOException("Arquivo PBM corrompido: menos pixels do que o esperado.");
                }
                grade[y][x] = Integer.parseInt(tokenPixel);
            }
        }

        scanner.close();
        return new ImagemPBM(largura, altura, grade);
    }

    /**
     * Salva uma grade de pixels (matriz) de volta para o formato PBM (P1).
     */
    public static void salvar(int[][] grade, String caminhoArquivo) throws IOException {
        int altura = grade.length;
        int largura = grade[0].length;
        PrintWriter escritor = new PrintWriter(new File(caminhoArquivo));

        // Cabeçalho PBM
        escritor.println("P1");
        escritor.println("# Reconstruido por decodificacao de codigo de cadeia");
        escritor.println(largura + " " + altura);

        // Matriz de pixels (1 = objeto, 0 = fundo)
        for (int y = 0; y < altura; y++) {
            for (int x = 0; x < largura; x++) {
                escritor.print((grade[y][x] > 0 ? "1" : "0") + " ");
            }
            escritor.println();
        }

        escritor.close();
    }

    /**
     * Auxiliar para extrair o próximo token do Scanner ignorando comentários (#).
     */
    private static String obterProximoToken(Scanner scanner) {
        while (scanner.hasNext()) {
            String token = scanner.next();
            if (token.startsWith("#")) {
                scanner.nextLine(); // Pula toda a linha do comentário
            } else {
                return token;
            }
        }
        return null;
    }
}
