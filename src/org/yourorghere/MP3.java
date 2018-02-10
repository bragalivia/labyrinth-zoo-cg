package org.yourorghere;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import javazoom.jl.player.Player;

public class MP3 {

	/**
	 * Objeto para nosso arquivo MP3 a ser tocado
	 */
	private File mp3;

	/**
	 * Objeto Player da biblioteca jLayer. Ele tocará o arquivo MP3
	 */
	private static Player player;

	/**
	 * Construtor que recebe o objeto File referenciando o arquivo MP3 a ser
	 * tocado e atribui ao atributo MP3 da classe.
	 * 
	 * @param mp3
	 */
	public MP3(File mp3) {
		this.mp3 = mp3;
	}

	/**
	 * Método que toca o MP3
	 */
	public void play() {
		new Thread(){
			@Override
			public void run() {
				try {
					FileInputStream fis = new FileInputStream(mp3);
					BufferedInputStream bis = new BufferedInputStream(fis);
					player = new Player(bis);
					player.play();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		}.start();
	}
	
}