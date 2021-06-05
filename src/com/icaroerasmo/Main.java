package com.icaroerasmo;

import java.util.List;

import com.icaroerasmo.data.Database;
import com.icaroerasmo.data.KMeans;
import com.icaroerasmo.data.Tupla;

public class Main {

	public static void main(String[] args) {
		KMeans kMeans = new KMeans(Database.carregaDatabase("./1.in"), 2);
		List<List<Tupla>> grupos = kMeans.kMeans();
		
		for(List<Tupla> grupo : grupos) {
			var indexOfGrupo = grupos.indexOf(grupo);
			System.out.println("<!-- INÍCIO GRUPO "+indexOfGrupo+"-->");
			for(Tupla tupla : grupo) {
				System.out.println(tupla);
			}
			System.out.println("<!-- FIM GRUPO "+indexOfGrupo+"-->");
		}
	}

}
