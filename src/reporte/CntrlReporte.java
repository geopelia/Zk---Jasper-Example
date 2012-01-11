package reporte;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkex.zul.Jasperreport;
import org.zkoss.zul.Button;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Window;

/**
 * @author atilio
 * Clase para generar llamar un reporte de Jasper Reports
 * desde zk usando archivos .jrxml,.jasper,  
 * y las etiquetas report de zk 
 * Nota: JasperPrint siempre debe recibir una fuente 
 * de datos asi está este vacia
 */
public class CntrlReporte extends GenericForwardComposer {
	Button btnReportes;
	Window winReportes;
	Jasperreport report;
	Listbox format;
	Iframe frame;
	String reportSrc,jrxmlSrc,folder;
	Map parameters = new HashMap();
	AMedia am,bm;
	Connection con;
	
	
	public CntrlReporte() throws SQLException {
		super();

		con = ConeccionBD.getCon();
		folder = Sessions.getCurrent().getWebApp().getRealPath("/WEB-INF/");
		reportSrc = Sessions.getCurrent().getWebApp().getRealPath("/WEB-INF/reportes/testEstatico.jasper");
		parameters.put("titulo", "Hola mundo");
		parameters.put("fecha_ven", "01/01/2012");
		jrxmlSrc = Sessions.getCurrent().getWebApp().getRealPath("/WEB-INF/reportes/ReportePersonal.jrxml");
		
		
	}
	
	public void showReportfromJrxml() throws JRException, IOException{
		/* Funciona aunque necesita tiempo para la compilacion del .jasper. 
		 * Requiere un iframe para mostrarse
		 * Importante al ejecutar report1.jrxml (En general reportes que 
		 * no se conecten a ninguna BD)
		 * se debe descomentar la linea comentada debajo que usa
		 * JREmptyDataSource() y comentar la que usa la variable con
		 * sino mostrara reportes en blanco 
		 * 
		 * */
		JasperReport jasp = JasperCompileManager.compileReport(jrxmlSrc);
		//JasperPrint jaspPrint = JasperFillManager.fillReport(jasp,parameters,new JREmptyDataSource());
		JasperPrint jaspPrint = JasperFillManager.fillReport(jasp, parameters, con);
		ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
		JRExporter exporter = new JRPdfExporter();
		exporter.setParameters(parameters);
		exporter.setParameter(JRExporterParameter.JASPER_PRINT ,jaspPrint);
		exporter.setParameter(JRExporterParameter.OUTPUT_STREAM,arrayOutputStream);
		exporter.exportReport();
		arrayOutputStream.close();
		final AMedia amedia = new AMedia("report.pdf","pdf","pdf/application", arrayOutputStream.toByteArray());
		frame.setContent(amedia);
		System.out.println("done :)");
	}

	public void showReportJasperPrint() throws JRException, IOException{
		/* Requiere un iframe para mostrarse
		 * Importante al ejecutar report1.jasper o testEstatico.jasper
		 * (Reportes que no se conectan a un BD)
		 * se debe descomentar la linea comentada debajo que usa
		 * JREmptyDataSource() y comentar la que usa la variable con
		 * sino mostrara reportes en blanco 
		*/
		
		//JasperPrint print = JasperFillManager.fillReport(reportSrc,parameters,new JREmptyDataSource());
		JasperPrint print = JasperFillManager.fillReport(reportSrc,parameters,con);
		ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
		JRExporter exporter = new JRPdfExporter();
		exporter.setParameters(parameters);
		exporter.setParameter(JRExporterParameter.JASPER_PRINT ,print);
		exporter.setParameter(JRExporterParameter.OUTPUT_STREAM,arrayOutputStream);
		exporter.exportReport();
		arrayOutputStream.close();
		am = new AMedia("mai-chan.pdf", "pdf","pdf/application", arrayOutputStream.toByteArray());
		frame.setContent(am);
		
		
	}
	
	public void showReportZKtag() {
		/* Requiere una etiquetas jaspereport
		 * Importante al ejecutar report1.jasper o testEstatico.jasper
		 * se debe comentar la linea setDataConnection
		 * (Reportes que no se conectan a un BD)
		 * ya que no se usa ninguna
		 * sino mostrara reportes en blanco
		 * */
		report.setParameters(parameters);
		//report.setDataConnection(con);
		report.setType((String) format.getSelectedItem().getValue());
		report.setSrc(reportSrc);
		
	}
	
	public void onClick$btnReportes() throws JRException, IOException {
		showReportZKtag();
		
		
	}

}

