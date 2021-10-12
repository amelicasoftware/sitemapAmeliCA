package mx.uaemex.mapsite;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import mx.uaemex.redalyc.XMLarticulos.TChangeFreq;
import mx.uaemex.redalyc.XMLarticulos.TUrl;
import mx.uaemex.redalyc.XMLarticulos.Urlset;

public class GenerarMapSiteArticulos {
	EntityManagerFactory emf = Persistence.createEntityManagerFactory("aplicacion");
	EntityManager em = emf.createEntityManager();
	private String fechaUltimaModificacion = "2021-10-12";

	public void crearXMLArticulos() {
		int numeroLinks = 0;
		List<ModeloArticuloSitemaps> clavesArticulo = this.getClaveArticulo();
		/*
		 * for(Object[] resultElement : clavesArticulo) {
		 * System.out.println(resultElement[0].toString()); }
		 */
		
		BigDecimal bigDecimal = new BigDecimal("1.0");
		Urlset urlSet = new Urlset();
		Urlset urlSetRevistas = new Urlset();
		Urlset urlSetExportar = new Urlset();
		Urlset urlSetVisor = new Urlset();
		Urlset urlSetMovil = new Urlset();
		Urlset urlSetHTML = new Urlset();
		Urlset urlSetPDF = new Urlset();
		int numeroArchivos = 0;
		int contadorArticulosJats = 0;
		System.out.println("todo ok");
		
		if (clavesArticulo.size() > 50000L) {
			numeroArchivos = (clavesArticulo.size() / 50000) + 1;
		} else
			numeroArchivos = 1;
		for (int i = 0; i < numeroArchivos; i++) {
			for (int j = 0; j < 50000; j++) {
				if (clavesArticulo.size() > (j + (i * 50000))) {
					String fecha = getFechaUltimaModificacion();
					TUrl tURL = new TUrl();
					tURL.setChangefreq(TChangeFreq.MONTHLY);
					String[] parts = clavesArticulo.get(j + (i * 50000)).getEdoJats().split(" ");
					//if(!parts[0].equals(null))
						//fecha = parts[0];
					//System.out.println("**********************************"+clavesArticulo.get(j + (i * 50000)).getEdoJats());
					tURL.setLastmod(fecha);
					tURL.setLoc(
							"http://portal.amelica.org/ameli/jatsRepo/"+ clavesArticulo.get(j + (i * 50000)).getClaveRevista()+"/"+clavesArticulo.get(j + (i * 50000)).getClave()+"/index.html");
					numeroLinks++;
					tURL.setPriority(bigDecimal);
					urlSet.getUrl().add(tURL);
					
					TUrl tURL2 = new TUrl();
					tURL2.setChangefreq(TChangeFreq.MONTHLY);
					tURL2.setLastmod(getFechaUltimaModificacion());
					tURL2.setLoc(
							"http://portal.amelica.org/ameli/jatsRepo/"+ clavesArticulo.get(j + (i * 50000)).getClaveRevista()+"/"+clavesArticulo.get(j + (i * 50000)).getClave()+"/"+clavesArticulo.get(j + (i * 50000)).getClave()+".pdf");
					numeroLinks++;
					tURL2.setPriority(bigDecimal);
					urlSetPDF.getUrl().add(tURL2);
					
					TUrl tURL3 = new TUrl();
					tURL3.setChangefreq(TChangeFreq.MONTHLY);
					tURL3.setLastmod(getFechaUltimaModificacion());
					tURL3.setLoc(
							"http://portal.amelica.org/ameli/jatsRepo/"+ clavesArticulo.get(j + (i * 50000)).getClaveRevista()+"/"+clavesArticulo.get(j + (i * 50000)).getClave()+"/html/index.html");
					numeroLinks++;
					tURL3.setPriority(bigDecimal);
					urlSetHTML.getUrl().add(tURL3);
					
					TUrl tURL4 = new TUrl();
					tURL4.setChangefreq(TChangeFreq.MONTHLY);
					tURL4.setLastmod(getFechaUltimaModificacion());
					tURL4.setLoc(
							"http://portal.amelica.org/ameli/jatsRepo/"+ clavesArticulo.get(j + (i * 50000)).getClaveRevista()+"/"+clavesArticulo.get(j + (i * 50000)).getClave()+"/movil/index.html");
					numeroLinks++;
					tURL4.setPriority(bigDecimal);
					urlSetMovil.getUrl().add(tURL4);
				}
				
				
			}
			escribirArchivo("mapsitearticulos" + i, urlSet);
			urlSet.getUrl().clear();
			escribirArchivo("mapsitearticulosPDF" + i, urlSetPDF);
			urlSetPDF.getUrl().clear();
			escribirArchivo("mapsitearticulosHTML" + i, urlSetHTML);
			urlSetHTML.getUrl().clear();
			escribirArchivo("mapsitearticulosMovil" + i, urlSetMovil);
			urlSetMovil.getUrl().clear();
		}
	}
	
