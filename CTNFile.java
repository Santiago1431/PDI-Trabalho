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
 * Classe utilitária para leitura e escrita de arquivos de Código de Cadeia (.ctn).
 */
public class CTNFile {

    /**
     * Classe para encapsular os dados de um arquivo de código de cadeia (.ctn).
     */
    public static class DadosCTN {
        public int altura;
        public int largura;
        public ChainCode.Ponto pontoInicial;
        public int nDirecoes;
        public String cadeiaHex;

        public DadosCTN(int altura, int largura, ChainCode.Ponto pontoInicial, int nDirecoes, String cadeiaHex) {
            this.altura = altura;
            this.largura = largura;
            this.pontoInicial = pontoInicial;
            this.nDirecoes = nDirecoes;
            this.cadeiaHex = cadeiaHex;
        }
    }

    /**
     * Salva o código de cadeia e os metadados no formato de arquivo CTN.
     * Formato:
     * <altura> <largura>
     * <linha_inicial> <coluna_inicial>
     * <número_de_direções>
     * <cadeia_hexadecimal>
     */
    public static void salvar(String caminhoArquivo, int altura, int largura, ChainCode.Ponto pontoInicial, int nDirecoes, String cadeiaHex) throws IOException {
        PrintWriter escritor = new PrintWriter(new File(caminhoArquivo));
        
        // Linha 1: Dimensões da imagem (linhas e colunas)
        escritor.println(altura + " " + largura);
        
        // Linha 2: Ponto inicial (y x)
        escritor.println(pontoInicial.y + " " + pontoInicial.x);
        
        // Linha 3: Número de direções
        escritor.println(nDirecoes);
        
        // Linha 4: Cadeia hexadecimal compacta
        escritor.println(cadeiaHex);
        
        escritor.close();
    }

    /**
     * Lê um arquivo no formato CTN e retorna um objeto DadosCTN.
     */
    public static DadosCTN ler(String caminhoArquivo) throws IOException {
        File arquivo = new File(caminhoArquivo);
        if (!arquivo.exists()) {
            throw new IOException("Arquivo CTN não encontrado: " + caminhoArquivo);
        }

        Scanner scanner = new Scanner(arquivo);

        int altura = scanner.nextInt();
        int largura = scanner.nextInt();
        
        int y = scanner.nextInt();
        int x = scanner.nextInt();
        ChainCode.Ponto pontoInicial = new ChainCode.Ponto(y, x);
        
        int nDirecoes = scanner.nextInt();
        String cadeiaHex = scanner.next().trim();

        scanner.close();
        return new DadosCTN(altura, largura, pontoInicial, nDirecoes, cadeiaHex);
    }
}
