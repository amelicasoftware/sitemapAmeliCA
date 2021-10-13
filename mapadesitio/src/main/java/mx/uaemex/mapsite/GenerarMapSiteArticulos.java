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
	private String fechaUltimaModificacion = "2021-10-12"; //se cambia cada que se corre el progrma

	//metodo para crear XML de articilos
	public void crearXMLArticulos() {
		int numeroLinks = 0;
		List<ModeloArticuloSitemaps> clavesArticulo = this.getClaveArticulo();
		BigDecimal bigDecimal = new BigDecimal("1.0");
		Urlset urlSet = new Urlset();
		Urlset urlSetMovil = new Urlset();
		Urlset urlSetHTML = new Urlset();
		Urlset urlSetPDF = new Urlset();
		int numeroArchivos = 0;
		System.out.println("todo ok");
		
		if (clavesArticulo.size() > 50000L) {
			numeroArchivos = (clavesArticulo.size() / 50000) + 1;
		} else
			numeroArchivos = 1;
		for (int i = 0; i < numeroArchivos; i++) {
			for (int j = 0; j < 50000; j++) {
				if (clavesArticulo.size() > (j + (i * 50000))) {
					//para los articulos en el visor redalyc
					TUrl tURL = new TUrl();
					tURL.setChangefreq(TChangeFreq.MONTHLY);
					tURL.setLastmod(clavesArticulo.get(j + (i * 50000)).getFechaUltMod());
					tURL.setLoc(
							"http://portal.amelica.org/ameli/jatsRepo/"+ clavesArticulo.get(j + (i * 50000)).getClaveRevista()+"/"+clavesArticulo.get(j + (i * 50000)).getClave()+"/index.html");
					numeroLinks++;
					tURL.setPriority(bigDecimal);
					urlSet.getUrl().add(tURL);
					//para los articulos en PDF
					TUrl tURL2 = new TUrl();
					tURL2.setChangefreq(TChangeFreq.MONTHLY);
					tURL2.setLastmod(clavesArticulo.get(j + (i * 50000)).getFechaUltMod());
					tURL2.setLoc(
							"http://portal.amelica.org/ameli/jatsRepo/"+ clavesArticulo.get(j + (i * 50000)).getClaveRevista()+"/"+clavesArticulo.get(j + (i * 50000)).getClave()+"/"+clavesArticulo.get(j + (i * 50000)).getClave()+".pdf");
					numeroLinks++;
					tURL2.setPriority(bigDecimal);
					urlSetPDF.getUrl().add(tURL2);
					//par los articulos en HTML
					TUrl tURL3 = new TUrl();
					tURL3.setChangefreq(TChangeFreq.MONTHLY);
					tURL3.setLastmod(clavesArticulo.get(j + (i * 50000)).getFechaUltMod());
					tURL3.setLoc(
							"http://portal.amelica.org/ameli/jatsRepo/"+ clavesArticulo.get(j + (i * 50000)).getClaveRevista()+"/"+clavesArticulo.get(j + (i * 50000)).getClave()+"/html/index.html");
					numeroLinks++;
					tURL3.setPriority(bigDecimal);
					urlSetHTML.getUrl().add(tURL3);
					//par los articulos en formato movil
					TUrl tURL4 = new TUrl();
					tURL4.setChangefreq(TChangeFreq.MONTHLY);
					tURL4.setLastmod(clavesArticulo.get(j + (i * 50000)).getFechaUltMod());
					tURL4.setLoc(
							"http://portal.amelica.org/ameli/jatsRepo/"+ clavesArticulo.get(j + (i * 50000)).getClaveRevista()+"/"+clavesArticulo.get(j + (i * 50000)).getClave()+"/movil/index.html");
					numeroLinks++;
					tURL4.setPriority(bigDecimal);
					urlSetMovil.getUrl().add(tURL4);
				}
				
				
			}
			//metodos para crear los archivos
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
	//metodo par crear xml revistas
	public void crearXMLrevistas() {
		int numeroLinks = 0;
		List<ModeloArticuloSitemaps> clavesArticulo = this.getClaveRevista();
		BigDecimal bigDecimal = new BigDecimal("1.0");
		Urlset urlSetRevistas = new Urlset();
		int numeroArchivos = 0;
		int contadorArticulosJats = 0;
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
	//metodo pra obtener los articulos de base de datos y crear una lista de objetos con los resultados
	public List<ModeloArticuloSitemaps> getClaveArticulo() {

		List<ModeloArticuloSitemaps> listaClaves = new ArrayList<ModeloArticuloSitemaps>();
		List<Object[]> listaobjArt = null;
		try {
			//Query qAux = em.createNativeQuery("select distinct tblentrev.cveentrev,tblrevtit.cverevtit from tblentrev natural inner join tblrevtit");
			Query qAux = em.createNativeQuery("select distinct tblentrev.cveentrev,tblrevtit.cverevtit, tblrevtit.fecultmod from tblentrev natural inner join tblrevtit");
			listaobjArt = (List<Object[]>) qAux.getResultList();
			//System.out.println("***********************************"+listaobjArt.size());
			for (Object[] resultElement : listaobjArt) {
				ModeloArticuloSitemaps langArt = new ModeloArticuloSitemaps();
				langArt.setClave(resultElement[1].toString());
				if(resultElement[2]==null)
					langArt.setFechaUltMod(getFechaUltimaModificacion());
				else {
					String[] part = resultElement[2].toString().split(" ");
					langArt.setFechaUltMod(part[0]);
				}
				langArt.setClaveRevista(resultElement[0].toString());
				listaClaves.add(langArt);
			}
		}

		catch (Exception e) {
			e.printStackTrace();
		}

		return listaClaves;
	}
	//metodo pra obtener las revistas de base de datos y crear una lista de objetos con los resultados
	public List<ModeloArticuloSitemaps> getClaveRevista() {

		List<ModeloArticuloSitemaps> listaClaves = new ArrayList<ModeloArticuloSitemaps>();
		List<Object[]> listaobjArt = null;
		try {
			Query qAux = em.createNativeQuery("select * from tblentrev");
			listaobjArt = (List<Object[]>) qAux.getResultList();
			for (Object[] resultElement : listaobjArt) {
				ModeloArticuloSitemaps langArt = new ModeloArticuloSitemaps();
				langArt.setClave(resultElement[1].toString());
				langArt.setFechaUltMod(resultElement[1].toString());
				langArt.setClaveRevista(resultElement[0].toString());
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
			File file = new File("C:/xmlMapas/" + nombreArchivo + ".xml"); 
			JAXBContext jaxbContext = JAXBContext.newInstance(Urlset.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			jaxbMarshaller.marshal(urlSet, file);
			//jaxbMarshaller.marshal(urlSet, System.out); //para imprimir en consola

		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		GenerarMapSiteArticulos mapSite = new GenerarMapSiteArticulos();
		try {
			mapSite.setFechaUltimaModificacion("2021-06-30");
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