	public void crearXMLrevistas() {
		int numeroLinks = 0;
		List<ModeloArticuloSitemaps> clavesArticulo = this.getClaveRevista();
		BigDecimal bigDecimal = new BigDecimal("1.0");
		Urlset urlSetRevistas = new Urlset();
		int numeroArchivos = 0;
		int contadorArticulosJats = 0;
		System.out.println("todo ok");
		if (clavesArticulo.size() > 50000L) {
			numeroArchivos = (clavesArticulo.size() / 50000) + 1;
		} else
			numeroArchivos = 1;
		for (int i = 0; i < numeroArchivos; i++) {
			for (int j = 0; j < 50000; j++) {
				if (clavesArticulo.size() > (j + (i * 50000))) {
					TUrl tURL1 = new TUrl();
					tURL1.setChangefreq(TChangeFreq.MONTHLY);
					tURL1.setLastmod(getFechaUltimaModificacion());
					tURL1.setLoc(
							"http://portal.amelica.org/revista.oa?id="+ clavesArticulo.get(j + (i * 50000)).getClaveRevista());
					numeroLinks++;
					tURL1.setPriority(bigDecimal);
					urlSetRevistas.getUrl().add(tURL1);
				}
				
				
			}
			escribirArchivo("mapsitearticulosRevistas" + i, urlSetRevistas);
			urlSetRevistas.getUrl().clear();
		}
	}

	public List<ModeloArticuloSitemaps> getClaveArticulo() {

		List<ModeloArticuloSitemaps> listaClaves = new ArrayList<ModeloArticuloSitemaps>();
		List<Object[]> listaobjArt = null;
		try {
			// Corregido con JATS
			Query qAux = em.createNativeQuery("select distinct tblentrev.cveentrev,tblrevtit.cverevtit from tblentrev natural inner join tblrevtit");
			//Query qAux = em.createNativeQuery("select distinct tblentrev.cveentrev,tblrevtit.cverevtit, tblrevtit.fecultmod from tblentrev natural inner join tblrevtit");
			// listaClaves = (List<Long>) qAux.getResultList();

			listaobjArt = (List<Object[]>) qAux.getResultList();
			for (Object[] resultElement : listaobjArt) {
				ModeloArticuloSitemaps langArt = new ModeloArticuloSitemaps();
				langArt.setClave(resultElement[1].toString());
				langArt.setEdoJats(resultElement[1].toString());
				langArt.setClaveRevista(resultElement[0].toString());
				langArt.setJatsPDF(resultElement[1].toString());
				listaClaves.add(langArt);
			}
		}

		catch (Exception e) {
			e.printStackTrace();
		}

		return listaClaves;
	}

	public List<ModeloArticuloSitemaps> getClaveRevista() {

		List<ModeloArticuloSitemaps> listaClaves = new ArrayList<ModeloArticuloSitemaps>();
		List<Object[]> listaobjArt = null;
		try {
			// Corregido con JATS
			Query qAux = em.createNativeQuery("select * from tblentrev");

			// listaClaves = (List<Long>) qAux.getResultList();

			listaobjArt = (List<Object[]>) qAux.getResultList();
			for (Object[] resultElement : listaobjArt) {
				ModeloArticuloSitemaps langArt = new ModeloArticuloSitemaps();
				langArt.setClave(resultElement[1].toString());
				langArt.setEdoJats(resultElement[1].toString());
				langArt.setClaveRevista(resultElement[0].toString());
				langArt.setJatsPDF(resultElement[1].toString());
				listaClaves.add(langArt);
			}
		}

		catch (Exception e) {
			e.printStackTrace();
		}

		return listaClaves;
	}

	public String getFechaUltimaModificacion() {
		return fechaUltimaModificacion;
	}

	public void setFechaUltimaModificacion(String fechaUltimaModificacion) {
		this.fechaUltimaModificacion = fechaUltimaModificacion;
	}

	public void escribirArchivo(String nombreArchivo, Urlset urlSet) {
		try {
//			File file = new File("C:/xmlMapas/todosHTTPS/"+nombreArchivo+".xml");	/*PARA EJECUTAR TODOS LOS SITEMAPS*/
			File file = new File("C:/xmlMapas/" + nombreArchivo + ".xml"); /* PARA PROCESOS QUINCENALES */
			JAXBContext jaxbContext = JAXBContext.newInstance(Urlset.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			// jaxbMarshaller.setProperty("jaxb.encoding", "Unicode");
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			// jaxbMarshaller.setProperty(CharacterEscapeHandler.class.getName(),new
			// CustomCharacterEscapeHandler());

			jaxbMarshaller.marshal(urlSet, file);
			// jaxbMarshaller.setProperty("jaxb.encoding", "UTF-8");
			jaxbMarshaller.marshal(urlSet, System.out);

		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		GenerarMapSiteArticulos mapSite = new GenerarMapSiteArticulos();
		try {
			mapSite.setFechaUltimaModificacion("2021-06-30");
			// mapSite.generarMapSiteArtXML();
			mapSite.crearXMLArticulos();
			mapSite.crearXMLrevistas();
			System.exit(0);
		} catch (Exception e) {
			System.out.println("un método del main fallo");
			e.printStackTrace();
		} finally {
			mapSite.em.close();
		}
	}

}
