package com.zanatta.brewer.storage.local;

import java.io.File;
import java.io.IOException;
import static java.nio.file.FileSystems.getDefault;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import com.zanatta.brewer.storage.FotoStorage;

public class FotoStorageLocal implements FotoStorage  {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FotoStorageLocal.class);
	
	private Path local;
	private Path localTemporario;
	
	public FotoStorageLocal(){
		this(getDefault().getPath(System.getenv("HOME"), ".brewerfotos"));
		
	}
	
	public FotoStorageLocal(Path path){
		this.local = path;
		criarPastas();
	}

	@Override
	public String salvarTemporariamente(MultipartFile[] files) {
		String novoNome =  null;
		
		if(files != null && files.length > 0){
			MultipartFile arquivo = files[0];
			novoNome = renomearArquivo(arquivo.getOriginalFilename());
			try {
				arquivo.transferTo(new File(this.localTemporario.toAbsolutePath().toString() + getDefault().getSeparator() + novoNome));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				throw new RuntimeException("Erro salvando a foto na pasta temporária", e);
			}
			
		}
		return novoNome;
		
	}
	
	private void criarPastas() {
		try {
			Files.createDirectories(this.local);
			this.localTemporario = getDefault().getPath(this.local.toString(), "temp");
			Files.createDirectories(this.localTemporario);
			
			if(LOGGER.isDebugEnabled()){
				LOGGER.debug("Pasta criada para salvar fotos");
				LOGGER.debug("Pasta default: " + this.local.toAbsolutePath());
				LOGGER.debug("Pasta temporaria: " + this.localTemporario.toAbsolutePath());
			}
		} catch (IOException e) {
			throw new RuntimeException("Erro criando pasta para salvar foto", e);
		}
	}
	
	private String renomearArquivo(String nomeOriginal){
		String novoNome = UUID.randomUUID().toString() + "_" + nomeOriginal;
		
		if (LOGGER.isDebugEnabled()){
			LOGGER.debug(String.format("Nome original: %s, novo nome: %s", nomeOriginal, novoNome));
		}
		
		return novoNome;
	}

}
